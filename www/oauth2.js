/*
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var exec = require("cordova/exec");

/**
  The global oauth2 object is the entry point for all methods
  @class
  @returns {object} oauth2 - The oauth2 api
*/
var OAuth2 = function () {};

function enrich(that, object) {
    that[object.name] = {
    requestAccess: function () {
      return oauth2.requestAccess(object.name);
    }
  }
  object.settings['accountId'] = object.name;
}

/**
  The OAuth2 adapter is the default type used when creating a new authorization module.
  This constructor is instantiated when the "add()" method is called
  @status Experimental
  @param {String} name - the name used to reference this particular authz module
  @param {Object} settings={} - the settings to be passed to the adapter
  @param {String} settings.clientId - the client id/ app Id of the protected service
  @param {String} settings.base - base url for all endpoints e.g. "https://accounts.google.com"
  @param {String} settings.accessTokenEndpoint - url to get the token
  @param {String} settings.refreshTokenEndpoint - url to refresh the token
  @param {String} settings.revokeTokenEndpoint - url to revoke the token
  @param {String} settings.redirectURL - the URL to redirect to
  @param {String} settings.authEndpoint - the endpoint for authorization
  @param {String} [settings.validationEndpoint] - the optional endpoint to validate your token.  Not in the Spec, but recommend for use with Google's API's
  @param {String} settings.scopes - a space separated list of "scopes" or things you want to access
  @returns {void}
  @example
  oauth2.add({
      name: "coolThing",
      settings: {
          clientId: "12345",
          redirectURL: "http://localhost:3000/redirector.html",
          authEndpoint: "http://localhost:3000/v1/authz",
          scopes: "userinfo coolstuff"
      }
  });
 */
OAuth2.prototype.add = function (object) {
  enrich(this, object);
  cordova.exec(null, null, 'OAuth2Plugin', 'add', [object.settings]);
};

/**
  Convenience function to add google as a provider
  @status Experimental
  @param {String} name - the name used to reference this particular authz module
  @param {Object} settings={} - the settings to be passed to the adapter
  @param {String} settings.clientId - the client id/ app Id of the protected service
  @param {String} settings.scopes - a space separated list of "scopes" or things you want to access
  @returns {void}
  @example
  oauth2.addGoogle({
      name: 'gplus',
      settings: {
        clientId: "617285928032-nnkcrot1827fmd738pug6clbqlgosffs.apps.googleusercontent.com",
        scopes: 'https://www.googleapis.com/auth/drive'
      }
    });
*/
OAuth2.prototype.addGoogle = function (object) {
  enrich(this, object);
  cordova.exec(null, null, 'OAuth2Plugin', 'addGoogle', [object.settings]);
};

/**
  Convenience function to add keycloak as a provider
  @status Experimental
  @param {String} name - the name used to reference this particular authz module
  @param {Object} settings={} - the settings to be passed to the adapter
  @param {String} settings.base - base url for all endpoints e.g. "https://keycloak:8080/auth"
  @param {String} settings.clientId - the client id/ app Id of the protected service
  @param {String} settings.realm - the keycloak realm 
  @returns {void}
  @example
  oauth2.addKeycloak({
      name: 'keycloak',
      settings: {
        base: '<keycloak server url e.g http(s)//host:port/context >,
        clientId: 'shoot-third-party',
        realm: "shoot-realm"
      }
    });  
*/
OAuth2.prototype.addKeycloak = function (object) {
  enrich(this, object);
  cordova.exec(null, null, 'OAuth2Plugin', 'addKeycloak', [object.settings]);
};

/**
  Convenience function to add Facebook as a provider
  @status Experimental
  @param {String} name - the name used to reference this particular authz module
  @param {Object} settings={} - the settings to be passed to the adapter
  @param {String} settings.clientId - the client id/ app Id of the protected service
  @param {String} settings.clientSecret - the client secret
  @param {String} settings.scopes - comma separated list of "scopes" you want access to
  @returns {void}
  @example
  oauth2.addFacebook({
    name: 'facebook',
    settings: {
      clientId: '1511044619160050',
      clientSecret: '3b08052d3d96e2120f2c53a36eebd02f',
      scopes: 'photo_upload, publish_actions'
    }
  });
*/
OAuth2.prototype.addFacebook = function (object) {
  enrich(this, object);
  cordova.exec(null, null, 'OAuth2Plugin', 'addFacebook', [object.settings]);
};

/**
  Request Access - If the client has no accessToken this will iniciate the oauth "dance", and return the accessToken. If an accessToken was already supplied this will be retuned immediately
  @returns {Object} The ES6 promise (accessToken as a response parameter; if an error is returned)
  @example
  oauth2.add({
      name: "coolThing",
      settings: {
          clientId: "12345",
          redirectURL: "http://localhost:3000/redirector.html",
          authEndpoint: "http://localhost:3000/v1/authz",
          scopes: "userinfo coolstuff"
      }
  });
  // Make the call.
  authz.services.coolThing.requestAccess()
      .then( function( accessToken ){
          ...
      })
      .catch( function( error ) {
          // an error happened
      });
  });
 */
OAuth2.prototype.requestAccess = function (accountId) {
  var success, error;

  return new Promise(function (resolve, reject) {
    error = function (error) {
      reject({
        error: error
      });
    };

    success = function (result) {
      resolve(result);
    };

    cordova.exec(success, error, 'OAuth2Plugin', 'requestAccess', [accountId]);
  });
}

/**
  Use Google Play Services to request an access token using one of the device's authorized accounts.

  @status Experimental
  @param {String} settings.scopes - comma separated list of "scopes" you want access to.  Defaults to the "https://www.googleapis.com/auth/plus.me" scope.
  @param {String} settings.accountTypes - space-separated list of account types used to filter the list of accounts. The access token will be requested for the account selected from this list. eg. "com.google"
  @returns {Object} The ES6 promise (accessToken as a response parameter; if an error is returned)
  @example
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
 */
OAuth2.prototype.requestAccessUsingPlayServices = function(scopes) {
    var success, error;

    return new Promise(function (resolve, reject) {
        error = function (error) {
            reject({
                error: error
            });
        };

        success = function (result) {
            resolve(result);
        };

        var args = scopes ? [scopes] : [''];
        cordova.exec(success, error, 'OAuth2Plugin', 'requestAccessUsingPlayServices', args);
    });
}

module.exports = new OAuth2();