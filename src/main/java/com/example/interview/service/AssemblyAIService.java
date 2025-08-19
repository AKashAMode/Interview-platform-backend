package com.example.interview.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class AssemblyAIService {
    
    private static final Logger logger = LoggerFactory.getLogger(AssemblyAIService.class);
    
    @Value("${assemblyai.api.key:}")
    private String apiKey;
    
    private static final String UPLOAD_URL = "https://api.assemblyai.com/v2/upload";
    private static final String TRANSCRIPT_URL = "https://api.assemblyai.com/v2/transcript";
    private static final String STREAMING_TOKEN_URL = "https://api.assemblyai.com/v2/realtime/token";
    
    private final RestTemplate restTemplate;
    
    public AssemblyAIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    
    public String getRealtimeToken() {
        validateApiKey();
        
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            headers.set("Authorization", apiKey);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("expires_in", 3600);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            logger.info("Preparing streaming token for AssemblyAI v3 streaming API");
        
            logger.info("Returning API key for v3 streaming authentication");
            return apiKey;
            
        } catch (Exception e) {
            logger.error("Error preparing streaming token: {}", e.getMessage());
            throw new RuntimeException("Failed to prepare streaming token: " + e.getMessage());
        }
    }
    
    public Map<String, Object> getStreamingCredentials() {
        validateApiKey();
        
        try {
            String token = getRealtimeToken();
            
            Map<String, Object> credentials = new HashMap<>();
            credentials.put("token", token);
            credentials.put("websocket_url", "wss://streaming.assemblyai.com/v3/ws");
            credentials.put("sample_rate", 16000);
            credentials.put("encoding", "pcm_s16le");
            
            return credentials;
        } catch (Exception e) {
            logger.error("Error getting streaming credentials: {}", e.getMessage());
            throw new RuntimeException("Failed to get streaming credentials: " + e.getMessage());
        }
    }
    
    public Map<String, Object> uploadAudio(byte[] audioData) {
        validateApiKey();
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set("Authorization", apiKey);
            
            HttpEntity<byte[]> entity = new HttpEntity<>(audioData, headers);
            
            logger.info("Uploading audio file to AssemblyAI");
            ResponseEntity<Map> response = restTemplate.postForEntity(UPLOAD_URL, entity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Audio file uploaded successfully");
                return response.getBody();
            } else {
                throw new RuntimeException("Upload failed with status: " + response.getStatusCode());
            }
            
        } catch (HttpClientErrorException e) {
            logger.error("Upload failed: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Upload failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error uploading audio: {}", e.getMessage());
            throw new RuntimeException("Failed to upload audio: " + e.getMessage());
        }
    }
    
    public Map<String, Object> createTranscript(String audioUrl) {
        validateApiKey();
        
        if (audioUrl == null || audioUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Audio URL cannot be empty");
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", apiKey);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("audio_url", audioUrl);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            logger.info("Creating transcript for audio URL");
            ResponseEntity<Map> response = restTemplate.postForEntity(TRANSCRIPT_URL, entity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Transcript creation initiated successfully");
                return response.getBody();
            } else {
                throw new RuntimeException("Transcript creation failed with status: " + response.getStatusCode());
            }
            
        } catch (HttpClientErrorException e) {
            logger.error("Transcript creation failed: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Transcript creation failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating transcript: {}", e.getMessage());
            throw new RuntimeException("Failed to create transcript: " + e.getMessage());
        }
    }
    
    public Map<String, Object> getTranscript(String transcriptId) {
        validateApiKey();
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            String url = TRANSCRIPT_URL + "/" + transcriptId;
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Get transcript failed with status: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            logger.error("Error getting transcript: {}", e.getMessage());
            throw new RuntimeException("Failed to get transcript: " + e.getMessage());
        }
    }
    
    private void validateApiKey() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("AssemblyAI API key is not configured. Add 'assemblyai.api.key=your_key' to application.properties");
        }
    }
}