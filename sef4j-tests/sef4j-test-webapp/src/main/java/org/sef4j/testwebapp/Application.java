package org.sef4j.testwebapp;

import org.sef4j.testwebapp.config.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    /**
     * Main method, used to run the application.
     */
    public static void main(String[] args) {
    	LOG.info("Application.main ...");
        SpringApplication app = new SpringApplication(ApplicationConfig.class);
        app.setShowBanner(false);

        app.run(args);
    }

}
