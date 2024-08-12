package com.demo.folder.api.server;

import com.demo.folder.command.action.NotificationMessageAction;
import com.demo.folder.command.service.FileProcessingService;
import com.demo.folder.command.service.NotificationMessageService;
import com.demo.folder.models.GeneralRequestStat;
import com.demo.folder.models.SingleRequestStat;
import com.demo.folder.models.SimulationStat;
import com.demo.folder.models.TimeStat;
import com.demo.folder.sim.SimulationParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileUploadAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadAPI.class);

    @Autowired
    private FileProcessingService fileProcessingService;

    @Autowired
    private NotificationMessageService notificationMessageService;

    @PostMapping(value = "/simulationStats", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> uploadSimulationStatsFile(@RequestParam(value = "file", required = false) MultipartFile file, @RequestBody(required = false) SimulationStat simulationStat) {
        return processUpload(file, simulationStat, fileProcessingService::processSimulationStatsFile, fileProcessingService::processSimulationStats, "Simulation stats");
    }

    @PostMapping(value = "/singleRequestStats", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> uploadSingleRequestStatsFile(@RequestParam(value = "file", required = false) MultipartFile file, @RequestBody(required = false) List<SingleRequestStat> singleRequestStats) {
        return processUpload(file, singleRequestStats, fileProcessingService::processSingleRequestStatsFile, fileProcessingService::processSingleRequestStats, "Single request stats");
    }

    @PostMapping(value = "/generalRequestStats", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> uploadGeneralRequestStatsFile(@RequestParam(value = "file", required = false) MultipartFile file, @RequestBody(required = false) List<GeneralRequestStat> generalRequestStats) {
        return processUpload(file, generalRequestStats, fileProcessingService::processGeneralRequestStatsFile, fileProcessingService::processGeneralRequestStats, "General request stats");
    }

    @PostMapping(value = "/timeStats", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> uploadTimeStatsFile(@RequestParam(value = "file", required = false) MultipartFile file, @RequestBody(required = false) TimeStat timeStat) {
        return processUpload(file, timeStat, fileProcessingService::processTimeStatsFile, fileProcessingService::processTimeStats, "Time stats");
    }
    @PostMapping(value = "/logFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadLogFile(@RequestParam("file") MultipartFile file) {
        try {
            SimulationParser simulationParser = new SimulationParser("simulation.log");
            // Produce a message to the Kafka topic after processing the log file
            fileProcessingService.processLogFile(file);
            // Here should be a function that adds msg to a topic
            notificationMessageService.processNotificationMessageRequest();
            // Consumer gets activated and calls functions such as: callPostEndpoints() and writeJson
            simulationParser.test();
            return ResponseEntity.status(HttpStatus.CREATED).body("Log file processed successfully.");
        } catch (IOException e) {
            LOGGER.error("Error processing log file: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process log file");
        }
    }

    private <T> ResponseEntity<String> processUpload(MultipartFile file, T requestBody, FileProcessor fileProcessor, RequestProcessor<T> requestProcessor, String entityName) {
        try {
            if (file != null) {
                fileProcessor.process(file);
                return ResponseEntity.status(HttpStatus.CREATED).body(entityName + " file processed successfully.");
            } else if (requestBody != null) {
                requestProcessor.process(requestBody);
                return ResponseEntity.status(HttpStatus.CREATED).body(entityName + " processed successfully.");
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file or data provided.");
            }
        } catch (IOException e) {
            LOGGER.error("Error processing {}: ", entityName, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process " + entityName);
        }
    }

    @FunctionalInterface
    private interface FileProcessor {
        void process(MultipartFile file) throws IOException;
    }

    @FunctionalInterface
    private interface RequestProcessor<T> {
        void process(T request);
    }

    @GetMapping(value = "/simulationStats", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimulationStat> getSimulationStats() {
        return getEntity(fileProcessingService.getSimulationStat(), "Simulation stats");
    }

    @GetMapping(value = "/singleRequestStats", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SingleRequestStat>> getSingleRequestStats() {
        return getEntity(fileProcessingService.getSingleRequestStats(), "Single request stats");
    }

    @GetMapping(value = "/generalRequestStats", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GeneralRequestStat>> getGeneralRequestStats() {
        return getEntity(fileProcessingService.getGeneralRequestStats(), "General request stats");
    }

    @GetMapping(value = "/timeStats", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TimeStat> getTimeStats() {
        return getEntity(fileProcessingService.getTimeStat(), "Time stats");
    }

    private <T> ResponseEntity<T> getEntity(T entity, String entityName) {
        if (entity == null) {
            LOGGER.warn("{} not found.", entityName);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.ok(entity);
    }
}
