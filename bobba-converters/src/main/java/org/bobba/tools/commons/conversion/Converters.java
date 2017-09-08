package org.bobba.tools.commons.conversion;

import com.google.common.collect.ImmutableMap;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.Validate.notNull;

public final class Converters {

    private Converters() {
    }

    public static <S> SimpleUnidirectionalConverter<S, S> sameObjectConverter() {
        return source -> source;
    }

    public static <SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE> Map<TARGET_KEY, TARGET_VALUE> convertOptionalMap(
            Map<SOURCE_KEY, SOURCE_VALUE> source,
            final SimpleUnidirectionalConverter<SOURCE_KEY, TARGET_KEY> keyConverter,
            final SimpleUnidirectionalConverter<SOURCE_VALUE, TARGET_VALUE> valueConverter) {
        return convertOptionalMap(source, keyConverter, valueConverter, null);
    }

    public static <SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE> Map<TARGET_KEY, TARGET_VALUE> convertOptionalMap(
            Map<SOURCE_KEY, SOURCE_VALUE> source,
            final SimpleUnidirectionalConverter<SOURCE_KEY, TARGET_KEY> keyConverter,
            final SimpleUnidirectionalConverter<SOURCE_VALUE, TARGET_VALUE> valueConverter,
            Map<TARGET_KEY, TARGET_VALUE> nullSourceMapping) {
        final SimpleUnidirectionalConverter<Map<SOURCE_KEY, SOURCE_VALUE>, Map<TARGET_KEY, TARGET_VALUE>> mapConverter =
                newOptionalMapConverter(keyConverter, valueConverter, nullSourceMapping);
        return convertAndHandleException(mapConverter, source);
    }

    public static <SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE> Map<TARGET_KEY, TARGET_VALUE> convertRequiredMap(
            Map<SOURCE_KEY, SOURCE_VALUE> source,
            final SimpleUnidirectionalConverter<SOURCE_KEY, TARGET_KEY> keyConverter,
            final SimpleUnidirectionalConverter<SOURCE_VALUE, TARGET_VALUE> valueConverter) {
        final SimpleUnidirectionalConverter<Map<SOURCE_KEY, SOURCE_VALUE>, Map<TARGET_KEY, TARGET_VALUE>> mapConverter =
                newRequiredMapConverter(keyConverter, valueConverter);
        return convertAndHandleException(mapConverter, source);
    }

    public static <SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE>
    SimpleUnidirectionalConverter<Map<SOURCE_KEY, SOURCE_VALUE>, Map<TARGET_KEY, TARGET_VALUE>> newRequiredMapConverter(
            final SimpleUnidirectionalConverter<SOURCE_KEY, TARGET_KEY> keyConverter,
            final SimpleUnidirectionalConverter<SOURCE_VALUE, TARGET_VALUE> valueConverter) {
        return newMapConverter(keyConverter, valueConverter, false, null);
    }

    public static <SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE>
    SimpleUnidirectionalConverter<Map<SOURCE_KEY, SOURCE_VALUE>, Map<TARGET_KEY, TARGET_VALUE>> newOptionalMapConverter(
            final SimpleUnidirectionalConverter<SOURCE_KEY, TARGET_KEY> keyConverter,
            final SimpleUnidirectionalConverter<SOURCE_VALUE, TARGET_VALUE> valueConverter) {
        return newMapConverter(keyConverter, valueConverter, true, null);
    }

    public static <SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE>
    SimpleUnidirectionalConverter<Map<SOURCE_KEY, SOURCE_VALUE>, Map<TARGET_KEY, TARGET_VALUE>> newOptionalMapConverter(
            final SimpleUnidirectionalConverter<SOURCE_KEY, TARGET_KEY> keyConverter,
            final SimpleUnidirectionalConverter<SOURCE_VALUE, TARGET_VALUE> valueConverter,
            Map<TARGET_KEY, TARGET_VALUE> nullSourceMapping) {
        return newMapConverter(keyConverter, valueConverter, true, nullSourceMapping);
    }

    public static <SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE>
    SimpleUnidirectionalConverter<Map<SOURCE_KEY, SOURCE_VALUE>, Map<TARGET_KEY, TARGET_VALUE>> newRequiredMapConverter(
            SimpleUnidirectionalConverter<Map.Entry<SOURCE_KEY, SOURCE_VALUE>,
                    Map.Entry<TARGET_KEY, TARGET_VALUE>> entryConverter) {

        return newMapConverter(entryConverter, true, null);
    }

    public static <SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE>
    SimpleUnidirectionalConverter<Map<SOURCE_KEY, SOURCE_VALUE>, Map<TARGET_KEY, TARGET_VALUE>> newOptionalMapConverter(
            SimpleUnidirectionalConverter<Map.Entry<SOURCE_KEY, SOURCE_VALUE>,
                    Map.Entry<TARGET_KEY, TARGET_VALUE>> entryConverter) {
        return newMapConverter(entryConverter, true, null);
    }

    public static <SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE>
    SimpleUnidirectionalConverter<Map<SOURCE_KEY, SOURCE_VALUE>, Map<TARGET_KEY, TARGET_VALUE>> newOptionalMapConverter(
            SimpleUnidirectionalConverter<Map.Entry<SOURCE_KEY, SOURCE_VALUE>,
                    Map.Entry<TARGET_KEY, TARGET_VALUE>> entryConverter,
            Map<TARGET_KEY, TARGET_VALUE> nullSourceMapping) {
        return newMapConverter(entryConverter, true, nullSourceMapping);
    }

    private static <SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE>
    SimpleUnidirectionalConverter<Map<SOURCE_KEY, SOURCE_VALUE>, Map<TARGET_KEY, TARGET_VALUE>> newMapConverter(
            final SimpleUnidirectionalConverter<SOURCE_KEY, TARGET_KEY> keyConverter,
            final SimpleUnidirectionalConverter<SOURCE_VALUE, TARGET_VALUE> valueConverter, boolean nullSourceAllowed,
            Map<TARGET_KEY, TARGET_VALUE> nullSourceMapping) {
        notNull(keyConverter);
        notNull(valueConverter);

        final SimpleUnidirectionalConverter<Map.Entry<SOURCE_KEY, SOURCE_VALUE>, Map.Entry<TARGET_KEY, TARGET_VALUE>>
                entryConverter =
                (Map.Entry<SOURCE_KEY, SOURCE_VALUE> source) -> {
                    final TARGET_KEY key = keyConverter.convert(source.getKey());
                    final TARGET_VALUE value = valueConverter.convert(source.getValue());

                    return new AbstractMap.SimpleEntry<>(key, value);
                };

        return newMapConverter(entryConverter, nullSourceAllowed, nullSourceMapping);
    }

    private static <SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE>
    SimpleUnidirectionalConverter<Map<SOURCE_KEY, SOURCE_VALUE>, Map<TARGET_KEY, TARGET_VALUE>> newMapConverter(
            final SimpleUnidirectionalConverter<Map.Entry<SOURCE_KEY, SOURCE_VALUE>,
                    Map.Entry<TARGET_KEY, TARGET_VALUE>> entryConverter,
            boolean nullSourceAllowed,
            Map<TARGET_KEY, TARGET_VALUE> nullSourceMapping) {
        notNull(entryConverter);
        return new AbstractMapConverter<SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE>(
                nullSourceAllowed, nullSourceMapping) {
            @Override
            protected Map.Entry<TARGET_KEY, TARGET_VALUE> convertEntry(Map.Entry<SOURCE_KEY, SOURCE_VALUE> entry)
                    throws Exception {
                return entryConverter.convert(entry);
            }
        };
    }

    public static <S, T> List<T> convertList(List<S> sourceList, SimpleUnidirectionalConverter<S, T> elementConverter) {
        if (sourceList == null) {
            return null;
        }
        final ArrayList<T> result = new ArrayList<>();
        convertList(sourceList, elementConverter, result::add);
        return result;
    }

    public static <S, T> void convertList(List<S> sourceList, SimpleUnidirectionalConverter<S, T> elementConverter,
                                          ElementConversionCallback<T> callback) {
        for (S source : sourceList) {
            final T target = convertAndHandleException(elementConverter, source);
            callback.onConvertedElement(target);
        }
    }

    public static <S, T> T convertAndHandleException(SimpleUnidirectionalConverter<S, T> elementConverter, S source) {
        notNull(elementConverter, "Converter cannot be null");
        try {
            return elementConverter.convert(source);
        } catch (ConversionException e) {
            throw e;
        } catch (Exception e) {
            throw new ConversionException("Exception when converting source: " + source, e);
        }
    }

    public static <S, T> Map.Entry<S, T> createSimpleMapEntry(S targetKey, T targetValue) {
        return new AbstractMap.SimpleEntry<>(targetKey, targetValue);
    }

    @SuppressWarnings("unchecked")
    public static MapConverterBuilder<?, ?, ?, ?> newMapConverter() {
        return new MapConverterBuilder(null, null);
    }

    @SuppressWarnings("unchecked")
    public static <SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE>
    MapConverterBuilder<SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE> newMapConverter(
            SimpleUnidirectionalConverter<SOURCE_KEY, TARGET_KEY> keyConverter,
            SimpleUnidirectionalConverter<SOURCE_VALUE, TARGET_VALUE> valueConverter) {
        return new MapConverterBuilder(keyConverter, valueConverter);
    }

    public static <S extends Enum<S>> SimpleUnidirectionalConverter<String, S> enumConverter(Class<S> enumClass) {
        return new StringToEnumConverter<>(enumClass);
    }

    public interface ElementConversionCallback<T> {
        void onConvertedElement(T target);
    }

    public static final class MapConverterBuilder<SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE> {

        private SimpleUnidirectionalConverter<SOURCE_KEY, TARGET_KEY> keyConverter;
        private SimpleUnidirectionalConverter<SOURCE_VALUE, TARGET_VALUE> valueConverter;
        private boolean nullSourceAllowed;
        private Map<TARGET_KEY, TARGET_VALUE> nullSourceMapping;

        public MapConverterBuilder(SimpleUnidirectionalConverter<SOURCE_KEY, TARGET_KEY> keyConverter,
                                   SimpleUnidirectionalConverter<SOURCE_VALUE, TARGET_VALUE> valueConverter) {
            this.keyConverter = keyConverter;
            this.valueConverter = valueConverter;
        }

        @SuppressWarnings("unchecked")
        public <NEW_SOURCE_KEY, NEW_TARGET_KEY>
        MapConverterBuilder<NEW_SOURCE_KEY, SOURCE_VALUE, NEW_TARGET_KEY, TARGET_VALUE> withKeyConverter(
                SimpleUnidirectionalConverter<NEW_SOURCE_KEY, NEW_TARGET_KEY> converter) {

            this.keyConverter = (SimpleUnidirectionalConverter) converter;
            return (MapConverterBuilder) this;
        }

        @SuppressWarnings("unchecked")
        public <NEW_SOURCE_VALUE, NEW_TARGET_VALUE>
        MapConverterBuilder<SOURCE_KEY, NEW_SOURCE_VALUE, TARGET_KEY, NEW_TARGET_VALUE> withValueConverter(
                SimpleUnidirectionalConverter<NEW_SOURCE_VALUE, NEW_TARGET_VALUE> converter) {

            this.valueConverter = (SimpleUnidirectionalConverter) converter;
            return (MapConverterBuilder) this;
        }

        public MapConverterBuilder<SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE> allowNullValue() {
            return mapNullValueTo(null);
        }

        public MapConverterBuilder<SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE> mapNullValueToEmptyMap() {
            return mapNullValueTo(ImmutableMap.of());
        }

        public MapConverterBuilder<SOURCE_KEY, SOURCE_VALUE, TARGET_KEY, TARGET_VALUE> mapNullValueTo(
                Map<TARGET_KEY, TARGET_VALUE> defaultNullValue) {
            this.nullSourceAllowed = true;
            this.nullSourceMapping = defaultNullValue;
            return this;
        }

        public SimpleUnidirectionalConverter<Map<SOURCE_KEY, SOURCE_VALUE>, Map<TARGET_KEY, TARGET_VALUE>> build() {
            return newMapConverter(keyConverter, valueConverter, nullSourceAllowed, nullSourceMapping);
        }
    }

}
