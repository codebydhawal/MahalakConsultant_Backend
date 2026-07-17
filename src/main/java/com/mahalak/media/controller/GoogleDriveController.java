package com.mahalak.media.controller;

import com.mahalak.media.IServices.GoogleDriveService;
import com.mahalak.media.dto.wrapper.DownloadResponse;
import com.mahalak.media.dto.wrapper.FileUploadResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/drive")
@RequiredArgsConstructor
public class GoogleDriveController {

    private final GoogleDriveService googleDriveService;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponseDto> upload(
            @RequestParam MultipartFile file) {
        System.out.println("Upload API called");
        return ResponseEntity.ok(
                googleDriveService.upload(file)
        );
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam String fileId) throws Exception {

        DownloadResponse response = googleDriveService.download(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.getMimeType()))
                .body(response.getData());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(
            @RequestParam String fileId) {

        googleDriveService.delete(fileId);

        return ResponseEntity.ok("Deleted Successfully");
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileUploadResponseDto>> list() {

        return ResponseEntity.ok(
                googleDriveService.listFiles()
        );
    }

}
