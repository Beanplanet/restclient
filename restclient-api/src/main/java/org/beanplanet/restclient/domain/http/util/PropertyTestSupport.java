package org.beanplanet.restclient.domain.http.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.joda.time.DateTime;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

import static org.beanplanet.restclient.domain.http.util.IterableUtil.nullSafe;
import static org.joda.time.DateTimeZone.UTC;


/**
 * A useful support class for testing readable and writable properties on beans.
 *
 * @author Gary Watson
 */
public final class PropertyTestSupport {
    private Set<Class<?>> PRIMATIVE_TYPES = new HashSet<>(Arrays.<Class<?>>asList(boolean.class, byte.class, char.class, double.class, float.class, int.class, long.class, short.class));

    public static interface PropertyValueGenerator<T> {
        T generateValue(BeanWrapper bean, String propertyName);
    }

    public static interface ValueGenerator<T> {
        T generateValue(Class<?> valueClass);
    }

    private static final Map<Class<?>, ValueGenerator<?>> propertyValueGenerators = new HashMap<>();

    private static final Map<Predicate<Class<?>>, ValueGenerator<?>> DEFAULT_PREDICATED_VALUE_GENERATORS = new HashMap<>();

    private final Map<Predicate<Class<?>>, ValueGenerator<?>> predicatedPropertyValueGenerators = new HashMap<>(DEFAULT_PREDICATED_VALUE_GENERATORS);

    private final Set<String> excludedPropertyNames = new LinkedHashSet<>();

    private final Set<String> excludedToStringPropertyNames = new LinkedHashSet<>();

    private final Set<String> excludedBuilderMethodNames = new LinkedHashSet<>();

    private boolean testWithNullValues = true;

    private Object instance;

    static {

    }

    static {
        propertyValueGenerators.put(String.class, new ValueGenerator<String>() {
            public String generateValue(Class<?> valueClass) {
                return "thePropertyValue";
            }
        });
        propertyValueGenerators.put(Boolean.class, new ValueGenerator<Boolean>() {
            public Boolean generateValue(Class<?> valueClass) {
                return Boolean.TRUE;
            }
        });
        propertyValueGenerators.put(boolean.class, new ValueGenerator<Boolean>() {
            public Boolean generateValue(Class<?> valueClass) {
                return Boolean.TRUE;
            }
        });
        propertyValueGenerators.put(Date.class, new ValueGenerator<Date>() {
            public Date generateValue(Class<?> valueClass) {
                return new Date();
            }
        });
        propertyValueGenerators.put(Double.class, new ValueGenerator<Double>() {
            public Double generateValue(Class<?> valueClass) {
                return Math.random() * Double.MAX_VALUE;
            }
        });
        propertyValueGenerators.put(Float.class, new ValueGenerator<Float>() {
            public Float generateValue(Class<?> valueClass) {
                return (float) (Math.random() * Float.MAX_VALUE);
            }
        });
        propertyValueGenerators.put(int.class, new ValueGenerator<Integer>() {
            public Integer generateValue(Class<?> valueClass) {
                return (int) (Math.random() * Integer.MAX_VALUE);
            }
        });
        propertyValueGenerators.put(Integer.class, new ValueGenerator<Integer>() {
            public Integer generateValue(Class<?> valueClass) {
                return (int) (Math.random() * Integer.MAX_VALUE);
            }
        });
        propertyValueGenerators.put(List.class, new ValueGenerator<List>() {
            public List generateValue(Class<?> valueClass) {
                return Collections.emptyList();
            }
        });
        propertyValueGenerators.put(Long.class, new ValueGenerator<Long>() {
            public Long generateValue(Class<?> valueClass) {
                return (long) (Math.random() * Long.MAX_VALUE);
            }
        });
        propertyValueGenerators.put(long.class, new ValueGenerator<Long>() {
            public Long generateValue(Class<?> valueClass) {
                return (long)(Math.random() * Long.MAX_VALUE);
            }
        });
        propertyValueGenerators.put(Set.class, new ValueGenerator<Set>() {
            public Set generateValue(Class<?> valueClass) {
                return Collections.emptySet();
            }
        });
        propertyValueGenerators.put(DateTime.class, new ValueGenerator<DateTime>() {
            @Override
            public DateTime generateValue(Class<?> valueClass) {
                return new DateTime(UTC);
            }
        });

        DEFAULT_PREDICATED_VALUE_GENERATORS.put(new Predicate<Class<?>>() {
            @Override
            public boolean evaluate(Class<?> context) {
                return context.isEnum() && context.getEnumConstants().length > 0;
            }
        }, new ValueGenerator<Object>() {
            @Override
            public Object generateValue(Class<?> valueClass) {
                return valueClass.getEnumConstants()[0];
            }
        });
    }

    public PropertyTestSupport() {
    }

    public PropertyTestSupport(Object instance) {
        this.instance = instance;
    }

    public final PropertyTestSupport testProperties() {
        testProperties(instance);
        return this;
    }

    public final PropertyTestSupport testProperties(Object instance) {
        BeanWrapper bean = new BeanWrapperImpl(instance);
        Collection<String> propertyNames = CollectionUtils.collect(Arrays.asList(bean.getPropertyDescriptors()), new Transformer<PropertyDescriptor, String>() {
            public String transform(PropertyDescriptor propertyDescriptor) {
                return propertyDescriptor.getName();
            }
        });

        testProperties(instance, propertyNames.toArray(new String[propertyNames.size()]));
        return this;
    }


    public final PropertyTestSupport testProperties(Object instance, String... propertyNames) {
        BeanWrapper bean = new BeanWrapperImpl(instance);
        for (String propertyName : nullSafe(propertyNames)) {
            if (excludedPropertyNames.contains(propertyName)) continue;

            if (bean.isReadableProperty(propertyName) && bean.isWritableProperty(propertyName)) {
                assertPropertyReadableAndWritable(bean, propertyName);
            } else if (bean.isReadableProperty(propertyName)) {
                assertPropertyReadable(bean, propertyName);
            } else if (bean.isWritableProperty(propertyName)) {
                assertPropertyWritable(bean, propertyName);
            }
        }

        return this;
    }

    public final PropertyTestSupport testBuilderProperties() {
        testBuilderProperties(instance);
        return this;
    }

    public final PropertyTestSupport testBuilderProperties(Object instance) {
        Collection<Method> withMethods = CollectionUtils.select(Arrays.asList(instance.getClass().getDeclaredMethods()), new org.apache.commons.collections4.Predicate<Method>() {
            public boolean evaluate(Method method) {
                return method.getName().startsWith("with") && method.getParameterTypes() != null && method.getParameterTypes().length == 1;
            }
        });
        Collection<String> methodNames = CollectionUtils.collect(withMethods, new Transformer<Method, String>() {
            public String transform(Method method) {
                return method.getName();
            }
        });
        testBuilderProperties(instance, methodNames.toArray(new String[methodNames.size()]));
        return this;
    }

    public final PropertyTestSupport testBuilderProperties(final Object instance, String ... methodNames) {
        for (final String methodName : nullSafe(methodNames)) {
            if (excludedBuilderMethodNames.contains(methodName)) continue;

            Collection<Method> withMethods = CollectionUtils.select(Arrays.asList(instance.getClass().getDeclaredMethods()), new org.apache.commons.collections4.Predicate<Method>() {
                public boolean evaluate(Method method) {
                    return method.getName().equals(methodName) && method.getParameterTypes() != null && method.getParameterTypes().length == 1;
                }
            });

            // If the builder has subclasses with the same method prefer the instance class that has the same covariant return type
            if (withMethods.size() > 1) {
                withMethods = CollectionUtils.select(withMethods, new org.apache.commons.collections4.Predicate<Method>() {
                    public boolean evaluate(Method method) {
                        return method.getReturnType() == instance.getClass();
                    }
                });
            }

            if (withMethods.size() != 1) {
                throw new AssertionError(String.format("Unable to find builder method [%s] with single argument signature on instance [%s]", methodName, instance.getClass()));
            }

            Method method = withMethods.iterator().next();
            Class<?> valueClass = method.getParameterTypes()[0];
            ValueGenerator<?> propertyValueGenerator = determinePropertyValueGenerator(valueClass);
            if (propertyValueGenerator == null) {
                throw new IllegalStateException(String.format("No property value generator registered for builder property [%s] type [%s] of instance type [%s]", methodName, valueClass, instance.getClass().getName()));
            }
            Object value = propertyValueGenerator.generateValue(valueClass);

            Object returnValue;
            try {
                returnValue = method.invoke(instance, new Object[]{value});
            } catch (Exception e) {
                throw new AssertionError(e);
            }

            if (returnValue == null || returnValue.getClass() != instance.getClass()) {
                throw new AssertionError(String.format("The builder method method [%s] on instance [%s] returned incorrect value type [%s] when expecting the instance", methodName, instance.getClass(), (returnValue != null ? returnValue.getClass() : null)));
            }
        }
        return this;
    }

    public final PropertyTestSupport testToString() {
        testToString(instance);
        return this;
    }

    public final PropertyTestSupport testToString(Object instance) {
        String toString = instance.toString();

        if (toString == null || toString.trim().length() == 0) {
            throw new AssertionError(String.format("The toString() of type %s was null or empty", instance.getClass().getName()));
        }
        return this;
    }

    /**
     * Simple property name-value pair checker for the toString() method of the object
     * under test that looks for the presence of "name=value" or "name=<value>" in the toString() output.
     *
     * Excluded properties are supported.
     *
     * @param propertyNvp   String array of property names and values, name1,  value1, name2, value3, name3, value3...
     * @return              this PropertyTestSupport object, to facilitate method chaining.
     */
    public final PropertyTestSupport testToStringProperties(String... propertyNvp) {

        if ((propertyNvp.length % 2) != 0 || propertyNvp.length == 0)
            throw new IllegalArgumentException(
                    "You must supply at least one property to check in the toString(), and each property must be followed by its value");

        String toString = instance.toString();

        if (excludedToStringPropertyNames != null) {
            for (String name : excludedToStringPropertyNames) {
                if (toString.contains(name + "=")) {
                    throw new AssertionError(String.format("The toString() of type %s contained excluded property [%s]", instance.getClass().getName(), name));
                }
            }
        }

        for (int i = 0; i < propertyNvp.length - 1; i = i + 2) {
            String property = String.valueOf(propertyNvp[i]);
            String value = propertyNvp[i + 1] == null ? null : String.valueOf(propertyNvp[i + 1]);

            if (!toString.contains(property + "=" + value) && !toString.contains(property + "=<" + value + ">")) {
                throw new AssertionError(String.format("The toString() of type %s did not contain property [%s] with value [%s]", instance.getClass().getName(), property, value));
            }
        }

        return this;
    }
    public PropertyTestSupport withNoArgConstructorValuesGenerator() {
        predicatedPropertyValueGenerators.put(new Predicate<Class<?>>() {
            @Override
            public boolean evaluate(Class<?> context) {
                try {
                    context.getConstructor(null);
                    return true;
                } catch (NoSuchMethodException nsmEx) {
                    return false;
                }
            }
        }, new ValueGenerator<Object>() {
            @Override
            public Object generateValue(Class<?> valueClass) {
                try {
                    return valueClass.newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        return this;
    }

    public <T> PropertyTestSupport withValueGenerator(Class<T> clazz, ValueGenerator<T> generator) {
        propertyValueGenerators.put(clazz, generator);
        return this;
    }

    public PropertyTestSupport withMockValuesGenerator() {
        predicatedPropertyValueGenerators.put(new Predicate<Class<?>>() {
            @Override
            public boolean evaluate(Class<?> context) {
                return context.isInterface();
            }
        }, new ValueGenerator<Object>() {
            @Override
            public Object generateValue(Class<?> valueClass) {
                return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{valueClass}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return null;
                    }
                });
            }
        });

        return this;
    }

    public PropertyTestSupport withMockitoValuesGenerator() {
        predicatedPropertyValueGenerators.put(new Predicate<Class<?>>() {
            @Override
            public boolean evaluate(Class<?> context) {
                // Dynamically invoke if on classpath ONLY
                try {
                    Class.forName("org.mockito.Mockito");
                    return true;
                } catch (ClassNotFoundException cnfEx) {
                    return false;
                }
            }
        }, new ValueGenerator<Object>() {
            @Override
            public Object generateValue(Class<?> valueClass) {
                try {
                    Class<?> mockitoClass = Class.forName("org.mockito.Mockito");

                    Method mockMethod = mockitoClass.getMethod("mock", new Class<?>[]{Class.class});

                    return mockMethod.invoke(null, valueClass);
                } catch (Exception ex) {
                    throw new RuntimeException("Unable to use Mockito to generate mocks: ", ex);
                }
            }
        });

        return this;
    }

    public PropertyTestSupport withExcludedProperties(String... propertyNames) {
        if (propertyNames != null) {
            excludedPropertyNames.addAll(Arrays.asList(propertyNames));
        }
        return this;
    }

    public PropertyTestSupport withExcludedToStringProperties(String... propertyNames) {
        if (propertyNames != null) {
            excludedToStringPropertyNames.addAll(Arrays.asList(propertyNames));
        }
        return this;
    }

    public PropertyTestSupport withExcludedBuilderMethodNames(String... methodNames) {
        if (methodNames != null) {
            excludedBuilderMethodNames.addAll(Arrays.asList(methodNames));
        }
        return this;
    }

    public PropertyTestSupport withTestWithNullValues(Boolean testWithNullValues) {
        this.testWithNullValues = testWithNullValues;
        return this;
    }

    public static void setProperty(Object obj, String fieldName, Object fieldValue) {
        Field field = ReflectionUtils.findField(obj.getClass(), fieldName);
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, obj, fieldValue);
    }

    private void assertPropertyReadableAndWritable(BeanWrapper bean, String propertyName) {
        Object value = generatePropertyValue(bean, propertyName);
        bean.setPropertyValue(propertyName, value);
        Object retrievedPropertyValue = bean.getPropertyValue(propertyName);
        if (!Objects.equals(value, retrievedPropertyValue)) {
            throw new AssertionError(String.format("The property get/set tests on bean [type=%s] failed on property %s. Set value [%s] did not match retrieved value [%s]", bean.getWrappedInstance().getClass().getName(), propertyName, value, retrievedPropertyValue));
        }

        if (testWithNullValues && !PRIMATIVE_TYPES.contains(bean.getPropertyType(propertyName))) {
            bean.setPropertyValue(propertyName, null);
            retrievedPropertyValue = bean.getPropertyValue(propertyName);
            if (retrievedPropertyValue != null) {
                throw new AssertionError(String.format("The property get/set tests on bean [type=%s] failed on property %s. Set null value did not match retrieved value [%s]", bean.getWrappedInstance().getClass().getName(), propertyName, retrievedPropertyValue));
            }
        }
    }

    private void assertPropertyReadable(BeanWrapper bean, String propertyName) {
        // Just call it ...
        bean.getPropertyValue(propertyName);
    }

    private void assertPropertyWritable(BeanWrapper bean, String propertyName) {
        // Just call it ...
        bean.setPropertyValue(propertyName, generatePropertyValue(bean, propertyName));
    }

    private Object generatePropertyValue(BeanWrapper bean, String propertyName) {
        Class<?> propertyType = bean.getPropertyType(propertyName);
        ValueGenerator<?> propertyValueGenerator = determinePropertyValueGenerator(propertyType);
        if (propertyValueGenerator == null) {
            throw new IllegalStateException(String.format("No property value generator registered for property [%s] type [%s] of bean type [%s]", propertyName, propertyType, bean.getWrappedInstance().getClass().getName()));
        }
        return propertyValueGenerator.generateValue(propertyType);
    }

    private <T> ValueGenerator<T> determinePropertyValueGenerator(Class<?> propertyType) {
        ValueGenerator<?> propertyValueGenerator = propertyValueGenerators.get(propertyType);
        if (propertyValueGenerator != null) return (ValueGenerator<T>) propertyValueGenerator;

        for (Map.Entry<Predicate<Class<?>>, ValueGenerator<?>> entry : predicatedPropertyValueGenerators.entrySet()) {
            if (entry.getKey().evaluate(propertyType)) {
                return (ValueGenerator<T>) entry.getValue();
            }
        }

        return null;
    }
}
