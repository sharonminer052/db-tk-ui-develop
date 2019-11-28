package com.databasepreservation.common.client.services;

import com.databasepreservation.common.client.ViewerConstants;
import com.databasepreservation.common.client.common.DefaultMethodCallback;
import com.google.gwt.core.client.GWT;
import io.swagger.annotations.ApiOperation;
import org.fusesource.restygwt.client.DirectRestService;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.function.Consumer;

/**
 * @author Miguel Guimarães <mguimaraes@keep.pt>
 */
@Path(".." + ViewerConstants.ENDPOINT_CONTEXT)
public interface ContextService extends DirectRestService {
  class Util {
    /**
     * @return the singleton instance
     */
    public static ContextService get() {
      return GWT.create(ContextService.class);
    }

    public static <T> ContextService call(MethodCallback<T> callback) {
      return REST.withCallback(callback).call(get());
    }

    public static <T> ContextService call(Consumer<T> callback) {
      return REST.withCallback(DefaultMethodCallback.get(callback)).call(get());
    }
  }

  @GET
  @Path("/environment")
  @Produces({MediaType.TEXT_PLAIN})
  @ApiOperation(value = "retrieves the environment", notes = "", response = String.class, responseContainer = "Environment")
  String getEnvironment();

  @GET
  @Path("/clientMachineHost")
  @Produces({MediaType.TEXT_PLAIN})
  @ApiOperation(value = "retrieves the client machine host", notes = "", response = String.class, responseContainer = "Client machine")
  String getClientMachine();
}
