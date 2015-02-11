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
using System.IO;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Threading.Tasks;
using System.Security.Cryptography;
using Windows.Storage;
using Windows.Storage.Streams;

namespace AeroGear.OAuth2
{
    public class TrustedSessionRepository : SessionRepositry
    {
        byte[] saltBytes = Encoding.Unicode.GetBytes("897752749");
        private DataContractJsonSerializer serializer = new DataContractJsonSerializer(typeof(Session));

        public async Task Save(string accessToken, string refreshToken, string accessTokenExpiration, string refreshTokenExpiration)
        {
            await Save(new Session() {
                accessToken = accessToken, 
                refreshToken = refreshToken, 
                accessTokenExpirationDate = DateTime.Now.AddSeconds(Double.Parse(accessTokenExpiration)),
                refreshTokenExpirationDate = DateTime.Now.AddSeconds(Double.Parse(refreshTokenExpiration)) 
            });
        }

        public async Task Save(Session session)
        {
            using (MemoryStream ms = new MemoryStream())
            {
                serializer.WriteObject(ms, session);
                var bytes = ms.ToArray();
                await SaveAccessToken(Encoding.UTF8.GetString(bytes, 0, bytes.Length), session.accountId);
            }
        }

        public async Task<Session> Read(string accountId)
        {
            string json = await ReadAccessToken(accountId);
            using (MemoryStream ms = new MemoryStream(Encoding.UTF8.GetBytes(json)))
            {
                return (Session) serializer.ReadObject(ms);
            }
        }

        public async Task<string> ReadAccessToken(string name)
        {
            StorageFolder local = Windows.Storage.ApplicationData.Current.LocalFolder;
            var file = await local.GetFileAsync(name + ".txt");

            var text = await ReadFileContentsAsync(file);
            byte[] result = ProtectedData.Unprotect(Encoding.UTF8.GetBytes(text), saltBytes);
            return Encoding.UTF8.GetString(result, 0, result.Length);
        }

        public async Task<IStorageFile> SaveAccessToken(string token, string name)
        {
            byte[] protectedToken = ProtectedData.Protect(Encoding.UTF8.GetBytes(token), saltBytes);

            StorageFolder local = Windows.Storage.ApplicationData.Current.LocalFolder;
            var file = await local.CreateFileAsync(name + ".txt", CreationCollisionOption.ReplaceExisting);
            await WriteDataToFileAsync(file, protectedToken);
            return file;
        }

        public async Task WriteDataToFileAsync(StorageFile file, byte[] data)
        {
            using (var s = await file.OpenStreamForWriteAsync())
            {
                await s.WriteAsync(data, 0, data.Length);
            }
        }

        public async Task<string> ReadFileContentsAsync(StorageFile file)
        {
            try
            {
                using (var stream = new StreamReader((await file.OpenAsync(FileAccessMode.Read)).AsStream()))
                {
                    return await stream.ReadToEndAsync();
                }
            }
            catch (Exception)
            {
                return string.Empty;
            }
        }
    }
}
