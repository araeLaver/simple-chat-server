package com.chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {
    
    private final String UPLOAD_DIR = "uploads/";
    
    public FileController() {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("sender") String sender,
            @RequestParam("roomId") String roomId) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("error", "파일이 선택되지 않았습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 파일 크기 제한 (10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                response.put("error", "파일 크기는 10MB를 초과할 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 안전한 파일명 생성
            String originalFilename = file.getOriginalFilename();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String safeFilename = timestamp + "_" + originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");
            
            // 파일 저장
            Path filePath = Paths.get(UPLOAD_DIR + safeFilename);
            Files.write(filePath, file.getBytes());
            
            response.put("filename", safeFilename);
            response.put("originalName", originalFilename);
            response.put("size", String.valueOf(file.getSize()));
            response.put("downloadUrl", "/api/files/download/" + safeFilename);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("error", "파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR + filename);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] fileContent = Files.readAllBytes(filePath);
            
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .body(fileContent);
                    
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}