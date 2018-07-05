package org.beanplanet.restclient.httpclient;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpMessage;
import org.apache.http.entity.ContentType;

import java.util.regex.Pattern;

/**
 * Useful HTTP entity matching functions.
 *
 * @author Gary Watson
 */
public class EntityMatchers {
    public static Predicate<HttpMessage> mimeTypes(String ... mimeTypeRegex) {
        if (mimeTypeRegex == null) return PredicateUtils.falsePredicate();

        final Pattern mimePatterns[] = new Pattern[mimeTypeRegex.length];
        for (int n=0; n < mimeTypeRegex.length; n++) {
            mimePatterns[n] = Pattern.compile(mimeTypeRegex[n]);
        }

        return new Predicate<HttpMessage>() {
            @Override
            public boolean evaluate(HttpMessage message) {
                Header header = message.getLastHeader(HttpHeaders.CONTENT_TYPE);
                if (header == null) return false;

                ContentType contentType = ContentType.parse(header.getValue());

                for (int n=0; n < mimePatterns.length; n++) {
                    if (mimePatterns[n].matcher(contentType.getMimeType()).matches()) {
                        return true;
                    }
                }

                return false;
            }
        };
    }
}
