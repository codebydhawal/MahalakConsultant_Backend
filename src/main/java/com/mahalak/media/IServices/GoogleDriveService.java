package com.mahalak.media.IServices;

import com.mahalak.media.dto.wrapper.DownloadResponse;
import com.mahalak.media.dto.wrapper.FileUploadResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GoogleDriveService {

    FileUploadResponseDto upload(MultipartFile file);

    DownloadResponse download(String fileId) throws Exception;

    void delete(String fileId);

    List<FileUploadResponseDto> listFiles();

}
