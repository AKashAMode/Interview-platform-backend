package com.example.interview.controller;

import com.example.interview.service.AssemblyAIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/transcription")
@CrossOrigin(origins = "*")
public class TranscriptionController {
    
    private static final Logger logger = LoggerFactory.getLogger(TranscriptionController.class);
    
    @Value("${assemblyai.api.key}")
    private String assemblyAIApiKey;
    
    private final AssemblyAIService assemblyAIService;
    
    public TranscriptionController(AssemblyAIService assemblyAIService) {
        this.assemblyAIService = assemblyAIService;
    }
    

    @GetMapping("/realtime-config")
    public ResponseEntity<Map<String, Object>> getRealtimeConfig() {
        logger.info("Requesting real-time streaming configuration");
        try {
            Map<String, Object> config = new HashMap<>();
            
          
            config.put("websocket_url", "wss://api.assemblyai.com/v2/realtime/ws");
            config.put("api_key", assemblyAIApiKey);
            config.put("sample_rate", 16000);
            config.put("encoding", "pcm_s16le"); 
            config.put("channels", 1); 
            
      
            config.put("silence_threshold", 500); 
            config.put("interim_results", true);
            config.put("message", "Real-time transcription configuration for AssemblyAI v2 API");
            
            logger.info("Real-time config prepared successfully");
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            logger.error("Error preparing real-time config: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get real-time configuration");
            error.put("details", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    

    @GetMapping("/token")
    public ResponseEntity<Map<String, Object>> getRealtimeToken() {
        logger.info("Requesting realtime token (legacy endpoint)");
        return getRealtimeConfig();
    }
    

    @GetMapping("/streaming-config")
    public ResponseEntity<Map<String, Object>> getStreamingConfig() {
        logger.info("Requesting streaming configuration (legacy endpoint)");
        return getRealtimeConfig();
    }
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadAudio(
            @RequestParam("audio") MultipartFile audioFile) {
        
        try {
            if (audioFile.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Audio file is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            byte[] audioData = audioFile.getBytes();
            Map<String, Object> result = assemblyAIService.uploadAudio(audioData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error uploading audio: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping(value = "/transcribe", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createTranscript(
            @RequestBody Map<String, String> request) {
        
        try {
            String audioUrl = request.get("audio_url");
            if (audioUrl == null || audioUrl.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "audio_url is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            Map<String, Object> result = assemblyAIService.createTranscript(audioUrl);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error creating transcript: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/transcripts/{transcriptId}")
    public ResponseEntity<Map<String, Object>> getTranscript(
            @PathVariable String transcriptId) {
        
        try {
            Map<String, Object> result = assemblyAIService.getTranscript(transcriptId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error getting transcript: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
}