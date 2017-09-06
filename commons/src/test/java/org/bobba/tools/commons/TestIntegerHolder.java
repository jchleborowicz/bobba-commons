package org.bobba.tools.commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class TestIntegerHolder {
    private int number;

    public TestIntegerHolder() {
    }

    public TestIntegerHolder(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TestIntegerHolder that = (TestIntegerHolder) o;

        return new EqualsBuilder()
                .append(number, that.number)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(number)
                .toHashCode();
    }
}
