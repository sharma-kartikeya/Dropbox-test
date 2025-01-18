package com.dropbox.DropboxTest.services.uploadservice;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UploadService {
    String uploadFile(MultipartFile file) throws IOException;

    void deleteFile(String key);

    void deleteFilesByKeys(List<String> keys);

    String getSignedUrl(String key);

    String putSignedUrl(String key, Map<String, String> metadata);
}
