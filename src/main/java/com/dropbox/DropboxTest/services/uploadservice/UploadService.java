package com.dropbox.DropboxTest.services.uploadservice;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UploadService {
    String upload(MultipartFile file);

    boolean removeFile(String key);

    String getSignedUrl(String key);

    String putSignedUrl(String key, Map<String, String> metadata);
}
