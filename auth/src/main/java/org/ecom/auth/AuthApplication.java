package org.ecom.auth;

import org.ecom.common.config.crypto.EncrypterConfig;
import org.ecom.common.config.mongo.MongoConfig;
import org.ecom.common.helper.ExceptionControllerAdvice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
@Import({MongoConfig.class, EncrypterConfig.class})
public class AuthApplication {

    @Bean
    public ExceptionControllerAdvice exceptionControllerAdvice()
    {
        return new ExceptionControllerAdvice();
    }

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}