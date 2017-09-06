package org.bobba.tools.commons.conversion.spring;

import org.bobba.tools.commons.conversion.Converters;
import org.bobba.tools.commons.conversion.PairMappingConverterBuilder;
import org.bobba.tools.commons.conversion.SimpleUnidirectionalConverter;
import org.springframework.beans.factory.FactoryBean;

import java.util.Map;

import static org.bobba.tools.commons.utils.CommonUtils.checkRequiredClassType;
import static org.apache.commons.lang3.Validate.notEmpty;

public class EnumPairMappingConverterFactoryBean<S extends Enum<S>, T extends Enum<T>>
        implements FactoryBean<SimpleUnidirectionalConverter<S, T>> {

    private Class<S> sourceClass;
    private Class<T> targetClass;
    private Map<String, String> mappings;

    public void setSourceClass(Class<S> sourceClass) {
        this.sourceClass = sourceClass;
    }

    public void setTargetClass(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    public void setMappings(Map<String, String> mappings) {
        this.mappings = mappings;
    }

    @Override
    public SimpleUnidirectionalConverter<S, T> getObject() throws Exception {
        validateProperties();

        final Map<S, T> enumMappings = Converters.convertOptionalMap(mappings, Converters.enumConverter(sourceClass),
                Converters.enumConverter(targetClass));

        return PairMappingConverterBuilder.newInstance(sourceClass, targetClass)
                .mapAll(enumMappings)
                .build();
    }

    private void validateProperties() {
        checkRequiredClassType(sourceClass, Enum.class);
        checkRequiredClassType(targetClass, Enum.class);
        notEmpty(mappings);
    }

    @Override
    public Class<?> getObjectType() {
        return SimpleUnidirectionalConverter.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
