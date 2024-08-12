package com.demo.folder.config;
import com.demo.folder.sim.SimulationParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.demo.folder")
public class AppConfig {

    @Bean
    public String simulationLogFilePath() {
        return "simulation.log";
    }

    @Bean
    public SimulationParser simulationParser(String simulationLogFilePath) {
        return new SimulationParser(simulationLogFilePath);
    }
}
