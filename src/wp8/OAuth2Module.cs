using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Http.Headers;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using Windows.ApplicationModel.Activation;
using Windows.Foundation.Collections;
using Windows.Security.Authentication.Web;

namespace AeroGear.OAuth2
{
    public class OAuth2Module : AuthzModule
    {
        private const string PARAM_TEMPLATE = @"?scope={0}&redirect_uri={1}&client_id={2}&response_type=code";

        protected SessionRepositry repository = new TrustedSessionRepository();
        protected Session session;

        public Config config { get; private set; }

        public async static Task<OAuth2Module> Create(Config config)
        {
            OAuth2Module module = new OAuth2Module();
            await module.init(config);
            return module; 
        }

        public async static Task<OAuth2Module> Create(Config config, SessionRepositry repository)
        {
            OAuth2Module module = new OAuth2Module();
            module.repository = repository;
            await module.init(config);
            return module;
        }

        public async Task init(Config config)
        {
            this.config = config;
            try
            {
                session = await repository.Read(config.accountId);
            }
            catch (IOException e)
            {
                session = new Session() { accountId = config.accountId };
            }
        }

        public async Task<bool> RequestAccessAndContinue()
        {
            if (session.accessToken == null || !session.TokenIsNotExpired())
            {
                if (session.refreshToken != null && session.RefreshTokenIsNotExpired())
                {
                    await RefreshAccessToken();
                    return true;
                }
                else
                {
                    RequestAuthorizationCode();
                    return false;
                }
            }
            else
            {
                return true;
            }
        }

        public virtual void RequestAuthorizationCode()
        {
            var param = string.Format(PARAM_TEMPLATE, config.scope, Uri.EscapeDataString(config.redirectURL), Uri.EscapeDataString(config.clientId));
            var uri = new Uri(config.baseURL + config.authzEndpoint).AbsoluteUri + param;

            var values = new ValueSet() { { "name", config.accountId } };
            WebAuthenticationBroker.AuthenticateAndContinue(new Uri(uri), new Uri(config.redirectURL), values, WebAuthenticationOptions.None);
        }

        public Tuple<string, string> AuthorizationFields()
        {
            if (session.accessToken != null)
            {
                return Tuple.Create("Authorization", "Bearer " + session.accessToken);
            }
            return null;
        }

        public AuthenticationHeaderValue AuthenticationHeaderValue()
        {
            return new AuthenticationHeaderValue("Bearer", session.accessToken);
        }

        protected virtual async Task RefreshAccessToken()
        {
            var parameters = new Dictionary<string, string>() { { "refresh_token", session.refreshToken }, { "client_id", config.clientId }, { "grant_type", "refresh_token" } };
            if (config.clientSecret != null)
            {
                parameters["client_secret"] = config.clientSecret;
            }
            await UpdateToken(parameters);
        }

        public async Task ExtractCode(WebAuthenticationBrokerContinuationEventArgs args)
        {
            if (args.WebAuthenticationResult.ResponseStatus == WebAuthenticationStatus.Success) 
            {
                IDictionary<string, string> queryParams = ParseQueryString(new Uri(args.WebAuthenticationResult.ResponseData).Query);
                if (queryParams.ContainsKey("code"))
                {
                    await ExchangeAuthorizationCodeForAccessToken(queryParams["code"]);
                }
                else
                {
                    throw new Exception("no code parameter found in redirect");
                }
            }
            else
            {
                throw new Exception(string.Format("user cancelled the authorization status: '{0}': details: {1}", args.WebAuthenticationResult.ResponseStatus, args.WebAuthenticationResult.ResponseErrorDetail));
            }
        }

        private async Task ExchangeAuthorizationCodeForAccessToken(string code)
        {
            var parameters = new Dictionary<string, string>() { { "grant_type", "authorization_code" }, { "code", code }, { "client_id", config.clientId }, { "redirect_uri", config.redirectURL } };
            if (config.clientSecret != null)
            {
                parameters["client_secret"] = config.clientSecret;
            }
            await UpdateToken(parameters);
        }

        private async Task UpdateToken(Dictionary<string, string> parameters)
        {
            var request = WebRequest.Create(config.baseURL + config.accessTokenEndpoint);
            request.Method = "POST";
            request.ContentType = "application/x-www-form-urlencoded";

            using (var postStream = await Task<Stream>.Factory.FromAsync(request.BeginGetRequestStream, request.EndGetRequestStream, request))
            {
                foreach (KeyValuePair<string, string> entry in parameters)
                {
                    var bytes = Encoding.UTF8.GetBytes(entry.Key + "=" + WebUtility.UrlEncode(entry.Value) + "&");
                    postStream.Write(bytes, 0, bytes.Length);
                }
            }

            using (var response = await Task<WebResponse>.Factory.FromAsync(request.BeginGetResponse, request.EndGetResponse, request))
            {
                session = await ParseResponse(response.GetResponseStream());
            }
        }

        protected virtual async Task<Session> ParseResponse(Stream respondeStream)
        {
            using (var stream = respondeStream)
            {
                DataContractJsonSerializer serializer = new DataContractJsonSerializer(typeof(Session));
                var session = (Session)serializer.ReadObject(stream);
                await repository.Save(session);
                return session;
            }
        }

        protected IDictionary<string, string> ParseQueryString(string query)
        {
            Dictionary<string, string> result = new Dictionary<string, string>();

            query = query.Substring(query.IndexOf('?') + 1);

            foreach (string valuePair in Regex.Split(query, "&"))
            {
                string[] pair = Regex.Split(valuePair, "=");
                result.Add(WebUtility.UrlDecode(pair[0]), WebUtility.UrlDecode(pair[1]));
            }

            return result;
        }
    }
}
