package org.sef4j.testwebapp.config;

import javax.sql.DataSource;

import org.sef4j.jdbc.optional.hikari.HikariDataSourceProxyInstrumenter;
import org.sef4j.jdbc.optional.tomcatpool.TomcatPoolDataSourceProxyInstrumenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

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
//        DataSourceBuilder factory = DataSourceBuilder
//                .create(this.properties.getClassLoader())
//                .driverClassName(this.properties.getDriverClassName())
//                .url(this.properties.getUrl())
//                .username(this.properties.getUsername())
//                .password(this.properties.getPassword());
//        return factory.build();
        
        HikariConfig config = new HikariConfig();

//        JDBCDataSource underlyingDS = new JDBCDataSource();
//        config.setDataSource(underlyingDS);
        config.setDriverClassName(this.properties.getDriverClassName());
        config.setJdbcUrl(this.properties.getUrl());
        config.setUsername(this.properties.getUsername());
        config.setPassword(this.properties.getPassword());
        
        HikariDataSource ds = new HikariDataSource(config);
        
        // cf next
//        HikariDataSourceProxyInstrumenter.injectSefDataSourceProxyInto(ds);
        
        return ds;
    }

    @Value("${" + DataSourceProperties.PREFIX + ".enableSef4jInstrumentation" + ":true}")
    protected boolean enableSef4jDataSourceProxy;
    
    @Bean
    public BeanPostProcessor sef4jDataSourcePostProcessor() {
        return new BeanPostProcessor() {

            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                if (enableSef4jDataSourceProxy && bean instanceof DataSource && beanName.equals("dataSource")) {
                    DataSource ds = (DataSource) bean;
                    if (ds instanceof HikariDataSource) {
                        LOG.info("injecting sef4j instrumentation in DataSource connection pool: HikariDataSource");
                        HikariDataSource hikariDS = (HikariDataSource) ds;
                        HikariDataSourceProxyInstrumenter.injectSefDataSourceProxyInto(hikariDS);
                    } else if (bean instanceof org.apache.tomcat.jdbc.pool.DataSource) {
                        LOG.info("injecting sef4j instrumentation in DataSource connection pool: Tomcat Pool");
                        TomcatPoolDataSourceProxyInstrumenter.injectSefDataSourceProxyInto(ds);
                    } else {
                        LOG.warn("IGNORE injecting sef4j instrumentation in DataSource connection pool: unknown pool type");
                    }
                }
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }
            
        };
    }
    
    
}
