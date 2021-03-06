/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package de.sub.goobi.forms;

import de.sub.goobi.helper.Helper;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitodo.data.database.beans.Authority;
import org.kitodo.data.database.beans.User;
import org.kitodo.data.database.beans.UserGroup;
import org.kitodo.data.database.exceptions.DAOException;
import org.kitodo.data.exceptions.DataException;
import org.kitodo.security.SecurityPasswordEncoder;
import org.kitodo.services.ServiceManager;

@Named("LoginForm")
@SessionScoped
public class LoginForm implements Serializable {
    private static final long serialVersionUID = 7732045664713555233L;
    private String login;
    private String password;
    private User myBenutzer;
    private boolean schonEingeloggt = false;
    private String passwortAendernAlt;
    private String passwortAendernNeu1;
    private String passwortAendernNeu2;
    private SecurityPasswordEncoder passwordEncoder = new SecurityPasswordEncoder();
    private transient ServiceManager serviceManager = new ServiceManager();
    private static final Logger logger = LogManager.getLogger(LoginForm.class);
    private boolean firstVisit = true;

    /*
     * änderung des Passworts
     */

    /**
     * neues Passwort übernehmen.
     */
    public String PasswortAendernSpeichern() {
        /* ist das aktuelle Passwort korrekt angegeben ? */
        /* ist das neue Passwort beide Male gleich angegeben? */
        if (!this.passwortAendernNeu1.equals(this.passwortAendernNeu2)) {
            Helper.setFehlerMeldung(Helper.getTranslation("passwordsDontMatch"));
        } else {
            try {
                /* wenn alles korrekt, dann jetzt speichern */
                serviceManager.getLdapServerService().changeUserPassword(this.myBenutzer, this.passwortAendernNeu1);
                User temp = serviceManager.getUserService().getById(this.myBenutzer.getId());
                temp.setPassword(passwordEncoder.encrypt(this.passwortAendernNeu1));
                serviceManager.getUserService().save(temp);
                this.myBenutzer = temp;
                Helper.setMeldung(Helper.getTranslation("passwortGeaendert"));
            } catch (DAOException e) {
                Helper.setErrorMessage("could not save", logger, e);
            } catch (NoSuchAlgorithmException e) {
                Helper.setErrorMessage("ldap errror", logger, e);
            } catch (DataException e) {
                Helper.setErrorMessage("could not insert to index", logger, e);
            }
        }
        return null;
    }

    /**
     * Benutzerkonfiguration speichern.
     */
    public String BenutzerkonfigurationSpeichern() {
        try {
            serviceManager.getUserService().save(this.myBenutzer);
        } catch (DataException e) {
            Helper.setErrorMessage("could not insert to index", logger, e);
        }
        return null;
    }

    /*
     * Getter und Setter
     */

    public String getLogin() {
        return this.login;
    }

    /**
     * Set login.
     *
     * @param login
     *            String
     */
    public void setLogin(String login) {
        if (this.login != null && !this.login.equals(login)) {
            this.schonEingeloggt = false;
        }
        this.login = login;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets current authenticated User.
     *
     * @return
     *      The user object or null if no user is authenticated.
     */
    public User getMyBenutzer() {
        if (myBenutzer != null) {
            return this.myBenutzer;
        } else {
            try {
                this.myBenutzer = serviceManager.getUserService().getAuthenticatedUser();
                return this.myBenutzer;
            } catch (DAOException e) {
                Helper.setErrorMessage(e.getLocalizedMessage(), logger, e);
                return null;
            }
        }
    }

    public void setMyBenutzer(User myClass) {
        this.myBenutzer = myClass;
    }

    /**
     * Get max rights.
     *
     * @return int
     */
    public int getMaximaleBerechtigung() {
        //TODO Only to keep compatibility to old frontend pages
        //TODO delete this methode when all new frontend is ready or security tags are replaced
        if (this.myBenutzer != null) {
            for (UserGroup userGroup : this.myBenutzer.getUserGroups()) {
                if (userGroup.getGlobalAuthorities().size() > 0) {
                    for (Authority authority : userGroup.getGlobalAuthorities()) {
                        if (authority.getTitle().equals("admin")) {
                            return 1; //Admin permission
                        }
                    }
                }
            }
            return 4; //User permission
        }
        return 0; //Anonymus permission

    }

    public String getPasswortAendernAlt() {
        return this.passwortAendernAlt;
    }

    public void setPasswortAendernAlt(String passwortAendernAlt) {
        this.passwortAendernAlt = passwortAendernAlt;
    }

    public String getPasswortAendernNeu1() {
        return this.passwortAendernNeu1;
    }

    public void setPasswortAendernNeu1(String passwortAendernNeu1) {
        this.passwortAendernNeu1 = passwortAendernNeu1;
    }

    public String getPasswortAendernNeu2() {
        return this.passwortAendernNeu2;
    }

    public void setPasswortAendernNeu2(String passwortAendernNeu2) {
        this.passwortAendernNeu2 = passwortAendernNeu2;
    }

    public boolean isSchonEingeloggt() {
        return this.schonEingeloggt;
    }

    /**
     * Checks and returns whether this is the first time the user visits a Kitodo
     * page in the current session or not. Makes use of the fact that "LoginForm" is
     * a SessionScoped bean.
     *
     * @return whether this is the users first visit to Kitodo during the current
     *         session or not
     */
    public boolean isFirstVisit() {
        boolean visit = firstVisit;
        if (firstVisit) {
            firstVisit = false;
        }
        return visit;
    }
}
