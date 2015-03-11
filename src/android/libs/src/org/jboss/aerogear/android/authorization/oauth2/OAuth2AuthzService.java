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

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.jboss.aerogear.android.store.DataManager;
import org.jboss.aerogear.android.store.generator.IdGenerator;
import org.jboss.aerogear.android.pipe.http.HeaderAndBody;
import org.jboss.aerogear.android.pipe.http.HttpException;
import org.jboss.aerogear.android.pipe.http.HttpProvider;
import org.jboss.aerogear.android.store.sql.SQLStore;
import org.jboss.aerogear.android.store.sql.SQLStoreConfiguration;
import org.jboss.aerogear.android.pipe.http.HttpRestProvider;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import static org.jboss.aerogear.android.pipe.util.UrlUtils.appendToBaseURL;

/**
 * This service manages tokens for Authorization sessions. It can also perform
 * basic OAuth2 Access Token/ Authorization exchange and manages refresh tokens.
 * 
 */
public class OAuth2AuthzService extends Service {

    private final AuthzBinder binder = new AuthzBinder(this);

    private SQLStore<OAuth2AuthzSession> sessionStore;
    private static final String TAG = OAuth2AuthzService.class.getSimpleName();

    public OAuth2AuthzService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * This will exchange an Authorization token for an Access Token
     * 
     * @param accountId the ID for the {@link OAuth2AuthzSession}
     * @param config the config
     * @return an accesstoken
     * @throws OAuth2AuthorizationException if something went wrong in the
     *             exchange
     */
    public String fetchAccessToken(String accountId, OAuth2Properties config) throws OAuth2AuthorizationException {
        OAuth2AuthzSession storedAccount = sessionStore.read(accountId);
        if (storedAccount == null) {
            return null;
        }

        if (!isNullOrEmpty(storedAccount.getAccessToken()) && storedAccount.tokenIsNotExpired()) {
            return storedAccount.getAccessToken();
        } else if (!isNullOrEmpty(storedAccount.getRefreshToken())) {
            refreshAccount(storedAccount, config);
            sessionStore.save(storedAccount);
            return storedAccount.getAccessToken();
        } else if (!isNullOrEmpty(storedAccount.getAuthorizationCode())) {
            exchangeAuthorizationCodeForAccessToken(storedAccount, config);
            sessionStore.save(storedAccount);
            return storedAccount.getAccessToken();
        } else {
            return null;
        }

    }

    /**
     * Put a session into the store.
     * 
     * @param account a new session
     */
    public void addAccount(OAuth2AuthzSession account) {
        String accountId = account.getAccountId();

        if (hasAccount(accountId)) {
            sessionStore.remove(accountId);
        }

        sessionStore.save(account);
    }

    /**
     * Will check if there is an account which has previously been granted an
     * authorization code and access code
     * 
     * @param accountId the accountId to check
     * @return true if there is a session for the account.
     */
    public boolean hasAccount(String accountId) {
        OAuth2AuthzSession storedAccount = sessionStore.read(accountId);
        if (storedAccount == null) {
            return false;
        }
        return !isNullOrEmpty(storedAccount.getAuthorizationCode())
                || !isNullOrEmpty(storedAccount.getAccessToken());
    }

    /**
     * Returns the OAuth2AuthzSession for accountId if any
     * 
     * @param accountId the accountId to look up
     * @return an OAuth2AuthzSession or null
     */
    public OAuth2AuthzSession getAccount(String accountId) {
        return sessionStore.read(accountId);
    }

    /**
     * Fetches all OAuth2AuthzSessions in the system.
     * 
     * @return all OAuth2AuthzSession's in the system
     */
    public List<String> getAccounts() {
        Collection<OAuth2AuthzSession> sessions = sessionStore.readAll();
        ArrayList<String> accountIds = new ArrayList<String>();
        
        for (OAuth2AuthzSession session : sessions) {
            accountIds.add(session.getAccountId());
        }
        
        return accountIds;
        
    }

    @Override
    public IBinder onBind(Intent intent) {
        openSessionStore();
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void openSessionStore() {
        DataManager.config("sessionStore", SQLStoreConfiguration.class)
                .forClass(OAuth2AuthzSession.class)
                .withContext(getApplicationContext())
                .withIdGenerator(new IdGenerator() {
                    @Override
                    public Serializable generate() {
                        return UUID.randomUUID().toString();
                    }
                }).store();

        sessionStore = (SQLStore<OAuth2AuthzSession>) DataManager.getStore("sessionStore");
        sessionStore.openSync();
    }

    private void exchangeAuthorizationCodeForAccessToken(OAuth2AuthzSession storedAccount, OAuth2Properties config) throws OAuth2AuthorizationException {
        final Map<String, String> data = new HashMap<String, String>();

        data.put("code", storedAccount.getAuthorizationCode());
        data.put("client_id", storedAccount.getClientId());
        if (config.getRedirectURL() != null) {
            data.put("redirect_uri", config.getRedirectURL());
        }
        data.put("grant_type", "authorization_code");
        if (config.getClientSecret() != null) {
            data.put("client_secret", config.getClientSecret());
        }

        if (!config.getAdditionalAccessParams().isEmpty()) {
            for (Pair<String, String> param : config.getAdditionalAccessParams()) {
                try {
                    data.put(URLEncoder.encode(param.first, "UTF-8"), param.second);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        runAccountAction(storedAccount, config, data, appendToBaseURL(config.getBaseURL(), config.getAccessTokenEndpoint()));

    }

    private void refreshAccount(OAuth2AuthzSession storedAccount, OAuth2Properties config) throws OAuth2AuthorizationException {
        final Map<String, String> data = new HashMap<String, String>();

        data.put("refresh_token", storedAccount.getRefreshToken());
        data.put("grant_type", "refresh_token");
        data.put("client_id", storedAccount.getClientId());
        if (config.getClientSecret() != null) {
            data.put("client_secret", config.getClientSecret());
        }

        if (!config.getAdditionalAccessParams().isEmpty()) {
            for (Pair<String, String> param : config.getAdditionalAccessParams()) {
                try {
                    data.put(URLEncoder.encode(param.first, "UTF-8"), param.second);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        runAccountAction(storedAccount, config, data, appendToBaseURL(config.getBaseURL(), config.getRefreshEndpoint()));
    }

    private void runAccountAction(OAuth2AuthzSession storedAccount, OAuth2Properties config, final Map<String, String> data, URL endpoint)
            throws OAuth2AuthorizationException {
        try {

            final HttpProvider provider = getHttpProvider(endpoint);
            final String formTemplate = "%s=%s";
            provider.setDefaultHeader("Content-Type", "application/x-www-form-urlencoded");

            final StringBuilder bodyBuilder = new StringBuilder();

            String amp = "";
            for (Map.Entry<String, String> entry : data.entrySet()) {
                bodyBuilder.append(amp);
                try {
                    bodyBuilder.append(String.format(formTemplate, entry.getKey(), URLEncoder.encode(entry.getValue(), "UTF-8")));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                amp = "&";
            }

            HeaderAndBody headerAndBody;

            try {
                headerAndBody = provider.post(bodyBuilder.toString().getBytes("UTF-8"));

            } catch (HttpException exception) {
                if (exception.getStatusCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                    JsonElement response = new JsonParser().parse(new String(exception.getData()));
                    JsonObject jsonResponseObject = response.getAsJsonObject();
                    String error = "";
                    if (jsonResponseObject.has("error")) {
                        JsonElement errorObject = jsonResponseObject.get("error");
                        if (errorObject.isJsonPrimitive())  {
                            error = errorObject.getAsString();
                        } else {
                            error = errorObject.toString();
                        }
                        
                        
                    }

                    throw new OAuth2AuthorizationException(error);
                } else {
                    throw exception;
                }
            }
            String responseString = new String(headerAndBody.getBody());

            try {
                JsonElement response = new JsonParser().parse(responseString);
                JsonObject jsonResponseObject = response.getAsJsonObject();

                String accessToken = jsonResponseObject.get("access_token").getAsString();
                storedAccount.setAccessToken(accessToken);

                // Will need to check this one day
                // String tokenType = jsonResponseObject.get("token_type").getAsString();
                if (jsonResponseObject.has("expires_in")) {
                    Long expiresIn = jsonResponseObject.get("expires_in").getAsLong();
                    Long expires_on = new Date().getTime() + expiresIn * 1000;
                    storedAccount.setExpires_on(expires_on);
                }

                if (jsonResponseObject.has("refresh_token")) {
                    String refreshToken = jsonResponseObject.get("refresh_token").getAsString();
                    if (!isNullOrEmpty(refreshToken)) {
                        storedAccount.setRefreshToken(refreshToken);
                    }
                }
            } catch (JsonParseException parseEx){
                try {
                    //Check if body is http form format
                    String[] values = responseString.split("&");
                    for (String pair : values) {
                        String property[] = pair.split("=");
                        String key = property[0];
                        String value = property[1];
                        if ("access_token".equals(key)) {
                            storedAccount.setAccessToken(value);
                        } else if ("expires_in".equals(key)) {
                            Long expiresIn = Long.parseLong(value);
                            Long expires_on = new Date().getTime() + expiresIn * 1000;
                            storedAccount.setExpires_on(expires_on);
                        } else if ("refresh_token".equals(key)) {
                            if (!isNullOrEmpty(value)) {
                                storedAccount.setRefreshToken(value);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    throw new OAuth2AuthorizationException(responseString);
                }
            }

            storedAccount.setAuthorizationCode("");

        } catch (UnsupportedEncodingException ex) {
            // Should never happen...
            Log.d(OAuth2AuthzService.class.getName(), null, ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * This method allows an implementation to change how the HttpProvider is
     * fetched. Override is mostly used for testing.
     * 
     * @param url the url endpoint
     * @return a httpProvider
     */
    protected HttpProvider getHttpProvider(URL url) {
        return new HttpRestProvider(url);
    }

    /**
     * Removes the account with the provided ID
     * 
     * @param accountId 
     */
    public void removeAccount(String accountId) {
        sessionStore.remove(accountId);
    }

    private boolean isNullOrEmpty(String value) {
        return (value == null || value.isEmpty());
    }

    public static class AuthzBinder extends Binder {

        private final OAuth2AuthzService service;

        private AuthzBinder(OAuth2AuthzService service) {
            this.service = service;
        }

        public OAuth2AuthzService getService() {
            return service;
        }

    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    public static class AGAuthzServiceConnection implements ServiceConnection {

        private OAuth2AuthzService service;
        private boolean bound = false;

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder iBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            AuthzBinder binder = (AuthzBinder) iBinder;
            this.service = binder.service;
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }

        public OAuth2AuthzService getService() {
            return service;
        }

        public boolean isBound() {
            return bound;
        }

    }

    ;

}
