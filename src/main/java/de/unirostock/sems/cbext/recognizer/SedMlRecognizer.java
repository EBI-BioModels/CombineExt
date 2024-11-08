/**
 * Copyright © 2014-2015:
 * - Martin Peters <martin@freakybytes.net>
 * - Martin Scharm <martin@binfalse.de>
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
package de.unirostock.sems.cbext.recognizer;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.cbext.FormatRecognizer;
import de.unirostock.sems.cbext.Formatizer;
import org.jlibsedml.Libsedml;
import org.jlibsedml.SEDMLDocument;
import org.jlibsedml.SedMLError;
import org.jlibsedml.XMLException;

import java.io.File;
import java.io.IOException;
import java.net.URI;


/**
 * The Class SedMlFormatizer to recognize sed-ml files.
 */
public class SedMlRecognizer
        extends FormatRecognizer {

   /** priority for this format recognizer */
   protected static int priority = 100;

   /**
    * Sets the priority of this format recognizer and triggers a resort of all
    * format recognizers.
    * <p>
    * The higher the priority, the earlier this recognizer gets called.
    * The first recognizer, which is able to identify a file, determines its
    * format.
    * Setting a negative priority will be ignored.
    * Default recognizers have a priority of 100.
    *
    * @param newPriority the new priority of this recogniser
    */
   public static void setPriority(int newPriority) {

      // no negative priorities!
      if (priority < 0)
         return;

      priority = newPriority;
      Formatizer.resortRecognizers();
   }

   /* (non-Javadoc)
    * @see de.unirostock.sems.cbext.FormatRecognizer#getPriority()
    */
   @Override
   public int getPriority() {
      return priority;
   }

   /*
    * (non-Javadoc)
    *
    * @see de.unirostock.sems.cbext.FormatParser#checkFormat(java.io.File,
    * java.lang.String)
    */
   @Override
   public URI getFormatByParsing(File file, String mimeType) {

      try {
         SEDMLDocument doc = Libsedml.readDocument(file);
         doc.validate();
         if (doc.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (SedMLError e : doc.getErrors())
               if (e.getSeverity().compareTo(SedMLError.ERROR_SEVERITY.ERROR) >= 0)
                  errors.append("[").append(e.getMessage()).append("]");
            if (errors.length() > 0)
               throw new IOException("error reading sedml file: "
                       + errors);
         }
         org.jlibsedml.Version v = doc.getVersion();
         return buildUri(IDENTIFIERS_BASE, "sed-ml.level-" + v.getLevel()
                 + ".version-" + v.getVersion());
      } catch (IOException | XMLException | IllegalArgumentException | NullPointerException e) {
         LOGGER.info(e, "file ", file, " seems to be no sedml file..");
      }

      // no format could be guessed
      return null;
   }


   /*
    * (non-Javadoc)
    *
    * @see
    * de.unirostock.sems.cbext.FormatRecognizer#getFormatFromMime(java.lang.String
    * )
    */
   @Override
   public URI getFormatFromMime(String mime) {
      // we cannot decide from just a mime type
      return null;
   }


   /*
    * (non-Javadoc)
    *
    * @see
    * de.unirostock.sems.cbext.FormatRecognizer#getFormatFromExtension(java.lang
    * .String)
    */
   @Override
   public URI getFormatFromExtension(String extension) {
      if (extension != null && extension.equals("sedml"))
         return buildUri(IDENTIFIERS_BASE, "sed-ml");
      return null;
   }

}
