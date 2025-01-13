package com.dropbox.DropboxTest.services.uploadservice;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    String upload(MultipartFile file);

    boolean removeFile(String key);

    String getSignedUrl(String key);
}
