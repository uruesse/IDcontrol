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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Ulrich Rüße <ulrich@ruesse.net>
 */
@FacesConverter("memberConverter")
public class MemberConverter implements Converter {

    private final static Logger LOGGER = Logger.getLogger(MemberConverter.class.getName());

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() == 13) {
            LOGGER.log(Level.INFO, "String: " + value);

            long l = Long.parseLong(value);
            if (l > 0) {
                MemberService service = (MemberService) fc.getExternalContext().getApplicationMap().get("memberService");
                return service.findMemberByMglnr(value);
            }

        }
        return null;
        /*
        if (value != null && value.trim().length() > 0) {
            LOGGER.log(Level.INFO, "String: " + value);

            try {
                MemberService service = (MemberService) fc.getExternalContext().getApplicationMap().get("memberService");
                //int i = Integer.parseInt(value);
                LOGGER.log(Level.INFO, "String: " + value + " " + service.getMembers().size());
                //return service.getMembers().get(0);
                return service.getMembers().get(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Einlesefehler", "Kein Mitglied."));
            }
        } else {
            return null;
        }
         */
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object != null) {
            return String.valueOf(((Member) object).getMglnr());
        } else {
            return null;
        }
    }

}
