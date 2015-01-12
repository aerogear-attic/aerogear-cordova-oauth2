using System;
using System.IO;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Threading.Tasks;
using Windows.Security.Cryptography;
using Windows.Security.Cryptography.DataProtection;
using Windows.Storage;
using Windows.Storage.Streams;

namespace AeroGear.OAuth2
{
    public class TrustedSessionRepository : SessionRepositry
    {
        private const BinaryStringEncoding ENCODING = BinaryStringEncoding.Utf8;
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

            var text = await FileIO.ReadBufferAsync(file);

            DataProtectionProvider Provider = new DataProtectionProvider();
            IBuffer buffUnprotected = await Provider.UnprotectAsync(text);

            return CryptographicBuffer.ConvertBinaryToString(ENCODING, buffUnprotected);
        }

        public async Task<IStorageFile> SaveAccessToken(string token, string name)
        {
            DataProtectionProvider Provider = new DataProtectionProvider("LOCAL=user");
            IBuffer buffMsg = CryptographicBuffer.ConvertStringToBinary(token, ENCODING);
            IBuffer buffProtected = await Provider.ProtectAsync(buffMsg);

            StorageFolder local = Windows.Storage.ApplicationData.Current.LocalFolder;
            var file = await local.CreateFileAsync(name + ".txt", CreationCollisionOption.ReplaceExisting);
            await FileIO.WriteBufferAsync(file, buffProtected);
            return file;
        }
    }
}
