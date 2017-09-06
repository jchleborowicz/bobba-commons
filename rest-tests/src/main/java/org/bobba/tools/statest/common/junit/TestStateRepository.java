package org.bobba.tools.statest.common.junit;

public interface TestStateRepository {


    /**
     * @throws TestStateObjectDoesNotExistException when object with given id does not exist in the repository.
     */
    <T> T load(String objectId, Class<T> aClass);

    void store(String objectId, Object object);

}
