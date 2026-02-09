package com.game.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.dto.TelemetryEventDTO;
import com.game.dto.TelemetryRequestDTO;
import com.game.repository.SessionRepository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TelemetryService {
    private static final String LOGS_DIR = "logs";
    private final SessionRepository sessionRepository;

    public TelemetryService() {
        this.sessionRepository = new SessionRepository();
    }

    public boolean saveTelemetry(TelemetryRequestDTO request) {
        try {
            if (!sessionRepository.sessionExists(request.getSessionId())) return false;

            String jsonData = convertToJSON(request);

            return writeToFile(jsonData);
        } catch (Exception e) {
            return false;
        }
    }

    private String convertToJSON(TelemetryRequestDTO request) throws JsonProcessingException {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date());
        ObjectMapper mapper = new ObjectMapper();

        StringBuilder sb = new StringBuilder();
        for (TelemetryEventDTO event : request.getEvents()) {
            Map<String, Object> eventMap = new HashMap<>();
            eventMap.put("sessionId", request.getSessionId());
            eventMap.put("serverTime", timestamp);
            eventMap.put("clientTime", event.getTimestamp());
            eventMap.put("type", event.getType());
            eventMap.put("ballId", event.getBallId());
            eventMap.put("kind", event.getKind());
            eventMap.put("hit", event.getHit());

            sb.append(mapper.writeValueAsString(eventMap)).append("\n");
        }

        return sb.toString();
    }

    private boolean writeToFile(String data) {
        File logsDir = new File(LOGS_DIR);
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateString = dateFormat.format(new Date());
        String fileName = String.format("%s/events-%s.ndjson", LOGS_DIR, dateString);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}