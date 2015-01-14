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
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace AeroGear.OAuth2
{
    public interface SessionRepositry
    {
        Task Save(string accessToken, string refreshToken, string accessTokenExpiration, string refreshTokenExpiration);
        Task Save(Session session);

        Task<Session> Read(string accountId);
    }

    [DataContract]
    public class Session
    {
        [DataMember]
        public string accountId { get; set; }
        [DataMember(Name = "access_token")]
        public string accessToken { get; set; }
        [DataMember]
        public DateTime accessTokenExpirationDate { get; set; }
        [DataMember]
        public DateTime refreshTokenExpirationDate { get; set; }

        [DataMember(Name = "expires_in")]
        public int accessTokenExpiration
        {
            get
            {
                return -1;
            }
            set
            {
                if (value != -1)
                {
                    accessTokenExpirationDate = DateTime.Now.AddSeconds(value);
                }
            }
        }

        [DataMember(Name = "refresh_token")]
        public string refreshToken { get; set; }

        public bool TokenIsNotExpired()
        {
            return DateTime.Now < accessTokenExpirationDate;
        }

        public bool RefreshTokenIsNotExpired()
        {
            return DateTime.Now < refreshTokenExpirationDate;
        }
    }
}
