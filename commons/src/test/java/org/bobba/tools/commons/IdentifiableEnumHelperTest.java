package org.bobba.tools.commons;

import org.bobba.tools.commons.IdentifiableEnum;
import org.bobba.tools.commons.IdentifiableEnumHelper;
import org.bobba.tools.commons.IdentifiableEnumIdNotFoundException;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class IdentifiableEnumHelperTest {

    @Test
    public void returnsCollectEnumItem() {
        final Map<String, TestEnum> test = IdentifiableEnumHelper.create(TestEnum.values());
        assertThat(test.get("1"), is(TestEnum.FIRST));
        assertThat(test.get("2"), is(TestEnum.SECOND));
        assertThat(test.get("3"), is(TestEnum.THIRD));
    }

    @Test(expected = IdentifiableEnumIdNotFoundException.class)
    public void throwsExceptionWhenElementNotFound() {
        final Map<String, TestEnum> test = IdentifiableEnumHelper.create(TestEnum.values());
        test.get("4");
    }

    @Test(expected = RuntimeException.class)
    public void throwsExceptionWhenIdRepeats() {
        IdentifiableEnumHelper.create(EnumWithRepeatingId.values());
    }

    public enum TestEnum implements IdentifiableEnum<String> {
        FIRST("1"),
        SECOND("2"),
        THIRD("3");

        private final String id;

        TestEnum(String id) {
            this.id = id;
        }


        @Override
        public String getId() {
            return id;
        }
    }

    public enum EnumWithRepeatingId implements IdentifiableEnum<Integer> {
        A(1),
        B(2),
        C(2),
        D(3);

        private final Integer id;

        EnumWithRepeatingId(Integer id) {
            this.id = id;
        }


        @Override
        public Integer getId() {
            return id;
        }
    }

}