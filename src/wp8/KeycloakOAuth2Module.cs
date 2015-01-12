using Newtonsoft.Json;
using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
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
            var time = Convert.ToInt32(decoded["exp"]) -Convert.ToInt32(decoded["iat"]);
            session.refreshTokenExpirationDate = DateTime.Now.AddSeconds(time);
            return session;
        }

        public dynamic DecodeToken(string token)
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

            return JsonConvert.DeserializeObject<dynamic>(json);
        }
    }
}
