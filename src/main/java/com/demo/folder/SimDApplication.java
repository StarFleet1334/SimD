package com.demo.folder;

import com.demo.folder.config.AppConfig;
import com.demo.folder.sim.SimulationParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration.class})
public class SimDApplication implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimDApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SimDApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        SimulationParser sim = context.getBean(SimulationParser.class);
//        LOGGER.info("FILE: " + sim.getFilePath());
//        sim.parseData();
//        sim.writeJson();

        /**
         *  Benefit of this approach:
         *  1. Loosely Coupled
         *  2. Easier to write unit tests as dependencies can be mocked
         *  3. Logic is separated - single resp principle
         *
         */
    }
}
