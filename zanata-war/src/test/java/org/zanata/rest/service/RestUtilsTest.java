package org.zanata.rest.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.core.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.zanata.rest.dto.VersionInfo;
import org.zanata.rest.dto.resource.Resource;
import org.zanata.rest.dto.resource.ResourceMeta;
import org.zanata.rest.dto.resource.TranslationsResource;
import org.zanata.seam.SeamAutowire;
import com.allen_sauer.gwt.log.client.Log;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@Test(groups = { "unit-tests" })
public class RestUtilsTest
{
   RestUtils restUtils;

   private ResourceTestObjectFactory resourceTestFactory = new ResourceTestObjectFactory();
   private TranslationsResourceTestObjectFactory transTestFactory = new TranslationsResourceTestObjectFactory();
   private final Logger log = LoggerFactory.getLogger(RestUtilsTest.class);

   @BeforeTest
   public void prepareSeam()
   {
      ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

      SeamAutowire seam = SeamAutowire.instance();
      restUtils = seam.reset()
                      .use("validatorFactory", validatorFactory)
                      .use("validator", validatorFactory.getValidator())
                      .autowire(RestUtils.class);
   }

   @DataProvider(name = "ResourceTestData")
   public Object[][] getResourceTestData()
   {
      return new Object[][] { new Object[] { resourceTestFactory.getPoHeaderTest() }, new Object[] { resourceTestFactory.getPotEntryHeaderTest() }, new Object[] { resourceTestFactory.getTextFlowCommentTest() }
, new Object[] { resourceTestFactory.getTextFlowTest2() }
      };
   }

   @DataProvider(name = "ResourceMetaTestData")
   public Object[][] getResourceMetaTestData()
   {
      return new Object[][] { new Object[] { resourceTestFactory.getResourceMeta() }, new Object[] { resourceTestFactory.getPoHeaderResourceMeta() }
      };
   }

   @DataProvider(name = "TranslationTestData")
   public Object[][] getTranslationTestData()
   {
      return new Object[][] { new Object[] { transTestFactory.getPoTargetHeaderTextFlowTargetTest() }, new Object[] { transTestFactory.getTestObject() }, new Object[] { transTestFactory.getTestObject2() }, new Object[] { transTestFactory.getTextFlowTargetCommentTest() }
      };
   }

   @Test(dataProvider = "ResourceTestData")
   public void testUnmarshallResource(Resource res) throws UnsupportedEncodingException
   {
      // SeamMockClientExecutor test = new SeamMockClientExecutor();
      // ClientRequest client = test.createRequest("http://example.com/");
      // MultivaluedMap<String, String> header = client.getHeaders();
      testRestUtilUnmarshall(res, Resource.class);
   }

   private <T extends Serializable> void testRestUtilUnmarshall(T entity, Class<T> type) throws UnsupportedEncodingException
   {
      InputStream messageBody = null;
      try
      {
         String testStr = entity.toString();
         log.info("expect:" + testStr);

         messageBody = new ByteArrayInputStream(testStr.getBytes("UTF-8"));
         T unmarshall = restUtils.unmarshall(type, messageBody, MediaType.APPLICATION_XML_TYPE, new Headers<String>());
         Log.info("got:" + unmarshall.toString());
         assertThat(entity.toString(), is(testStr));
      }
      finally
      {
         if (messageBody != null)
         {
            try
            {
               messageBody.close();
            }
            catch (IOException e)
            {
            }
         }
      }
   }

   @Test
   public void testUnmarshallJasonTranslationsResource()
   {
      log.info("start jason");
      InputStream messageBody = null;
      try
      {
         // String testStr =
         // "{\"resId\":\"782f49c4e93c32403ba0b51821b38b90\",\"state\":\"Approved\",\"translator\":{},\"content\":\"title: ttff\",\"extensions\":[{\"object-type\": \"comment\",\"value\": \"testcomment\", \"space\": \"preserve\"}]}";
         String testStr = "{\"textFlowTargets\":[{\"resId\":\"rest1\",\"state\":\"Approved\",\"translator\":{\"email\":\"root@localhost\",\"name\":\"Admin user\"},\"content\": \"<title>\u8bbf\u95ee\u5b58\u53d6\u63a7\u5236\u5217\u8868</title>\"},{\"resId\":\"rest2\",\"state\":\"Approved\",\"translator\":{\"email\":\"root@localhost\",\"name\":\"Admin user\"},\"content\":\"hello world\"}]}";

         messageBody = new ByteArrayInputStream(testStr.getBytes("UTF-8"));
         TranslationsResource unmarshall = restUtils.unmarshall(TranslationsResource.class, messageBody, MediaType.APPLICATION_JSON_TYPE, null);
         log.info("got:" + unmarshall.toString());
      }
      catch (UnsupportedEncodingException e)
      {
         e.printStackTrace();
      }
      finally
      {
         if (messageBody != null)
         {
            try
            {
               messageBody.close();
            }
            catch (IOException e)
            {
            }
         }
      }
   }


   @Test(dataProvider = "TranslationTestData")
   public void testUnmarshallTranslation(TranslationsResource res) throws UnsupportedEncodingException
   {
      testRestUtilUnmarshall(res, TranslationsResource.class);
   }

   @Test(dataProvider = "ResourceMetaTestData")
   public void testUnmarshallResourceMeta(ResourceMeta res) throws UnsupportedEncodingException
   {
      testRestUtilUnmarshall(res, ResourceMeta.class);
   }

   @Test
   public void testVersion() throws UnsupportedEncodingException
   {
      VersionInfo ver = new VersionInfo(null, null);
      testRestUtilUnmarshall(ver, VersionInfo.class);
   }

}
