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

import android.util.Log;
import android.util.Pair;
import org.apache.cordova.CallbackContext;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.impl.authz.AuthorizationManager;
import org.jboss.aerogear.android.impl.authz.oauth2.OAuth2AuthorizationConfiguration;
import org.jboss.aerogear.android.impl.authz.oauth2.OAuth2AuthzModule;
import org.jboss.aerogear.cordova.android.reflect.BasePlugin;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author edewit@redhat.com
 */
public class OAuth2Plugin extends BasePlugin {
  private static final String TAG = OAuth2Plugin.class.getSimpleName();
  private OAuth2AuthzModule module;

  public boolean add(JSONObject data, CallbackContext callbackContext) throws JSONException, MalformedURLException {
    Log.d(TAG, "add account");

    OAuth2AuthorizationConfiguration config = AuthorizationManager.config("name", OAuth2AuthorizationConfiguration.class);
    config.setBaseURL(new URL(data.getString("base")));
    config.setAccountId(data.getString("accountId"));
    config.setAccessTokenEndpoint(data.getString("accessTokenEndpoint"));
    config.setAuthzEndpoint(data.getString("authzEndpoint"));
    config.setClientId(data.getString("clientId"));
    config.setRefreshEndpoint(data.getString("refreshTokenEndpoint"));
    config.setRedirectURL(data.getString("redirectURL"));
    //config.setAdditionalAccessParams(data.getString("scopes").split(","))
    config.setAdditionalAccessParams(new HashSet<Pair<String, String>>(Arrays.asList(new Pair<String, String>("openid", "email"))));

    module = (OAuth2AuthzModule) config.asModule();
    callbackContext.success();
    return true;
  }

  public boolean requestAccess(final CallbackContext callbackContext) {
    Log.d(TAG, "requesting access");

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
}
