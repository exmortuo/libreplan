package org.navalplanner.business.common.daos.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.navalplanner.business.common.daos.IGenericDao;
import org.navalplanner.business.common.exceptions.InstanceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

// FIXME: This class is not currently used. I prefer GenericDaoHibernate, since
// it represents a non-intrusive use of Spring.

/**
 * An implementation of <code>IGenericDao</code> based on Spring's
 * <code>HibernateTemplate</code>. Concrete DAOs must extend directly from
 * this class. This constraint is imposed by the constructor of this class that
 * must infer the type of the entity from the concrete DAO declaration. <p/>
 *
 * This class autowires a <code>SessionFactory</code> bean and allows to
 * implement Spring's HibernateTemplate-based DAOs. Subclasses access
 * <code>HibernateTemplate</code> by calling on
 * <code>getHibernateTemplate()</code> method.
 *
 * @author Fernando Bellas Permuy <fbellas@udc.es>
 *
 * @param <E> Entity class
 * @param <PK> Primary key class
 */
public class GenericDaoHibernateTemplate<E, PK extends Serializable> implements
        IGenericDao<E, PK> {

    private Class<E> entityClass;

    private HibernateTemplate hibernateTemplate;

    @SuppressWarnings("unchecked")
    public GenericDaoHibernateTemplate() {
        this.entityClass = (Class<E>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public void save(E entity) {
        hibernateTemplate.saveOrUpdate(entity);
    }

    public E merge(E entity) {
        return entityClass.cast(hibernateTemplate.merge(entity));
    }

    public void checkVersion(E entity) {

        /* Get id and version from entity. */
        Serializable entityId;
        long entityVersion;

        try {

            Method getIdMethod = entityClass.getMethod("getId");
            entityId = (Serializable) getIdMethod.invoke(entity);

            if (entityId == null) {
                return;
            }

            Method getVersionMethod = entityClass.getMethod("getVersion");
            entityVersion = (Long) getVersionMethod.invoke(entity);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        /* Check version. */
        final Serializable id = entityId;
        final long versionValueInMemory = entityVersion;

        hibernateTemplate.execute(new HibernateCallback() {

            public Object doInHibernate(Session session) {

                Long versionValueInDB = (Long)
                    session.createCriteria(entityClass).
                    add(Restrictions.idEq(id)).
                    setProjection(Projections.property("version")).
                    uniqueResult();

                if (versionValueInDB == null) {
                    return null;
                }

                if (versionValueInMemory != versionValueInDB) {
                    throw new StaleObjectStateException(entityClass.getName(),
                        id);
                }

                return null;

            }
        });

    }

    public void lock(E entity) {
        hibernateTemplate.lock(entity, LockMode.UPGRADE);
    }

    @SuppressWarnings("unchecked")
    public E find(PK id) throws InstanceNotFoundException {

        E entity = (E) hibernateTemplate.get(entityClass, id);

        if (entity == null) {
            throw new InstanceNotFoundException(id, entityClass.getName());
        }

        return entity;

    }

    public boolean exists(final PK id) {

        return (Boolean) hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                return session.createCriteria(entityClass).
                    add(Restrictions.idEq(id)).
                    setProjection(Projections.id()).
                    uniqueResult() != null;
            }
        });

    }

    public void remove(PK id) throws InstanceNotFoundException {
        hibernateTemplate.delete(find(id));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends E> List<T> list(Class<T> klass) {
        return hibernateTemplate.loadAll(klass);
    }

}
