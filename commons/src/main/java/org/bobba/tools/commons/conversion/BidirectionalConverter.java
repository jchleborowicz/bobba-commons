package org.bobba.tools.commons.conversion;

public interface BidirectionalConverter<S, T> extends SimpleBidirectionalConverter<S, T> {

    void convertForward(S source, T target) throws Exception;

    void convertBackward(T source, S target) throws Exception;

    UnidirectionalConverter<S, T> getForwardConverter();

    UnidirectionalConverter<T, S> getBackwardConverter();

    BidirectionalConverter<T, S> getReversedConverter();

}
