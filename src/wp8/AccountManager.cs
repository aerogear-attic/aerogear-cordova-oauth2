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
using System;
using System.Collections.Generic;
using System.IO;
using System.IO.IsolatedStorage;
using System.Linq;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Threading.Tasks;

namespace AeroGear.OAuth2
{
    public sealed class AccountManager
    {
        private IList<Config> configs = new List<Config>();
        private IDictionary<string, OAuth2Module> modules = new Dictionary<string, OAuth2Module>();
        private static readonly AccountManager instance = new AccountManager();

        private AccountManager() { }

        public static AccountManager Instance
        {
            get 
            {
                return instance;
            }
        }

        public static async Task<OAuth2Module> AddAccount(Config config)
        {
            if (Instance.modules.ContainsKey(config.accountId))
            {
                return Instance.modules[config.accountId];
            }
            else
            {
                OAuth2Module module = await OAuth2Module.Create(config);
                Instance.modules[config.accountId] = module;
                Instance.configs.Add(config);
                return module;
            }
        }

        public static async Task<OAuth2Module> AddKeyCloak(KeycloakConfig config)
        {
            OAuth2Module module = await KeycloakOAuth2Module.Create(config);
            Instance.modules[config.accountId] = module;
            Instance.configs.Add(config);
            return module;
        }

        public static async Task<OAuth2Module> AddFacebook(FacebookConfig config)
        {
            OAuth2Module module = await FacebookOAuth2Module.Create(config);
            Instance.modules[config.accountId] = module;
            Instance.configs.Add(config);
            return module;
        }

        public static OAuth2Module GetAccountByName(string name)
        {
            return Instance.modules[name];
        }

        public static OAuth2Module GetAccountByClientId(string clientId)
        {
            return Instance.modules.Where(entry => entry.Value.config.clientId == clientId).Single().Value;
        }

        public static void SaveSate()
        {
            IsolatedStorageSettings.ApplicationSettings["accountManager"] = Instance.configs;
            IsolatedStorageSettings.ApplicationSettings.Save();
        }

        public async static Task Restore()
        {
            Instance.configs = (IList<Config>) IsolatedStorageSettings.ApplicationSettings["accountManager"];
            foreach (Config config in Instance.configs) 
            {
                await RestoreAccount(config);
            }
        }

        private async static Task RestoreAccount(Config config)
        {
            OAuth2Module module;
            if (config.GetType() == typeof(KeycloakConfig))
            {
                module = await KeycloakOAuth2Module.Create(config);
            }
            else if (config.GetType() == typeof(FacebookConfig))
            {
                module = await FacebookOAuth2Module.Create(config);
            }
            else
            {
                module = await OAuth2Module.Create(config);
            }
            
            Instance.modules[config.accountId] = module;
        }
    }

    [DataContract]
    public class GoogleConfig : Config
    {
        public static GoogleConfig Create(string clientId, IList<string> scopes, string accountId)
        {
            var protocol = ManifestInfo.GetProtocol();
            return new GoogleConfig()
            {
                baseURL = "https://accounts.google.com/",
                authzEndpoint = "o/oauth2/auth",
                redirectURL = protocol + ":/oauth2Callback",
                accessTokenEndpoint = "o/oauth2/token",
                refreshTokenEndpoint = "o/oauth2/token",
                revokeTokenEndpoint = "rest/revoke",
                clientId = clientId,
                scopes = scopes,
                accountId = accountId
            };
        }
    }

    [DataContract]
    public class KeycloakConfig : Config
    {
        [DataMember]
        public string host { get; set; }
        [DataMember]
        public string realm { get; set; }
        public static KeycloakConfig Create(string clientId, string host, string realm)
        {
            var protocol = ManifestInfo.GetProtocol();
            var defaulRealmName = clientId + "-realm";
            var realmName = realm != null ? realm : defaulRealmName;
            return new KeycloakConfig() {
                baseURL = host + "/auth/",
                authzEndpoint = string.Format("realms/{0}/tokens/login", realmName),
                redirectURL = protocol + ":/oauth2Callback",
                accessTokenEndpoint = string.Format("realms/{0}/tokens/access/codes", realmName),
                clientId = clientId,
                refreshTokenEndpoint = string.Format("realms/{0}/tokens/refresh", realmName),
                revokeTokenEndpoint = string.Format("realms/%@/tokens/logout", realmName),
                accountId = clientId
            };
        }
    }

    [DataContract]
    public class FacebookConfig : Config
    {
        public static FacebookConfig Create(string clientId, string clientSecret, List<string> scopes, string accountId)
        {
            return new FacebookConfig()
            {
                baseURL = "",
                authzEndpoint = "https://www.facebook.com/dialog/oauth",
                redirectURL = "fb" + clientId + "://authorize/",
                accessTokenEndpoint = "https://graph.facebook.com/oauth/access_token",
                clientId = clientId,
                refreshTokenEndpoint = "https://graph.facebook.com/oauth/access_token",
                clientSecret = clientSecret,
                revokeTokenEndpoint = "https://www.facebook.com/me/permissions",
                scopes = scopes,
                accountId = accountId
            };
        }
    }
}
