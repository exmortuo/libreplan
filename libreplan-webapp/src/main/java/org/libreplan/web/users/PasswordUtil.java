/*
 * This file is part of LibrePlan
 *
 * Copyright (C) 2011 ComtecSF, S.L.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.libreplan.web.users;

import org.libreplan.business.common.Registry;
import org.libreplan.business.users.entities.User;
import org.libreplan.web.users.bootstrap.MandatoryUser;
import org.zkoss.zk.ui.util.Clients;

/**
 * A class which is used to encapsulate some common behaviour of passwords.
 *
 * @author Cristina Alvarino Perez <cristina.alvarino@comtecsf.es>
 * @author Ignacio Diaz Teijido <ignacio.diaz@comtecsf.es>
 */
public class PasswordUtil {

    public static void checkIfChangeDefaultPasswd(User user,
            String clearPassword) {
        if (user.getLoginName().equalsIgnoreCase(
                MandatoryUser.ADMIN.getLoginName())) {
            checkIfChangeDefaultPasswd(MandatoryUser.ADMIN, clearPassword);
            return;
        }
        if (user.getLoginName().equalsIgnoreCase(
                MandatoryUser.WSREADER.getLoginName())) {
            checkIfChangeDefaultPasswd(MandatoryUser.WSREADER, clearPassword);
            return;
        }
        if (user.getLoginName().equalsIgnoreCase(
                MandatoryUser.WSWRITER.getLoginName())) {
            checkIfChangeDefaultPasswd(MandatoryUser.WSWRITER, clearPassword);
            return;
        }
        if (user.getLoginName().equalsIgnoreCase(
                MandatoryUser.WSSUBCONTRACTING.getLoginName())) {
            checkIfChangeDefaultPasswd(MandatoryUser.WSSUBCONTRACTING,
                    clearPassword);
            return;
        }
        if (user.getLoginName().equalsIgnoreCase(
                MandatoryUser.MANAGER.getLoginName())) {
            checkIfChangeDefaultPasswd(MandatoryUser.MANAGER, clearPassword);
            return;
        }
        if (user.getLoginName().equalsIgnoreCase(
                MandatoryUser.HRESOURCES.getLoginName())) {
            checkIfChangeDefaultPasswd(MandatoryUser.HRESOURCES, clearPassword);
            return;
        }
        if (user.getLoginName().equalsIgnoreCase(
                MandatoryUser.OUTSOURCING.getLoginName())) {
            checkIfChangeDefaultPasswd(MandatoryUser.OUTSOURCING, clearPassword);
            return;
        }
        if (user.getLoginName().equalsIgnoreCase(
                MandatoryUser.REPORTS.getLoginName())) {
            checkIfChangeDefaultPasswd(MandatoryUser.REPORTS, clearPassword);
            return;
        }
    }

    private static void checkIfChangeDefaultPasswd(MandatoryUser user,
            String clearPassword) {
        boolean changedPasswd = true;
        if (clearPassword.isEmpty()
                || clearPassword.equals(user.getClearPassword())) {
            changedPasswd = false;
        }
        // save the field changedDefaultAdminPassword in configuration.
        Registry.getConfigurationDAO().saveChangedDefaultPassword(
                user.getLoginName(), changedPasswd);
    }

    /**
     * It calls a JavaScript method called
     * <b>showOrHideDefaultPasswordWarnings</b> defined in
     * "/libreplan-webapp/js/defaultPasswordWarnings.js" to show or hide the
     * default password warnings if the user has changed the password or has
     * been disabled
     */
    public static void showOrHideDefaultPasswordWarnings() {
        boolean adminNotDefaultPassword = MandatoryUser.ADMIN
                .hasChangedDefaultPasswordOrDisabled();
        boolean wsreaderNotDefaultPassword = MandatoryUser.WSREADER
                .hasChangedDefaultPasswordOrDisabled();
        boolean wswriterNotDefaultPassword = MandatoryUser.WSWRITER
                .hasChangedDefaultPasswordOrDisabled();
        boolean wssubcontractingNotDefaultPassword = MandatoryUser.WSSUBCONTRACTING
                .hasChangedDefaultPasswordOrDisabled();
        boolean managerNotDefaultPassword = MandatoryUser.MANAGER
                .hasChangedDefaultPasswordOrDisabled();
        boolean hresourcesNotDefaultPassword = MandatoryUser.HRESOURCES
                .hasChangedDefaultPasswordOrDisabled();
        boolean outsourcingNotDefaultPassword = MandatoryUser.OUTSOURCING
                .hasChangedDefaultPasswordOrDisabled();
        boolean reportsNotDefaultPassword = MandatoryUser.REPORTS
                .hasChangedDefaultPasswordOrDisabled();

        Clients.evalJavaScript("showOrHideDefaultPasswordWarnings("
                + adminNotDefaultPassword + ", "
                + wsreaderNotDefaultPassword + ", "
                + wswriterNotDefaultPassword + ", "
                + wssubcontractingNotDefaultPassword + ", "
                + managerNotDefaultPassword + ", "
                + hresourcesNotDefaultPassword + ", "
                + outsourcingNotDefaultPassword + ", "
                + reportsNotDefaultPassword + ");");
    }

}
