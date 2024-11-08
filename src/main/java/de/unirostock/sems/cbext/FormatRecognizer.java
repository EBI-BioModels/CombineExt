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
package de.unirostock.sems.cbext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * Instances of the class FormatRecognizer are unable to recognise the format of
 * files.
 * <p>
 * They determine the formats by either
 * <ul>
 * <li>parsing the files: {@link #getFormatByParsing (java.io.File,
 * java.lang.String)}</li>
 * <li>understanding a MIME-type: {@link #getFormatFromMime
 * (java.lang.String)}</li>
 * <li>recognising a file extension:
 * {@link #getFormatFromExtension (java.lang.String)}</li>
 * </ul>
 * <p>
 * If one of the methods fail to determine the format they must return
 * <code>null</code>.
 * <p>
 * The priority determines the order we used to ask different recognizers.
 * Default recognizers have a priority of 100.
 * The first recognizer, which returns a valid format, determines the format to
 * be used.
 * If you ever need to change the priority of different recognizers you should
 * resort the recognizers
 * in the Formatizer class by calling {@link Formatizer#resortRecognizers()}.
 *
 * @author Martin Scharm
 */
public abstract class FormatRecognizer {
   private static Logger LOGGER = LoggerFactory.getLogger(FormatRecognizer.class);
   /**
    * identifiers.org base uri.
    */
   public static final String IDENTIFIERS_BASE = "https://identifiers.org/combine.specifications/";
   /**
    * purl.org base uri.
    */
   protected static final String PURL_BASE = "https://purl.org/NET/mediatypes/";

   /**
    * Builds a URI as `start+end` without caring about an exception. Only use if
    * you're sure it's not going to fail. If we cannot produce this URI, we're
    * returning a default.
    *
    * @param pre        the start
    * @param post       the end
    * @param defaultUri the default URI
    * @return the URI as pre+post
    */
   public static URI buildUri(String pre, String post, URI defaultUri) {
      try {
         return new URI(pre + post);
      } catch (URISyntaxException e) {
          LOGGER.error("wasn't able to create URI {}/{}", pre, post);
      }
      return defaultUri;
   }

   /**
    * <p>
    * Builds a URI as <code>start+end</code> without caring about an exception. Only use if
    * you're sure it's not going to fail. If we cannot produce this URI, we're
    * returning <code>null</code>.</p>
    *
    * @param pre  the start
    * @param post the end
    * @return the URI as start+end
    */
   public static URI buildUri(String pre, String post) {
      return buildUri(pre, post, null);
   }

   /**
    * Returns the priority of this format recognizer.
    * <p>
    * The higher the priority, the earlier this recognizer gets called.
    * The first recognizer, which is able to identify a file, determines it's
    * format.
    * Priority should not be negative. Default recognizers have a priority around
    * 100.
    *
    * @return an integer &gt; 0
    */
   public abstract int getPriority();

   /**
    * Parses the given file and tries to determine the format, such as purl.org
    * or identifiers.org URI.
    * <p>
    * If the recognizer is unable to understand the file it must return
    * <code>null</code>.
    *
    * @param file     Path to the file
    * @param mimeType MIME type for quick evaluation.
    * @return A format URI or null.
    */
   public abstract URI getFormatByParsing(File file, String mimeType);

   /**
    * Tries to map the given mime type to a format.
    * <p>
    * If the recognizer is unable to understand the mime type it must return
    * <code>null</code>.
    *
    * @param mime MIME type
    * @return A format URI or null
    */
   public abstract URI getFormatFromMime(String mime);

   /**
    * Tries to map the given file extension to a format.
    * <p>
    * If the recognizer is unable to understand the extension it must return
    * <code>null</code>.
    *
    * @param extension file extension
    * @return A format URI or null
    */
   public abstract URI getFormatFromExtension(String extension);

}
