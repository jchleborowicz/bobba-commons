package org.bobba.tools.statest.common.junit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

public class GenericStatestResult implements Iterable<GenericStatestResult.Entry> {

    private final List<Entry> entries = new ArrayList<>();

    public GenericStatestResult() {
    }

    public GenericStatestResult(Object[] objects) {
        addAll(objects);
    }

    public static GenericStatestResult of(Object... objects) {
        return new GenericStatestResult(objects);
    }

    public GenericStatestResult addAll(Object[] objects) {
        notNull(objects, "Argument cannot be null");
        for (Object object : objects) {
            add(object);
        }
        return this;
    }

    public GenericStatestResult add(Object object) {
        this.entries.add(new Entry(object));
        return this;
    }

    public GenericStatestResult add(String objectId, Object object) {
        this.entries.add(new Entry(objectId, object));
        return this;
    }

    @Override
    public Iterator<Entry> iterator() {
        return entries.iterator();
    }

    public static final class Entry {
        private String objectId;
        private final Object value;

        public Entry(Object value) {
            notNull(value, "Unnamed null values not supported");
            this.value = value;
        }

        public Entry(String objectId, Object value) {
            this.objectId = notEmpty(objectId, "Object ID is empty");
            this.value = value;
        }

        public String getObjectId() {
            return objectId;
        }

        public Object getValue() {
            return value;
        }
    }

}
