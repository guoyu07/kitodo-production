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

package org.kitodo.data.database.persistence;

import java.util.List;

import org.kitodo.data.database.beans.User;
import org.kitodo.data.database.exceptions.DAOException;

public class UserDAO extends BaseDAO<User> {

    private static final long serialVersionUID = 834210840673022251L;

    @Override
    public User getById(Integer id) throws DAOException {
        User result = retrieveObject(User.class, id);
        if (result == null) {
            throw new DAOException("Object can not be found in database");
        }
        return result;
    }

    @Override
    public List<User> getAll() {
        return getByQuery("FROM User WHERE deleted = 0");
    }

    @Override
    public List<User> getAll(int offset, int size) throws DAOException {
        return retrieveObjects("FROM User WHERE deleted = 0 ORDER BY id ASC", offset, size);
    }

    public User save(User user) throws DAOException {
        storeObject(user);
        return retrieveObject(User.class, user.getId());
    }

    @Override
    public void remove(User user) throws DAOException {
        user.selfDestruct();
        save(user);
    }

    @Override
    public void remove(Integer id) throws DAOException {
        User user = getById(id);
        user.selfDestruct();
        save(user);
    }

    public List<User> search(String query, String parameter) throws DAOException {
        return retrieveObjects(query, parameter);
    }

    /**
     * Search for a list of users by a named parameter.
     *
     * @param query
     *            Search query
     * @param namedParameter
     *            Name of named parameter
     * @param parameter
     *            Parameter value
     * @return list of users
     * @throws DAOException
     *             if a HibernateException is thrown
     */
    public List<User> search(String query, String namedParameter, String parameter) throws DAOException {
        return retrieveObjects(query, namedParameter, parameter);
    }

    /**
     * Refresh user object after some changes.
     *
     * @param user
     *            object
     */
    public void refresh(User user) {
        refreshObject(user);
    }

    /**
     * Get all active users sorted by surname and name.
     *
     * @return sorted list of all active users as User objects
     */
    public List<User> getAllActiveUsersSortedByNameAndSurname() {
        return getByQuery("FROM User WHERE active = 1 AND deleted = 0 ORDER BY surname ASC, name ASC");
    }
}
