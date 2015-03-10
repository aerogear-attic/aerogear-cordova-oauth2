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
package org.jboss.aerogear.android.authorization;

import java.util.Collection;
import java.util.HashSet;
import org.jboss.aerogear.android.core.Config;

public abstract class AuthorizationConfiguration<CONFIGURATION extends AuthorizationConfiguration> implements Config<CONFIGURATION> {

    private Collection<OnAuthorizationCreatedListener> listeners = new HashSet<OnAuthorizationCreatedListener>();

    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CONFIGURATION setName(String name) {
        this.name = name;
        return (CONFIGURATION) this;
    }

    /**
     * OnAuthorizationCreatedListeners are a collection of classes to be
     * notified when the configuration of the Pipe is complete.
     * 
     * @return the current collection.
     */
    public Collection<OnAuthorizationCreatedListener> getOnAuthorizationCreatedListeners() {
        return listeners;
    }

    /**
     * OnAuthorizationCreatedListeners are a collection of classes to be
     * notified when the configuration of the Pipe is complete.
     * 
     * @param listener new listener to add to the collection
     * @return this configuration
     */
    public CONFIGURATION addOnAuthorizationCreatedListener(OnAuthorizationCreatedListener listener) {
        this.listeners.add(listener);
        return (CONFIGURATION) this;
    }

    /**
     * OnAuthorizationCreatedListeners are a collection of classes to be
     * notified when the configuration of the Pipe is complete.
     * 
     * @param listeners new collection to replace the current one
     * @return this configuration
     */
    public CONFIGURATION setOnAuthorizationCreatedListeners(Collection<OnAuthorizationCreatedListener> listeners) {
        listeners.addAll(listeners);
        return (CONFIGURATION) this;
    }

    /**
     * 
     * Creates a authenticationModule based on the current configuration and
     * notifies all listeners
     * 
     * @return An AuthenticationModule based on this configuration
     * 
     * @throws IllegalStateException if the AuthenticationModule can not be
     *             constructed.
     * 
     */
    public final AuthzModule asModule() {

        AuthzModule newModule = buildModule();
        for (OnAuthorizationCreatedListener listener : getOnAuthorizationCreatedListeners()) {
            listener.onAuthorizationCreated(this, newModule);
        }
        return newModule;
    }

    /**
     * 
     * Validates configuration parameters and returns a AuthenticationModule
     * instance.
     * 
     * @return An AuthenticationModule based on this configuration
     * 
     * @throws IllegalStateException if the Pipe can not be constructed.
     */
    protected abstract AuthzModule buildModule();

}
