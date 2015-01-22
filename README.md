# AeroGear OAuth2 Cordova
The plugin handles OAuth2 *authorization code grant* in a secure manner. The OAuth2 dance uses external browser approach, OAuth2 access and refresh tokens are stored securely encrypted in your native device. Supports iOS, android and windows phone platforms.

The plugin wraps the following libs:

* [aerogear-ios-oauth2](https://github.com/aerogear/aerogear-ios-oauth2)
* [aerogear-android-authz](https://github.com/aerogear/aerogear-android-authz)

If you want to see the plugin in action please refer to [aerogear-cordova-cookbook]() Shoot'nShare demo app or/ad just follow the Getting started guide to create your own.

## Getting Started

The Cordova command line tooling is based on node.js so first you’ll need to install node then you can install Cordova by executing:

	$ npm install -g cordova

To deploy on iOS you need to install the ios-deploy package as well

	$ npm install -g ios-deploy

### Create the App

Create a new app by executing:

	$ cordova create <project-name> [app-id] [app-name]

### Add platform(s)

Specify a set of target platforms by executing:

	$ cordova platform add <platform>

The available platform values are ios and android.

### Install the plugin

Install the aerogear-oauth2-plugin plugin by executing:

	$ cordova plugin add https://github.com/edewit/aerogear-oauth2-plugin.git

### Sample example

In `wwww/js/index.js` file, to start the OAuth2 dance as soon as `onDeviceReady` event is fired, add the foloowing snippet:

```javascript
  onDeviceReady: function () {
    app.receivedEvent('deviceready');
    oauth2.addGoogle({
      name: 'gplus',
      settings: {
        clientId: "617285928032-nnkcrot1827fmd738pug6clbqlgosffs.apps.googleusercontent.com",
        scopes: 'https://www.googleapis.com/auth/drive'
      }
    });
    
    oauth2.addKeycloak({
      name: 'keycloak',
      settings: {
        base: 'http://192.168.1.15:8080/auth',
        clientId: 'shoot-third-party',
        realm: "shoot-realm"
      }
    });
    
    oauth2.addFacebook({
      name: 'facebook',
      settings: {
        clientId: '1511044619160050',
        clientSecret: '3b08052d3d96e2120f2c53a36eebd02f',
        scopes: 'photo_upload, publish_actions'
      }
    });    

    oauth2.gplus.requestAccess()
      .then(function (token) {
        console.log(token);
        // add token to the http header on futher http requests:
        // 'Authorization': 'Bearer ' + token
      }, function (err) {
        alert(err.error);
      });
  },
```

### Facebook iOS

For our OAuth2 on iOS plugin we use external browser approach, in order to re-enter the app you need to provide a URI schema. This is called the ```redirect_uri```. By convention Google uses your iOS bundle. Facebook uses as ```redirect_uri``` 2 letters ```fb``` followed by the ```client_id```. As the ```client_id``` is not in your config.xml, once the project is deployed you will have to go to [project_name]-info.plist and modify it to change fbYYY where YYY is your facebook ```client_id```.

### Workaround for iOS

Our iOS version is using a Swift library. Cordova is not yet fully supporting Swift, when running cordova plugin add command, you will get an usefull information telling you to open xcode and do the followinf 3 tweaks:

1. change `deployment target` to 7.0 or above
![deployment target](ios_step_1.png "deployment target")

2. add `[Project Name]/Plugins/org.jboss.aerogear.cordova.oauth2/Bridging-Header.h` to Objective-c Bridging Header    under the Swift Compiler - Code Generation options
![bridging header](ios_step_2.png "bridging header")

3. set LD_RUNPATH_SEARCH_PATHS to "$(inherited) @executable_path/Frameworks"
![search path](ios_step_3.png "search path")

## Todo

This is a very early version:
- remove Swift hack on xcodeproject
- remove Android hack on gradle
- expose refreshToken, revokeToken