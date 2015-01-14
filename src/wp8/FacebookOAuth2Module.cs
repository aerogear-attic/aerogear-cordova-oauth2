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
using System.Threading.Tasks;

namespace AeroGear.OAuth2
{
    public class FacebookOAuth2Module : OAuth2Module
    {
        public async static new Task<OAuth2Module> Create(Config config)
        {
            FacebookOAuth2Module module = new FacebookOAuth2Module();
            await module.init(config);
            return module;
        }

        protected override async Task<Session> ParseResponse(Stream respondeStream)
        {
            StreamReader reader = new StreamReader(respondeStream);
            string queryString = await reader.ReadToEndAsync();
            IDictionary<string, string> data = ParseQueryString(queryString);

            Session session = new Session() 
            {
                accessToken = data["access_token"],
                accessTokenExpiration = int.Parse(data["expires"])
            };
            return session;
        }
    }
}
