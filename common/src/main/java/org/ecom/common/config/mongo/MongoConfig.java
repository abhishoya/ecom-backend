package org.ecom.common.config.mongo;

import com.mongodb.lang.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.PlatformTransactionManager;

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

