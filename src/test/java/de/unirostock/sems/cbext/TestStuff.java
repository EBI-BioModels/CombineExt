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

import de.unirostock.sems.cbext.collections.DefaultIconCollection;
import de.unirostock.sems.cbext.recognizer.DefaultRecognizer;
import de.unirostock.sems.cbext.recognizer.SbmlRecognizer;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import static de.unirostock.sems.cbext.FormatRecognizer.buildUri;
import static org.junit.Assert.*;


/**
 * The Class TestStuff.
 *
 * @author Martin Scharm
 */
@RunWith(JUnitParamsRunner.class)
public class TestStuff {

   /**
    * Test format parser.
    */
   @Test
   public void testFormatParser() {
      assertNotNull("valid URI shouln't return null",
              buildUri("http://", "binfalse.de"));
      assertNull("valid URI shouldn't return null",
              buildUri(":", "binfalse.de"));
   }


   /**
    * Test default icon mapper.
    */
   @Test
   public void testDefaultIconMapper() {
      DefaultIconCollection dim = new DefaultIconCollection();
      assertEquals("expected a priority of 100 for the DefaultIconMapper", 100,
              dim.getPriority());

      Set<Object> keys = dim.getAvailableFormatIcons();
      for (Object k : keys) {
         try {
            URI format = new URI((String) k);
            assertTrue("did not find icon for format: " + format,
                    dim.hasIcon(format));
            assertNotNull("cannot find icon name for format: " + format,
                    dim.formatToIconName(format));
            assertNotNull("cannot find icon url for format: " + format,
                    dim.formatToIconUrl(format));
            assertNotNull("cannot open icon stream for format: " + format,
                    dim.formatToIconStream(format));

            format = new URI("http://binfalse.de");
            assertFalse("did not expect to find icon for format: " + format,
                    dim.hasIcon(format));
            assertNull("did not expect to find icon name for format: " + format,
                    dim.formatToIconName(format));
            assertNull("did not expect to find icon url for format: " + format,
                    dim.formatToIconUrl(format));
            assertNull("did not expect to open icon stream for format: " + format,
                    dim.formatToIconStream(format));

         } catch (URISyntaxException e) {
            e.printStackTrace();
            fail("there is apparently a wrong format url in the icon mapper list?");
         }
      }
   }


   /**
    * Test default extension mapper.
    */
   @Test
   public void testDefaultRecognizer() {
      DefaultRecognizer dem = new DefaultRecognizer();
      assertEquals("expected a priority of 100 for the DefaultExtensionMapper",
              100, dem.getPriority());

      assertNull("didn't expect format for null", dem.getFormatFromMime(null));

      try {
         URI format = new URI(Formatizer.PURL_ORG_PREFIX + "application/xml");
         assertEquals("unexpected format for .xml", format,
                 dem.getFormatFromExtension("xml"));
         assertNull("unexpected format for .xml",
                 dem.getFormatFromMime("application/xml"));
         assertEquals("unexpected format for .xml", format,
                 Formatizer.getFormatFromMime("application/xml"));
      } catch (URISyntaxException e) {
         e.printStackTrace();
         fail("couldn't create URI");
      }
   }


   /**
    * Test iconizer.
    */
   @Test
   public void testIconizer() {
      Iconizer iconizr = new Iconizer();
      try {
         File tmp = iconizr.extractIconExample();
         tmp = iconizr.extractIconExample();
         tmp.delete();
      } catch (IOException | URISyntaxException e) {
         e.printStackTrace();
         fail("unexpected exception");
      }

      try {
         Iconizer.addIconCollection(null);
         fail("expected to get an exception from a null mapper");
      } catch (IllegalArgumentException e) {
         // that's ok
      }

      // test empty icon
      InputStream fin = Iconizer.formatToIconStream(null);
      byte[] bytes = new byte[1024];
      int noOfBytes = 0, b = 0;

      try {
         while ((b = fin.read(bytes)) != -1) {
            noOfBytes += b;
         }
      } catch (IOException e) {
         e.printStackTrace();
         fail("failed to read generic icon");
      }
      assertEquals("generic icon has unexpected size", 1487, noOfBytes);

      // test icon for unknown uri
      fin = Iconizer.formatToIconStream(buildUri("https://",
              "binfalse.de"));
      bytes = new byte[1024];
      noOfBytes = 0;
      b = 0;

      try {
         while ((b = fin.read(bytes)) != -1) {
            noOfBytes += b;
         }
      } catch (IOException e) {
         e.printStackTrace();
         fail("failed to read generic icon");
      }
      assertEquals("generic icon has unexpected size", 1487, noOfBytes);

   }


   /**
    * Test formatizer.
    */
   @Test
   public void testFormatizer() {
      new Formatizer();

      try {
         Formatizer.addFormatRecognizer(null);
         fail("expected to get an exception from a null parser");
      } catch (IllegalArgumentException e) {
         // that's ok
      }

      assertNull("expected null for a null file", Formatizer.guessFormat(null));
      assertNull("expected null for a non-file",
              Formatizer.guessFormat(new File("non ex ist ing")));
      assertEquals("expected cellml format for a cellml file",
              "https://identifiers.org/combine.specifications/cellml", Formatizer
                      .guessFormat(new File("test/aguda_b_1999.cellml")).toString());
      assertEquals("expected biopax format for a file with biopax extension",
              "https://identifiers.org/combine.specifications/biopax", Formatizer
                      .guessFormat(new File("test/aguda_b_1999-invalid.biopax"))
                      .toString());
      assertEquals(
              "expected plaintext format for a plain text file w/o extension",
              Formatizer.GENERIC_UNKNOWN.toString(),
              Formatizer.guessFormat(new File("test/plaintext")).toString());

      assertEquals("expected generic format for null ext",
              Formatizer.GENERIC_UNKNOWN, Formatizer.getFormatFromExtension(null));
      assertEquals("expected generic format for null mime",
              Formatizer.GENERIC_UNKNOWN, Formatizer.getFormatFromMime(null));

   }

   private Object[] params2TestReadSBMLDocument() {
      return new Object[]{
        new Object[]{
          "test/00001-sbml-l2v1.xml",
          "sbml.level-2.version-1",
        },
        new Object[]{
          "test/BIOMD0000000459.xml",
          "sbml.level-2.version-4",
        },
        new Object[]{
          "test/BIOMD0000000624.xml",
          "sbml.level-2.version-4",
        },
        new Object[]{
          "test/Stucki2005.xml",
          "sbml.level-2.version-4",
        }
      };
   }
   @Test
   @Parameters(method = "params2TestReadSBMLDocument")
   public void testReadSBMLDocument(final String sbmlFilePath, final String expectedLevelVersion) {
      File file = new File(sbmlFilePath);
      try {
         SBMLReader reader = new SBMLReader();
         SBMLDocument doc = reader.readSBMLFromFile(file.getAbsolutePath());
         assertNotNull(doc);
         String actualLevelVersion = "sbml.level-" + doc.getLevel() + ".version-" + doc.getVersion();
         assertEquals(expectedLevelVersion, actualLevelVersion);
      } catch (Exception e) {
         //LOGGER.info(e, "file ", file, " seems to be a valid SBML document.");
         System.out.println("file " + file.getName() + " seems to be an invalid SBML document.");
         e.printStackTrace();
      }
   }

   @Test
   public void testSBMLLevelAndVersion() {
      File file = new File("test/BIOMD0000000459.xml");
      try {
         SbmlRecognizer sbmlRecognizer = new SbmlRecognizer();
         String[] lv = sbmlRecognizer.getSbmlLevelAndVersion(file.getAbsolutePath());
         System.out.println("Level: " + lv[0] + " Version: " + lv[1]);
         assertEquals("2", lv[0]);
         assertEquals("4", lv[1]);
         URI uri = sbmlRecognizer.getFormatByParsing(file, "application/xml");
         System.out.println(uri);
         URI expectedURI = new URI("https://identifiers.org/combine.specifications/sbml.level-2.version-4");
         assertEquals(expectedURI, uri);
      } catch (IOException e) {
         throw new RuntimeException(e);
      } catch (XMLStreamException e) {
         throw new RuntimeException(e);
      } catch (URISyntaxException e) {
          throw new RuntimeException(e);
      }
   }
}
