using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using AeroGear.OAuth2;

namespace org.jboss.aerogear.cordova.Plugins.org.jboss.aerogear.cordova.oauth2
{
    public partial class P : PhoneApplicationPage
    {
        public P()
        {
            InitializeComponent();
        }

        protected async override void OnNavigatedTo(System.Windows.Navigation.NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);

            string code = null;
            if (NavigationContext.QueryString.TryGetValue("code", out code))
            {
                var module = AccountManager.GetAccountByName(NavigationContext.QueryString["module"]);
                await module.ExchangeAuthorizationCodeForAccessToken(code);
            }

            NavigationService.Navigate(new Uri("/MainPage.xaml", UriKind.Relative));
        }
    }
}