package org.beanplanet.restclient.domain.http.util;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for {@link IterableUtil}.
 *
 * @author Gary Watson
 */
public class IterableUtilTest {
    @Test
    public void nullSafeWithNullIterable() {
        assertThat(IterableUtil.nullSafe((Iterable<?>) null), notNullValue());
    }

    @Test
    public void nullSafeWithNotNullIterable() {
        List<String> iterable = Arrays.asList("a", "b", "c");
        assertThat(IterableUtil.nullSafe(iterable) == iterable, is(true));
    }

    @Test
    public void nullSafeWithNullArray() {
        assertThat(IterableUtil.nullSafe((Object[]) null), notNullValue());
    }

    @Test
    public void nullSafeWithNotNullArray() {
        String[] array = new String[]{"a", "b", "c"};
        assertThat(IterableUtil.nullSafe(array), Matchers.<Iterable>equalTo(Arrays.asList(array)));
    }

    @Test
    public void toSetWithNull() {
        assertThat(IterableUtil.toSet(null), nullValue());
    }

    @Test
    public void toSet() {
        List<String> iterable = Arrays.asList("a", "e", "a", "i", "e", "o", "u");
        assertThat(IterableUtil.toSet(iterable), equalTo(new LinkedHashSet<>(Arrays.asList("a", "e", "i", "o", "u"))));
    }

    @Test
    public void nullSafeIterableEnumerationNullEnumeration() {
        assertThat(IterableUtil.nullSafeIterableEnumeration(null), Matchers.<Object>equalTo(Collections.emptyList()));
    }

    @Test
    public void nullSafeIterableEnumeration() {
        Iterable<Integer> iterable = IterableUtil.nullSafeIterableEnumeration(new Vector(Arrays.asList(1, 2, 3)).elements());

        assertThat(iterable.iterator().hasNext(), is(true));
        assertThat(iterable.iterator().next(), equalTo(1));
        assertThat(iterable.iterator().hasNext(), is(true));
        assertThat(iterable.iterator().next(), equalTo(2));
        assertThat(iterable.iterator().hasNext(), is(true));
        assertThat(iterable.iterator().next(), equalTo(3));
        assertThat(iterable.iterator().hasNext(), is(false));
    }
}
