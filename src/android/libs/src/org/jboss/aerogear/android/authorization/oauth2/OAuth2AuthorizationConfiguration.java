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
import java.util.Collections;

import org.jboss.aerogear.android.core.Config;
import org.jboss.aerogear.android.authorization.AuthzModule;
import org.jboss.aerogear.android.authorization.AuthorizationConfiguration;

public class OAuth2AuthorizationConfiguration extends AuthorizationConfiguration<OAuth2AuthorizationConfiguration> implements Config<OAuth2AuthorizationConfiguration> {

    private String authzEndpoint = "";
    private String refreshEndpoint = "";
    private String redirectURL = "";
    private URL baseURL;
    private String accessTokenEndpoint = "";
    private List<String> scopes = new ArrayList<String>();
    private String clientId = "";
    private String clientSecret = "";
    private String accountId = "";
    private final Set<Pair<String, String>> additionalAuthorizationParams = new HashSet<Pair<String, String>>();
    private final Set<Pair<String, String>> additionalAccessParams = new HashSet<Pair<String, String>>();

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
     * @param authzEndpoint new authzEndpoint
     * @return the current configuration
     */
    public OAuth2AuthorizationConfiguration setAuthzEndpoint(String authzEndpoint) {
        this.authzEndpoint = authzEndpoint;
        return this;
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
     * @return the current configuration.
     */
    public OAuth2AuthorizationConfiguration setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
        return this;
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
     * @return the current configuration
     * 
     */
    public OAuth2AuthorizationConfiguration setAccessTokenEndpoint(String accessTokenEndpoint) {
        this.accessTokenEndpoint = accessTokenEndpoint;
        return this;
    }

    /**
     * 
     * Scopes are a list of permissions the application will request at
     * Authorization.
     * 
     * @return a copy of the current list of scopes
     */
    public List<String> getScopes() {
        return scopes;
    }

    /**
     * 
     * Scopes are a list of permissions the application will request at
     * Authorization.
     * 
     * @param scopes a new List of scopes
     * @return the current configuration
     */
    public OAuth2AuthorizationConfiguration setScopes(List<String> scopes) {
        this.scopes = scopes;
        return this;
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
     * @param clientId a new clientId
     * @return the current configuration
     */
    public OAuth2AuthorizationConfiguration setClientId(String clientId) {
        this.clientId = clientId;
        return this;
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
     * @param clientSecret a new clientSecret
     * @return the current configuration
     */
    public OAuth2AuthorizationConfiguration setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
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
     * @param accountId the new account ID
     * @return the current configuration
     */
    public OAuth2AuthorizationConfiguration setAccountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    /**
     * Sometimes a implementation will need additional parameters when
     * authorization is performed.
     * 
     * @return the current set of authorization parameters.
     */
    public Set<Pair<String, String>> getAdditionalAuthorizationParams() {
        return Collections.unmodifiableSet(additionalAuthorizationParams);
    }

    /**
     * Sometimes a implementation will need additional parameters when
     * authorization is performed.
     * 
     * @param additionalAuthorizationParam add a new Pair of AuthorizationParams
     * @return the current configuration
     */
    public OAuth2AuthorizationConfiguration addAdditionalAuthorizationParam(Pair<String, String> additionalAuthorizationParam) {
        this.additionalAuthorizationParams.add(additionalAuthorizationParam);
        return this;
    }

    /**
     * Remove an additional authorization param
     *
     * @param additionalAuthorizationParam Authorization param to be removed
     * @return the current configuration
     */
    public OAuth2AuthorizationConfiguration removeAdditionalAuthorizationParam(Pair<String, String> additionalAuthorizationParam) {
        this.additionalAuthorizationParams.remove(additionalAuthorizationParam);
        return this;
    }

    /**
     * Sometimes a implementation will need additional parameters when access is performed.
     * 
     * @return the current set of authorization parameters.
     */
    public Set<Pair<String, String>> getAdditionalAccessParams() {
        return Collections.unmodifiableSet(additionalAccessParams);
    }

    /**
     * Sometimes a implementation will need additional parameters when access is performed.
     * 
     * @param additionalAccessParam add a new Pair of AccessParams
     * @return the current configuration
     */
    public OAuth2AuthorizationConfiguration addAdditionalAccessParam(Pair<String, String> additionalAccessParam) {
        this.additionalAccessParams.add(additionalAccessParam);
        return this;
    }

    /**
     * Remove an additional access param
     *
     * @param additionalAccessParam Access param to be removed
     * @return the current configuration
     */
    public OAuth2AuthorizationConfiguration removeAdditionalAccessParam(Pair<String, String> additionalAccessParam) {
        this.additionalAccessParams.remove(additionalAccessParam);
        return this;
    }

    @Override
    protected AuthzModule buildModule() {
        if (baseURL == null) {
            throw new IllegalStateException("BaseURL may not be null");
        }

        OAuth2Properties params = new OAuth2Properties(baseURL, getName());
        params.setAccessTokenEndpoint(accessTokenEndpoint);
        params.setAccountId(accountId);
        params.setAuthzEndpoint(authzEndpoint);
        params.setRefreshEndpoint(refreshEndpoint);
        params.setClientId(clientId);
        params.setClientSecret(clientSecret);
        params.setRedirectURL(redirectURL);
        params.setScopes(scopes);
        params.getAdditionalAccessParams().addAll(additionalAccessParams);
        params.getAdditionalAuthorizationParams().addAll(additionalAuthorizationParams);

        return new OAuth2AuthzModule(params);
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
     * The baseURL is the url which endpoints will be appended to.
     * 
     * @param baseURL new baseURL
     * @return the current configuration.
     */
    public OAuth2AuthorizationConfiguration setBaseURL(URL baseURL) {
        this.baseURL = baseURL;
        return this;
    }

    /**
     * The refresh endpoint is the path to the location of the refresh token.
     * 
     * Defaults to an empty String.
     * 
     * @return the current baseURL
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
     * 
     * @return the current configuration.
     */
    public OAuth2AuthorizationConfiguration setRefreshEndpoint(String refreshEndpoint) {
        this.refreshEndpoint = refreshEndpoint;
        return this;
    }

}
