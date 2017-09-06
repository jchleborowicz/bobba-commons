package org.bobba.tools.commons.conversion;

public abstract class AbstractBidirectionalConverter<S, T>
        extends AbstractSimpleBidirectionalConverter<S, T> implements BidirectionalConverter<S, T> {

    private final UnidirectionalConverter<S, T> forwardConverter;
    private final UnidirectionalConverter<T, S> backwardConverter;

    public AbstractBidirectionalConverter(Class<S> classA, Class<T> classB) {
        this.forwardConverter = new AbstractUnidirectionalConverter<S, T>(classA, classB) {
            @Override
            protected void safeConvert(S source, T target) throws Exception {
                safeConvertForward(source, target);
            }
        };
        this.backwardConverter = new AbstractUnidirectionalConverter<T, S>(classB, classA) {
            @Override
            protected void safeConvert(T source, S target) throws Exception {
                safeConvertBackward(source, target);
            }
        };
    }

    @Override
    public final void convertForward(S source, T target) throws Exception {
        forwardConverter.convert(source, target);
    }

    @Override
    public final void convertBackward(T source, S target) throws Exception {
        backwardConverter.convert(source, target);
    }

    @Override
    protected T safeConvertForward(S source) throws Exception {
        return forwardConverter.convert(source);
    }

    @Override
    protected S safeConvertBackward(T source) throws Exception {
        return backwardConverter.convert(source);
    }

    /**
     * It is safe to assume that both source and target are not null.
     */
    protected abstract void safeConvertForward(S source, T target) throws Exception;

    /**
     * It is safe to assume that both source and target are not null.
     */
    protected abstract void safeConvertBackward(T source, S target) throws Exception;

    @Override
    public UnidirectionalConverter<S, T> getForwardConverter() {
        return forwardConverter;
    }

    @Override
    public UnidirectionalConverter<T, S> getBackwardConverter() {
        return backwardConverter;
    }

    @Override
    public BidirectionalConverter<T, S> getReversedConverter() {
        return new BidirectionalConverter<T, S>() {

            private final UnidirectionalConverter<T, S> forwardConverter =
                    AbstractBidirectionalConverter.this.backwardConverter;
            private final UnidirectionalConverter<S, T> backwardConverter =
                    AbstractBidirectionalConverter.this.forwardConverter;

            @Override
            public S convertForward(T source) throws Exception {
                return this.forwardConverter.convert(source);
            }

            @Override
            public void convertForward(T source, S target) throws Exception {
                this.forwardConverter.convert(source, target);
            }

            @Override
            public T convertBackward(S source) throws Exception {
                return this.backwardConverter.convert(source);
            }

            @Override
            public void convertBackward(S source, T target) throws Exception {
                this.backwardConverter.convert(source, target);
            }

            @Override
            public UnidirectionalConverter<T, S> getForwardConverter() {
                return this.forwardConverter;
            }

            @Override
            public UnidirectionalConverter<S, T> getBackwardConverter() {
                return this.backwardConverter;
            }

            @Override
            public BidirectionalConverter<S, T> getReversedConverter() {
                return AbstractBidirectionalConverter.this;
            }
        };
    }
}
