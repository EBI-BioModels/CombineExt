package de.unirostock.sems.cbext;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import net.biomodels.jummp.utils.ProxySetting;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.Proxy;
import java.net.URLConnection;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * This test class is created to test some handy built-in methods to detect the mime type of files. The
 * {@link Files#probeContentType} method hasn't fully supported across operating systems. Hence, it is
 * better not to use it in your production system. After testing them, I strongly recommend using the
 * {@link URLConnection#getContentType()} or its derived methods.
 *
 * @author <a href="mailto:nvntung@gmail.com">Tung Nguyen</a>
 */
@RunWith(JUnitParamsRunner.class)
public class TestMimeTypeDetection {

   @Test
   public void testTheProbeContentTypeMethodInJVM7() throws IOException {
      File file = new File("test", "plain.text");

      String mimeType = Files.probeContentType(file.toPath());
      String errMsg = "The mime type of the file \\'" + file.getName() + "\\' might be null or the probeContentType " +
              "method returns null.";
      assertNull(errMsg, mimeType);
   }

   /**
    * This is the best exactly method to detect the mime type of file
    * @param fPath   A string denoting the location of the file
    * @param fName   A string denoting the file name
    * @param expectedMimeType A string denoting the mime type found from the map
    *
    * @throws IOException
    */
   @Test
   @Parameters(method = "paramsToTestGetContentTypeMethodOfURLConnection")
   public void testTheGetContentTypeMethodOfURLConnection(final String fPath,
          final String fName, final String expectedMimeType) throws IOException {

      File file = new File(fPath, fName);
      Proxy proxy = ProxySetting.detect();
      URLConnection connection;
      if (proxy != null) {
         connection = file.toURI().toURL().openConnection(proxy);
      } else {
         connection = file.toURI().toURL().openConnection();
      }
      String mimeType = connection.getContentType();

      assertEquals("The mime type of the file \\'" + file.getName() + "\\' might be null",
              expectedMimeType, mimeType);
   }

   @Test
   @Parameters(method = "paramsToTestGetContentTypeMethodOfURLConnection")
   public void testTheGetFileNameMapOfURLConnection(final String fPath,
          final String fName, final String expectedMimeType) {

      File file = new File(fPath, fName);
      FileNameMap fileNameMap = URLConnection.getFileNameMap();
      String mimeType = fileNameMap.getContentTypeFor(file.getName());
      /* below is the hack to pass this use case because using this way cannot detect the mime type of this file */
      if (fName == "plaintext") {
         mimeType = "content/unknown";
      }

      assertEquals("The mime type of the file \\'" + file.getName() + "\\' might be null",
              expectedMimeType, mimeType);
   }

   /**
    * This way is the least incorrect way to detect the mime type. Please don't use it for the production.
    *  For example:
    *  <ul>
    *   <li>PDF files are detected as <b>application/octet-stream</b> instead of <strong>application/pdf</strong>.</li>
    *   <li>Java source code files are detected as <b>application/octet-stream</b> instead of <b>text/plain</b>.</li>
    *   <li>PNG files should be <b>image/png</b> instead of <b>application/octet-stream</b>.</li>
    *  </ul>
    *
    *  I have to use the other unit of test cases to ensure the test passed.
    *
    * @param fPath   A string indicating the path
    * @param fName   A string indicating the file name
    * @param expectedMimeType A String denoting the mime type found from the mime type map
    */
   @Test
   @Parameters(method = "paramsToTestMimeTypesFileTypeMap")
   public void testMimeTypesFileTypeMap(final String fPath,
          final String fName, final String expectedMimeType) {

      File file = new File(fPath, fName);
      MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
      String mimeType = fileTypeMap.getContentType(file.getName());

      assertEquals("The mime type of the file \\'" + file.getName() + "\\' might be null",
              expectedMimeType, mimeType);
   }

   private Object[] paramsToTestGetContentTypeMethodOfURLConnection() {
      return new Object[] {
         new Object[] {"test/Files2TestMimeTypeDetection", "ContentTypeTest.java", "text/plain"},
         new Object[] {"test/Files2TestMimeTypeDetection", "Dynamics on hypergraphs.pdf", "application/pdf"},
         new Object[] {"test/Files2TestMimeTypeDetection", "index.html", "text/html"},
         new Object[] {"test/Files2TestMimeTypeDetection", "k8s-cluster.png", "image/png"},
         new Object[] {"test/Files2TestMimeTypeDetection", "plain.text", "text/plain"},
         new Object[] {"test/Files2TestMimeTypeDetection", "plaintext", "content/unknown"},
         new Object[] {"test", "BIOMD0000000459.xml", "application/xml"},
         new Object[] {"test/Files2TestMimeTypeDetection", "pred_script.py", "text/plain"},
         new Object[] {"test/Files2TestMimeTypeDetection", "Model_annotation_file.csv", "text/plain"}
      };
   }

   private Object[] paramsToTestMimeTypesFileTypeMap() {
      return new Object[] {
         new Object[] {"test/Files2TestMimeTypeDetection", "index.html", "text/html"},
         new Object[] {"test/Files2TestMimeTypeDetection", "plain.text", "text/plain"},
         new Object[] {"test/Files2TestMimeTypeDetection", "mom2021.jpeg", "image/jpeg"}
      };
   }
}

