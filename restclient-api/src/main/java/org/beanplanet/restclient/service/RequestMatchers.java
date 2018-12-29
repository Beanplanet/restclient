package org.beanplanet.restclient.service;

import org.apache.commons.collections4.Predicate;
import org.beanplanet.restclient.domain.http.HttpRequest;

import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Useful HTTP Request matchers.
 *
 * @author Gary Watson
 */
public class RequestMatchers {
    public static Predicate<HttpRequest> host(String hostnameRegex) {
        return hosts(hostnameRegex);
    }

    public static Predicate<HttpRequest> hosts(String ... hostnameRegex) {
        final Pattern hostnamePatterns[] = new Pattern[hostnameRegex.length];
        for (int n=0; n < hostnameRegex.length; n++) {
            hostnamePatterns[n] = Pattern.compile(hostnameRegex[n]);
        }

        return new Predicate<HttpRequest>() {
            @Override
            public boolean evaluate(HttpRequest request) {
                if ( isEmpty(request.getHostname()) ) return false;

                for (Pattern hostnamePatternMatcher : hostnamePatterns) {
                    if (hostnamePatternMatcher.matcher(request.getHostname()).matches()) return true;
                }
                return false;
            }
        };
    }
}
