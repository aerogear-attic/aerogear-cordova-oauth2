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
package org.jboss.aerogear.android.authorization.oauth2;

/**
 * A wrapper for various exceptions which are defined in the OAuth2 spec.
 */
public class OAuth2AuthorizationException extends Exception {

    public enum Error {
        INVALID_REQUEST, INVALID_CLIENT, INVALID_GRANT, UNAUTHORIZED_CLIENT, UNSUPPORTED_GRANT_TYPE, INVALID_SCOPE, OTHER;

        public static Error getErrorEnum(String inError) {
            for (Error error : values()) {
                if (error.name().equalsIgnoreCase(inError)) {
                    return error;
                }
            }
            return OTHER;
        }

    };

    public final String error;
    public final Error type;

    public OAuth2AuthorizationException(String error) {
        super(error);
        this.error = error;
        type = Error.getErrorEnum(error);
    }

    /**
     * 
     * @return a string representaiton of the error returned.
     */
    public String getError() {
        return error;
    }

    /**
     * 
     * @return an enumerated type of error.
     */
    public Error getType() {
        return type;
    }

    @Override
    public String toString() {
        return "AuthorizationException{" + "error=" + error + '}';
    }

}
