package org.bobba.tools.commons.conversion;

/**
 * Converts source into target type.
 *
 * @param <S> source type.
 * @param <T> target type.
 */
public interface SimpleUnidirectionalConverter<S, T> {

    T convert(S source) throws Exception;

}
