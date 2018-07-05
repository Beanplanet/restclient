package org.beanplanet.restclient.httpclient.service;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;

/**
 * The base of an entity provider which can produce content based on a matching predicate or rule.
 *
 * @author Gary Watson
 */
public abstract class AbstractRuleMatchingEntityProvider implements RuleMatchingEntityProvider {
    private Predicate<HttpMessage> matcher;

    public AbstractRuleMatchingEntityProvider() { this(PredicateUtils.<HttpMessage>falsePredicate()); }

    public AbstractRuleMatchingEntityProvider(Predicate<HttpMessage> matcher) {
        this.matcher = matcher;
    }

    /**
     * Determines whether the provider can produce an entity for the given HTTP Message, content type and object.
     *
     * @param message the HTTP message for which the entity is to be created.
     * @param object the object to be converted to an HTTP message entity.
     * @return false if this provider cannot produce an entity of the specified content type for the given object or true if the
     * provider may be able to produce an entity. If true, a subsequent call to {@link #createEntityForObject(HttpMessage, Object)} may be
     * made to attempt to produce an entity.
     */
    public boolean canCreateEntityForObject(HttpMessage message, Object object) {
        return matcher.evaluate(message);
    }

    @Override
    public boolean canCreateObjectForEntity(HttpMessage message, HttpEntity entity, Class<?> clazz) {
        return matcher.evaluate(message);
    }

    @Override
    public Predicate<HttpMessage> getMatcher() {
        return matcher;
    }

    public void setMatcher(Predicate<HttpMessage> matcher) {
        this.matcher = matcher;
    }
}
