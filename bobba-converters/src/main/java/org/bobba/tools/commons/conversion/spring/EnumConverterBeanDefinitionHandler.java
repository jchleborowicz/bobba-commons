package org.bobba.tools.commons.conversion.spring;

import org.bobba.tools.commons.conversion.ConversionException;
import org.bobba.tools.commons.conversion.SimpleUnidirectionalConverter;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.Validate.notNull;

public class EnumConverterBeanDefinitionHandler extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return SimpleUnidirectionalConverter.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        final AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        beanDefinition.setBeanClass(EnumPairMappingConverterFactoryBean.class);

        final MutablePropertyValues propertyValues = createPropertyValuesDefinition(element);

        beanDefinition.setPropertyValues(propertyValues);
    }

    private MutablePropertyValues createPropertyValuesDefinition(Element element) {
        final MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("sourceClass", getSourceEnumClass(element));
        propertyValues.add("targetClass", getTargetEnumClass(element));
        propertyValues.add("mappings", getMappings(element));
        return propertyValues;
    }

    private String getSourceEnumClass(Element element) {
        return getNotNullElement(element, "sourceEnumClass");
    }

    private String getTargetEnumClass(Element element) {
        return getNotNullElement(element, "targetEnumClass");
    }

    private Map<String, String> getMappings(Element element) {
        final List<Element> mappings = DomUtils.getChildElementsByTagName(element, "map");

        final HashMap<String, String> result = new HashMap<>();
        for (Element mapping : mappings) {
            final String source = mapping.getAttribute("source");
            if (result.containsKey(source)) {
                throw new ConversionException("Enum conversion key defined twice: " + source);
            }
            final String target = mapping.getAttribute("target");
            result.put(source, target);
        }
        return result;
    }

    private String getNotNullElement(Element element, String attributeName) {
        final String result = element.getAttribute(attributeName);
        return notNull(result);
    }
}
