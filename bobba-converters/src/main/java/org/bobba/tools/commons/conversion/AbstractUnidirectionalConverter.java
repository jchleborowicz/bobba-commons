package org.bobba.tools.commons.conversion;

import static org.apache.commons.lang3.Validate.notNull;
import static org.bobba.tools.commons.conversion.CommonUtils.checkRequiredObjectType;

public abstract class AbstractUnidirectionalConverter<S, T>
        extends AbstractSimpleUnidirectionalConverter<S, T> implements UnidirectionalConverter<S, T> {

    public AbstractUnidirectionalConverter(Class<S> sourceType, Class<T> targetType) {
        super(sourceType, targetType);
    }

    public AbstractUnidirectionalConverter(Class<S> sourceType, Class<T> targetType, boolean nullSourceAllowed) {
        super(sourceType, targetType, nullSourceAllowed);
    }

    public AbstractUnidirectionalConverter(Class<S> sourceType, Class<T> targetType, boolean nullSourceAllowed,
                                           T nullSourceMapping) {
        super(sourceType, targetType, nullSourceAllowed, nullSourceMapping);
    }

    @Override
    protected final T safeConvert(S source) throws Exception {
        notNull(source);

        final T target = getTargetType().newInstance();
        convert(source, target);
        return target;
    }

    @Override
    public void convert(S source, T target) throws Exception {
        checkRequiredObjectType(source, getSourceType());
        checkRequiredObjectType(target, getTargetType());

        safeConvert(source, target);
    }

    /**
     * Method to convert source object to target object. It is safe to assume that both source and target are not null.
     *
     * @param source source object.
     * @param target target object.
     */
    protected abstract void safeConvert(S source, T target) throws Exception;

}
