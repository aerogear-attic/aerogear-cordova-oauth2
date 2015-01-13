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
        private static volatile ManifestInfo instance;
        private static object syncRoot = new Object();

        protected ManifestInfo() { }

        private static ManifestInfo GetInstance()
        {
            if (instance == null)
            {
                lock (syncRoot)
                {
                    instance = new ManifestInfo();
                    instance.document = instance.GetDocument();
                }
            }
            return instance;
        }

        private XDocument GetDocument()
        {
            return XDocument.Load("WMAppManifest.xml");
        }

        public static string GetProtocol()
        {
            var instance = GetInstance();

            var attribute = (from node in instance.document.Descendants("Extensions")
                             select node.Element("Protocol").Attribute("Name")).Single();

            return attribute.Value;
        }
    }
}
