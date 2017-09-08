package org.bobba.tools.commons.conversion;

/**
 * Converts source object to target object.
 *
 * @param <S> source object.
 * @param <T> target object.
 */
public interface UnidirectionalConverter<S, T> extends SimpleUnidirectionalConverter<S, T> {

    void convert(S source, T target) throws Exception;

}
