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

import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthroizationConfigurationProvider;
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationConfiguration;
import java.util.HashMap;
import java.util.Map;
import org.jboss.aerogear.android.core.ConfigurationProvider;

public final class AuthorizationManager {
    private static Map<String, AuthzModule> modules = new HashMap<String, AuthzModule>();

    private static Map<Class<? extends AuthorizationConfiguration<?>>, ConfigurationProvider<?>> configurationProviderMap = new HashMap<Class<? extends AuthorizationConfiguration<?>>, ConfigurationProvider<?>>();

    private static OnAuthorizationCreatedListener onAuthorizationCreatedListener = new OnAuthorizationCreatedListener() {
        @Override
        public void onAuthorizationCreated(AuthorizationConfiguration<?> configuration, AuthzModule module) {
            modules.put(configuration.getName(), module);
        }
    };

    static {
        OAuth2AuthroizationConfigurationProvider oauth2ConfigurationProvider = new OAuth2AuthroizationConfigurationProvider();
        AuthorizationManager.registerConfigurationProvider(OAuth2AuthorizationConfiguration.class, oauth2ConfigurationProvider);

    }

    private AuthorizationManager() {
    }

    /**
     * 
     * This will add a new Configuration that this Manager can build
     * Configurations for.
     * 
     * @param <CFG> the actual Configuration type
     * @param configurationClass the class of configuration to be registered
     * @param provider the instance which will provide the configuration.
     */
    public static <CFG extends AuthorizationConfiguration<CFG>> void registerConfigurationProvider
            (Class<CFG> configurationClass, ConfigurationProvider<CFG> provider) {
        configurationProviderMap.put(configurationClass, provider);
    }

    /**
     * Begins a new fluent configuration stanza.
     * 
     * @param <CFG> the Configuration type.
     * @param name an identifier which will be used to fetch the AuthzManager after
     *            configuration is finished.
     * @param authorizationConfigurationClass the class of the configuration type.
     * 
     * @return a AuthorizationConfiguration which can be used to build a AuthzManager object.
     */
    public static <CFG extends AuthorizationConfiguration<CFG>> CFG config(String name, Class<CFG> authorizationConfigurationClass) {

        @SuppressWarnings("unchecked")
        ConfigurationProvider<? extends AuthorizationConfiguration<CFG>> provider =
                (ConfigurationProvider<? extends AuthorizationConfiguration<CFG>>)
                configurationProviderMap.get(authorizationConfigurationClass);

        if (provider == null) {
            throw new IllegalArgumentException("Configuration not registered!");
        }

        return provider.newConfiguration()
                .setName(name)
                .addOnAuthorizationCreatedListener(onAuthorizationCreatedListener);

    }

    /**
     * Fetches a named module
     * 
     * @param name the name of the AuthzManager given in {@link AuthorizationManager#config(java.lang.String, java.lang.Class) }
     * 
     * @return the named AuthzModule or null
     */
    public static AuthzModule getModule(String name) {
        return modules.get(name);
    }

}
