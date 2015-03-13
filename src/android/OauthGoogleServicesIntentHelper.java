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

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class OauthGoogleServicesIntentHelper {
  private static final String TAG = "OauthGoogleServices";
  private static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
  private static final int REQUEST_AUTHORIZATION = 2;
  private static final String KEY_AUTH_TOKEN = "authtoken";
  private static final String PROFILE_SCOPE = "https://www.googleapis.com/auth/plus.me";
  private String scopes;
  private CallbackContext callbackContext;
  public CordovaInterface cordova;
  private static Class CLASS_GoogleAuthUtil;
  private static Class CLASS_UserRecoverableAuthException;
  private static Method METHOD_getToken;
  private static Method METHOD_getIntent;
  public static final boolean available;

  static { // lazy initialization holder class idiom - a class will not be initialized until it is used [JLS, 12.4.1]
    boolean _available;
    try {
      CLASS_GoogleAuthUtil = Class.forName("com.google.android.gms.auth.GoogleAuthUtil");
      CLASS_UserRecoverableAuthException = Class.forName("com.google.android.gms.auth.UserRecoverableAuthException");
      METHOD_getToken = CLASS_GoogleAuthUtil.getMethod("getToken", Context.class, String.class, String.class);
      METHOD_getIntent = CLASS_UserRecoverableAuthException.getMethod("getIntent");
      _available = true;
    } catch (ClassNotFoundException e) {
      _available = false;
      Log.e(TAG, e.getMessage());
      Log.e(TAG, "ClassNotFoundException, Google Play Services not available");
    } catch (NoSuchMethodException e) {
      _available = false;
      Log.e(TAG, e.getMessage());
      Log.e(TAG, "NoSuchMethodException, Google Play Services not available");
    }
    available = _available;
    Log.i(TAG, "Google play services availability: " + available);
  }

  public OauthGoogleServicesIntentHelper(CordovaInterface cordova, CallbackContext callbackContext) {
    this.cordova = cordova;
    this.callbackContext = callbackContext;
  }

  public boolean triggerIntent(final JSONObject data) throws JSONException {
    scopes = "oauth2:" + ( data.has("scopes") ? data.getString("scopes") : PROFILE_SCOPE );

    final String[] accountTypes = (data.has("accountTypes"))
      ? data.getString("accountTypes").split("\\s")
      : new String[]{"com.google"};

    Runnable runnable = new Runnable() {
      public void run() {
        try {
          Intent intent = AccountManager.newChooseAccountIntent(null, null, accountTypes, false, null, null, null, null);
          cordova.getActivity().startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
        } catch (ActivityNotFoundException e) {
          Log.e(TAG, "Activity not found: " + e.toString());
          callbackContext.error("Plugin cannot find activity: " + e.toString());
        } catch (Exception e) {
          Log.e(TAG, "Exception: " + e.toString());
          callbackContext.error("Plugin failed to get account: " + e.toString());
        }
      }

      ;
    };
    cordova.getActivity().runOnUiThread(runnable);
    return true;
  }

  public void onActivityResult(int requestCode, int resultCode, final Intent data) {
    if (callbackContext != null) {
      try {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
          if (resultCode == Activity.RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Log.i(TAG, "account:" + accountName);
            getToken(accountName);
          } else {
            callbackContext.error("plugin failed to get account");
          }
        } else if (requestCode == REQUEST_AUTHORIZATION) {
          if (resultCode == Activity.RESULT_OK) {
            String token = data.getStringExtra(KEY_AUTH_TOKEN);
            callbackContext.success(token);
          } else {
            callbackContext.error("plugin failed to get token");
          }
        } else {
          Log.i(TAG, "Unhandled activityResult. requestCode: " + requestCode + " resultCode: " + resultCode);
        }
      } catch (Exception e) {
        callbackContext.error("Plugin failed to get email: " + e.toString());
        Log.e(TAG, "Exception: " + e.toString());
      }
    } else {
      Log.d(TAG, "No callback to go to!");
    }
  }

  private void getToken(final String accountName) {
    Runnable runnable = new Runnable() {
      public void run() {
        String token;
        try {
          Log.i(TAG, "Retrieving token for: " + accountName);
          Log.i(TAG, "with scope(s): " + scopes);
          token = (String) METHOD_getToken.invoke(null, cordova.getActivity(), accountName, scopes);
          callbackContext.success(token);
        } catch (InvocationTargetException ite) {
          Throwable userRecoverableException = ite.getCause();
          if (CLASS_UserRecoverableAuthException != null && CLASS_UserRecoverableAuthException.isInstance(userRecoverableException)) {
            try {
              Intent intent = (Intent) METHOD_getIntent.invoke(userRecoverableException);
              Log.e(TAG, "UserRecoverableAuthException: Attempting recovery...");
              cordova.getActivity().startActivityForResult(intent, REQUEST_AUTHORIZATION);
            } catch (IllegalAccessException e) {
              Log.i(TAG, "error" + e.getMessage());
              callbackContext.error("plugin failed to get token: " + e.getMessage());
            } catch (InvocationTargetException e) {
              Log.i(TAG, "error" + e.getCause().getMessage());
              callbackContext.error("plugin failed to get token: " + e.getCause().getMessage());
            }
          }
        } catch (Exception e) {
          Log.i(TAG, "error" + e.getMessage());
          callbackContext.error("plugin failed to get token: " + e.getMessage());
        }
      }
    };
    cordova.getThreadPool().execute(runnable);
  }
}
