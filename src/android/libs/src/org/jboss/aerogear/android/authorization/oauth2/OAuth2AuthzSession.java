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

import android.os.Parcel;
import android.os.Parcelable;
import org.jboss.aerogear.android.core.RecordId;

import java.util.Date;

/**
 * This is a wrapper for various bits of authorization metadata.
 * 
 * For details of the various fields, see the Oauth spec.
 */
public class OAuth2AuthzSession implements Parcelable {

    @RecordId
    private String accountId = "";

    private String clientId = "";
    private String accessToken = "";
    private String authorizationCode = "";
    private String refreshToken = "";
    private long expires_on = 0;

    private OAuth2AuthzSession(Parcel in) {
        clientId = in.readString();
        accessToken = in.readString();
        authorizationCode = in.readString();
        refreshToken = in.readString();
        accountId = in.readString();
        expires_on = in.readLong();
    }

    public OAuth2AuthzSession() {
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpires_on() {
        return expires_on;
    }

    public void setExpires_on(long expires_on) {
        this.expires_on = expires_on;
    }

    /**
     * AccountId represents the ID of the account type used to fetch sessions
     * for the type
     * 
     * @return the current account type.
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * AccountId represents the ID of the account type used to fetch sessions
     * for the type
     * 
     * @param accountId an accountId
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || ((Object) this).getClass() != o.getClass())
            return false;

        OAuth2AuthzSession that = (OAuth2AuthzSession) o;

        if (expires_on != that.expires_on)
            return false;
        if (accessToken != null ? !accessToken.equals(that.accessToken) : that.accessToken != null)
            return false;
        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null)
            return false;
        if (authorizationCode != null ? !authorizationCode.equals(that.authorizationCode) : that.authorizationCode != null)
            return false;
        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null)
            return false;
        if (refreshToken != null ? !refreshToken.equals(that.refreshToken) : that.refreshToken != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = accountId != null ? accountId.hashCode() : 0;
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (accessToken != null ? accessToken.hashCode() : 0);
        result = 31 * result + (authorizationCode != null ? authorizationCode.hashCode() : 0);
        result = 31 * result + (refreshToken != null ? refreshToken.hashCode() : 0);
        result = 31 * result + (int) (expires_on ^ (expires_on >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "AuthzSession {" + "cliendId=" + clientId + ", accessToken=" + accessToken
                + ", authorizationCode=" + authorizationCode + ", refreshToken=" + refreshToken
                + ", accountId=" + accountId + ", expires_on=" + expires_on + "}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(clientId);
        dest.writeString(accessToken);
        dest.writeString(authorizationCode);
        dest.writeString(refreshToken);
        dest.writeString(accountId);
        dest.writeLong(expires_on);
    }

    public static final Parcelable.Creator<OAuth2AuthzSession> CREATOR = new Parcelable.Creator<OAuth2AuthzSession>() {
        @Override
        public OAuth2AuthzSession createFromParcel(Parcel in) {
            return new OAuth2AuthzSession(in);
        }

        @Override
        public OAuth2AuthzSession[] newArray(int size) {
            return new OAuth2AuthzSession[size];
        }

    };

    public boolean tokenIsNotExpired() {
        if (expires_on == 0) {
            return true;
        }
        return (expires_on > new Date().getTime());
    }

}
