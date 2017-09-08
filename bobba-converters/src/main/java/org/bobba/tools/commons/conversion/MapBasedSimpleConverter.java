package org.bobba.tools.commons.conversion;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class MapBasedSimpleConverter<S, T> extends AbstractSimpleUnidirectionalConverter<S, T> {

    private final ImmutableMap<S, T> mappings;
    private final boolean defaultedUnmappedElements;
    private final T unmappedElementsMappedTo;

    public MapBasedSimpleConverter(Class<S> sourceType, Class<T> targetType, Map<S, T> mappings) {
        this(sourceType, targetType, false, null, mappings, false, null);
    }

    public MapBasedSimpleConverter(Class<S> sourceType, Class<T> targetType, boolean nullSourceAllowed,
                                   T nullSourceMapping, Map<S, T> mappings, boolean defaultedUnmappedElements,
                                   T unmappedElementsMappedTo) {
        super(sourceType, targetType, nullSourceAllowed, nullSourceMapping);
        this.mappings = ImmutableMap.copyOf(mappings);
        this.defaultedUnmappedElements = defaultedUnmappedElements;
        this.unmappedElementsMappedTo = unmappedElementsMappedTo;
    }

    @Override
    protected T safeConvert(S source) {
        if (!mappings.containsKey(source)) {
            if (defaultedUnmappedElements) {
                return unmappedElementsMappedTo;
            } else {
                throw new ConversionException("Source object not found: " + source);
            }
        }
        return mappings.get(source);
    }

    public ImmutableMap<S, T> getMappings() {
        return mappings;
    }

}
