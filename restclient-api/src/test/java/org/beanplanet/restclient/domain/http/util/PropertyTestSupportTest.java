package org.beanplanet.restclient.domain.http.util;

import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.fail;

public class PropertyTestSupportTest {

    @Test
    public void testToStringCheckingPropertiesWithOddNumberOfParams() {
        // Given
        PropertyTestSupport propertyTestSupport = new PropertyTestSupport(new HashMap<>());

        try {
            // When
            propertyTestSupport.testToStringProperties("property1", "value1", "property2");
            fail("Was expecting an exception here, because the number of properties does not match the number of property values");
        } catch (Exception e) {
            // Then
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                       is("You must supply at least one property to check in the toString(), and each property must be followed by its value"));
        }
    }

    @Test
    public void testToStringCheckingPropertiesNoParamsSupplied() {
        // Given
        PropertyTestSupport propertyTestSupport = new PropertyTestSupport(new HashMap<>());

        try {
            // When
            propertyTestSupport.testToStringProperties();
            fail("Was expecting an exception here, because the number of properties does not match the number of property values");
        } catch (Exception e) {
            // Then
            assertThat(e, instanceOf(IllegalArgumentException.class));
            assertThat(e.getMessage(),
                       is("You must supply at least one property to check in the toString(), and each property must be followed by its value"));
        }
    }

    @Test
    public void testToStringCheckingPropertyWithNullValue() {
        // Given
        HashMap<Object, Object> instance = new HashMap<>();
        instance.put("property1", "value1");
        instance.put("property2", null);
        PropertyTestSupport propertyTestSupport = new PropertyTestSupport(instance);

        // When
        propertyTestSupport.testToStringProperties("property1", "value1", "property2", null);

        // Then
        // no failure expected
    }

    @Test
    public void testToStringCheckingPropertyWithNumberValue() {
        // Given
        HashMap<Object, Object> instance = new HashMap<>();
        instance.put("property1", "value1");
        instance.put("property2", 2.3);
        PropertyTestSupport propertyTestSupport = new PropertyTestSupport(instance);

        // When
        propertyTestSupport.testToStringProperties("property1", "value1", "property2", "2.3");

        // Then
        // no failure expected
    }

    @Test
    public void testToStringCheckingProperties() {
        // Given
        HashMap<Object, Object> instance = new HashMap<>();
        instance.put("property1", "value1");
        instance.put("property2", "value2");
        PropertyTestSupport propertyTestSupport = new PropertyTestSupport(instance);

        // When
        propertyTestSupport.testToStringProperties("property1", "value1", "property2", "value2");

        // Then
        // no failure expected
    }

    @Test
    public void testToStringCheckingPropertiesWithMissingProperty() {
        // Given
        HashMap<Object, Object> instance = new HashMap<>();
        instance.put("property1", "value1");
        instance.put("property2", "value2");
        PropertyTestSupport propertyTestSupport = new PropertyTestSupport(instance);

        try {
            // When
            propertyTestSupport.testToStringProperties("property1", "value1", "property2", "value2", "propertyXXX", "XXX-value");
            fail("Was expecting an error to be thrown at this point, because a property was missing from the toString()");
        } catch (Error e) {
            // Then
            assertThat(e, instanceOf(AssertionError.class));
            assertThat(e.getMessage(), is("The toString() of type java.util.HashMap did not contain property [propertyXXX] with value [XXX-value]"));
        } catch (Throwable throwable) {
            fail("Should not have arrived here, as they say...test has failed...");
        }
    }

    @Test
    public void testToStringCheckingPropertiesWithExclusionsWhereExcludedNameIsInString() {
        // Given
        HashMap<Object, Object> instance = new HashMap<>();
        instance.put("property1", "value1");
        instance.put("property2", "value2");
        PropertyTestSupport propertyTestSupport = new PropertyTestSupport(instance);

        try {
            // When
            propertyTestSupport.withExcludedToStringProperties("property1").testToStringProperties("property1", "value1", "property2", "value2");
            fail("Was expecting an error to be thrown at this point, because an excluded property appears in the toString()");
        } catch (Error e) {
            // Then
            assertThat(e, instanceOf(AssertionError.class));
            assertThat(e.getMessage(), is("The toString() of type java.util.HashMap contained excluded property [property1]"));
        } catch (Throwable throwable) {
            fail("Should not have arrived here, as they say...test has failed...");
        }
    }

    @Test
    public void testToStringCheckingPropertiesWithExclusions() {
        // Given
        HashMap<Object, Object> instance = new HashMap<>();
        instance.put("property1", "value1");
        instance.put("property2", "value2");
        PropertyTestSupport propertyTestSupport = new PropertyTestSupport(instance);

        // When
        propertyTestSupport.withExcludedToStringProperties("anotherProperty").testToStringProperties("property1", "value1", "property2", "value2");

        // Then
        // no failure expected
    }
}