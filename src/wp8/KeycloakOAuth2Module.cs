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
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Threading.Tasks;

namespace AeroGear.OAuth2
{
    public class KeycloakOAuth2Module : OAuth2Module
    {
        public async static new Task<OAuth2Module> Create(Config config)
        {
            KeycloakOAuth2Module module = new KeycloakOAuth2Module();
            await module.init(config);
            return module;
        }

        protected override async Task<Session> ParseResponse(Stream respondeStream)
        {
            Session session = await base.ParseResponse(respondeStream);
            var decoded = DecodeToken(session.refreshToken);
            var time = decoded.exp - decoded.iat;
            session.refreshTokenExpirationDate = DateTime.Now.AddSeconds(time);
            return session;
        }

        public KeyCloakResponse DecodeToken(string token)
        {
            string toDecode = token.Split('.')[1];
            string stringtoDecode = toDecode.Replace('-', '+');
            stringtoDecode = toDecode.Replace('_', '/');

            switch (stringtoDecode.Length % 4)
            {
                case 2:
                    stringtoDecode = stringtoDecode + "==";
                    break;
                case 3: stringtoDecode = stringtoDecode + "=";
                    break;
            }

            byte[] data = Convert.FromBase64String(stringtoDecode);
            string json = Encoding.UTF8.GetString(data, 0, data.Length);

            DataContractJsonSerializer serializer = new DataContractJsonSerializer(typeof(KeyCloakResponse));
            return (KeyCloakResponse)serializer.ReadObject(new MemoryStream(Encoding.UTF8.GetBytes(json)));
        }
    }

    public class KeyCloakResponse
    {
        public int exp { get; set; }
        public int iat { get; set; }
    }
}
