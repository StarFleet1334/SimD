package com.demo.folder.command.service;

import com.demo.folder.models.GeneralRequestStat;
import com.demo.folder.models.SingleRequestStat;
import com.demo.folder.models.SimulationStat;
import com.demo.folder.models.TimeStat;
import com.demo.folder.sim.SimulationParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class FileProcessingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessingService.class);

    private static final String SIMULATION_STATS_KEY = "simulationStats";
    private static final String SINGLE_REQUEST_STATS_KEY = "singleRequestStats";
    private static final String GENERAL_REQUEST_STATS_KEY = "generalRequestStats";
    private static final String TIME_STATS_KEY = "timeStats";
    private static final String LOG_FILE = "logfile";



    private final Map<String, SimulationStat> simulationStatsMap = new HashMap<>();
    private final Map<String, List<SingleRequestStat>> singleRequestStatsMap = new HashMap<>();
    private final Map<String, List<GeneralRequestStat>> generalRequestStatsMap = new HashMap<>();
    private final Map<String, TimeStat> timeStatsMap = new HashMap<>();

    public void processLogFile(MultipartFile file) throws IOException {
        processFile(file, LOG_FILE, ".log");
    }

    public void processSimulationStatsFile(MultipartFile file) throws IOException {
        processFile(file, SIMULATION_STATS_KEY, ".json");
    }

    public void processSingleRequestStatsFile(MultipartFile file) throws IOException {
        processFile(file, SINGLE_REQUEST_STATS_KEY, ".json");
    }

    public void processGeneralRequestStatsFile(MultipartFile file) throws IOException {
        processFile(file, GENERAL_REQUEST_STATS_KEY, ".json");
    }

    public void processTimeStatsFile(MultipartFile file) throws IOException {
        processFile(file, TIME_STATS_KEY, ".json");
    }

    public void processSimulationStats(SimulationStat simulationStat) {
        simulationStatsMap.put(SIMULATION_STATS_KEY, simulationStat);
    }

    public void processSingleRequestStats(List<SingleRequestStat> singleRequestStats) {
        singleRequestStatsMap.put(SINGLE_REQUEST_STATS_KEY, singleRequestStats);
    }

    public void processGeneralRequestStats(List<GeneralRequestStat> generalRequestStats) {
        generalRequestStatsMap.put(GENERAL_REQUEST_STATS_KEY, generalRequestStats);
    }

    public void processTimeStats(TimeStat timeStat) {
        timeStatsMap.put(TIME_STATS_KEY, timeStat);
    }

    private void processFile(MultipartFile file, String key, String extension) throws IOException {
        Path tempFile = null;
        try {
            if (extension.equals(".log")) {
                LOGGER.info("Processing log file");
                Path resourcesPath = Paths.get("src/main/resources/simulations/");
                if (!Files.exists(resourcesPath)) {
                    Files.createDirectories(resourcesPath);
                }
                Path logFilePath = resourcesPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));
                Files.write(logFilePath, file.getBytes(), StandardOpenOption.CREATE);

            } else {
                tempFile = Files.createTempFile(key, extension);
                Files.write(tempFile, file.getBytes(), StandardOpenOption.CREATE);

                SimulationParser parser = new SimulationParser(tempFile.toString());
//                parser.parseData();

                simulationStatsMap.put(key, parser.getSimulationStat());
                singleRequestStatsMap.put(key, parser.getSingleRequestStats());
                generalRequestStatsMap.put(key, parser.getGeneralRequestStats());
                timeStatsMap.put(key, parser.getTimeStat());

//                parser.writeJson();
            }
        } catch (IOException e) {
            LOGGER.error("Error processing file for key: {}", key, e);
            throw e;
        } finally {
            if (!extension.equals(".log")) {
                if (tempFile != null) {
                    try {
                        Files.delete(tempFile);
                    } catch (IOException e) {
                        LOGGER.warn("Failed to delete temporary file: {}", tempFile, e);
                    }
                }
            }

        }
    }

    public SimulationStat getSimulationStat() {
        return simulationStatsMap.get(SIMULATION_STATS_KEY);
    }

    public List<SingleRequestStat> getSingleRequestStats() {
        return singleRequestStatsMap.get(SINGLE_REQUEST_STATS_KEY);
    }

    public List<GeneralRequestStat> getGeneralRequestStats() {
        return generalRequestStatsMap.get(GENERAL_REQUEST_STATS_KEY);
    }

    public TimeStat getTimeStat() {
        return timeStatsMap.get(TIME_STATS_KEY);
    }
}
