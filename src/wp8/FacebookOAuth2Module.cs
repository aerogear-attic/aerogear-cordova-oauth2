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
