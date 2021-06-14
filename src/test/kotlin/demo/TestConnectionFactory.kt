package demo

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.boot.r2dbc.OptionsCapableConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.stereotype.Component
import org.testcontainers.containers.PostgreSQLContainer
import javax.annotation.PreDestroy

@Component
class TestPostgresContainer : PostgreSQLContainer<TestPostgresContainer>("postgres:13.1") {

    init {
        withInitScript("init.sql")
        start()
    }

    @PreDestroy
    fun destroy() {
        stop()
    }

}

@Configuration
class TestConnectionFactory(val container: TestPostgresContainer): AbstractR2dbcConfiguration() {

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        val options = ConnectionFactoryOptions.builder()
            .option(ConnectionFactoryOptions.DRIVER, "postgres")
            .option(ConnectionFactoryOptions.PROTOCOL, "r2dbc:postgresql")
            .option(ConnectionFactoryOptions.HOST, container.host)
            .option(ConnectionFactoryOptions.PORT, container.firstMappedPort)
            .option(ConnectionFactoryOptions.DATABASE, container.databaseName)
            .option(ConnectionFactoryOptions.USER, container.username)
            .option(ConnectionFactoryOptions.PASSWORD, container.password)
            .build()

        val factory = ConnectionFactories.get(options)

        return OptionsCapableConnectionFactory(options, factory)
    }

}
