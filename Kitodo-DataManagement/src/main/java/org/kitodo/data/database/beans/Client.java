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

package org.kitodo.data.database.beans;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "client")
public class Client extends BaseIndexedBean {

    private static final long serialVersionUID = -5538496170333987498L;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<UserGroupClientAuthorityRelation> userGroupClientAuthorityRelations;

    /**
     * Gets name.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name
     *            The name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets projects.
     *
     * @return The projects.
     */
    public List<Project> getProjects() {
        if (this.projects == null) {
            this.projects = new ArrayList<>();
        }
        return projects;
    }

    /**
     * Sets projects.
     *
     * @param projects
     *            The projects.
     */
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    /**
     * Gets userGroupClientAuthorityRelations.
     *
     * @return The userGroupClientAuthorityRelations.
     */
    public List<UserGroupClientAuthorityRelation> getUserGroupClientAuthorityRelations() {
        if (this.userGroupClientAuthorityRelations == null) {
            this.userGroupClientAuthorityRelations = new ArrayList<>();
        }
        return userGroupClientAuthorityRelations;
    }

    /**
     * Sets userGroupClientAuthorityRelations.
     *
     * @param userGroupClientAuthorityRelations
     *            The userGroupClientAuthorityRelations.
     */
    public void setUserGroupClientAuthorityRelations(
            List<UserGroupClientAuthorityRelation> userGroupClientAuthorityRelations) {
        this.userGroupClientAuthorityRelations = userGroupClientAuthorityRelations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Client client = (Client) o;
        return name != null ? name.equals(client.name) : client.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
