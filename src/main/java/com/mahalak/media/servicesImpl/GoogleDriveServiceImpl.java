package com.mahalak.media.servicesImpl;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.mahalak.media.IServices.GoogleDriveService;
import com.mahalak.media.config.GoogleOAuthHelper;
import com.mahalak.media.dto.wrapper.DownloadResponse;
import com.mahalak.media.dto.wrapper.FileUploadResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleDriveServiceImpl implements GoogleDriveService {

    private final GoogleOAuthHelper googleOAuthHelper;

    @Value("${google.drive.folder-id}")
    private String folderId;

    @Override
    public FileUploadResponseDto upload(MultipartFile multipartFile) {

        try {

            Drive drive = getDriveService();

            File metadata = new File();
            metadata.setName(multipartFile.getOriginalFilename());

            if (folderId != null && !folderId.isBlank()) {
                metadata.setParents(List.of(folderId));
            }

            java.io.File tempFile = java.io.File.createTempFile(
                    "upload-",
                    multipartFile.getOriginalFilename()
            );

            multipartFile.transferTo(tempFile);

            FileContent mediaContent = new FileContent(
                    multipartFile.getContentType(),
                    tempFile
            );

            File uploadedFile = drive.files()
                    .create(metadata, mediaContent)
                    .setFields("id,name,size,mimeType")
                    .execute();

            tempFile.delete();

            return FileUploadResponseDto.builder()
                    .fileId(uploadedFile.getId())
                    .fileName(uploadedFile.getName())
                    .mimeType(uploadedFile.getMimeType())
                    .size(uploadedFile.getSize())
                    .downloadUrl("https://drive.google.com/uc?id=" + uploadedFile.getId())
                    .build();

        } catch (Exception e) {

            log.error("Google Drive Upload Error", e);
            throw new RuntimeException("Failed to upload file to Google Drive", e);

        }
    }

//    @Override
//    public byte[] download(String fileId) {
//
//        try {
//
//            Drive drive = getDriveService();
//
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//            drive.files()
//                    .get(fileId)
//                    .executeMediaAndDownloadTo(outputStream);
//
//            return outputStream.toByteArray();
//
//        } catch (Exception e) {
//
//            log.error("Google Drive Download Error", e);
//            throw new RuntimeException("Failed to download file", e);
//
//        }
//    }

    @Override
    public DownloadResponse download(String fileId) throws Exception {

        Drive drive = getDriveService();

        File file = drive.files()
                .get(fileId)
                .setFields("mimeType")
                .execute();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        drive.files()
                .get(fileId)
                .executeMediaAndDownloadTo(out);

        DownloadResponse response = new DownloadResponse();
        response.setData(out.toByteArray());
        response.setMimeType(file.getMimeType());

        return response;
    }

    @Override
    public void delete(String fileId) {

        try {

            Drive drive = getDriveService();

            drive.files()
                    .delete(fileId)
                    .execute();

        } catch (Exception e) {

            log.error("Google Drive Delete Error", e);
            throw new RuntimeException("Failed to delete file", e);

        }
    }

    @Override
    public List<FileUploadResponseDto> listFiles() {

        try {

            Drive drive = getDriveService();

            FileList result = drive.files()
                    .list()
                    .setFields("files(id,name,mimeType,size)")
                    .execute();

            return result.getFiles()
                    .stream()
                    .map(file -> FileUploadResponseDto.builder()
                            .fileId(file.getId())
                            .fileName(file.getName())
                            .mimeType(file.getMimeType())
                            .size(file.getSize())
                            .downloadUrl("http://localhost:8080/api/drive/download?fileId=" + file.getId())
                            .build())
                    .toList();

        } catch (Exception e) {

            log.error("Google Drive List Error", e);
            throw new RuntimeException("Failed to list files", e);

        }
    }

    /**
     * Creates an authenticated Google Drive client using OAuth credentials.
     */
    private Drive getDriveService() throws Exception {

        Credential credential = googleOAuthHelper.getCredential();

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                credential
        )
                .setApplicationName("MahalakMedia")
                .build();
    }
}