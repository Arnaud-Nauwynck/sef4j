package org.sef4j.testwebapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan
//@EnableWebMvc
@EnableAutoConfiguration(
		exclude = {
				MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class
		})
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    /**
     * Main method, used to run the application.
     */
    public static void main(String[] args) {
    	LOG.info("Application.main ...");
        SpringApplication app = new SpringApplication(Application.class);
        app.setShowBanner(false);

        app.run(args);
    }

}
