package org.beanplanet.restclient.domain.http.util;

import org.apache.commons.collections4.iterators.EnumerationIterator;

import java.util.*;

/**
 * {@link Iterable} utilities that do not seem to exist in Google Iterables or Apache Collections APIs!
 *
 * @author Gary Watson
 */
public final class IterableUtil {
   /**
    * Private constructor for this static utility class.
    */
   private IterableUtil() {
   }

   /**
    * Guarantees to return a non-null {@link Iterable}: either the one specified, if not null, or an iterable with no
    * elements otherwise.
    *
    * @param iterable the iterable which may be null.
    * @return the iterable specified if it was not null or an empty collection otherwise.
    */
   public static <T> Iterable<T> nullSafe(Iterable<T> iterable) {
      return (iterable != null ? iterable : Collections.<T>emptyList());
   }

   /**
    * Guarantees to return a non-null {@link Iterable} for a possibly null array.
    *
    * @param array the array which may be null.
    * @return an iterable, either backed by the array specified if the array was not null, empty collection otherwise.
    */
   public static <T> Iterable<T> nullSafe(T[] array) {
      return (array != null ? Arrays.<T>asList(array) : Collections.<T>emptyList());
   }

   /**
    * Exhaustively consumes all elements from the {@link Iterable} and returns an ordered
    * set of the elements.
    *
    * @param iterable the collections whose elements are to be returned.
    * @return an ordered set of elements taken from the iterable collection or null if the iterable was null.
    */
   public static <T> LinkedHashSet<T> toSet(Iterable<T> iterable) {
      if (iterable == null) {
         return null;
      }

      LinkedHashSet<T> set = new LinkedHashSet<>();
      for (T element : iterable) {
         set.add(element);
      }

      return set;
   }

   /**
    * Guarantees to return a non-null {@link Iterable} for a possibly null enumeration.
    *
    * @param enumeration the enumeration over whose elements the iterable will iterate, which may be null.
    * @return an iterable, either backed by the enumeration specified if the enumeration was not null, or an empty collection otherwise.
    */
   public static <T> Iterable<T> nullSafeIterableEnumeration(final Enumeration<T> enumeration) {
      if (enumeration == null) return Collections.emptyList();

      return new Iterable<T>() {
         public Iterator<T> iterator() {
            return new EnumerationIterator<T>(enumeration);
         }
      };
   }
}
