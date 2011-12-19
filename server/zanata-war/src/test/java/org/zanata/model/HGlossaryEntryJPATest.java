/*
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.hibernate.Session;
import org.junit.Assert;
import org.testng.annotations.Test;
import org.zanata.ZanataDbunitJpaTest;
import org.zanata.common.LocaleId;
import org.zanata.dao.GlossaryDAO;
import org.zanata.dao.LocaleDAO;
import org.zanata.model.HGlossaryEntry;
import org.zanata.model.HGlossaryTerm;
import org.zanata.model.HLocale;
import org.zanata.service.impl.LocaleServiceImpl;

/**
 *
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 *
 **/
@Test(groups = { "jpa-tests" })
public class HGlossaryEntryJPATest extends ZanataDbunitJpaTest
{
   IMocksControl control = EasyMock.createControl();

   <T> T createMock(String name, Class<T> toMock)
   {
      T mock = control.createMock(name, toMock);
      return mock;
   }

   private GlossaryDAO dao;
   HGlossaryEntry entry;
   LocaleServiceImpl localeService;


   @Test
   public void testHashMap()
   {

      List<HGlossaryEntry> entryList = dao.getEntries();

      for (HGlossaryEntry hGlossaryEntry : entryList)
      {
         for (Map.Entry<HLocale, HGlossaryTerm> entry : hGlossaryEntry.getGlossaryTerms().entrySet())
         {
            Assert.assertTrue(hGlossaryEntry.getGlossaryTerms().containsKey(entry.getKey()));
            Assert.assertNotNull(hGlossaryEntry.getGlossaryTerms().get(entry.getKey()));
         }
      }

   }

   @Test
   public void testTermsSize()
   {
      List<HGlossaryEntry> entryList = dao.getEntries();
      assertThat(entryList.get(0).getGlossaryTerms().size(), is(3));
   }

   @Override
   protected void prepareDBUnitOperations()
   {
      localeService = new LocaleServiceImpl();
      LocaleDAO localeDAO = new LocaleDAO(getSession());
      localeService.setLocaleDAO(localeDAO);

      dao = new GlossaryDAO((Session) getEm().getDelegate());

      localeService.save(LocaleId.EN_US);
      localeService.save(LocaleId.DE);
      localeService.save(LocaleId.ES);

      entry = new HGlossaryEntry();
      entry.setVersionNum(1);
      entry.setCreationDate(new Date());
      entry.setLastChanged(new Date());

      entry.setSrcLocale(localeService.getByLocaleId(LocaleId.EN_US));
      entry.setSourceRef("source ref");

      // Glossary Term 1 - EN_US
      setupTerm("TERM 1", localeService.getByLocaleId(LocaleId.EN_US));

      // Glossary Term 2 - DE
      setupTerm("TERM 2", localeService.getByLocaleId(LocaleId.DE));

      // Glossary Term 3 - ES
      setupTerm("TERM 3", localeService.getByLocaleId(LocaleId.ES));

      dao.makePersistent(entry);
      dao.flush();
      dao.clear();

   }

   private void setupTerm(String content, HLocale locale)
   {
      HGlossaryTerm term = new HGlossaryTerm(content);
      term.setVersionNum(1);
      term.setCreationDate(new Date());
      term.setLastChanged(new Date());

      // Glossary Term Locale
      term.setLocale(locale);
      term.setGlossaryEntry(entry);

      entry.getGlossaryTerms().put(locale, term);
   }
}


 