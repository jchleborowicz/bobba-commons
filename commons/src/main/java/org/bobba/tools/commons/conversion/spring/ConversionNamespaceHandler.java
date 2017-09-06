package org.bobba.tools.commons.conversion.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class ConversionNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("enumConverter", new EnumConverterBeanDefinitionHandler());
    }
}
