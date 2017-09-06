package org.bobba.tools.commons;

import java.util.HashMap;
import java.util.Map;

public final class IdentifiableEnumHelper {

    public static <T extends IdentifiableEnum<S>, S> Map<S, T> create(T[] values) {
        final Map<S, T> result = new HashMap<S, T>();
        final Class<?> enumClass = values.getClass();
        final String enumClassName = enumClass.getName();
        for (T value : values) {
            final S id = value.getId();
            if (result.containsKey(id)) {
                throw new RuntimeException(
                        "Duplicated element id " + id + " in identifiable enum class " + enumClassName);
            }
            result.put(id, value);
        }
        return new HashMap<S, T>(result) {
            @Override
            public T get(Object key) {
                final T result = super.get(key);
                if (result == null) {
                    throw new IdentifiableEnumIdNotFoundException(
                            "Cannot get id " + key + " in enum class " + enumClassName);
                }
                return result;
            }
        };
    }
}
