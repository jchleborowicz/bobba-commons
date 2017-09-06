package org.bobba.tools.commons.dao;

import org.bobba.tools.commons.model.DomainObject;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.LockMode;
import org.springframework.orm.hibernate3.HibernateOperations;

import java.io.Serializable;

public class BaseDaoImpl<KEY extends Serializable, ENTITY extends DomainObject<KEY>> implements BaseDao<KEY, ENTITY> {

    @Getter
    @Setter
    private HibernateOperations hibernateOperations;

    @Getter
    private final Class<ENTITY> entityClass;

    public BaseDaoImpl(Class<ENTITY> entityClass) {
        this(null, entityClass);
    }

    public BaseDaoImpl(HibernateOperations hibernateOperations, Class<ENTITY> entityClass) {
        this.hibernateOperations = hibernateOperations;
        this.entityClass = entityClass;
    }

    @Override
    public ENTITY get(KEY id) {
        return hibernateOperations.get(entityClass, id);
    }

    @Override
    public ENTITY get(KEY id, LockMode lockMode) {
        return hibernateOperations.get(entityClass, id, lockMode);
    }

    @Override
    public ENTITY load(KEY id) {
        return hibernateOperations.load(entityClass, id);
    }

    @Override
    public KEY save(ENTITY entity) {
        //noinspection unchecked
        return (KEY) getHibernateOperations().save(entity);
    }

    @Override
    public void update(ENTITY entity) {
        hibernateOperations.update(entity);
    }

}
