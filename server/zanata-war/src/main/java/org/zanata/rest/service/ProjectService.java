package org.zanata.rest.service;

import static org.zanata.common.EntityStatus.Obsolete;
import static org.zanata.common.EntityStatus.Retired;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Identity;
import org.zanata.dao.AccountDAO;
import org.zanata.dao.ProjectDAO;
import org.zanata.model.HAccount;
import org.zanata.model.HIterationProject;
import org.zanata.model.HProject;
import org.zanata.model.HProjectIteration;
import org.zanata.model.validator.SlugValidator;
import org.zanata.rest.MediaTypes;
import org.zanata.rest.dto.Link;
import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;
import org.zanata.util.ZanataUtil;

@Name("projectService")
@Path(ProjectService.SERVICE_PATH)
@Transactional
public class ProjectService implements ProjectResource
{

   public static final String PROJECT_SLUG_TEMPLATE = "{projectSlug:" + SlugValidator.PATTERN + "}";
   public static final String SERVICE_PATH = "/projects/p/" + PROJECT_SLUG_TEMPLATE;

   @PathParam("projectSlug")
   String projectSlug;

   @HeaderParam(HttpHeaderNames.ACCEPT)
   @DefaultValue(MediaType.APPLICATION_XML)
   @Context
   private MediaType accept;

   @Context
   private UriInfo uri;

   @HeaderParam("Content-Type")
   @Context
   private MediaType requestContentType;

   @Context
   private HttpHeaders headers;

   @Context
   private Request request;

   Log log = Logging.getLog(ProjectService.class);

   @In
   ProjectDAO projectDAO;

   @In
   AccountDAO accountDAO;

   @In
   Identity identity;

   @In
   ETagUtils eTagUtils;

   public ProjectService()
   {
   }

   public ProjectService(ProjectDAO projectDAO, AccountDAO accountDAO, Identity identity, ETagUtils eTagUtils)
   {
      this.projectDAO = projectDAO;
      this.accountDAO = accountDAO;
      this.eTagUtils = eTagUtils;
      this.identity = identity;
   }

   @Override
   @HEAD
   @Produces( { MediaTypes.APPLICATION_ZANATA_PROJECT_XML, MediaTypes.APPLICATION_ZANATA_PROJECT_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public Response head()
   {
      EntityTag etag = eTagUtils.generateTagForProject(projectSlug);
      ResponseBuilder response = request.evaluatePreconditions(etag);
      if (response != null)
      {
         return response.build();
      }
      return Response.ok().tag(etag).build();
   }

   @Override
   @GET
   @Produces( { MediaTypes.APPLICATION_ZANATA_PROJECT_XML, MediaTypes.APPLICATION_ZANATA_PROJECT_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public Response get()
   {
      EntityTag etag = eTagUtils.generateTagForProject(projectSlug);

      ResponseBuilder response = request.evaluatePreconditions(etag);
      if (response != null)
      {
         return response.build();
      }

      HProject hProject = projectDAO.getBySlug(projectSlug);
      
      // Obsolete projects are not exposed
      if( ZanataUtil.in(hProject.getStatus(), Obsolete) )
      {
         return Response.status(Status.NOT_FOUND).build();
      }

      Project project = toResource(hProject, accept);
      return Response.ok(project).tag(etag).build();
   }

   /**
    * @return 200 If the project was modified.
    *         201 If the project was created.
    *         404 If the project was not found, or is obsolete.
    *         403 If the project was not modified for some other reason (e.g. project is retired).
    */
   @Override
   @PUT
   @Consumes( { MediaTypes.APPLICATION_ZANATA_PROJECT_XML, MediaTypes.APPLICATION_ZANATA_PROJECT_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
   public Response put(Project project)
   {

      ResponseBuilder response;
      EntityTag etag;

      HProject hProject = projectDAO.getBySlug(projectSlug);

      if (hProject == null)
      { // must be a create operation
         response = request.evaluatePreconditions();
         if (response != null)
         {
            return response.build();
         }
         hProject = new HIterationProject();
         hProject.setSlug(projectSlug);
         // pre-emptive entity permission check
         identity.checkPermission(hProject, "insert");

         response = Response.created(uri.getAbsolutePath());
      }
      // Project is Obsolete
      else if( ZanataUtil.in(hProject.getStatus(), Obsolete) )
      {
         response = Response.status(Status.NOT_FOUND);
         return response.entity("Obsolete Project.").build();
      }
      // Project is retired
      else if( ZanataUtil.in(hProject.getStatus(), Retired) )
      {
         response = Response.status(Status.FORBIDDEN);
         return response.entity("Retired Project.").build();
      }
      else
      {  // must be an update operation
         // pre-emptive entity permission check
         identity.checkPermission(hProject, "update");
         etag = eTagUtils.generateTagForProject(projectSlug);
         response = request.evaluatePreconditions(etag);
         if (response != null)
         {
            return response.build();
         }

         response = Response.ok();
      }

      transfer(project, hProject);

      hProject = projectDAO.makePersistent(hProject);
      projectDAO.flush();

      if (identity != null && hProject.getMaintainers().isEmpty())
      {
         HAccount hAccount = accountDAO.getByUsername(identity.getCredentials().getUsername());
         if (hAccount != null && hAccount.getPerson() != null)
         {
            hProject.getMaintainers().add(hAccount.getPerson());
         }
         projectDAO.flush();
      }

      etag = eTagUtils.generateTagForProject(projectSlug);
      return response.tag(etag).build();

   }

   public static void transfer(Project from, HProject to)
   {
      to.setName(from.getName());
      to.setDescription(from.getDescription());
      // TODO Currently all Projects are created as Current
      //to.setStatus(from.getStatus());
   }

   public static void transfer(HProject from, Project to)
   {
      to.setId(from.getSlug());
      to.setName(from.getName());
      to.setDescription(from.getDescription());
      to.setStatus(from.getStatus());
   }

   public static Project toResource(HProject hProject, MediaType mediaType)
   {
      Project project = new Project();
      transfer(hProject, project);
      if (hProject instanceof HIterationProject)
      {
         HIterationProject itProject = (HIterationProject) hProject;
         for (HProjectIteration pIt : itProject.getProjectIterations())
         {
            ProjectIteration iteration = new ProjectIteration();
            ProjectIterationService.transfer(pIt, iteration);
            iteration.getLinks(true).add(new Link(URI.create("iterations/i/" + pIt.getSlug()), "self", MediaTypes.createFormatSpecificType(MediaTypes.APPLICATION_ZANATA_PROJECT_ITERATION, mediaType)));
            project.getIterations(true).add(iteration);
         }
      }

      return project;
   }

}
