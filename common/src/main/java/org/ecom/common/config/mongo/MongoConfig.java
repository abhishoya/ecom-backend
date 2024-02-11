package org.ecom.common.config.mongo;

import com.mongodb.lang.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.*;
import org.springframework.data.mongodb.config.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.repository.config.*;
import org.springframework.transaction.*;

@Configuration
@EnableAutoConfiguration
@EnableMongoRepositories(basePackages = "org.ecom")
public class MongoConfig extends AbstractMongoClientConfiguration  {

    @Value("${spring.data.mongodb.uri}")
    String uri;

    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDbFactory());
    }

    @NonNull
    @Bean(name = "mongoDbFactory")
    public MongoDatabaseFactory mongoDbFactory() {
        return new SimpleMongoClientDatabaseFactory(uri);
    }

    @NonNull
    @Override
    protected String getDatabaseName() {
        return "db";
    }

    @Bean
    public PlatformTransactionManager mongoTransactionManager() {
        return new MongoTransactionManager(mongoDbFactory());
    }
}

