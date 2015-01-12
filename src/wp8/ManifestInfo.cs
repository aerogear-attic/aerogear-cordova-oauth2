using System;
using System.Threading.Tasks;
using System.Xml;
using System.Xml.Linq;
using System.Linq;
using Windows.ApplicationModel;
using Windows.Storage;
using System.Collections.Generic;
using System.IO;

namespace AeroGear.OAuth2
{
    public class ManifestInfo
    {
        private XDocument document;
        private XNamespace xname;
        private static ManifestInfo instance;

        protected ManifestInfo() { }

        private async static Task<ManifestInfo> GetInstance()
        {
            if (instance == null)
            {
                instance = new ManifestInfo();
                await instance.init();
            }
            return instance;
        }

        private async Task init()
        {
            StorageFile file = await Package.Current.InstalledLocation.GetFileAsync("AppxManifest.xml");
            var stream = new StreamReader((await file.OpenAsync(FileAccessMode.Read)).AsStream());
            string manifestXml = await stream.ReadToEndAsync();
            document = XDocument.Parse(manifestXml);
            xname = XNamespace.Get("http://schemas.microsoft.com/appx/2010/manifest");
        }

        public async static Task<string> GetProtocol()
        {
            var instance = await GetInstance();

            var attribute = (from node in instance.document.Descendants(instance.xname + "Extension")
                             where (string)node.Attribute("Category") == "windows.protocol"
                             select node.Element(instance.xname + "Protocol").Attribute("Name")).Single();

            return attribute.Value;
        }
    }
}
