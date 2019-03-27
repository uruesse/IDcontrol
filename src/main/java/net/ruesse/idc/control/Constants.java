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

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
public class Constants {

    public static final String PERSISTENCE_UNIT_NAME = "net.ruesse.IDControl.PU";

    // später in eine Properties Datei bringen
    public static final String WORKING_DIR = "/Users/ulrich/Documents/Entwicklung/IDControlFiles";
    public static final String DIR_SEP = "/";

    public static final String LOGO_DIR = WORKING_DIR + DIR_SEP + "Logo" + DIR_SEP;
    public static final String REPORTTEMPLATES_DIR = WORKING_DIR + DIR_SEP + "Reporttemplates" + DIR_SEP;
    public static final String REPORTS_DIR = WORKING_DIR + DIR_SEP + "Reports" + DIR_SEP;
    public static final String TEMP_DIR = WORKING_DIR + DIR_SEP + "Tmp" + DIR_SEP;

    public static final String REPORT_SRC = ".jrxml";
    public static final String REPORT_DST = ".jasper";
}
