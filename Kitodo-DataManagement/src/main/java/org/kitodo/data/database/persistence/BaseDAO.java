/*
 * Copyright 2005 Nick Heudecker
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.kitodo.data.database.persistence;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.kitodo.data.database.beans.BaseBean;
import org.kitodo.data.database.exceptions.DAOException;
import org.kitodo.data.database.helper.Helper;
import org.kitodo.data.database.helper.Util;

/**
 * Base class for DAOs. This class defines common CRUD methods.
 *
 * <p>
 * Changes have been made by Steffen Hankiewicz with later changes by Beatrycze
 * Kmiec
 * </p>
 *
 * @author Nick Heudecker &lt;nick@systemmobile.com&gt;
 * @author Steffen Hankiewicz &lt;steffen.hankiewicz@intranda.com&gt;
 * @author Beatrycze Kmiec &lt;beatrycze.kmiec@slub-dresden.de&gt;
 */

public abstract class BaseDAO<T extends BaseBean> implements Serializable {
    private static final long serialVersionUID = 4676125965631365912L;

    /**
     * Removes the object from the database.
     *
     * @param object
     *            the class to remove
     * @throws DAOException
     *             add description
     */
    protected void removeObject(T object) throws DAOException {
        try {
            Session session = Helper.getHibernateSession();
            synchronized (object) {
                session.evict(object);
                session.delete(object);
                session.flush();
                session.beginTransaction().commit();
            }
        } catch (Exception e) {
            rollback();
            throw new DAOException(e);
        }
    }

    /**
     * Removes the object from the database with with specified class type and
     * <code>id</code>.
     *
     * @param cls
     *            the class type to remove
     * @param id
     *            the id of the class type
     * @throws DAOException
     *             add description
     */
    @SuppressWarnings("rawtypes")
    protected static void removeObject(Class cls, Integer id) throws DAOException {
        try {
            Session session = Helper.getHibernateSession();
            // first load the object with the current session.
            // the object must be loaded in this session before it is deleted.
            synchronized (cls) {
                Object obj = session.load(cls, id);
                session.delete(obj);
                session.flush();
                session.beginTransaction().commit();
            }
        } catch (Exception e) {
            rollback();
            throw new DAOException(e);
        }
    }

    /**
     * Retrieves and <code>Object</code> of the class type specified by
     * <code>c</code>, and having the given <code>id</code>.
     *
     * @param cls
     *            the class to load
     * @param id
     *            object id
     * @return Object may be null if object with ID doesn't exist
     * @throws DAOException
     *             add description
     */
    @SuppressWarnings({"unchecked" })
    protected T retrieveObject(Class cls, Integer id) throws DAOException {
        try {
            Session session = Helper.getHibernateSession();
            if (session == null) {
                session = HibernateUtil.getSessionFactory().openSession();
                T o = (T) session.get(cls, id);
                session.close();
                return o;
            }
            return (T) session.get(cls, id);
        } catch (HibernateException he) {
            throw new DAOException(he);
        }
    }

    /**
     * Own design one of previous authors for returning the objects.
     *
     * @param query
     *            string
     * @return list of results
     */
    @SuppressWarnings("unchecked")
    protected List<T> retrieveObjects(String query) {
        Session session = Helper.getHibernateSession();
        return (List<T>) session.createQuery(query).list();
    }

    /**
     * Own design one of previous authors for maximum number of objects
     * returned.
     *
     * @param query
     *            string
     * @param first
     *            result
     * @param max
     *            amount of results
     * @return constrained list of results
     */
    @SuppressWarnings("unchecked")
    protected List<T> retrieveObjects(String query, int first, int max) throws DAOException {
        try {
            Session session = Helper.getHibernateSession();
            Query q = session.createQuery(query);
            q.setFirstResult(first);
            q.setMaxResults(max);
            return (List<T>) q.list();
        } catch (HibernateException he) {
            throw new DAOException(he);
        }
    }

    /**
     * Standard design for returning the objects.
     *
     * @param query
     *            string
     * @param parameter
     *            string
     * @return list of results
     */
    @SuppressWarnings("unchecked")
    protected List<T> retrieveObjects(String query, String parameter) throws DAOException {
        try {
            Session session = Helper.getHibernateSession();
            Query q = session.createQuery(query);
            q.setParameter(0, parameter);
            return (List<T>) q.list();
        } catch (HibernateException he) {
            throw new DAOException(he);
        }
    }

    /**
     * Retrieve list of objects by query string and named parameter.
     *
     * @param query
     *            string
     * @param namedParameter
     *            Name of named parameter
     * @param parameter
     *            Parameter value
     * @return List
     */
    @SuppressWarnings("unchecked")
    protected List<T> retrieveObjects(String query, String namedParameter, String parameter) throws DAOException {
        try {
            Session session = Helper.getHibernateSession();
            Query q = session.createQuery(query);
            q.setString(namedParameter, parameter);
            return (List<T>) q.list();
        } catch (HibernateException he) {
            throw new DAOException(he);
        }
    }

    @SuppressWarnings("unchecked")
    protected List<T> retrieveAllObjects(Class cls) {
        Session session = Helper.getHibernateSession();
        Query query = session.createQuery("FROM " + cls.getSimpleName());
        return (List<T>) query.list();
    }

    /**
     * Own design one of previous authors for determining the number of objects.
     *
     * @param query
     *            string
     * @return amount of results
     * @throws DAOException
     *             add description
     */
    protected Long retrieveAmount(String query) throws DAOException {
        try {
            Session session = Helper.getHibernateSession();
            return (Long) session.createQuery("select count(*) " + query).uniqueResult();
        } catch (HibernateException he) {
            throw new DAOException(he);
        }
    }

    /**
     * Stores <code>object</code>, making it persistent.
     *
     * @param object
     *            to persist
     * @throws DAOException
     *             add description
     */
    protected void storeObject(T object) throws DAOException {
        try {
            Session session = Helper.getHibernateSession();
            session.saveOrUpdate(object);
            session.flush();
            session.beginTransaction().commit();
        } catch (HibernateException he) {
            rollback();
            throw new DAOException(he);
        }
    }

    protected void storeList(List<T> list) throws DAOException {
        try {
            Session session = Helper.getHibernateSession();
            for (T obj : list) {
                session.saveOrUpdate(obj);
            }
            session.flush();
            session.beginTransaction().commit();
        } catch (HibernateException he) {
            rollback();
            throw new DAOException(he);

        }
    }

    /**
     * Performs a rollback on the current session. Exceptions are logged.
     *
     * @throws DAOException
     *             if the current session can't be retrieved or an exception is
     *             thrown while performing the rollback.
     */
    protected static void rollback() throws DAOException {
        try {
            Session session = Helper.getHibernateSession();
            if (session != null) {
                session.beginTransaction().rollback();
            }
        } catch (HibernateException he) {
            throw new DAOException(he);
        }
    }

    /**
     * Retrieves the HQL query from the resource bundle.
     *
     * @param key
     *            the HQL query to lookup
     */
    protected String getQuery(String key) {
        return Util.getQuery(key);
    }

    /**
     * Refresh object associated with the session.
     *
     * @param object
     *            associated with the session
     */
    protected void refreshObject(T object) {
        Session session = Helper.getHibernateSession();
        if (session == null) {
            session = HibernateUtil.getSessionFactory().openSession();
            session.refresh(object);
            session.close();
        }
        session.refresh(object);
    }

    @SuppressWarnings("unchecked")
    protected T loadObjects(Class cls, Integer id) throws DAOException {
        try {
            Session session = Helper.getHibernateSession();
            if (session == null) {
                session = HibernateUtil.getSessionFactory().openSession();
                T o = (T) session.load(cls, id);
                session.close();
                return o;
            }
            return (T) session.load(cls, id);
        } catch (HibernateException he) {
            throw new DAOException(he);
        }
    }

    /**
     * Update of the object.
     *
     * @param object
     *            to update
     */
    protected void updateObject(T object) {
        Session session = Helper.getHibernateSession();
        if (session == null) {
            session = HibernateUtil.getSessionFactory().openSession();
            session.update(object);
            session.close();
        }
        session.update(object);
    }
}
