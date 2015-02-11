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
using WPCordovaClassLib.Cordova.Commands;
using WPCordovaClassLib.Cordova.JSON;

using AeroGear.OAuth2;
using WPCordovaClassLib.Cordova;
using System.IO.IsolatedStorage;

public class OAuth2Plugin : BaseCommand
{
    public async void add(string unparsedConfig)
    {
        var options = JsonHelper.Deserialize<string[]>(unparsedConfig)[0];
        var config = JsonHelper.Deserialize<Config>(options);

        await AccountManager.AddAccount(config);
    }

    public async void addGoogle(string unparsedConfig)
    {
        var options = JsonHelper.Deserialize<string[]>(unparsedConfig)[0];
        var config = JsonHelper.Deserialize<Config>(options);
        GoogleConfig googleConfig = GoogleConfig.Create(config.clientId, config.scopes, config.accountId);
        await AccountManager.AddAccount(googleConfig);
    }

    public async void addKeycloak(string unparsedConfig)
    {
        var options = JsonHelper.Deserialize<string[]>(unparsedConfig)[0];
        var config = JsonHelper.Deserialize<KeycloakConfig>(options);
        KeycloakConfig keycloak = KeycloakConfig.Create(config.clientId, config.host, config.realm);
        await AccountManager.AddKeyCloak(keycloak);
    }

    public async void requestAccess(string unparsed)
    {
        var accountId = JsonHelper.Deserialize<string[]>(unparsed)[0];
        var module = AccountManager.GetAccountByName(accountId);

        AccountManager.SaveSate();
        IsolatedStorageSettings.ApplicationSettings["module"] = accountId;

        await module.RequestAccessAndContinue();

        PluginResult result = new PluginResult(PluginResult.Status.OK, module.GetAccessToken());
        DispatchCommandResult(result);
    }
}