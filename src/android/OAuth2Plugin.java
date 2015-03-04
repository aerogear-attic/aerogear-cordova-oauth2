/*
 * JBoss, Home of Professional Open Source.
 * Copyright Red Hat, Inc., and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.cordova.oauth2;

import android.content.Intent;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.authorization.AuthzModule;
import org.jboss.aerogear.android.authorization.AuthorizationManager;
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationConfiguration;
import org.jboss.aerogear.cordova.android.reflect.BasePlugin;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * @author edewit@redhat.com
 */
public class OAuth2Plugin extends BasePlugin {
  private static final String TAG = OAuth2Plugin.class.getSimpleName();
  private OauthGoogleServicesIntentHelper intentHelper;

  public boolean add(JSONObject data, CallbackContext callbackContext) throws JSONException, MalformedURLException {
    Log.d(TAG, "add account");

    final OAuth2AuthorizationConfiguration configuration = AuthorizationManager.config(data.getString("accountId"), OAuth2AuthorizationConfiguration.class)
        .setBaseURL(new URL(data.getString("base")))
        .setAccountId(data.getString("accountId"))
        .setAccessTokenEndpoint(data.getString("accessTokenEndpoint"))
        .setAuthzEndpoint(data.getString("authzEndpoint"))
        .setClientId(data.getString("clientId"))
        .setRefreshEndpoint(data.getString("refreshTokenEndpoint"))
        .setRedirectURL(data.getString("redirectURL"));

    if (data.has("clientSecret")) {
      configuration.setClientSecret(data.getString("clientSecret"));

    }

    if (data.has("scopes")) {
      configuration.setScopes(Arrays.asList(data.getString("scopes").split(",")));
    }

    configuration.asModule();
    callbackContext.success();
    return true;
  }

  public boolean addGoogle(JSONObject data, CallbackContext callbackContext) throws JSONException, MalformedURLException {
    Log.d(TAG, "add google account");

    data.put("base", "https://accounts.google.com");
    data.put("authzEndpoint", "o/oauth2/auth");
    data.put("redirectURL", "http://localhost");
    data.put("accessTokenEndpoint", "o/oauth2/token");
    data.put("refreshTokenEndpoint", "o/oauth2/token");

    return add(data, callbackContext);
  }

  public boolean addKeycloak(JSONObject data, CallbackContext callbackContext) throws JSONException, MalformedURLException {
    Log.d(TAG, "add keycload account");

    String realm = data.getString("realm");
    data.put("authzEndpoint", String.format("realms/%s/tokens/login", realm));
    data.put("accessTokenEndpoint", String.format("realms/%s/tokens/access/codes", realm));
    data.put("redirectURL", "http://oauth2callback");
    data.put("refreshTokenEndpoint", String.format("realms/%s/tokens/refresh", realm));

    return add(data, callbackContext);
  }

  public boolean addFacebook(JSONObject data, CallbackContext callbackContext) throws JSONException, MalformedURLException {
    Log.d(TAG, "add facebook account");

    data.put("base", "https://");
    data.put("authzEndpoint", "www.facebook.com/dialog/oauth");
    data.put("accessTokenEndpoint", "graph.facebook.com/oauth/access_token");
    data.put("redirectURL", "https://localhost/");
    data.put("refreshTokenEndpoint", "graph.facebook.com/oauth/access_token");

    return add(data, callbackContext);
  }

  public boolean requestAccess(String name, final CallbackContext callbackContext) {
    Log.d(TAG, "requesting access");

    final AuthzModule module = AuthorizationManager.getModule(name);
    module.requestAccess(cordova.getActivity(), new Callback<String>() {
      @Override
      public void onSuccess(String o) {
        callbackContext.success(o);
      }

      @Override
      public void onFailure(Exception e) {
        callbackContext.error(e.getMessage());
      }
    });
    return true;
  }

  public boolean requestAccessUsingPlayServices(JSONObject data, CallbackContext callbackContext) throws JSONException {
    cordova.setActivityResultCallback(this);
    if (OauthGoogleServicesIntentHelper.available) {
      intentHelper = new OauthGoogleServicesIntentHelper(cordova, callbackContext);
      intentHelper.triggerIntent(data);
    } else {
      callbackContext.error("Google Play Services is not available");
    }
    return true;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, final Intent data) {
    if (intentHelper != null) {
      intentHelper.onActivityResult(requestCode, resultCode, data);
    }
    super.onActivityResult(requestCode, resultCode, data);
  }
}
