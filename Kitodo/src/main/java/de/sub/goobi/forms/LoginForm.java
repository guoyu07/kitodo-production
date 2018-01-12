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

import de.sub.goobi.config.ConfigCore;
import de.sub.goobi.helper.Helper;
import de.sub.goobi.helper.ldap.Ldap;
import de.sub.goobi.metadaten.MetadatenSperrung;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitodo.api.filemanagement.filters.FileNameEndsWithFilter;
import org.kitodo.data.database.beans.Authorization;
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
                Ldap myLdap = new Ldap();
                myLdap.changeUserPassword(this.myBenutzer, this.passwortAendernAlt, this.passwortAendernNeu1);
                User temp = serviceManager.getUserService().getById(this.myBenutzer.getId());
                temp.setPassword(passwordEncoder.encrypt(this.passwortAendernNeu1));
                serviceManager.getUserService().save(temp);
                this.myBenutzer = temp;
                Helper.setMeldung(Helper.getTranslation("passwortGeaendert"));
            } catch (DAOException e) {
                Helper.setFehlerMeldung("could not save", e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                Helper.setFehlerMeldung("ldap errror", e.getMessage());
            } catch (DataException e) {
                Helper.setFehlerMeldung("could not insert to index", e.getMessage());
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
            Helper.setMeldung(null, "", Helper.getTranslation("configurationChanged"));
        } catch (DataException e) {
            Helper.setFehlerMeldung("could not insert to index", e.getMessage());
        }
        return null;
    }

    private void AlteBilderAufraeumen() throws IOException {
        /* Pages-Verzeichnis mit den temporären Images ermitteln */
        URI path = ConfigCore.getTempImagesPathAsCompleteDirectory();

        /* Verzeichnis einlesen */
        FilenameFilter filter = new FileNameEndsWithFilter(".png");
        ArrayList<URI> uris = serviceManager.getFileService().getSubUris(filter, path);

        /* alle Dateien durchlaufen und die alten löschen */
        for (URI uri : uris) {
            URI file = path.resolve(uri);
            if ((System.currentTimeMillis() - new File(file).lastModified()) > 7200000) {
                serviceManager.getFileService().delete(file);
            }
        }
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
                User newUser = serviceManager.getUserService().getAuthenticatedUser();
                if (newUser != null) {
                    myBenutzer = new User(newUser);
                }
                return this.myBenutzer;
            } catch (DAOException e) {
                Helper.setFehlerMeldung(e);
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
                if (userGroup.getAuthorizations().size() > 0) {
                    for (Authorization authorization : userGroup.getAuthorizations()) {
                        if (authorization.getTitle().equals("admin")) {
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
     * The function getUserHomeDir() returns the home directory of the currently
     * logged in user, if any, or the empty string otherwise.
     *
     * @return the home directory of the current user.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public static URI getCurrentUserHomeDir() throws IOException {
        URI result = null;
        ServiceManager serviceManager = new ServiceManager();
        LoginForm loginForm = (LoginForm) Helper.getManagedBeanValue("#{LoginForm}");
        if (loginForm != null) {
            result = serviceManager.getUserService().getHomeDirectory(loginForm.getMyBenutzer());
        }
        return result;
    }
}
