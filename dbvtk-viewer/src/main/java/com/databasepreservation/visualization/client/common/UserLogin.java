/**
 * The contents of this file are based on those found at https://github.com/keeps/roda
 * and are subject to the license and copyright detailed in https://github.com/keeps/roda
 */
package com.databasepreservation.visualization.client.common;

import java.util.List;
import java.util.Vector;

import org.roda.core.data.v2.user.User;

import com.databasepreservation.visualization.client.BrowserService;
import com.databasepreservation.visualization.client.BrowserServiceAsync;
import com.databasepreservation.visualization.shared.client.CachedAsynRequest;
import com.databasepreservation.visualization.shared.client.ClientLogger;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public class UserLogin {
  private static final ClientLogger logger = new ClientLogger(UserLogin.class.getName());

  private static BrowserServiceAsync browserService = BrowserService.Util.getInstance();

  private static UserLogin instance = null;

  private final List<LoginStatusListener> listeners;

  /**
   * @return the singleton instance
   */
  public static UserLogin getInstance() {
    if (instance == null) {
      instance = new UserLogin();
    }
    return instance;
  }

  private UserLogin() {
    listeners = new Vector<>();
  }

  private final CachedAsynRequest<User> getUserRequest = new CachedAsynRequest<User>() {
    @Override
    public void getFromServer(AsyncCallback<User> callback) {
      browserService.getAuthenticatedUser(callback);
    }
  };

  /**
   * Get current authenticated user. User is cached and only refreshed when
   * login or logout actions are called.
   *
   * @param callback
   *          call back handler that receives error if failed or AuthOfficeUser
   *          if success.
   */
  public void getAuthenticatedUser(final AsyncCallback<User> callback) {
    getUserRequest.request(callback);
  }

  /**
   * Login into DBVTK
   */
  public void login() {
    String currentURL = Window.Location.getHref().replaceAll("#", "%23");
    String hash = Window.Location.getHash();
    if (hash.length() > 0) {
      hash = hash.substring(1);
      hash = UriUtils.encode(hash);
    }
    String locale = LocaleInfo.getCurrentLocale().getLocaleName();
    Window.open("/login?service=" + currentURL + "&hash=" + hash + "&locale=" + locale, "_self", "");
  }

  public void login(String username, String password, final AsyncCallback<User> callback) {
    GWT.log("logging in with user " + username);
    browserService.login(username, password, new AsyncCallback<User>() {

      @Override
      public void onFailure(Throwable caught) {
        callback.onFailure(caught);
      }

      @Override
      public void onSuccess(User newUser) {
        getUserRequest.setCached(newUser);
        onLoginStatusChanged(newUser);
        callback.onSuccess(newUser);
      }
    });
  }

  public void logout() {
    String currentURL = Window.Location.getHref().replaceAll("#", "%23");
    String locale = LocaleInfo.getCurrentLocale().getLocaleName();
    Window.open("/logout?service=" + currentURL + "&locale=" + locale, "_self", "");
    getUserRequest.clearCache();
  }

  /**
   * Add a login status listener
   *
   * @param listener
   */
  public void addLoginStatusListener(LoginStatusListener listener) {
    listeners.add(listener);
  }

  /**
   * Remove a login status listener
   *
   * @param listener
   */
  public void removeLoginStatusListener(LoginStatusListener listener) {
    listeners.remove(listener);
  }

  public void onLoginStatusChanged(User newUser) {
    for (LoginStatusListener listener : listeners) {
      listener.onLoginStatusChanged(newUser);
    }
  }
}
