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
package net.ruesse.idc.control;

import java.util.logging.Logger;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
public class Constants {

    private static final Logger LOGGER = Logger.getLogger(Constants.class.getName());

    public static final String PERSISTENCE_UNIT_NAME = "net.ruesse.IDControl.PU";

    public static final String REPORT_SRC = ".jrxml";
    public static final String REPORT_DST = ".jasper";

    public static String getPERSISTENCE_UNIT_NAME() {
        return PERSISTENCE_UNIT_NAME;
    }
}
