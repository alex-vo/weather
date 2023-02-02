package com.mintos.weather;

import com.mintos.weather.service.IPAddressResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.Times;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.FileCopyUtils;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@ContextConfiguration(initializers = {ComponentTestInitializer.class})
@AutoConfigureWebTestClient
public abstract class BaseComponentTest {

    public static final String DEFAULT_IP = "1.1.1.1";

    private MockServerClient mockServerClient;

    protected MockServerClient getMockServerClient() {
        if (mockServerClient == null) {
            mockServerClient = new MockServerClient(ComponentTestInitializer.mockServerContainer.getHost(), ComponentTestInitializer.mockServerContainer.getServerPort());
        }

        return mockServerClient;
    }

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    R2dbcEntityTemplate r2dbcEntityTemplate;

    @SpyBean
    protected IPAddressResolver ipAddressResolver;

    @BeforeEach
    public void setUp(@Value("classpath:db/initComponentTestData.sql") Resource script) {
        executeScript(script);
        doReturn(DEFAULT_IP).when(ipAddressResolver).getIp(any(ServerHttpRequest.class));
    }

    @AfterEach
    void tearDown(@Value("classpath:db/clearComponentTestData.sql") Resource script) {
        executeScript(script);
    }

    private void executeScript(Resource sqlScript) {
        String sql = asString(sqlScript);
        r2dbcEntityTemplate.getDatabaseClient()
            .sql(sql)
            .fetch()
            .first()
            .as(StepVerifier::create)
            .verifyComplete();
    }

    private String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected <T> T performGet(String uri, int expectedStatus, Class<T> responseType) {
        return webTestClient
            .get()
            .uri(uri)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(expectedStatus)
            .expectBody(responseType)
            .returnResult()
            .getResponseBody();
    }

    protected <T> T performSuccessfulGet(String uri, Class<T> responseType) {
        return webTestClient
            .get()
            .uri(uri)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(responseType)
            .returnResult()
            .getResponseBody();
    }

    protected void setUpIPAPIResponse(String ip, int status, String body) {
        setUpIPAPIResponse(ip, status, body, true);
    }

    protected void setUpIPAPIResponse(String ip, int status, String body, boolean clearNeeded) {
        if (clearNeeded) {
            getMockServerClient()
                .clear(
                    request()
                        .withMethod("GET")
                        .withPath("/ip/json/" + ip)
                );
        }
        getMockServerClient()
            .when(
                request()
                    .withMethod("GET")
                    .withPath("/ip/json/" + ip),
                Times.exactly(1)
            )
            .respond(
                response()
                    .withStatusCode(status)
                    .withContentType(MediaType.APPLICATION_JSON)
                    .withBody(body)
            );
    }

    protected void setUpWeatherAPIResponse(String region, int status, String body) {
        getMockServerClient()
            .clear(
                request()
                    .withMethod("GET")
                    .withPath("/weather/v1/current.json")
                    .withQueryStringParameter("q", region)
            )
            .when(
                request()
                    .withMethod("GET")
                    .withPath("/weather/v1/current.json")
                    .withQueryStringParameter("q", region)
            )
            .respond(
                response()
                    .withStatusCode(status)
                    .withContentType(MediaType.APPLICATION_JSON)
                    .withBody(body)
            );
    }

}
