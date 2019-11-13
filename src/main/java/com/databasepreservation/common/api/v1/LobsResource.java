package com.databasepreservation.common.api.v1;

import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.roda.core.data.exceptions.NotFoundException;
import org.roda.core.data.exceptions.RODAException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.databasepreservation.common.api.utils.ApiUtils;
import com.databasepreservation.common.api.utils.DownloadUtils;
import com.databasepreservation.common.api.utils.StreamResponse;
import com.databasepreservation.common.server.ViewerFactory;
import com.databasepreservation.common.server.index.DatabaseRowsSolrManager;
import com.databasepreservation.common.shared.ViewerConstants;
import com.databasepreservation.common.shared.ViewerStructure.ViewerDatabase;
import com.databasepreservation.common.shared.ViewerStructure.ViewerRow;
import com.databasepreservation.common.utils.LobPathManager;
import com.databasepreservation.common.utils.UserUtility;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Resource used to export search results
 * 
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
@Service
@Path(LobsResource.ENDPOINT)
@Api(value = LobsResource.SWAGGER_ENDPOINT)
public class LobsResource {
  public static final String ENDPOINT = "/" + ViewerConstants.API_SERVLET + ViewerConstants.API_V1_LOBS_RESOURCE;
  public static final String SWAGGER_ENDPOINT = "v1 lobs";

  private static Logger LOGGER = LoggerFactory.getLogger(LobsResource.class);

  @Context
  private HttpServletRequest request;

  @GET
  @Path("/{" + ViewerConstants.API_PATH_PARAM_DATABASE_UUID + "}/{" + ViewerConstants.API_PATH_PARAM_TABLE_UUID
    + "}/{" + ViewerConstants.API_PATH_PARAM_ROW_UUID + "}/{" + ViewerConstants.API_PATH_PARAM_COLUMN_ID + "}/{"
    + ViewerConstants.API_PATH_PARAM_LOB_FILENAME + "}")
  @Produces({MediaType.APPLICATION_OCTET_STREAM})
  @ApiOperation(value = "Download LOB", notes = "download the specified LOB.", response = String.class, responseContainer = "LOB")
  public Response getLOB(@PathParam(ViewerConstants.API_PATH_PARAM_DATABASE_UUID) String databaseUUID,
    @PathParam(ViewerConstants.API_PATH_PARAM_TABLE_UUID) String tableUUID,
    @PathParam(ViewerConstants.API_PATH_PARAM_ROW_UUID) String rowUUID,
    @PathParam(ViewerConstants.API_PATH_PARAM_COLUMN_ID) Integer columnID,
    @PathParam(ViewerConstants.API_PATH_PARAM_LOB_FILENAME) String filename) throws RODAException {
    DatabaseRowsSolrManager solrManager = ViewerFactory.getSolrManager();

    UserUtility.Authorization.checkDatabaseAccessPermission(this.request, databaseUUID);

    ViewerRow row = solrManager.retrieveRows(databaseUUID, rowUUID);

    if (row != null) {
      try {
        return ApiUtils.okResponse(new StreamResponse(filename, MediaType.APPLICATION_OCTET_STREAM,
          DownloadUtils.stream(Files.newInputStream(LobPathManager.getPath(ViewerFactory.getViewerConfiguration(),
            databaseUUID, tableUUID, columnID, rowUUID)))));
      } catch (IOException e) {
        throw new RODAException("There was an IO problem retrieving the LOB.", e);
      }
    } else {
      throw new NotFoundException("LOB not found.");
    }
  }
}