package com.databasepreservation.common.api.v1;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.roda.core.data.exceptions.GenericException;
import org.roda.core.data.exceptions.NotFoundException;
import org.roda.core.data.exceptions.RequestNotValidException;
import org.roda.core.data.utils.JsonUtils;
import org.roda.core.data.v2.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.databasepreservation.common.client.ViewerConstants;
import com.databasepreservation.common.client.exceptions.RESTException;
import com.databasepreservation.common.client.index.FindRequest;
import com.databasepreservation.common.client.index.IndexResult;
import com.databasepreservation.common.client.models.activity.logs.ActivityLogEntry;
import com.databasepreservation.common.client.models.activity.logs.ActivityLogWrapper;
import com.databasepreservation.common.client.models.activity.logs.LogEntryState;
import com.databasepreservation.common.client.services.ActivityLogService;
import com.databasepreservation.common.server.ViewerFactory;
import com.databasepreservation.common.server.activity.log.strategies.ActivityLogStrategy;
import com.databasepreservation.common.utils.ControllerAssistant;
import com.databasepreservation.common.utils.I18nUtility;
import com.databasepreservation.common.utils.UserUtility;

/**
 * @author Miguel Guimarães <mguimaraes@keep.pt>
 */
@Service
@Path(ViewerConstants.ENDPOINT_ACTIVITY_LOG)
public class ActivityLogResource implements ActivityLogService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ActivityLogResource.class);

  @Context
  private HttpServletRequest request;

  @Override
  public IndexResult<ActivityLogEntry> find(FindRequest findRequest, String locale) {
    ControllerAssistant controllerAssistant = new ControllerAssistant() {};
    User user = UserUtility.getUser(request);
    LogEntryState state = LogEntryState.SUCCESS;

    controllerAssistant.checkRoles(user);

    try {
      final IndexResult<ActivityLogEntry> result = ViewerFactory.getSolrManager().find(ActivityLogEntry.class,
        findRequest.filter, findRequest.sorter, findRequest.sublist, findRequest.facets);
      return I18nUtility.translate(result, ActivityLogEntry.class, locale);
    } catch (GenericException | RequestNotValidException e) {
      state = LogEntryState.FAILURE;
      throw new RESTException(e);
    } finally {
      // register action
      controllerAssistant.registerAction(user, state, ViewerConstants.CONTROLLER_FILTER_PARAM,
        JsonUtils.getJsonFromObject(findRequest.filter), ViewerConstants.CONTROLLER_FACET_PARAM,
        JsonUtils.getJsonFromObject(findRequest.facets));
    }
  }

  @Override
  public ActivityLogWrapper retrieve(String logUUID) {
    ControllerAssistant controllerAssistant = new ControllerAssistant() {};
    User user = UserUtility.getUser(request);
    LogEntryState state = LogEntryState.SUCCESS;

    controllerAssistant.checkRoles(user);

    try {
      final ActivityLogEntry retrieve = ViewerFactory.getSolrManager().retrieve(ActivityLogEntry.class, logUUID);
      final ActivityLogStrategy strategy = ViewerFactory.getActivityLogStrategyFactory()
        .getStrategy(retrieve.getActionComponent(), retrieve.getActionMethod());
      return strategy.apply(new ActivityLogWrapper(retrieve));
    } catch (GenericException | NotFoundException e) {
      state = LogEntryState.FAILURE;
      throw new RESTException(e);
    } finally {
      // register action
      controllerAssistant.registerAction(user, state, ViewerConstants.CONTROLLER_LOG_ID_PARAM, logUUID);
    }
  }
}
