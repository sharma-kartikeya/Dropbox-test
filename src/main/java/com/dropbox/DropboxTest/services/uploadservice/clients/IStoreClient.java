package com.dropbox.DropboxTest.services.uploadservice.clients;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface IStoreClient {
    String uploadFile(MultipartFile file) throws IOException;

    Boolean deleteFile(String key);

    String getSignedUrl(String key);

    String putSignedUrl(String key, Map<String, String> metadata);
}
