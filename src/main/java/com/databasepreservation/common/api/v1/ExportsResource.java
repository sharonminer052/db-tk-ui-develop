package com.databasepreservation.common.api.v1;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.roda.core.data.exceptions.RODAException;
import org.roda.core.data.utils.JsonUtils;
import org.roda.core.data.v2.index.IndexResult;
import org.roda.core.data.v2.index.filter.Filter;
import org.roda.core.data.v2.index.sort.Sorter;
import org.roda.core.data.v2.index.sublist.Sublist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.databasepreservation.common.api.utils.ApiUtils;
import com.databasepreservation.common.api.utils.StreamResponse;
import com.databasepreservation.common.api.utils.ViewerStreamingOutput;
import com.databasepreservation.common.api.v1.utils.IterableIndexResultsCSVOutputStream;
import com.databasepreservation.common.api.v1.utils.ResultsCSVOutputStream;
import com.databasepreservation.common.api.v1.utils.ZipOutputStream;
import com.databasepreservation.common.server.ViewerFactory;
import com.databasepreservation.common.server.index.DatabaseRowsSolrManager;
import com.databasepreservation.common.server.index.utils.IterableIndexResult;
import com.databasepreservation.common.shared.ViewerConstants;
import com.databasepreservation.common.shared.ViewerStructure.ViewerDatabase;
import com.databasepreservation.common.shared.ViewerStructure.ViewerRow;
import com.databasepreservation.common.shared.ViewerStructure.ViewerTable;
import com.databasepreservation.common.utils.UserUtility;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Resource used to export search results
 * 
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
@Service
@Path(ExportsResource.ENDPOINT)
@Api(value = ExportsResource.SWAGGER_ENDPOINT)
public class ExportsResource {
  public static final String ENDPOINT = "/" + ViewerConstants.API_SERVLET + ViewerConstants.API_V1_EXPORT_RESOURCE;
  public static final String SWAGGER_ENDPOINT = "v1 exports";

  private static Logger LOGGER = LoggerFactory.getLogger(ExportsResource.class);

  @Context
  private HttpServletRequest request;

  @GET
  @Path("/csv/{" + ViewerConstants.API_PATH_PARAM_DATABASE_UUID + "}")
  @Produces({MediaType.APPLICATION_OCTET_STREAM})
  @ApiOperation(value = "Export as CSV", notes = "Export query results as CSV.", response = String.class, responseContainer = "CSVExport")
  public Response getCSVResultsPost(@PathParam(ViewerConstants.API_PATH_PARAM_DATABASE_UUID) String databaseUUID,
    @QueryParam(ViewerConstants.API_QUERY_PARAM_FILTER) String filterParam,
    @QueryParam(ViewerConstants.API_QUERY_PARAM_FIELDS) String fieldsListParam,
    @QueryParam(ViewerConstants.API_QUERY_PARAM_SORTER) String sorterParam,
    @QueryParam(ViewerConstants.API_QUERY_PARAM_SUBLIST) String subListParam,
    @QueryParam(ViewerConstants.API_PATH_PARAM_FILENAME) String filename,
    @QueryParam(ViewerConstants.API_PATH_PARAM_ZIP_FILENAME) String zipFilename,
    @QueryParam(ViewerConstants.API_PATH_PARAM_EXPORT_DESCRIPTION) Boolean exportDescriptions,
    @QueryParam(ViewerConstants.API_PATH_PARAM_EXPORT_LOBS) Boolean exportLOBs,
    @QueryParam(ViewerConstants.API_PATH_PARAM_TABLE_UUID) String tableUUID) throws RODAException {
    DatabaseRowsSolrManager solrManager = ViewerFactory.getSolrManager();

    Filter filter = JsonUtils.getObjectFromJson(filterParam, Filter.class);
    List<String> fields = JsonUtils.getListFromJson(fieldsListParam, String.class);
    Sorter sorter = JsonUtils.getObjectFromJson(sorterParam, Sorter.class);
    Sublist sublist = null;
    if (StringUtils.isNotBlank(subListParam)) {
      sublist = JsonUtils.getObjectFromJson(subListParam, Sublist.class);
    }

    UserUtility.Authorization.checkDatabaseAccessPermission(this.request, databaseUUID);

    final ViewerDatabase database = solrManager.retrieve(ViewerDatabase.class, databaseUUID);
    final ViewerTable table = database.getMetadata().getTable(tableUUID);

    if (Boolean.FALSE.equals(exportLOBs) && zipFilename == null) {
      if (sublist == null) {
        final IterableIndexResult allRows = solrManager.findAllRows(databaseUUID, filter, sorter, fields);
        return ApiUtils.okResponse(new ViewerStreamingOutput(
          new IterableIndexResultsCSVOutputStream(allRows, table, fields, filename, exportDescriptions, ','))
            .toStreamResponse());
      } else {
        final IndexResult<ViewerRow> rows = solrManager.findRows(databaseUUID, filter, sorter, sublist, null, fields);

        return ApiUtils.okResponse(
          new ViewerStreamingOutput(new ResultsCSVOutputStream(rows, table, fields, filename, exportDescriptions, ','))
            .toStreamResponse());
      }
    } else {
      fields.add("uuid");
      final IterableIndexResult allRows = solrManager.findAllRows(databaseUUID, filter, sorter, fields);
      final IterableIndexResult clone = solrManager.findAllRows(databaseUUID, filter, sorter, fields);
      fields.remove("uuid");
      return ApiUtils.okResponse(new StreamResponse(
        new ZipOutputStream(databaseUUID, table, allRows, clone, zipFilename, filename, fields, sublist)));
    }
  }
}
