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

var OAuth2 = function () {};

OAuth2.prototype.add = function (settings) {
  cordova.exec(null, null, 'OAuth2Plugin', 'add', [settings]);
};

OAuth2.prototype.requestAccess = function (accountId) {
  var success, error;

  return new Promise(function (resolve, reject) {
    error = function (error) {
      reject({ error: error });
    };

    success = function (result) {
      resolve(result);
    };

    cordova.exec(success, error, 'OAuth2Plugin', 'requestAccess', [accountId]);
  });
}

module.exports = new OAuth2();