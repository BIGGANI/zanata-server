package org.zanata.rest.service;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.jboss.seam.annotations.Name;
import org.zanata.rest.MediaTypes;
import org.zanata.rest.dto.VersionInfo;
import org.zanata.util.VersionUtility;


@Name("versionService")
@Path(VersionService.SERVICE_PATH)
public class VersionService implements VersionResource
{
   public static final String SERVICE_PATH = "/version";
   
   private final VersionInfo version;

   public VersionService()
   {
      this(VersionUtility.getAPIVersionInfo());
   }

   VersionService(VersionInfo ver)
   {
      this.version = ver;
   }
   
   /**
    * Retrieve VersionInfo
    * 
    * @return VersionInfo
    */
   @Override
   @GET
   @Produces({ MediaTypes.APPLICATION_ZANATA_VERSION_JSON, MediaTypes.APPLICATION_ZANATA_VERSION_XML })
   public Response get()
   {
      return Response.ok(version).build();
   }
}
