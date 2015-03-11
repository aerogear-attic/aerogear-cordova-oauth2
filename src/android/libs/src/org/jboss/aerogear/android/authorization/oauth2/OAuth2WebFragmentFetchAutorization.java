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
package org.jboss.aerogear.android.authorization.oauth2;

import android.app.Activity;
import android.net.Uri;
import android.util.Pair;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.jboss.aerogear.android.core.Callback;
import static org.jboss.aerogear.android.pipe.util.UrlUtils.appendToBaseURL;

/**
 * This class displays a WebView Dialog Fragment to facilitates exchanging
 * credentials for authz tokens.
 * 
 * @author summers
 */
public class OAuth2WebFragmentFetchAutorization {

    private final Activity activity;
    private final String state;
  
    private FakeR fakeR;

    public OAuth2WebFragmentFetchAutorization(Activity activity, String state) {
        this.activity = activity;
        this.state = state;
        this.fakeR = new FakeR(activity);
    }

    public void performAuthorization(OAuth2Properties config, final Callback<String> callback) {

        try {
            doAuthorization(config, callback);
        } catch (UnsupportedEncodingException ex) {
            callback.onFailure(ex);
        } catch (MalformedURLException ex) {
            callback.onFailure(ex);
        }

    }

    private String formatScopes(ArrayList<String> scopes) throws UnsupportedEncodingException {

        StringBuilder scopeValue = new StringBuilder();
        String append = "";
        for (String scope : scopes) {
            scopeValue.append(append);
            scopeValue.append(URLEncoder.encode(scope, "UTF-8"));
            append = "+";
        }

        return scopeValue.toString();
    }

    private void doAuthorization(OAuth2Properties config, final Callback<String> callback) throws UnsupportedEncodingException, MalformedURLException {

        URL baseURL = config.getBaseURL();
        URL authzEndpoint = appendToBaseURL(baseURL, config.getAuthzEndpoint());
        Uri redirectURL = Uri.parse(config.getRedirectURL());
        ArrayList<String> scopes = new ArrayList<String>(config.getScopes());
        String clientId = config.getClientId();

        String query = "?scope=%s&redirect_uri=%s&client_id=%s&state=%s&response_type=code";
        query = String.format(query, formatScopes(scopes),
                URLEncoder.encode(redirectURL.toString(), "UTF-8"),
                clientId, state);

        if (config.getAdditionalAuthorizationParams() != null
                && config.getAdditionalAuthorizationParams().size() > 0) {
            for (Pair<String, String> param : config.getAdditionalAuthorizationParams()) {
                query += String.format("&%s=%s", URLEncoder.encode(param.first, "UTF-8"), URLEncoder.encode(param.second, "UTF-8"));
            }
        }

        URL authURL = new URL(authzEndpoint.toString() + query);

        final OAuthWebViewDialog dialog = OAuthWebViewDialog.newInstance(authURL, redirectURL);
        dialog.setReceiver(new OAuthWebViewDialog.OAuthReceiver() {
            @Override
            public void receiveOAuthCode(String code) {
                dialog.removeReceive();
                dialog.dismiss();
                callback.onSuccess(code);
            }

            @Override
            public void receiveOAuthError(final String error) {
                dialog.removeReceive();
                dialog.dismiss();
                callback.onFailure(new OAuth2AuthorizationException(error));
            }
        });

        dialog.setStyle(fakeR.getId("style", "Theme_Light_NoTitleBar"), 0);
        dialog.show(activity.getFragmentManager(), "TAG");
    }

}
