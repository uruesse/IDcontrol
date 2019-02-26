/*
 * Copyright 2019 Ulrich Rüße <ulrich@ruesse.net>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ruesse.idc.ressources;

import java.util.ResourceBundle;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
public class MsgBundle {

    private static final ResourceBundle MSG = ResourceBundle.getBundle("net/ruesse/idc/ressources/message");

    public static String getMessage(String strVar) {
        String retString;

        try {
            retString = MSG.getString(strVar);
        } catch (java.util.MissingResourceException e) {
            retString = "#{"+strVar+"}";
        }
        return retString;
    }

}
