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
