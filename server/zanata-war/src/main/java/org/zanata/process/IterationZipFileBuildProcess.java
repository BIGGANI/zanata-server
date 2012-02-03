/*
 * Copyright 2010, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.process;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.zanata.adapter.po.PoWriter2;
import org.zanata.common.LocaleId;
import org.zanata.dao.DocumentDAO;
import org.zanata.dao.LocaleDAO;
import org.zanata.dao.TextFlowTargetDAO;
import org.zanata.model.HDocument;
import org.zanata.model.HLocale;
import org.zanata.rest.dto.resource.Resource;
import org.zanata.rest.dto.resource.TranslationsResource;
import org.zanata.rest.service.ResourceUtils;
import org.zanata.service.ConfigurationService;
import org.zanata.service.FileSystemService;

/**
 * Background Process implementation that builds a zip file with all translation file
 * for a given Project Iteration and a given Locale.
 * 
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@Name("iterationZipFileBuildProcess")
@AutoCreate
public class IterationZipFileBuildProcess extends BackgroundProcess<IterationZipFileBuildProcessHandle>
{   
   @In
   private DocumentDAO documentDAO;
   
   @In
   private LocaleDAO localeDAO;
   
   @In
   private ResourceUtils resourceUtils;
   
   @In
   private TextFlowTargetDAO textFlowTargetDAO;
   
   @In
   private FileSystemService fileSystemServiceImpl;
   
   @In
   private ConfigurationService configurationServiceImpl;

   @Override
   protected void runProcess(IterationZipFileBuildProcessHandle zipHandle) throws Exception
   {
      final String projectSlug = zipHandle.getProjectSlug();
      final String iterationSlug = zipHandle.getIterationSlug();
      final String projectType = "podir";
      final String localeId = zipHandle.getLocaleId();
      final String userName = zipHandle.getInitiatingUserName();

      final List<HDocument> allIterationDocs = this.documentDAO.getAllByProjectIteration(projectSlug, iterationSlug);
      zipHandle.setMaxProgress(allIterationDocs.size() + 1);
      
      final String projectDirectory = projectSlug + "-" + iterationSlug + "/";
      final HLocale hLocale = this.localeDAO.findByLocaleId(new LocaleId(localeId));
      final String mappedLocale = hLocale.getLocaleId().getId();
      final String localeDirectory = projectDirectory + mappedLocale + "/";

      final File downloadFile = this.fileSystemServiceImpl.createDownloadStagingFile("zip");
      final FileOutputStream output = new FileOutputStream( downloadFile );
      final ZipOutputStream zipOutput = new ZipOutputStream(output);
      zipOutput.setMethod(ZipOutputStream.DEFLATED);
      final PoWriter2 poWriter = new PoWriter2();
      final Set<String> extensions = new HashSet<String>();
      
      extensions.add("gettext");
      extensions.add("comment");
      
      // Generate the download descriptor file
      String downloadId = this.fileSystemServiceImpl.createDownloadDescriptorFile(downloadFile, 
            projectSlug + "_" + iterationSlug + "_" + localeId + ".zip",
            userName);
      zipHandle.setDownloadId(downloadId);
      
      // Add the config file at the root of the project directory
      String configFilename = projectDirectory + this.configurationServiceImpl.getConfigurationFileName();
      zipOutput.putNextEntry(new ZipEntry(configFilename));
      zipOutput.write( 
            this.configurationServiceImpl.getConfigurationFileContents(projectSlug, iterationSlug, projectType).getBytes());
      zipOutput.closeEntry();
      zipHandle.incrementProgress(1);


      for (HDocument document : allIterationDocs)
      {
         // Stop the process if signaled to do so
         if (zipHandle.getShouldStop())
         {
            zipOutput.close();
            downloadFile.delete();
            this.fileSystemServiceImpl.deleteDownloadDescriptorFile(downloadId);
            return;
         }
            
         TranslationsResource translationResource = new TranslationsResource();
         resourceUtils.transferToTranslationsResource(
               translationResource, document, hLocale, extensions, 
               textFlowTargetDAO.findTranslations(document, hLocale));

         Resource res = this.resourceUtils.buildResource( document );
         
         String filename = localeDirectory + document.getDocId() + ".po";
         zipOutput.putNextEntry(new ZipEntry(filename));
         poWriter.writePo(zipOutput, "UTF-8", res, translationResource);
         zipOutput.closeEntry();
         zipHandle.incrementProgress(1);
      }
      
      zipOutput.flush();
      zipOutput.close();
   }

}
