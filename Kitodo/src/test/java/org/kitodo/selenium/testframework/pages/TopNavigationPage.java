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

package org.kitodo.selenium.testframework.pages;

import static org.kitodo.selenium.testframework.Browser.hoverWebElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitodo.selenium.testframework.Browser;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class TopNavigationPage {

    private static final Logger logger = LogManager.getLogger(TopNavigationPage.class);

    @SuppressWarnings("unused")
    @FindBy(id = "user-menu")
    private WebElement userMenuButton;

    @SuppressWarnings("unused")
    @FindBy(id = "logout-form:logout")
    private WebElement logoutButton;

    @SuppressWarnings("unused")
    @FindBy(id = "dashboard-menu")
    private WebElement dashboardMenuButton;

    @SuppressWarnings("unused")
    @FindBy(id = "dashboard-menu-header")
    private WebElement dashboardMenuHeader;

    @SuppressWarnings("unused")
    @FindBy(id = "linkTasks")
    private WebElement linkTasks;

    @SuppressWarnings("unused")
    @FindBy(id = "linkProcesses")
    private WebElement linkProcesses;

    @SuppressWarnings("unused")
    @FindBy(id = "linkProjects")
    private WebElement linkProjects;

    @SuppressWarnings("unused")
    @FindBy(id = "linkUsers")
    private WebElement linkUsers;

    @SuppressWarnings("unused")
    @FindBy(id = "linkModules")
    private WebElement linkModules;

    @SuppressWarnings("unused")
    @FindBy(id = "linkClients")
    private WebElement linkClients;

    @SuppressWarnings("unused")
    @FindBy(id = "linkHelp")
    private WebElement linkHelp;

    @SuppressWarnings("unused")
    @FindBy(id = "linkIndexing")
    private WebElement linkIndexing;

    /**
     * Hovers user menu and logs out.
     */
    public void logout() throws InterruptedException {
        int attempt = 1;
        int maximumAttempts = 10;
        while (attempt <= maximumAttempts) {
            try {
                hoverWebElement(userMenuButton);
                break;
            } catch (ElementNotVisibleException e) {
                logger.error("User menu icon not visible, retrying now, " + attempt);
                Thread.sleep(1000);
                attempt++;
            }
        }
        if (attempt > maximumAttempts) {
            throw new ElementNotVisibleException("User menu icon not visible");
        }

        hoverWebElement(logoutButton);
        logoutButton.click();
        Thread.sleep(Browser.getDelayAfterLogout());
    }

    /**
     * Hovers dashboard menu and clicks on link to help page
     */
    public void gotoHelp() throws InterruptedException {
        hoverWebElement(dashboardMenuButton);
        hoverWebElement(linkHelp);
        linkHelp.click();
        Thread.sleep(Browser.getDelayAfterLinkClick());
    }

    /**
     * Hovers dashboard menu and clicks on link to tasks page
     */
    public void gotoTasks() throws InterruptedException {
        hoverWebElement(dashboardMenuButton);
        hoverWebElement(linkHelp);
        linkTasks.click();
        Thread.sleep(Browser.getDelayAfterLinkClick());
    }

    /**
     * Hovers dashboard menu and clicks on link to processes page
     */
    public void gotoProcesses() throws InterruptedException {
        hoverWebElement(dashboardMenuButton);
        hoverWebElement(linkProcesses);
        linkProcesses.click();
        Thread.sleep(Browser.getDelayAfterLinkClick());
    }

    /**
     * Hovers dashboard menu and clicks on link to projects page
     */
    public void gotoProjects() throws InterruptedException {
        hoverWebElement(dashboardMenuButton);
        hoverWebElement(linkProjects);
        linkProjects.click();
        Thread.sleep(Browser.getDelayAfterLinkClick());
    }

    /**
     * Hovers dashboard menu and clicks on link to users page
     */
    public void gotoUsers() throws InterruptedException {
        hoverWebElement(dashboardMenuButton);
        hoverWebElement(linkUsers);
        linkUsers.click();
        Thread.sleep(Browser.getDelayAfterLinkClick());
    }

    /**
     * Hovers dashboard menu and clicks on link to modules page
     */
    public void gotoModules() throws InterruptedException {
        hoverWebElement(dashboardMenuButton);
        hoverWebElement(linkModules);
        linkModules.click();
        Thread.sleep(Browser.getDelayAfterLinkClick());
    }

    /**
     * Hovers dashboard menu and clicks on link to clients page
     */
    public void gotoClients() throws InterruptedException {
        hoverWebElement(dashboardMenuButton);
        hoverWebElement(linkClients);
        linkClients.click();
        Thread.sleep(Browser.getDelayAfterLinkClick());
    }

    /**
     * Hovers dashboard menu and clicks on link to indexing page
     */
    public void gotoIndexing() throws InterruptedException {
        hoverWebElement(dashboardMenuButton);
        hoverWebElement(linkIndexing);
        linkIndexing.click();
        Thread.sleep(Browser.getDelayAfterLinkClick());
    }

    /**
     * Hovers dashboard menu and checks menu header if all buttons are displayed.
     * 
     * @return True if "Admin" is displayed.
     */
    public boolean isShowingAllLinks() throws InterruptedException {
        hoverWebElement(dashboardMenuButton);
        if (!linkHelp.isDisplayed()) {
            return false;
        }
        if (!linkClients.isDisplayed()) {
            return false;
        }
        if (!linkIndexing.isDisplayed()) {
            return false;
        }
        if (!linkModules.isDisplayed()) {
            return false;
        }
        if (!linkProjects.isDisplayed()) {
            return false;
        }
        if (!linkTasks.isDisplayed()) {
            return false;
        }
        if (!linkUsers.isDisplayed()) {
            return false;
        }
        if (!linkProcesses.isDisplayed()) {
            return false;
        }
        return true;
    }
}
