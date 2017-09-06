package org.bobba.tools.commons.conversion;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMapConverter<SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE>
        extends AbstractSimpleUnidirectionalConverter<Map<SOURCE_KEY, SOURCE_VALUE>, Map<TARGET_KEY, TARGET_VALUE>> {

    public AbstractMapConverter() {
        this(false);
    }

    public AbstractMapConverter(boolean nullSourceAllowed) {
        this(nullSourceAllowed, null);
    }

    @SuppressWarnings("unchecked")
    public AbstractMapConverter(boolean nullSourceAllowed, Map<TARGET_KEY, TARGET_VALUE> nullSourceMapping) {
        super((Class) Map.class, (Class) Map.class, nullSourceAllowed, nullSourceMapping);
    }

    @Override
    protected Map<TARGET_KEY, TARGET_VALUE> safeConvert(Map<SOURCE_KEY, SOURCE_VALUE> source) throws Exception {
        final Map<TARGET_KEY, TARGET_VALUE> target = createMapInstance();

        final Map<TARGET_KEY, SOURCE_KEY> targetToSourceKeys = new HashMap<TARGET_KEY, SOURCE_KEY>();

        for (Map.Entry<SOURCE_KEY, SOURCE_VALUE> entry : source.entrySet()) {
            Map.Entry<TARGET_KEY, TARGET_VALUE> targetEntry = internalConvertEntry(entry);

            final TARGET_KEY targetKey = targetEntry.getKey();
            if (targetToSourceKeys.containsKey(targetKey)) {
                throw new ConversionException("Same target map key \"" + targetKey
                        + "\" created for two source map keys: \"" + targetToSourceKeys.get(targetKey)
                        + "\" and \"" + entry.getKey() + "\"");
            }

            target.put(targetKey, targetEntry.getValue());
            targetToSourceKeys.put(targetKey, entry.getKey());
        }
        return target;
    }

    private Map.Entry<TARGET_KEY, TARGET_VALUE> internalConvertEntry(Map.Entry<SOURCE_KEY, SOURCE_VALUE> entry)
            throws Exception {
        final Map.Entry<TARGET_KEY, TARGET_VALUE> result = convertEntry(entry);
        if (result == null) {
            throw new ConversionException("Converted object is null for input " + entry);
        }
        return result;
    }

    protected abstract Map.Entry<TARGET_KEY, TARGET_VALUE> convertEntry(Map.Entry<SOURCE_KEY, SOURCE_VALUE> entry)
            throws Exception;

    protected Map<TARGET_KEY, TARGET_VALUE> createMapInstance() {
        return new HashMap<TARGET_KEY, TARGET_VALUE>();
    }

}
