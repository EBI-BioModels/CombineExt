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

import de.unirostock.sems.cbext.recognizer.*;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

import static org.junit.Assert.*;


/**
 * @author Martin Scharm
 * @author Tung Nguyen
 *
 */
@RunWith(JUnitParamsRunner.class)
public class TestFormatGuessing {

   /** The Constant XML_FILE. */
   public static final File XML_FILE = new File("test/some.xml");


   private Object[] params2TestGuessSBML() {
      return new Object[]{
              new Object[]{
                "test/00001-sbml-l2v1.xml",
                "https://identifiers.org/combine.specifications/sbml.level-2.version-1",
              },
              new Object[]{
                "test/BIOMD0000000459.xml",
                "https://identifiers.org/combine.specifications/sbml.level-2.version-4",
              },
              new Object[]{
                "test/BIOMD0000000624.xml",
                "https://identifiers.org/combine.specifications/sbml.level-2.version-4",
              },
              new Object[]{
                "test/Stucki2005.xml",
                "https://identifiers.org/combine.specifications/sbml.level-2.version-4",
              }
      };
   }

   /**
    * Test SBML guessing.
    */
   @Test
   @Parameters(method = "params2TestGuessSBML")
   public void testGuessSBML(final String filePath, final String correctFormat) {
      File file = new File(filePath);
      String detectedFormat = Formatizer.guessFormat(file).toString();
      assertEquals(correctFormat, detectedFormat);

      // test the recognizer
      SbmlRecognizer recognizer = new SbmlRecognizer();
      String mime = null;
      try {
         mime = Files.probeContentType(file.toPath());
      } catch (IOException e) {
         e.printStackTrace();
         fail("wasn't able to get mime...");
      }
      URI uri = recognizer.getFormatByParsing(file, mime);
      String fn = file.getAbsolutePath();
      assertEquals("got wrong format for " + fn, correctFormat, uri.toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatByParsing(XML_FILE, mime));

      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime("something"));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(mime));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(null));

      assertEquals("got wrong format for " + fn,
              "https://identifiers.org/combine.specifications/sbml", recognizer.getFormatFromExtension("sbml").toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension(null));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension("stuff"));
   }


   /**
    * Test SBOL guessing.
    */
   @Test
   public void testGuessSBOL() {
      File f = new File("test/guess-SBOLj-examples-data-BBa_I0462.xml");
      String fn = f.getAbsolutePath();
      String correctFormat = "https://identifiers.org/combine.specifications/sbol";
      URI format = Formatizer.guessFormat(f);
      assertEquals("got wrong format for " + fn, correctFormat,
              format.toString());

      // test the recognizer
      SbolRecognizer recognizer = new SbolRecognizer();
      String mime = null;
      try {
         mime = Files.probeContentType(f.toPath());
      } catch (IOException e) {
         e.printStackTrace();
         fail("wasn't able to get mime...");
      }

      assertEquals("got wrong format for " + fn, correctFormat, recognizer
              .getFormatByParsing(f, mime).toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatByParsing(XML_FILE, mime));

      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime("something"));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(mime));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(null));

      assertEquals("got wrong format for " + fn, correctFormat, recognizer
              .getFormatFromExtension("sbol").toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension(null));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension("stuff"));
   }


   /**
    * Test BioPax guessing.
    */
   @Test
   public void testGuessBioPax() {
      File f = new File(
              "test/guess-biopax-paxtools-core-src-main-resources-org-biopax-paxtools-model-biopax-level3.owl");
      String fn = f.getAbsolutePath();
      String correctFormat = "https://identifiers.org/combine.specifications/biopax.level-3";
      URI format = Formatizer.guessFormat(f);
      assertEquals("got wrong format for " + fn, correctFormat, format.toString());

      // test the recognizer
      BioPaxRecognizer recognizer = new BioPaxRecognizer();
      String mime = null;
      try {
         mime = Files.probeContentType(f.toPath());
      } catch (IOException e) {
         e.printStackTrace();
         fail("wasn't able to get mime...");
      }

      assertEquals("got wrong format for " + fn, correctFormat, recognizer
              .getFormatByParsing(f, mime).toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatByParsing(XML_FILE, mime));

      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime("something"));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(mime));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(null));

      correctFormat = "https://identifiers.org/combine.specifications/biopax";
      assertEquals("got wrong format for " + fn, correctFormat, recognizer
              .getFormatFromExtension("biopax").toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension(null));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension("stuff"));
   }


   /**
    * Test CellML guessing.
    */
   @Test
   public void testGuessSEDML() {
      File f = new File("test/BIOMD0000000459-SEDML.xml");
      String fn = f.getAbsolutePath();
      String correctFormat = "https://identifiers.org/combine.specifications/sed-ml.level-1.version-1";
      URI format = Formatizer.guessFormat(f);
      assertEquals("got wrong format for " + fn, correctFormat,
              format.toString());

      // test the recognizer
      SedMlRecognizer recognizer = new SedMlRecognizer();
      String mime = null;
      try {
         mime = Files.probeContentType(f.toPath());
         System.out.println(mime);
      } catch (IOException e) {
         e.printStackTrace();
         fail("wasn't able to get mime...");
      }

      assertEquals("got wrong format for " + fn, correctFormat, recognizer
              .getFormatByParsing(f, mime).toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatByParsing(XML_FILE, mime));

      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime("something"));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(mime));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(null));

      assertEquals("got wrong format for " + fn,
              "https://identifiers.org/combine.specifications/sed-ml", recognizer
                      .getFormatFromExtension("sedml").toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension(null));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension("stuff"));
   }


   /**
    * Test SEDML L1V2 guessing.
    */
   @Test
   public void testGuessSEDMLL1V2() {
      File f = new File("test/v3-example1-repeated-steady-scan-oscli.xml");
      String fn = f.getAbsolutePath();
      String correctFormat = "https://identifiers.org/combine.specifications/sed-ml.level-1.version-2";
      URI format = Formatizer.guessFormat(f);
      assertEquals("got wrong format for " + fn, correctFormat, format.toString());

      // test the recognizer
      SedMlRecognizer recognizer = new SedMlRecognizer();
      String mime = null;
      try {
         mime = Files.probeContentType(f.toPath());
         System.out.println(mime);
      } catch (IOException e) {
         e.printStackTrace();
         fail("wasn't able to get mime...");
      }

      assertEquals("got wrong format for " + fn, correctFormat, recognizer
              .getFormatByParsing(f, mime).toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatByParsing(XML_FILE, mime));

      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime("something"));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(mime));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(null));

      assertEquals("got wrong format for " + fn,
              "https://identifiers.org/combine.specifications/sed-ml", recognizer
                      .getFormatFromExtension("sedml").toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension(null));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension("stuff"));
   }


   /**
    * Test CellML guessing.
    */
   @Test
   public void testGuessCellML() {
      File f = new File("test/aguda_b_1999.cellml");
      String fn = f.getAbsolutePath();
      String correctFormat = "https://identifiers.org/combine.specifications/cellml";
      URI format = Formatizer.guessFormat(f);
      assertEquals("got wrong format for " + fn, correctFormat,
              format.toString());

      // test the recognizer
      CellMlRecognizer recognizer = new CellMlRecognizer();
      String mime = null;
      try {
         mime = Files.probeContentType(f.toPath());
      } catch (IOException e) {
         e.printStackTrace();
         fail("wasn't able to get mime...");
      }

      assertEquals("got wrong format for " + fn, correctFormat, recognizer
              .getFormatByParsing(f, mime).toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatByParsing(XML_FILE, mime));

      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime("something"));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(mime));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(null));

      assertEquals("got wrong format for " + fn, correctFormat, recognizer
              .getFormatFromExtension("cellml").toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension(null));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension("stuff"));
   }


   /**
    * Test SBGN guessing.
    */
   @Test
   public void testGuessSBGN() {
      File f = new File("test/guess-sbgn-AF-activity-nodes.sbgn");
      String fn = f.getAbsolutePath();
      String correctFormat = "https://identifiers.org/combine.specifications/sbgn";
      URI format = Formatizer.guessFormat(f);
      assertEquals("got wrong format for " + fn, correctFormat,
              format.toString());

      // test the recognizer
      SbgnRecognizer recognizer = new SbgnRecognizer();
      String mime = null;
      try {
         mime = Files.probeContentType(f.toPath());
      } catch (IOException e) {
         e.printStackTrace();
         fail("wasn't able to get mime...");
      }

      assertEquals("the same format for " + fn, correctFormat, recognizer.getFormatByParsing(f, mime).toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatByParsing(XML_FILE, mime));

      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime("something"));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(mime));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(null));

      assertEquals("got wrong format for " + fn, correctFormat, recognizer
              .getFormatFromExtension("sbgn").toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension(null));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension("stuff"));

      f = new File("test/guess-sbgn-ER-binary-no-outcome.sbgn");
      fn = f.getAbsolutePath();
      format = Formatizer.guessFormat(f);
      assertEquals("got wrong format for " + fn, correctFormat,
              format.toString());

      // test the recognizer
      try {
         mime = Files.probeContentType(f.toPath());
      } catch (IOException e) {
         e.printStackTrace();
         fail("wasn't able to get mime...");
      }

      assertEquals("got wrong format for " + fn, correctFormat, recognizer
              .getFormatByParsing(f, mime).toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatByParsing(XML_FILE, mime));

      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime("something"));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(mime));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(null));

      assertEquals("got wrong format for " + fn, correctFormat, recognizer
              .getFormatFromExtension("sbgn").toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension(null));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension("stuff"));

      f = new File("test/guess-sbgn-PD-clone-marker.sbgn");
      fn = f.getAbsolutePath();
      format = Formatizer.guessFormat(f);
      assertEquals("got wrong format for " + fn, correctFormat,
              format.toString());

      // test the recognizer
      try {
         mime = Files.probeContentType(f.toPath());
      } catch (IOException e) {
         e.printStackTrace();
         fail("wasn't able to get mime...");
      }

      assertEquals("got wrong format for " + fn, correctFormat, recognizer
              .getFormatByParsing(f, mime).toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatByParsing(XML_FILE, mime));

      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime("something"));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(mime));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromMime(null));

      assertEquals("got wrong format for " + fn, correctFormat, recognizer
              .getFormatFromExtension("sbgn").toString());
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension(null));
      assertNull("got wrong format for " + fn,
              recognizer.getFormatFromExtension("stuff"));
   }

   private Object[] params2TestGuessOtherFormats() {
      return new Object[]{
        new Object[]{
          "test/Files2TestMimeTypeDetection/Model_annotation_file.csv",
          "https://purl.org/NET/mediatypes/text/csv"
        },
        new Object[]{
          "test/Files2TestMimeTypeDetection/index.html",
          "https://purl.org/NET/mediatypes/text/html"
        },
        new Object[]{
          "test/AutoGeneratedFiles/BIOMD0000000053.m",
          "https://purl.org/NET/mediatypes/application/x-matlab"
        },
        new Object[]{
          "test/AutoGeneratedFiles/BIOMD0000000053.pdf",
          "https://purl.org/NET/mediatypes/application/pdf"
        },
        new Object[]{
          "test/AutoGeneratedFiles/BIOMD0000000053.png",
          "https://purl.org/NET/mediatypes/image/png"
        },
        new Object[]{
          "test/AutoGeneratedFiles/BIOMD0000000053.sci",
          "https://purl.org/NET/mediatypes/application/octet-stream"
        },
        new Object[]{
          "test/AutoGeneratedFiles/BIOMD0000000053.vcml",
          "https://purl.org/NET/mediatypes/application/xml"
        },
        new Object[]{
          "test/AutoGeneratedFiles/BIOMD0000000053.xpp",
          "https://purl.org/NET/mediatypes/text/plain"
        },
        new Object[]{
          "test/AutoGeneratedFiles/BIOMD0000000053-biopax2.owl",
          "https://identifiers.org/combine.specifications/biopax.level-2"
        },
        new Object[]{
          "test/AutoGeneratedFiles/BIOMD0000000053-biopax3.owl",
          "https://identifiers.org/combine.specifications/biopax.level-3"
        },
        new Object[]{
          "test/AutoGeneratedFiles/BIOMD0000000053_url.xml",
          "https://identifiers.org/combine.specifications/sbml.level-2.version-3"
        },
        new Object[]{
          "test/AutoGeneratedFiles/BIOMD0000000053_urn.xml",
          "https://identifiers.org/combine.specifications/sbml.level-2.version-3"
        },
        new Object[]{
          "test/AutoGeneratedFiles/Description_miniFROG_yeastGEM.xlsx",
          "https://purl.org/NET/mediatypes/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        },
        new Object[]{
          "test/AutoGeneratedFiles/FROG_Report_YeastGEM_rich_medium.zip",
          "https://purl.org/NET/mediatypes/application/zip"
        },new Object[]{
          "test/AutoGeneratedFiles/Yeast_GEM_rich.mat",
          "https://purl.org/NET/mediatypes/application/x-matlab"
        }
      };
   }

   /**
    * These test data are designed to test other automatically generated formats such as svg, vcml or xpp.
    */
   @Test
   @Parameters(method = "params2TestGuessOtherFormats")
   public void testGuessOtherFormats(final String filePath, final String expectedFormat) {
      File file = new File(filePath);
      String filename = file.getAbsolutePath();
      URI formatURI = Formatizer.guessFormat(file);
      assertEquals("got an expected format for " + filename, expectedFormat, formatURI.toString());
   }
}
