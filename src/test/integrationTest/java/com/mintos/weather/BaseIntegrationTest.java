package com.mintos.weather;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;

class IntegrationTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
            "spring.r2dbc.url=r2dbc:h2:mem:///~/db/weatherdb",
            "spring.r2dbc.username=sa",
            "spring.r2dbc.password=",
            "spring.liquibase.url=jdbc:h2:mem:~/db/weatherdb;DB_CLOSE_DELAY=-1",
            "spring.liquibase.user=sa",
            "spring.liquibase.password="
        ).applyTo(applicationContext.getEnvironment());
    }
}

@DataR2dbcTest
@ContextConfiguration(initializers = {IntegrationTestInitializer.class})
@AutoConfigureCache
public class BaseIntegrationTest {

    @Autowired
    ConnectionFactory connectionFactory;

    @BeforeEach
    void rollOutTestData(@Value("classpath:db/initIntegrationTestData.sql") Resource script) {
        executeScript(script);
    }

    @AfterEach
    void cleanUpTestData(@Value("classpath:db/clearIntegrationTestData.sql") Resource script) {
        executeScript(script);
    }

    private void executeScript(Resource sqlScript) {
        Mono.from(connectionFactory.create())
            .flatMap(connection -> ScriptUtils.executeSqlScript(connection, sqlScript))
            .block();
    }

}
