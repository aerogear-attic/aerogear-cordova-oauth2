/**
 * JBoss, Home of Professional Open Source Copyright Red Hat, Inc., and
 * individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jboss.aerogear.android.authorization;

import android.app.Activity;
import java.net.URI;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.module.AuthorizationFields;
import org.jboss.aerogear.android.pipe.module.PipeModule;
import org.jboss.aerogear.android.pipe.http.HttpProvider;
import org.jboss.aerogear.android.pipe.Pipe;

/**
 * The AuthzModule wraps access to Authorization providers and exposes
 * authorization state and tokens to Android applications.
 */
public interface AuthzModule extends PipeModule {

    /**
     * 
     * If a module is Authorized that means that it can be used to provide
     * authorization meta-data to calling code.
     * 
     * @return if the module is in a state that allows it to be used.
     */
    public boolean isAuthorized();

    /**
     * 
     * This function checks if a module has all of the necessary information to
     * make a authorized request. This method does not check the state of those
     * tokens. For that you should use {@link AuthzModule#isAuthorized()
     * }
     * 
     * @return if the module has completed Authorization.
     */
    public boolean hasCredentials();

    /**
     * 
     * Begin requesting access for the application. This method MUST be
     * asynchronous. An implementation MAY start a new activity, but the calling
     * Activity MUST handle the response itself.
     * 
     * @param activity the calling activity.
     * @param callback a callback to be called upon completion of the
     *            authorization action.
     */
    public void requestAccess(Activity activity, Callback<String> callback);

    /**
     * 
     * Refreshing access will synchronously check the current of the tokens and
     * refresh them if necessary.
     * 
     * This is used by AeroGear if tokens expired while a reference to the
     * module is still held by a pipe.
     * 
     * @return true if access to the system is in a good state.
     */
    public boolean refreshAccess();

    /**
     * This will remove all information about the account with the Module's 
     * AccountId
     */
    public void deleteAccount();
    
    /**
     * This method is called be {@link Pipe} implementations when they need
     * security applied to their {@link HttpProvider}. The headers/data/query
     * parameters returned should be applied to the Url and HttpProvider
     * directly before a call.
     * 
     * @param requestUri the Request-Line URI.
     * @param method the HTTP method being used
     * @param requestBody the body of the request. This method promises to not
     *            modify the body.
     * 
     * @return the current AuthorizationFields for security
     * 
     */
    public AuthorizationFields getAuthorizationFields(URI requestUri, String method, byte[] requestBody);

}
