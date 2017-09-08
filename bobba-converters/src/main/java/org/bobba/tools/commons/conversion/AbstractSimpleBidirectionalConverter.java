package org.bobba.tools.commons.conversion;

public abstract class AbstractSimpleBidirectionalConverter<S, T> implements SimpleBidirectionalConverter<S, T> {

    private final SimpleUnidirectionalConverter<S, T> forwardConverter;
    private final SimpleUnidirectionalConverter<T, S> backwardConverter;

    protected AbstractSimpleBidirectionalConverter() {
        this.forwardConverter = new SimpleUnidirectionalConverter<S, T>() {
            @Override
            public T convert(S source) throws Exception {
                return safeConvertForward(source);
            }
        };
        this.backwardConverter = new SimpleUnidirectionalConverter<T, S>() {
            @Override
            public S convert(T source) throws Exception {
                return safeConvertBackward(source);
            }
        };
    }

    /**
     * It is safe to to assume that source is not null.
     */
    protected abstract T safeConvertForward(S source) throws Exception;

    /**
     * It is safe to to assume that source is not null.
     */
    protected abstract S safeConvertBackward(T source) throws Exception;

    @Override
    public final T convertForward(S source) throws Exception {
        return forwardConverter.convert(source);
    }

    @Override
    public final S convertBackward(T source) throws Exception {
        return backwardConverter.convert(source);
    }

    @Override
    public SimpleUnidirectionalConverter<S, T> getForwardConverter() {
        return forwardConverter;
    }

    @Override
    public SimpleUnidirectionalConverter<T, S> getBackwardConverter() {
        return backwardConverter;
    }

    @Override
    public SimpleBidirectionalConverter<T, S> getReversedConverter() {
        return new SimpleBidirectionalConverter<T, S>() {
            @Override
            public S convertForward(T source) throws Exception {
                return AbstractSimpleBidirectionalConverter.this.convertBackward(source);
            }

            @Override
            public T convertBackward(S source) throws Exception {
                return AbstractSimpleBidirectionalConverter.this.convertForward(source);
            }

            @Override
            public SimpleUnidirectionalConverter<T, S> getForwardConverter() {
                return AbstractSimpleBidirectionalConverter.this.backwardConverter;
            }

            @Override
            public SimpleUnidirectionalConverter<S, T> getBackwardConverter() {
                return AbstractSimpleBidirectionalConverter.this.forwardConverter;
            }

            @Override
            public SimpleBidirectionalConverter<S, T> getReversedConverter() {
                return AbstractSimpleBidirectionalConverter.this;
            }
        };
    }

}
