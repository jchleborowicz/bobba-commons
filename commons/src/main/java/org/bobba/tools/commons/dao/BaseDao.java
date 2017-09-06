package org.bobba.tools.commons.dao;

import org.bobba.tools.commons.model.DomainObject;
import org.hibernate.LockMode;

import java.io.Serializable;

/**
 * Base dao for Hibernate objects.
 *
 * @param <KEY>    entity key type.
 * @param <ENTITY> entity type.
 */
public interface BaseDao<KEY extends Serializable, ENTITY extends DomainObject<KEY>> {

    ENTITY get(KEY id);

    ENTITY get(KEY id, LockMode lockMode);

    ENTITY load(KEY id);

    KEY save(ENTITY entity);

    void update(ENTITY entity);
}
