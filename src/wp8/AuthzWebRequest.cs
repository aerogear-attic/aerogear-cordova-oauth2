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
using System.Net;
using System.Threading.Tasks;

namespace AeroGear.OAuth2
{
    public class AuthzWebRequest : WebRequest
    {
        private WebRequest webRequest;

        public AuthzWebRequest(WebRequest request)
        {
            this.webRequest = request;
        }

        public AuthzWebRequest(WebRequest request, AuthzModule authzModule) : this(request)
        {
            this.authzModule = authzModule;
        }

        private AuthzModule authzModule;

        public new static WebRequest Create(string requestUriString)
        {
            var request = WebRequest.Create(requestUriString);
            return new AuthzWebRequest(request);
        }

        public static WebRequest Create(string requestUriString, AuthzModule authzModule)
        {
            var request = WebRequest.Create(requestUriString);
            return new AuthzWebRequest(request, authzModule);
        }

        public new static WebRequest Create(Uri requestUri)
        {
            var webRequest = WebRequest.Create(requestUri);
            return new AuthzWebRequest(webRequest);
        }

        public static WebRequest Create(Uri requestUri, AuthzModule authzModule)
        {
            var webRequest = WebRequest.Create(requestUri);
            return new AuthzWebRequest(webRequest, authzModule);
        }

        public override void Abort()
        {
            webRequest.Abort();
        }

        public override IAsyncResult BeginGetRequestStream(AsyncCallback callback, object state)
        {
            if (authzModule != null)
            {
                var field = authzModule.AuthorizationFields();
                webRequest.Headers[field.Item1] = field.Item2;
            }
            return webRequest.BeginGetRequestStream(callback, state);
        }

        public override IAsyncResult BeginGetResponse(AsyncCallback callback, object state)
        {
            return webRequest.BeginGetResponse(callback, state);
        }

        public override string ContentType
        {
            get
            {
                return webRequest.ContentType;
            }
            set
            {
                webRequest.ContentType = value;
            }
        }

        public override System.IO.Stream EndGetRequestStream(IAsyncResult asyncResult)
        {
            return webRequest.EndGetRequestStream(asyncResult);
        }

        public override WebResponse EndGetResponse(IAsyncResult asyncResult)
        {
            return webRequest.EndGetResponse(asyncResult);
        }

        public override WebHeaderCollection Headers
        {
            get
            {
                return webRequest.Headers;
            }
            set
            {
                webRequest.Headers = value;
            }
        }

        public override string Method
        {
            get
            {
                return webRequest.Method;
            }
            set
            {
                webRequest.Method = value;
            }
        }

        public override Uri RequestUri
        {
            get { return webRequest.RequestUri; }
        }
    }
}
