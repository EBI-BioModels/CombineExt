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
import de.unirostock.sems.cbext.recognizer.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * The Class Formatizer to generate format URIs for certain files.
 *
 * This class hosts a bunch of format recognizers (see {@link FormatRecognizer})
 * which are able to recognize files and provide format URIs.
 * By default, we are able to recognize SED-ML, BioPax, CellML, SBML, and SBOL.
 * You can extend the default list by passing further FormatRecognizers to
 * {@link #addFormatRecognizer (de.unirostock.sems.cbext.FormatRecognizer)}.
 *
 * To retrieve the format of a certain file you might
 * <ul>
 * <li>pass the file to {@link #guessFormat (java.io.File)}</li>
 * <li>pass its mime type to {@link #getFormatFromMime (java.lang.String)}</li>
 * <li>pass its extension to {@link #getFormatFromExtension (java.lang.String)}</li>
 * </ul>
 * The result will be a link to, e.g., purl.org or identifiers.org.
 *
 * @author Martin Scharm
 */
public class Formatizer {

   /** list of registered format recognizers. */
   private static final List<FormatRecognizer> recognizerList = new ArrayList<>();
   public static final String PURL_ORG_PREFIX = "https://purl.org/NET/mediatypes/";

   static {
      String defaultUri = Formatizer.PURL_ORG_PREFIX + "application/x.unknown";
      try {
         GENERIC_UNKNOWN = new URI(defaultUri);
      } catch (URISyntaxException e) {
         e.printStackTrace();
         LOGGER.error(e, "error generating generic default uri: ", defaultUri);
      }

      // add default recognizers
      addDefaultRecognizers();
   }

   /** The generic unknown format URI. */
   public static URI GENERIC_UNKNOWN;


   /**
    * Adds another recognizer to the formatizer.
    *
    * @param recognizer
    *          the recognizer that considers more formats
    */
   public static void addFormatRecognizer(FormatRecognizer recognizer) {
      if (recognizer == null)
         throw new IllegalArgumentException(
                 "The formatizer is not allowed to be null.");

      recognizerList.add(recognizer);
      resortRecognizers();
   }


   /**
    * Add all default recognizers to the list of recognizers.
    *
    * Currently, we have recognizers for SED-ML, BioPax, CellML, SBGN, SBML,
    * SBOL, as well as a default recognizer.
    */
   public static void addDefaultRecognizers() {
      recognizerList.add(new SedMlRecognizer());
      recognizerList.add(new BioPaxRecognizer());
      recognizerList.add(new CellMlRecognizer());
      recognizerList.add(new SbgnRecognizer());
      recognizerList.add(new SbmlRecognizer());
      recognizerList.add(new SbolRecognizer());
      recognizerList.add(new DefaultRecognizer());
      resortRecognizers();
   }


   /**
    * Remove all recognizers that we know so far.
    */
   public static void removeRecognizers() {
      recognizerList.clear();
   }


   /**
    * Resort known format recognizers.
    *
    * Must be called if the priorities of recognizers are modified.
    */
   public static void resortRecognizers() {
      Collections.sort(recognizerList, new RecognizerComparator());
   }


   /**
    * Guess format given a file.
    *
    * @param file
    *          the file
    * @return the format
    */
   public static URI guessFormat(File file) {
      if (file == null || !file.isFile())
         return null;

      String mime;
      try {
         URLConnection connection = file.toURI().toURL().openConnection();
         mime = connection.getContentType();
      } catch (IOException e) {
         LOGGER.warn(e, "could not get mime from file " + file);
         return null;
      }

      URI format = null;
      for (FormatRecognizer recognizer : recognizerList) {
         format = recognizer.getFormatByParsing(file, mime);
         if (format != null)
            break;
      }

      if (format != null) {
         // found a format
         return format;
      } else {
         // ok, parsing failed. let's still try file extensions.
         String name = file.getName();
         int dot = name.lastIndexOf(".");
         if (dot > 0) {
            String ext = name.substring(dot + 1);
            if (ext.equals("sbml") || ext.equals("sedml")
                    || ext.equals("sed-ml") || ext.equals("sbgn")
                    || ext.equals("omex") || ext.equals("cellml")
                    || ext.equals("biopax") || ext.equals("xml")) {
               format = getFormatFromExtension(ext);
            }
         }
         if (null == format || format.equals(GENERIC_UNKNOWN)) {
            // guessing via the file extension still failed, try to map mime-type
            format = getFormatFromMime(mime);
         }
         return format;
      }
   }


   /**
    * Gets the format given a mime type.
    *
    * @param mime
    *          the mime type
    * @return the format
    */
   public static URI getFormatFromMime(String mime) {
      if (mime == null)
         return GENERIC_UNKNOWN;

      URI format = null;
      for (FormatRecognizer recognizer : recognizerList) {
         if ((format = recognizer.getFormatFromMime(mime)) != null)
            break;
      }

      if (format != null)
         return format;
      else if ("content/unknown" != mime) {
         return FormatRecognizer.buildUri(PURL_ORG_PREFIX, mime, GENERIC_UNKNOWN);
      } else {
         return GENERIC_UNKNOWN;
      }
   }


   /**
    * Gets the format given a file extension.
    *
    * @param extension
    *          the file extension
    * @return the format
    */
   public static URI getFormatFromExtension(String extension) {
      if (extension == null)
         return GENERIC_UNKNOWN;

      URI format = null;
      for (FormatRecognizer recognizer : recognizerList) {
         if ((format = recognizer.getFormatFromExtension(extension)) != null)
            break;
      }

      if (format != null)
         return format;
      else
         return GENERIC_UNKNOWN;
   }

   /**
    * Comparator for list of Recognizers.
    */
   private static class RecognizerComparator
           implements Comparator<FormatRecognizer> {

      /*
       * (non-Javadoc)
       *
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */
      @Override
      public int compare(FormatRecognizer o1, FormatRecognizer o2) {
         return o2.getPriority() - o1.getPriority();
      }

   }
}
