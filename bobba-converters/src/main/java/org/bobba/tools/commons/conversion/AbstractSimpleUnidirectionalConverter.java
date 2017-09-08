package org.bobba.tools.commons.conversion;

import static org.bobba.tools.commons.conversion.CommonUtils.checkObjectType;

public abstract class AbstractSimpleUnidirectionalConverter<S, T> implements SimpleUnidirectionalConverter<S, T> {

    private final Class<S> sourceType;
    private final Class<T> targetType;
    private final boolean nullSourceAllowed;
    /**
     * Contains mapping for null source object.
     */
    private final T nullSourceMapping;

    protected AbstractSimpleUnidirectionalConverter(Class<S> sourceType, Class<T> targetType) {
        this(sourceType, targetType, false);
    }

    protected AbstractSimpleUnidirectionalConverter(Class<S> sourceType, Class<T> targetType,
                                                    boolean nullSourceAllowed) {
        this(sourceType, targetType, nullSourceAllowed, null);
    }

    protected AbstractSimpleUnidirectionalConverter(Class<S> sourceType, Class<T> targetType, boolean nullSourceAllowed,
                                                    T nullSourceMapping) {
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.nullSourceAllowed = nullSourceAllowed;
        this.nullSourceMapping = nullSourceMapping;
    }

    protected Class<S> getSourceType() {
        return sourceType;
    }

    protected Class<T> getTargetType() {
        return targetType;
    }

    protected boolean isNullSourceAllowed() {
        return nullSourceAllowed;
    }

    @Override
    public final T convert(S source) throws Exception {
        if (source == null) {
            return handleNullSource();
        } else {
            return handleNotNullSource(source);
        }
    }

    private T handleNotNullSource(S source) throws Exception {
        checkSourceType(source);

        final T target = safeConvert(source);

        checkTargetType(target);

        return target;
    }

    private T handleNullSource() {
        if (nullSourceAllowed) {
            return nullSourceMapping;
        } else {
            throw new ConversionException("Null source is not allowed");
        }
    }

    private void checkSourceType(S source) {
        checkObjectType(source, sourceType);
    }

    private void checkTargetType(T target) {
        checkObjectType(target, targetType);
    }

    /**
     * It is safe to to assume that source is not null.
     */
    protected abstract T safeConvert(S source) throws Exception;

}
