package org.bobba.tools.commons.model;

import java.io.Serializable;

/**
 * Base interface for domain ojbect classes - classes persisted by hibernate.
 * <p>Each class should contain id and version number</p>
 * @param <T> type of entity id.
 */
public interface DomainObject<T extends Serializable> extends Serializable {

    T getId();

    void setId(T id);

    int getVersion();

    void setVersion(int version);

}
