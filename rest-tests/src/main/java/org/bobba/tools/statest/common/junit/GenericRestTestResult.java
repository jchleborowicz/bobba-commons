package org.bobba.tools.statest.common.junit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

public class GenericRestTestResult implements Iterable<GenericRestTestResult.Entry> {

    private List<Entry> entries = new ArrayList<Entry>();

    public GenericRestTestResult() {
    }

    public GenericRestTestResult(Object[] objects) {
        addAll(objects);
    }

    public static GenericRestTestResult of(Object... objects) {
        return new GenericRestTestResult(objects);
    }

    public GenericRestTestResult addAll(Object[] objects) {
        notNull(objects, "Argument cannot be null");
        for (Object object : objects) {
            add(object);
        }
        return this;
    }

    public GenericRestTestResult add(Object object) {
        this.entries.add(new Entry(object));
        return this;
    }

    public GenericRestTestResult add(String objectId, Object object) {
        this.entries.add(new Entry(objectId, object));
        return this;
    }

    @Override
    public Iterator<Entry> iterator() {
        return entries.iterator();
    }

    public static final class Entry {
        private String objectId;
        private Object value;

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
