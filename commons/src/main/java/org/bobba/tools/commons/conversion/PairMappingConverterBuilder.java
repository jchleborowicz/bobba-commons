package org.bobba.tools.commons.conversion;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Mapper for fixed object pairs.
 *
 * @param <S> source object type.
 * @param <T> target object type.
 */
public final class PairMappingConverterBuilder<S, T> {

    private final Class<S> sourceType;
    private final Class<T> targetType;

    private final Map<S, T> mapping = new HashMap<>();
    private boolean acceptsNull;
    private T nullMapping;
    private boolean defaultedUnmappedElements;
    private T unmappedElementsMappedTo;

    public PairMappingConverterBuilder(Class<S> sourceType, Class<T> targetType) {
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    public static <S, T> PairMappingConverterBuilder<S, T> newInstance(Class<S> sourceType, Class<T> targetType) {
        return new PairMappingConverterBuilder<>(sourceType, targetType);
    }

    public PairMappingConverterBuilder<S, T> acceptsNull() {
        this.acceptsNull = true;
        return this;
    }

    public PairMappingConverterBuilder<S, T> acceptsNull(T nullMapping) {
        this.acceptsNull = true;
        this.nullMapping = nullMapping;
        return this;
    }

    public PairMappingConverterBuilder<S, T> map(S source, T target) {
        validateSourceObject(source);
        validateTargetObject(target);

        mapping.put(source, target);

        return this;
    }

    public PairMappingConverterBuilder<S, T> mapAll(Map<S, T> source) {
        for (Map.Entry<S, T> entry : source.entrySet()) {
            map(entry.getKey(), entry.getValue());
        }

        return this;
    }

    public PairMappingConverterBuilder<S, T> mapAllOthersTo(T target) {
        this.defaultedUnmappedElements = true;
        this.unmappedElementsMappedTo = target;

        return this;
    }

    private void validateTargetObject(T target) {
        if (target != null && !targetType.isInstance(target)) {
            throw new RuntimeException("Source is not of expected type. Required type: " + targetType + ", actual: "
                    + target.getClass());
        }
    }

    private void validateSourceObject(S source) {
        notNull(source, "Source object cannot be null");
        if (!sourceType.isInstance(source)) {
            throw new RuntimeException("Source is not of expected type. Required type: " + sourceType + ", actual: "
                    + source.getClass());
        }
        if (mapping.containsKey(source)) {
            throw new RuntimeException("Mapping already defined for source: " + source);
        }
    }

    public MapBasedSimpleConverter<S, T> build() {
        return new MapBasedSimpleConverter<>(sourceType, targetType, acceptsNull, nullMapping, mapping,
                defaultedUnmappedElements, unmappedElementsMappedTo);
    }

}
