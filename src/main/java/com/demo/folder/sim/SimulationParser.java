package com.demo.folder.sim;

import com.demo.folder.models.GeneralRequestStat;
import com.demo.folder.models.SimulationStat;
import com.demo.folder.models.SingleRequestStat;
import com.demo.folder.models.TimeStat;
import com.demo.folder.utils.Endpoint;
import com.demo.folder.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.demo.folder.models.LineType.REQUEST;
import static com.demo.folder.models.LineType.USER;

@Getter
public class SimulationParser {
    private static final Logger logger = LoggerFactory.getLogger(SimulationParser.class);
    private final String filePath;
    @Setter
    private SimulationStat simulationStat;
    private final List<SingleRequestStat> singleRequestStats = new ArrayList<>();
    private final List<GeneralRequestStat> generalRequestStats = new ArrayList<>();
    private final TimeStat timeStat = new TimeStat();

    public SimulationParser(String filePath) {
        this.filePath = filePath;
    }

    public void test() {
        logger.info("Testing...");
        String filePath = "src/main/resources/simulations/simulation.log"; // Adjust the path if necessary
        try (SimulationReader reader = new SimulationReader(new BufferedReader(new FileReader(filePath)))) {
            List<String> header = reader.readNext();
            simulationStat = new SimulationStat(header);
            List<String> line;
            while ((line = reader.readNext()) != null) {
                switch (line.get(0)) {
                    case REQUEST:
                        singleRequestStats.add(new SingleRequestStat(line));
                        timeStat.updateRequest(line);
                        break;
                    case USER:
                        timeStat.updateUser(line);
                        break;
                }
                simulationStat.setEndTime(line);
            }
            timeStat.convert();
            timeStat.addResponseTimes(singleRequestStats);
            timeStat.calculatePercentiles();
            calculateRequestsStats();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            callPostEndpoints();
            writeJson();
        }
    }

//    public void parseData() {
//
//        try (SimulationReader reader = new SimulationReader(Utils.getReaderFromResource(filePath))) {
//            List<String> header = reader.readNext();
//            simulationStat = new SimulationStat(header);
//            List<String> line;
//            while ((line = reader.readNext()) != null) {
//                switch (line.get(0)) {
//                    case REQUEST:
//                        singleRequestStats.add(new SingleRequestStat(line));
//                        timeStat.updateRequest(line);
//                        break;
//                    case USER:
//                        timeStat.updateUser(line);
//                        break;
//                }
//                simulationStat.setEndTime(line);
//            }
//            timeStat.convert();
//            timeStat.addResponseTimes(singleRequestStats);
//            timeStat.calculatePercentiles();
//            calculateRequestsStats();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void calculateRequestsStats() {
        generalRequestStats.add(new GeneralRequestStat("allRequests"));
        Set<String> requestNames = singleRequestStats.stream()
                .map(SingleRequestStat::getName)
                .collect(Collectors.toSet());
        requestNames.forEach(requestName ->
                generalRequestStats.add(new GeneralRequestStat(requestName)));
        for (GeneralRequestStat requestStat : generalRequestStats) {
            for (SingleRequestStat request : singleRequestStats) {
                if (request.getName().equals(requestStat.getName()) || requestStat.getName().equals("allRequests")) {
                    requestStat.countResult(request);
                }
            }
            requestStat.calculateMetrics();
        }
    }

    public void writeJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            String resourcesPath = Paths.get("src/main/resources/metric").toAbsolutePath().toString();

            File simulationStatsFile = new File(resourcesPath + "/simulationStats.json");
            File singleRequestStatsFile = new File(resourcesPath + "/singleRequestStats.json");
            File generalRequestStatsFile = new File(resourcesPath + "/generalRequestStats.json");
            File timeStatsFile = new File(resourcesPath + "/timeStats.json");

            objectMapper.writeValue(simulationStatsFile, simulationStat);
            objectMapper.writeValue(singleRequestStatsFile, singleRequestStats);
            objectMapper.writeValue(generalRequestStatsFile, generalRequestStats);
            objectMapper.writeValue(timeStatsFile, timeStat);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void callPostEndpoints() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<SimulationStat> simulationStatEntity = new HttpEntity<>(simulationStat, headers);
            restTemplate.exchange("http://localhost:8080" + Endpoint.SIMULATION_STATS.getUrl(), HttpMethod.POST, simulationStatEntity, String.class);

            HttpEntity<List<SingleRequestStat>> singleRequestStatsEntity = new HttpEntity<>(singleRequestStats, headers);
            restTemplate.exchange("http://localhost:8080" + Endpoint.SINGLE_REQUEST_STATS.getUrl(), HttpMethod.POST, singleRequestStatsEntity, String.class);

            HttpEntity<List<GeneralRequestStat>> generalRequestStatsEntity = new HttpEntity<>(generalRequestStats, headers);
            restTemplate.exchange("http://localhost:8080" + Endpoint.GENERAL_REQUEST_STATS.getUrl(), HttpMethod.POST, generalRequestStatsEntity, String.class);

            HttpEntity<TimeStat> timeStatEntity = new HttpEntity<>(timeStat, headers);
            restTemplate.exchange("http://localhost:8080" + Endpoint.TIME_STATS.getUrl(), HttpMethod.POST, timeStatEntity, String.class);

        } catch (Exception e) {
            logger.error("Failed to call POST endpoints", e);
        }
    }

}
