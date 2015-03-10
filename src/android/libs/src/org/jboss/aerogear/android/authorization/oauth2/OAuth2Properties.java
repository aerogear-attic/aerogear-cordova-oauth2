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

import android.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Authorization configuration class.
 */
public final class OAuth2Properties {

    private final URL baseURL;
    private final String name;
    private String authzEndpoint = "";
    private String refreshEndpoint = "";
    private String redirectURL = "";
    private String accessTokenEndpoint = "";
    private List<String> scopes = new ArrayList<String>();
    private String clientId = "";
    private String clientSecret = "";
    private String accountId = "";
    private Set<Pair<String, String>> additionalAuthorizationParams = new HashSet<Pair<String, String>>();
    private Set<Pair<String, String>> additionalAccessParams = new HashSet<Pair<String, String>>();

    /**
     * 
     * @param baseURL This is the url which endpoints will be appended to.
     * @param name A name to reference the AuthorizationModule which this config
     *            will create.
     */
    public OAuth2Properties(URL baseURL, String name) {
        this.baseURL = baseURL;
        this.name = name;
    }

    /**
     * The baseURL is the url which endpoints will be appended to.
     * 
     * @return the current baseURL
     */
    public URL getBaseURL() {
        return baseURL;
    }

    /**
     * The authzEnpoint defines the endpoint which the Authorization module will
     * use to obtain an authorization token.
     * 
     * @return the current authzEndpoint
     */
    public String getAuthzEndpoint() {
        return authzEndpoint;
    }

    /**
     * The authzEnpoint defines the endpoint which the Authorization module will
     * use to obtain an authorization token.
     * 
     * @param authzEndpoint a new authzEndpoint
     */
    public void setAuthzEndpoint(String authzEndpoint) {
        this.authzEndpoint = authzEndpoint;
    }

    /**
     * The redirect URL is the url which handles consuming a response from the
     * authorization server.
     * 
     * @return the current redirectURL.
     */
    public String getRedirectURL() {
        return redirectURL;
    }

    /**
     * The redirect URL is the url which handles consuming a response from the
     * authorization server.
     * 
     * @param redirectURL a new redirectURL
     * 
     */
    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    /**
     * The accessTokenEndpoint is responsible for generating an accesstoken for
     * an authorized user.
     * 
     * @return the current accessTokenEndpoint
     * 
     */
    public String getAccessTokenEndpoint() {
        return accessTokenEndpoint;
    }

    /**
     * The accessTokenEndpoint is responsible for generating an accesstoken for
     * an authorized user.
     * 
     * @param accessTokenEndpoint a new accessTokenEndpoint
     * 
     */
    public void setAccessTokenEndpoint(String accessTokenEndpoint) {
        this.accessTokenEndpoint = accessTokenEndpoint;
    }

    /**
     * 
     * Scopes are a list of permissions the application will request at
     * Authorization.
     * 
     * @return a copy of the current list of scopes
     */
    public List<String> getScopes() {
        return new ArrayList<String>(scopes);
    }

    /**
     * 
     * Scopes are a list of permissions the application will request at
     * Authorization.
     * 
     * @param scopes a new list of scopes to replace the current one
     */
    public void setScopes(List<String> scopes) {
        this.scopes = new ArrayList<String>(scopes);
    }

    /**
     * The client ID is the ID assigned to the application by the service
     * provider.
     * 
     * @return the current clientID
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * The client ID is the ID assigned to the application by the service
     * provider.
     * 
     * @param clientId a new client ID
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * The client secret is assigned to the application by the service provider.
     * 
     * @return the current clientSecret
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * The client secret is assigned to the application by the service provider.
     * 
     * @param clientSecret a new client secret
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * Name refers to the name of the module this config will become
     * 
     * @return the current name
     */
    public String getName() {
        return name;
    }

    /**
     * The account ID parameter is to identify it ID of the {@link OAuth2AuthzSession} which will be used to store the information.
     * It is unique per app and generated by you, the developer.
     * 
     * @return the current account
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * The account ID parameter is to identify it ID of the {@link OAuth2AuthzSession} which will be used to store the information.
     * It is unique per app and generated by you, the developer.
     * 
     * @param accountId a new accountId
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * Sometimes a implementation will need additional parameters when
     * authorization is performed.
     * 
     * @return the current set of authorization parameters.
     */
    public Set<Pair<String, String>> getAdditionalAuthorizationParams() {
        return additionalAuthorizationParams;
    }

    /**
     * Sometimes a implementation will need additional parameters when access is
     * performed.
     * 
     * @return the current set of authorization parameters.
     */
    public Set<Pair<String, String>> getAdditionalAccessParams() {
        return additionalAccessParams;
    }

    /**
     * The refresh endpoint is the path to the location of the refresh token.
     * 
     * Defaults to an empty String.
     * 
     * @return the current refreshEndpoint
     */

    public String getRefreshEndpoint() {
        return refreshEndpoint;
    }

    /**
     * The refresh endpoint is the path to the location of the refresh token.
     * 
     * Defaults to an empty String.
     * 
     * @param refreshEndpoint a new endpoint.
     */
    public void setRefreshEndpoint(String refreshEndpoint) {
        this.refreshEndpoint = refreshEndpoint;
    }

}
