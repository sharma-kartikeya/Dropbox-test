package com.dropbox.DropboxTest.services.uploadservice.clients;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IStoreClient {
    String uploadFile(MultipartFile file) throws IOException;

    Boolean deleteFile(String key) throws IOException;
}
