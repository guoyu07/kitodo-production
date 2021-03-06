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

package org.kitodo.services.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityAccessService {

    private static SecurityAccessService instance = null;

    /**
     * Return singleton variable of type SecurityAccessService.
     *
     * @return unique instance of SecurityAccessService
     */
    public static SecurityAccessService getInstance() {
        if (Objects.equals(instance, null)) {
            synchronized (SecurityAccessService.class) {
                if (Objects.equals(instance, null)) {
                    instance = new SecurityAccessService();
                }
            }
        }
        return instance;
    }

    private Collection<? extends GrantedAuthority> getAuthoritiesOfCurrentAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities();
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Checks if the current user has a specified authority globally or for a
     * project.
     * 
     * @param authorityTitle
     *            The authority title.
     * @param projectId
     *            The project id.
     * @return True if the current user has the specified authority.
     */
    public boolean hasAuthorityGlobalOrForProject(String authorityTitle, int projectId) {
        if (hasGlobalAuthority(authorityTitle)) {
            return true;
        } else {
            String titleOfRequiredAuthority = authorityTitle + "_PROJECT_" + projectId;
            return hasAuthority(titleOfRequiredAuthority);
        }
    }

    /**
     * Checks if the current user is admin or has a specified authority globally or
     * for a project.
     *
     * @param authorityTitle
     *            The authority title.
     * @param projectId
     *            The project id.
     * @return True if the current user has the specified authority.
     */
    public boolean isAdminOrHasAuthorityGlobalOrForProject(String authorityTitle, int projectId) {
        return isAdmin() || hasAuthorityGlobalOrForProject(authorityTitle, projectId);
    }

    /**
     * Checks if the current user has a specified authority globally or for client.
     *
     * @param authorityTitle
     *            The authority title.
     * @param clientId
     *            The project id.
     * @return True if the current user has the specified authority.
     */
    public boolean hasAuthorityGlobalOrForClient(String authorityTitle, int clientId) {
        if (hasGlobalAuthority(authorityTitle)) {
            return true;
        } else {
            String titleOfRequiredAuthority = authorityTitle + "_CLIENT_" + clientId;
            return hasAuthority(titleOfRequiredAuthority);
        }
    }

    /**
     * Checks if the current user is admin or has a specified authority globally or
     * for a client.
     *
     * @param authorityTitle
     *            The authority title.
     * @param clientId
     *            The project id.
     * @return True if the current user has the specified authority.
     */
    public boolean isAdminOrHasAuthorityGlobalOrForClient(String authorityTitle, int clientId) {
        return isAdmin() || hasAuthorityGlobalOrForClient(authorityTitle, clientId);
    }

    private boolean hasAuthority(String authorityTitle) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(authorityTitle);
        Collection<? extends GrantedAuthority> authorities = getAuthoritiesOfCurrentAuthentication();
        return authorities.contains(authority);
    }

    private String[] getStringArray(String strings) {
        strings = strings.replaceAll("\\s+", ""); // remove whitespaces
        return strings.split(",");
    }

    /**
     * Checks if the current user is admin.
     * 
     * @return True if the current user has the admin authority
     */
    public boolean isAdmin() {
        return hasGlobalAuthority("admin");
    }

    /**
     * Checks if the current user has a specified authority globally.
     *
     * @param authorityTitle
     *            The authority title.
     * @return True if the current user has the specified authority.
     */
    public boolean hasGlobalAuthority(String authorityTitle) {
        return hasAuthority(authorityTitle + "_GLOBAL");
    }

    /**
     * Checks if the current user is admin or has a specified authority globally.
     *
     * @param authorityTitle
     *            The authority title.
     * @return True if the current user is admin or has the specified authority
     *         globally.
     */
    public boolean isAdminOrHasGlobalAuthority(String authorityTitle) {
        return isAdmin() || hasGlobalAuthority(authorityTitle);
    }

    /**
     * Checks if the current user has a specified authority globally, for any client
     * or for any project.
     *
     * @param authorityTitle
     *            The authority title.
     * @return True if the current user has the specified authority globally, for
     *         any client or for any project.
     */
    public boolean hasAuthorityGlobalOrForAnyClientOrForAnyProject(String authorityTitle) {
        if (hasGlobalAuthority(authorityTitle)) {
            return true;
        }
        if (hasAuthority(authorityTitle + "_CLIENT_ANY")) {
            return true;
        }
        return hasAuthority(authorityTitle + "_PROJECT_ANY");
    }

    /**
     * Checks if the current user is admin or has a specified authority globally,
     * for any client or for any project.
     *
     * @param authorityTitle
     *            The authority title.
     * @return True if the current user is admin or has the specified authority for
     *         any client or project.
     */
    public boolean isAdminOrHasAuthorityGlobalOrForAnyClientOrForAnyProject(String authorityTitle) {
        return isAdmin() || hasAuthorityGlobalOrForAnyClientOrForAnyProject(authorityTitle);
    }

    /**
     * Checks if the current user has one of the specified authorities globally, for
     * any client or for any project.
     *
     * @param authorityTitlesComplete
     *            The authority titles separated with commas.
     * @return True if the current user is admin or has any of the specified
     *         authorities for any client or project.
     */
    public boolean isAdminOrHasAnyAuthorityGlobalOrForAnyClientOrForAnyProject(String authorityTitlesComplete) {
        if (isAdmin()) {
            return true;
        }
        String[] authorityTitles = getStringArray(authorityTitlesComplete);
        for (String authorityTitle : authorityTitles) {
            if (hasAuthorityGlobalOrForAnyClientOrForAnyProject(authorityTitle)) {
                return true;
            }
        }
        return false;
    }
}
