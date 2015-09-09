# AeroGear OAuth2 Cordova
The plugin handles OAuth2 *authorization code grant* in a secure manner. The OAuth2 dance uses external browser approach, OAuth2 access and refresh tokens are stored securely encrypted in your native device. Supports iOS, android and windows phone platforms.

The plugin wraps the following libs:

* [aerogear-ios-oauth2](https://github.com/aerogear/aerogear-ios-oauth2)
* [aerogear-android-authz](https://github.com/aerogear/aerogear-android-authz)

If you want to see the plugin in action please refer to [aerogear-cordova-cookbook]() Shoot'nShare demo app or/ad just follow the Getting started guide to create your own.

|                 | Project Info  |
| --------------- | ------------- |
| License:        | Apache License, Version 2.0  |
| Build:          | Cordova Plugin  |
| Documentation:  | https://aerogear.org/docs/specs/aerogear-cordova/  |
| Issue tracker:  | https://issues.jboss.org/browse/AGCORDOVA  |
| Mailing lists:  | [aerogear-users](http://aerogear-users.1116366.n5.nabble.com/) ([subscribe](https://lists.jboss.org/mailman/listinfo/aerogear-users))  |
|                 | [aerogear-dev](http://aerogear-dev.1069024.n5.nabble.com/) ([subscribe](https://lists.jboss.org/mailman/listinfo/aerogear-dev))  |


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

Install the aerogear-oauth2-cordova plugin by executing:

	$ cordova plugin add aerogear-cordova-oauth2

### Sample example

In `wwww/js/index.js` file, to start the OAuth2 dance as soon as `onDeviceReady` event is fired, add the following snippet:

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

### Google Play Services
On Android you can use Google Play Services to retrieve an Oauth2 token using on of the device's authorized accounts.
To make the Google Play Services available to your application, be sure to add the `com.google.playservices` cordova plugin to your project.
Then request an Oauth2 token using Google Play Services as in this example:
```javascript
   oauth2.requestAccessUsingPlayServices({
        scopes: 'openid',
        accountTypes: 'com.google'
     })
     .then( function( accessToken ){
        ...
     })
     .catch( function( error ) {
        // an error happened
     });
   });
```

### Facebook iOS

For our OAuth2 on iOS plugin we use external browser approach, in order to re-enter the app you need to provide a URI schema. This is called the ```redirect_uri```. By convention Google uses your iOS bundle. Facebook uses as ```redirect_uri``` 2 letters ```fb``` followed by the ```client_id```. As the ```client_id``` is not in your config.xml, once the project is deployed you will have to go to [project_name]-info.plist and modify it to change fbYYY where YYY is your facebook ```client_id```.

## Xcode

When you need / want to build the project with Xcode you'll need to make [some changes to the project](xcode.md)

## Todo

This is a very early version:
- expose refreshToken, revokeToken

## Documentation

For more details about the current release, please consult [our documentation](https://aerogear.org/docs/specs/aerogear-cordova/).

## Development

If you would like to help develop AeroGear you can join our [developer's mailing list](https://lists.jboss.org/mailman/listinfo/aerogear-dev), join #aerogear on Freenode, or shout at us on Twitter @aerogears.

Also takes some time and skim the [contributor guide](http://aerogear.org/docs/guides/Contributing/)

## Questions?

Join our [user mailing list](https://lists.jboss.org/mailman/listinfo/aerogear-users) for any questions or help! We really hope you enjoy app development with AeroGear!

## Found a bug?

If you found a bug please create a ticket for us on [Jira](https://issues.jboss.org/browse/AGCORDOVA) with some steps to reproduce it.
