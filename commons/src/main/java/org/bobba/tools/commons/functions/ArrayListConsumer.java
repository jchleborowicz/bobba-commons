package org.bobba.tools.commons.functions;

import com.google.common.collect.Lists;

import java.util.List;

public class ArrayListConsumer<T> implements Consumer<T> {

    private final List<T> content = Lists.newArrayList();

    @Override
    public void accept(T t) {
        content.add(t);
    }

    public List<T> getContent() {
        return content;
    }
}
