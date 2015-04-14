package org.sef4j.testwebapp.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = { "org.sef4j.testwebapp" })
@EnableAutoConfiguration(
        exclude = {
                MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class,
                SecurityAutoConfiguration.class
                // org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration.class

        })
//@EnableWebMvc
@EntityScan(basePackages = { "org.sef4j.testwebapp.domain" })
@EnableJpaRepositories(basePackages = { "org.sef4j.testwebapp.repository" })
public class ApplicationConfig {

    
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationConfig.class);
    
    @Autowired
    private DataSourceProperties properties;
    
    /** cf org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration */
    @Bean
    @ConfigurationProperties(prefix = DataSourceProperties.PREFIX)
    public DataSource dataSource() {
        LOG.info("building DataSource");
        DataSourceBuilder factory = DataSourceBuilder
                .create(this.properties.getClassLoader())
                .driverClassName(this.properties.getDriverClassName())
                .url(this.properties.getUrl())
                .username(this.properties.getUsername())
                .password(this.properties.getPassword());
        return factory.build();
    }
    
}
