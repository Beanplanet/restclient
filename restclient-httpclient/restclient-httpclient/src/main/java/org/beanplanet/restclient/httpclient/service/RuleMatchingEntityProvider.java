package org.beanplanet.restclient.httpclient.service;

import org.apache.commons.collections4.Predicate;
import org.apache.http.HttpMessage;

/**
 * A rule matching entity provider. The rule executes in context of an HTTP message (request/response).
 *
 * @author Gary Watson
 */
public interface RuleMatchingEntityProvider extends EntityProvider {
    Predicate<HttpMessage> getMatcher();
}
