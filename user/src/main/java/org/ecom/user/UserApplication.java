package org.ecom.user;

import com.fasterxml.jackson.databind.*;
import lombok.extern.slf4j.*;
import org.ecom.common.config.crypto.*;
import org.ecom.common.config.mongo.*;
import org.ecom.common.config.security.*;
import org.ecom.common.helper.ExceptionControllerAdvice;
import org.modelmapper.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.data.mongo.*;
import org.springframework.boot.autoconfigure.jdbc.*;
import org.springframework.boot.autoconfigure.mongo.*;
import org.springframework.boot.autoconfigure.orm.jpa.*;
import org.springframework.context.annotation.*;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
@Import({MongoConfig.class, EncrypterConfig.class, CommonSecurityConfig.class, CommonAuthFilter.class})
@Slf4j
public class UserApplication {
    @Bean
    public ModelMapper modelMapper()
    {
        return new ModelMapper();
    }

    @Bean
    public ExceptionControllerAdvice exceptionControllerAdvice()
    {
        return new ExceptionControllerAdvice();
    }

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}