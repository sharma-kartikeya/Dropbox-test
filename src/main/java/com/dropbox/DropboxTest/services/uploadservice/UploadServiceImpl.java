package com.dropbox.DropboxTest.services.uploadservice;

import com.dropbox.DropboxTest.services.uploadservice.clients.IStoreClient;
import com.dropbox.DropboxTest.services.uploadservice.clients.StoreClientFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class UploadServiceImpl implements UploadService {

    private final IStoreClient storeClient;

    public UploadServiceImpl(StoreClientFactory factory) {
        this.storeClient = factory.getClient(StoreClientFactory.StoreClients.S3);
    }

    @Override
    public String upload(MultipartFile file) {
        try {
            return this.storeClient.uploadFile(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean removeFile(String key) {
        try {
            return this.storeClient.deleteFile(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getSignedUrl(String key) {
        return this.storeClient.getSignedUrl(key);
    }

    @Override
    public String putSignedUrl(String key, Map<String, String> metadata) {
        return this.storeClient.putSignedUrl(key, metadata);
    }
}
