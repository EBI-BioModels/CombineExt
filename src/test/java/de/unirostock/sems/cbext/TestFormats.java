/**
 * Copyright © 2014-2015:
 * - Martin Scharm <martin@binfalse.de>
 * - Martin Peters <martin@freakybytes.net>
 * <p>
 * This file is part of the CombineExt library.
 * <p>
 * CombineExt is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * CombineExt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with CombineExt. If not, see <http://www.gnu.org/licenses/>.
 */
package de.unirostock.sems.cbext;

import de.binfalse.bflog.LOGGER;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import net.biomodels.jummp.utils.ProxySetting;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

import static org.junit.Assert.*;


/**
 * The Class TestFormats.
 *
 * @author Martin Scharm
 */
@RunWith(JUnitParamsRunner.class)
public class TestFormats {
   /**
    * Check format.
    *
    * @param filePath      the file path
    * @param expectedGuess the expected format by guess
    * @param expectedExt   the expected format from the extension
    * @param expectedMime  the expected format from the mime
    */
   public static void checkFormat(String filePath, String expectedGuess,
                                  String expectedExt, String expectedMime) {
      File file = new File(filePath);
      String absFilePath = file.getAbsolutePath();
      try {
         URI format = Formatizer.guessFormat(file);
         assertEquals("got wrong format for guessing " + absFilePath, expectedGuess, format.toString());

         Proxy proxy = ProxySetting.detect();
         URLConnection connection;
         if (proxy != null) {
            connection = file.toURI().toURL().openConnection(proxy);
         } else {
            connection = file.toURI().toURL().openConnection();
         }
         format = Formatizer.getFormatFromMime(connection.getContentType());
         assertEquals("got wrong format for mime of " + absFilePath, expectedMime, format.toString());

         String fileExt = file.getName().substring(file.getName().lastIndexOf(".") + 1);
         format = Formatizer.getFormatFromExtension(fileExt);
         assertEquals("got wrong format for ext of " + absFilePath, expectedExt, format.toString());
      } catch (IOException e) {
         e.printStackTrace();
         fail("couldn't test format for " + absFilePath);
      }
   }

   /**
    * Test some stuff that definitely need to be correct.
    */
   @Test
   public void testStatics() {
      try {
         assertEquals("omex uri is incorrect", new URI(
                         "https://identifiers.org/combine.specifications/omex"),
                 Formatizer.getFormatFromExtension("omex"));
      } catch (URISyntaxException e) {
         e.printStackTrace();
         LOGGER.error(e, "something went wrong");
         fail("error testing statics.");
      }
   }

   /**
    *
    */
   @Test
   public void testIconize() {
      URI format = null;
      try {
         format = new URI(
                 "https://identifiers.org/combine.specifications/sbml.level-1.version-1");
      } catch (URISyntaxException e) {
         e.printStackTrace();
         fail("failed to gen sbml uri");
      }

      String iconName = Iconizer.formatToIcon(format);
      assertEquals("expected to get the SBML icon", "Blue-sbml.png", iconName);

      URL iconUrl = Iconizer.formatToIconUrl(format);
      assertTrue("unexpected URL for SBML icon",
              iconUrl.toString().endsWith("Blue-sbml.png"));

      InputStream fin = Iconizer.formatToIconStream(format);
      byte[] bytes = new byte[1024];
      int noOfBytes = 0, b = 0;

      try {
         while ((b = fin.read(bytes)) != -1) {
            noOfBytes += b;
         }
      } catch (IOException e) {
         e.printStackTrace();
         fail("failed to read sbml image");
      }
      assertEquals("sbml image has unexpected size", 1880, noOfBytes);

      assertEquals("expected generic icon for null-format",
              Iconizer.GENERIC_UNKNOWN, Iconizer.formatToIcon(null));
      assertEquals("expected generic icon for bullshit format",
              Iconizer.class.getResource("/icons/" + Iconizer.GENERIC_UNKNOWN),
              Iconizer.formatToIconUrl(null));
      try {
         assertEquals("expected generic icon for bullshit format",
                 Iconizer.GENERIC_UNKNOWN,
                 Iconizer.formatToIcon(new URI("https://identifiers.org/bull/shit")));
         assertEquals("expected generic icon for bullshit format",
                 Iconizer.class.getResource("/icons/" + Iconizer.GENERIC_UNKNOWN),
                 Iconizer.formatToIconUrl(new URI("https://identifiers.org/bull/shit")));
      } catch (URISyntaxException e) {
         e.printStackTrace();
         fail("why did I end here?");
      }
   }

   /**
    *
    */
   @Test
   @Parameters(method = "params2TestFormatize")
   public void testFormatize(String file, String expectedGuess, String expectedExt, String expectedMime) {
      checkFormat(file, expectedGuess, expectedExt, expectedMime);
   }

   private Object[] params2TestFormatize() {
      return new Object[]{
              new Object[]{
                      "test/aguda_b_1999.cellml",
                      "https://identifiers.org/combine.specifications/cellml",
                      "https://identifiers.org/combine.specifications/cellml",
                      "https://purl.org/NET/mediatypes/application/xml"

              },
              new Object[]{
                      "test/aguda_b_1999.cellml.wrong.ext",
                      "https://purl.org/NET/mediatypes/application/xml",
                      Formatizer.GENERIC_UNKNOWN.toString(),
                      "https://purl.org/NET/mediatypes/application/xml"
              },
              new Object[]{
                      "test/guess-biopax-paxtools-core-src-main-resources-org-biopax-paxtools-model-biopax-level3.owl",
                      "https://identifiers.org/combine.specifications/biopax.level-3",
                      Formatizer.GENERIC_UNKNOWN.toString(),
                      "https://purl.org/NET/mediatypes/application/xml"
              },
              new Object[]{
                      "test/guess-SBOLj-examples-data-BBa_I0462.xml",
                      "https://identifiers.org/combine.specifications/sbol",
                      "https://purl.org/NET/mediatypes/application/xml",
                      "https://purl.org/NET/mediatypes/application/xml"
              },
              new Object[]{
                      "test/some.xml",
                      "https://purl.org/NET/mediatypes/application/xml",
                      "https://purl.org/NET/mediatypes/application/xml",
                      "https://purl.org/NET/mediatypes/application/xml"
              },
              new Object[]{
                      "test/some.rdf",
                      "https://purl.org/NET/mediatypes/application/xml",
                      Formatizer.GENERIC_UNKNOWN.toString(),
                      "https://purl.org/NET/mediatypes/application/xml"
              },
              new Object[]{
                      "test/plain.text",
                      "https://purl.org/NET/mediatypes/text/plain",
                      Formatizer.GENERIC_UNKNOWN.toString(),
                      "https://purl.org/NET/mediatypes/text/plain"
              }
      };
   }
}
