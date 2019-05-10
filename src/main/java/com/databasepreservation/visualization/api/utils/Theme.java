/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package com.databasepreservation.visualization.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import com.databasepreservation.visualization.shared.ViewerConstants;
import org.apache.commons.io.IOUtils;
import org.roda.core.data.exceptions.NotFoundException;
import org.roda.core.data.v2.common.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.databasepreservation.visualization.api.common.ConsumesOutputStream;
import com.databasepreservation.visualization.server.ViewerConfiguration;

public class Theme {

  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(Theme.class);

  private static final Date INITIAL_DATE = new Date();

  private Theme() {
    super();
  }

  public static Pair<String, InputStream> getThemeResource(String resourceId, String fallbackResourceId) {
    InputStream themeResourceInputstream = ViewerConfiguration.getThemeFileAsStream(resourceId);
    if (themeResourceInputstream == null) {
      themeResourceInputstream = ViewerConfiguration.getThemeFileAsStream(fallbackResourceId);
      resourceId = fallbackResourceId;
    }
    return new Pair<>(resourceId, themeResourceInputstream);
  }

  public static StreamResponse getThemeResourceStreamResponse(final Pair<String, InputStream> themeResourceInputstream)
    throws IOException, NotFoundException {
    StreamResponse streamResponse = null;

    String resourceId = themeResourceInputstream.getFirst();
    String mimeType;
    if (resourceId.endsWith(".html")) {
      mimeType = ViewerConstants.MEDIA_TYPE_TEXT_HTML;
    } else if (resourceId.endsWith(".css")) {
      mimeType = "text/css";
    } else if (resourceId.endsWith(".png")) {
      mimeType = "image/png";
    } else if (resourceId.endsWith(".js")) {
      mimeType = "text/javascript";
    } else {
      mimeType = ViewerConstants.MEDIA_TYPE_APPLICATION_OCTET_STREAM;
    }

    ConsumesOutputStream stream = new ConsumesOutputStream() {
      @Override
      public String getMediaType() {
        return mimeType;
      }

      @Override
      public String getFileName() {
        return resourceId;
      }

      @Override
      public void consumeOutputStream(OutputStream out) throws IOException {
        IOUtils.copy(themeResourceInputstream.getSecond(), out);
      }
    };

    streamResponse = new StreamResponse(resourceId, mimeType, stream);

    return streamResponse;
  }

  public static Date getLastModifiedDate(String resourceId) throws IOException {
    Date modifiedDate;
    URL file = ViewerConfiguration
      .getConfigurationFile(ViewerConstants.VIEWER_THEME_FOLDER + "/" + resourceId);

    if ("file".equalsIgnoreCase(file.getProtocol())) {
      try {
        Path filePath = Paths.get(file.toURI());
        modifiedDate = new Date(Files.getLastModifiedTime(filePath).toMillis());
      } catch (URISyntaxException e) {
        modifiedDate = INITIAL_DATE;
      }
    } else {
      modifiedDate = INITIAL_DATE;
    }

    return modifiedDate;
  }

}
