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

import de.unirostock.sems.cbext.FormatRecognizer;
import de.unirostock.sems.cbext.Formatizer;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;


/**
 * The Class SbmlFormatizer to recognize sbml files.
 */
public class SbmlRecognizer
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
      URI result = null;
      try {
         String[] levelVersion = getSbmlLevelAndVersion(file.getAbsolutePath());
         if (levelVersion.length == 2 && levelVersion[0] != null && levelVersion[1] != null) {
            result = buildUri(IDENTIFIERS_BASE, "sbml.level-" + levelVersion[0] + ".version-" + levelVersion[1]);
         }
      } catch (Exception e) {
         //LOGGER.info(e, "file ", file, " seems to be a valid SBML document.");
         System.out.println("file " + file.getName() + " seems to be an invalid SBML document.");
      }

      return result;
   }


   @Override
   public URI getFormatFromMime(String mime) {
      // we cannot decide from just a mime type
      return null;
   }


   @Override
   public URI getFormatFromExtension(String extension) {
      if (extension != null && extension.equals("sbml"))
         return buildUri(IDENTIFIERS_BASE, "sbml");
      return null;
   }

   public String[] getSbmlLevelAndVersion(String sbmlFilePath) throws IOException, XMLStreamException {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = xmlInputFactory.createXMLEventReader(Files.newInputStream(Paths.get(sbmlFilePath)));
        String[] strLevelVersion = new String[2];
        while (reader.hasNext()) {
            XMLEvent nextEvent = reader.nextEvent();
            if (nextEvent.isStartElement()) {
                StartElement startElement = nextEvent.asStartElement();
                if (startElement.getName().getLocalPart() == "sbml") {
                    Iterator<Attribute> itAttrs = startElement.getAttributes();
                    String level = "";
                    String version = "";
                    while (itAttrs.hasNext()) {
                        Attribute attr = itAttrs.next();
                        if (attr.getName().getLocalPart() == "level") {
                            level = attr.getValue();
                        }
                        if (attr.getName().getLocalPart() == "version") {
                            version = attr.getValue();
                        }
                        if (level != null && version != null) {
                            strLevelVersion[0] = level;
                            strLevelVersion[1] = version;
                        }
                    }
                    break;
                }
            }
        }
        return strLevelVersion;
    }
}
