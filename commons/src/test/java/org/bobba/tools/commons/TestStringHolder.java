package org.bobba.tools.commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class TestStringHolder {

    private String text;

    public TestStringHolder() {
    }

    public TestStringHolder(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TestStringHolder that = (TestStringHolder) o;

        return new EqualsBuilder()
                .append(text, that.text)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(text)
                .toHashCode();
    }
}
