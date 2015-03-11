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

import android.app.Activity;
import android.content.Context;

/**
 * R replacement for PhoneGap Build.
 *
 * ([^.\w])R\.(\w+)\.(\w+)
 * $1fakeR("$2", "$3")
 *
 * @author Maciej Nux Jaros
 */
public class FakeR {
    private Context context;
    private String packageName;

    public FakeR(Activity activity) {
        context = activity.getApplicationContext();
        packageName = context.getPackageName();
    }

    public FakeR(Context context) {
        this.context = context;
        packageName = context.getPackageName();
    }

    public int getId(String key) {
        return context.getResources().getIdentifier(key, "id", packageName);
    }
  
    public int getId(String group, String key) {
        return context.getResources().getIdentifier(key, group, packageName);
    }

    public static int getId(Context context, String group, String key) {
        return context.getResources().getIdentifier(key, group, context.getPackageName());
    }
}