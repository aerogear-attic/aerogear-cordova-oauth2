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
