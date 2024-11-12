package org.beanplanet.restclient.synchronous.request;

import org.beanplanet.core.net.http.Request;
import org.beanplanet.restclient.AbstractContainerisedTest;
import org.beanplanet.restclient.HttpBinAnythingResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class HttpVersionTest extends AbstractContainerisedTest {
    @ParameterizedTest
    @EnumSource(value = Request.Version.class, mode = EnumSource.Mode.EXCLUDE, names = "HTTP_1_0")
    void givenAnHttpVersion_whenARequestWithTheVersionConfigured_thenTheHttpVersionIsSentCorrectly(final Request.Version version) {
        client.version(version)
              .get("http://localhost:" + httpbin.getFirstMappedPort() + "/anything")
              .execute()
              .body(HttpBinAnythingResponse.class);
    }
}
