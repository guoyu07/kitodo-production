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

import java.io.File;
import java.util.Objects;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitodo.data.database.beans.Ruleset;
import org.kitodo.data.database.exceptions.DAOException;
import org.kitodo.data.exceptions.DataException;
import org.kitodo.model.LazyDTOModel;
import org.kitodo.services.ServiceManager;

@Named("RulesetForm")
@SessionScoped
public class RulesetForm extends BasisForm {
    private static final long serialVersionUID = -445707928042517243L;
    private Ruleset ruleset;
    private transient ServiceManager serviceManager = new ServiceManager();
    private static final Logger logger = LogManager.getLogger(RulesetForm.class);

    @Inject
    @Named("ProjekteForm")
    private ProjekteForm projectForm;

    /**
     * Empty default constructor that also sets the LazyDTOModel instance of this
     * bean.
     */
    public RulesetForm() {
        super();
        super.setLazyDTOModel(new LazyDTOModel(serviceManager.getRulesetService()));
    }

    /**
     * Initialize new Ruleset.
     *
     * @return page
     */
    public String createNewRuleset() {
        this.ruleset = new Ruleset();
        return redirectToEdit();
    }

    /**
     * Save.
     *
     * @return page or empty String
     */
    public String saveRuleset() {
        try {
            if (hasValidRulesetFilePath(this.ruleset, ConfigCore.getParameter("RegelsaetzeVerzeichnis"))) {
                serviceManager.getRulesetService().save(this.ruleset);
                return redirectToList();
            } else {
                Helper.setFehlerMeldung("RulesetNotFound");
                return null;
            }
        } catch (DataException e) {
            Helper.setErrorMessage("fehlerNichtSpeicherbar", logger, e);
            return null;
        }
    }

    /**
     * Checks that ruleset file exists.
     *
     * @param ruleset
     *            ruleset
     * @param pathToRulesets
     *            path to ruleset
     * @return true if ruleset file exists
     */
    private boolean hasValidRulesetFilePath(Ruleset ruleset, String pathToRulesets) {
        File rulesetFile = new File(pathToRulesets + ruleset.getFile());
        return rulesetFile.exists();
    }

    /**
     * Remove.
     *
     * @return redirect link or empty String
     */
    public String removeRuleset() {
        try {
            if (hasAssignedProcesses(this.ruleset)) {
                Helper.setFehlerMeldung("RulesetInUse");
                return null;
            } else {
                serviceManager.getRulesetService().remove(this.ruleset);
            }
        } catch (DataException e) {
            Helper.setErrorMessage("fehlerNichtLoeschbar", logger, e);
            return null;
        }
        return redirectToList();
    }

    private boolean hasAssignedProcesses(Ruleset ruleset) throws DataException {
        Integer number = serviceManager.getProcessService().findByRuleset(ruleset).size();
        return number > 0;
    }

    /**
     * Method being used as viewAction for ruleset edit form. If given parameter
     * 'id' is '0', the form for creating a new ruleset will be displayed.
     *
     * @param id
     *            ID of the ruleset to load
     */
    public void loadRuleset(int id) {
        try {
            if (!Objects.equals(id, 0)) {
                setRuleset(this.serviceManager.getRulesetService().getById(id));
            }
            setSaveDisabled(true);
        } catch (DAOException e) {
            Helper.setErrorMessage("errorLoadingOne", new Object[] {Helper.getTranslation("regelsatz"), id }, logger,
                e);
        }
    }

    /*
     * Getter und Setter
     */

    public Ruleset getRuleset() {
        return this.ruleset;
    }

    public void setRuleset(Ruleset inPreference) {
        Helper.getHibernateSession().clear();
        this.ruleset = inPreference;
    }

    // TODO:
    // replace calls to this function with "/pages/rulesetEdit" once we have
    // completely switched to the new frontend pages
    private String redirectToEdit() {
        try {
            String referrer = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap()
                    .get("referer");
            String callerViewId = referrer.substring(referrer.lastIndexOf('/') + 1);
            if (!callerViewId.isEmpty() && callerViewId.contains("projects.jsf")) {
                return "/pages/rulesetEdit?" + REDIRECT_PARAMETER;
            } else {
                return "/pages/RegelsaetzeBearbeiten?" + REDIRECT_PARAMETER;
            }
        } catch (NullPointerException e) {
            // This NPE gets thrown - and therefore must be caught - when "RulesetForm" is
            // used from it's integration test
            // class "RulesetFormIT", where no "FacesContext" is available!
            return "/pages/RegelsaetzeBearbeiten?" + REDIRECT_PARAMETER;
        }
    }

    // TODO:
    // replace calls to this function with "/pages/projects.jsf" once we have
    // completely switched to the new frontend pages
    private String redirectToList() {
        try {
            String referrer = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap()
                    .get("referer");
            String callerViewId = referrer.substring(referrer.lastIndexOf('/') + 1);
            if (!callerViewId.isEmpty() && callerViewId.contains("rulesetEdit.jsf")) {
                return "/pages/projects.jsf?id=" + projectForm.getActiveTabIndex() + "&" + REDIRECT_PARAMETER;
            } else {
                return "/pages/RegelsaetzeAlle?" + REDIRECT_PARAMETER;
            }
        } catch (NullPointerException e) {
            // This NPE gets thrown - and therefore must be caught - when "RulesetForm" is
            // used from it's integration test
            // class "RulesetFormIT", where no "FacesContext" is available!
            return "/pages/RegelsaetzeAlle?" + REDIRECT_PARAMETER;
        }
    }

}
