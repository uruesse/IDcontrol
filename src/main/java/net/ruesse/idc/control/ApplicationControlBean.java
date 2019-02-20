/*
 * Copyright 2019 ulrich.
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

import java.io.Serializable;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author ulrich
 */
@ApplicationScoped
@ManagedBean
public class ApplicationControlBean implements Serializable {

    private boolean isDemo;

    public ApplicationControlBean() {
        System.out.println("Im Konstruktor ApplicationControlBean");
        isDemo = true;
    }

    public boolean isIsDemo() {
        System.out.println("In isIsDemo ApplicationControlBean");
        return isDemo;
    }

    public void setIsDemo(boolean isDemo) {
        System.out.println("In setIsDemo ApplicationControlBean");
        this.isDemo = isDemo;
    }

    public String ToggleIsDemo() {
        System.out.println("In toggleIsDemo ApplicationControlBean");
        this.isDemo = !this.isDemo;
        return "";
    }
}