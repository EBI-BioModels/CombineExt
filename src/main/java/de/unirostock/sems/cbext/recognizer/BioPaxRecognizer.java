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
import org.biopax.paxtools.io.BioPAXIOHandler;
import org.biopax.paxtools.model.BioPAXLevel;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.util.BioPaxIOException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;


/**
 * The Class BioPaxFormatizer to recognize BioPax files.
 */
public class BioPaxRecognizer
        extends FormatRecognizer {

   /** priority for this format recognizer */
   protected static int priority = 100;

   /**
    * Sets the priority of this format recognizer and triggers a resort of all
    * format recognizers.
    *
    * The higher the priority, the earlier this recognizer gets called.
    * The first recognizer, which is able to identify a file, determines it's
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
         BioPAXIOHandler handler = new org.biopax.paxtools.io.SimpleIOHandler(); // auto-detects
         // Level
         Model model = handler.convertFromOWL(Files.newInputStream(file.toPath()));
         BioPAXLevel bioPAXLevel = model.getLevel();
         String level;
         String d = bioPAXLevel.name().substring(1);
         level = ".level-".concat(d);
         return buildUri(IDENTIFIERS_BASE, "biopax" + level);
      } catch (IOException | BioPaxIOException e) {
         LOGGER.info(e, "file ", file, " seems not to be a valid BioPAX document.");
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
      if (extension != null && extension.equals("biopax"))
         return buildUri(IDENTIFIERS_BASE, "biopax");
      return null;
   }

}
