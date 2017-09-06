package org.bobba.tools.commons.conversion;

import org.bobba.tools.commons.utils.CommonUtils;

public class StringToEnumConverter<T extends Enum<T>> extends AbstractSimpleUnidirectionalConverter<String, T>{

    private final Class<T> enumClass;

    public StringToEnumConverter(Class<T> enumClass) {
        super(String.class, enumClass);
        this.enumClass = enumClass;
    }

    @Override
    protected T safeConvert(String source) throws Exception {
        return CommonUtils.getEnum(enumClass, source);
    }

}
