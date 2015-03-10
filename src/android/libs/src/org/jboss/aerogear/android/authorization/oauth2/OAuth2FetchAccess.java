/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.aerogear.android.authorization.oauth2;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import org.jboss.aerogear.android.core.Callback;

/**
 * This is a helper class which fetches an access token given an authorization
 * token.
 * 
 * @author summers
 */
public class OAuth2FetchAccess {

    private final OAuth2AuthzService service;

    public OAuth2FetchAccess(OAuth2AuthzService service) {
        this.service = service;
    }

    public void fetchAccessCode(final String accountId, final OAuth2Properties config, final Callback<String> callback) {

        if (Looper.myLooper() == Looper.getMainLooper()) {// foreground thread
            new AsyncTask<Object, Void, Object>() {

                @Override
                protected Object doInBackground(Object... params) {
                    try {
                        return service.fetchAccessToken((String) params[0], (OAuth2Properties) params[1]);
                    } catch (OAuth2AuthorizationException ex) {
                        return ex;
                    }
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (result instanceof String || result == null) {
                        callback.onSuccess((String) result);
                    } else {
                        callback.onFailure((Exception) result);
                    }
                }

            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, accountId, config);

        } else { // background thread
            new Handler(Looper.myLooper()).post(new Runnable() {

                @Override
                public void run() {
                    try {

                        String code = service.fetchAccessToken(accountId, config);
                        callback.onSuccess((String) code);

                    } catch (OAuth2AuthorizationException ex) {
                        callback.onFailure(ex);
                    }
                }
            });
        }
    }

}
