package com.softserve.itacademy.config;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

@Configuration
@Profile("h2")
public class H2Config {

    /**
     * TCP server which allows to connect to the H2 database using IntelliJ IDEA
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server startTcpServer() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpPort", "9093");
    }
}