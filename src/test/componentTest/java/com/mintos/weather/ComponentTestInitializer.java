package com.mintos.weather;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

public class ComponentTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static MockServerContainer mockServerContainer;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        mockServerContainer = new MockServerContainer(DockerImageName.parse("mockserver/mockserver"));
        mockServerContainer.start();
        TestPropertyValues.of(
            "spring.r2dbc.url=r2dbc:h2:mem:///~/db/weatherdb",
            "spring.r2dbc.username=sa",
            "spring.r2dbc.password=",
            "spring.liquibase.url=jdbc:h2:mem:~/db/weatherdb;DB_CLOSE_DELAY=-1",
            "spring.liquibase.user=sa",
            "spring.liquibase.password=",
            String.format("weatherapi.base-uri=http://%s:%d/weather", mockServerContainer.getHost(), mockServerContainer.getServerPort()),
            String.format("ipapi.base-uri=http://%s:%d/ip", mockServerContainer.getHost(), mockServerContainer.getServerPort())
        ).applyTo(applicationContext.getEnvironment());
    }
}
