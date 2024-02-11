package org.ecom.user;

import com.fasterxml.jackson.databind.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.*;
import org.ecom.common.config.crypto.*;
import org.ecom.common.config.mongo.*;
import org.ecom.common.model.response.*;
import org.modelmapper.*;
import org.springframework.boot.*;
import org.springframework.boot.actuate.autoconfigure.tracing.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.data.mongo.*;
import org.springframework.boot.autoconfigure.jdbc.*;
import org.springframework.boot.autoconfigure.mongo.*;
import org.springframework.boot.autoconfigure.orm.jpa.*;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.context.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.*;
import org.springframework.web.filter.*;

import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.Date;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
@ConditionalOnEnabledTracing
@Import({MongoConfig.class, EncrypterConfig.class})
@Slf4j
public class UserApplication {
    @Bean
    public ModelMapper modelMapper()
    {
        return new ModelMapper();
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}