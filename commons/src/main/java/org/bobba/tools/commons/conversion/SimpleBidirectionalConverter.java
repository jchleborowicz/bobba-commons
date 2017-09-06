package org.bobba.tools.commons.conversion;

public interface SimpleBidirectionalConverter<S, T> {

    T convertForward(S source) throws Exception;

    S convertBackward(T source) throws Exception;

    SimpleUnidirectionalConverter<S, T> getForwardConverter();

    SimpleUnidirectionalConverter<T, S> getBackwardConverter();

    SimpleBidirectionalConverter<T, S> getReversedConverter();

}
