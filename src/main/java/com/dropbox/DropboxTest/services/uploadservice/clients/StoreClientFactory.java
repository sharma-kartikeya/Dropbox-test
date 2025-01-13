package com.dropbox.DropboxTest.services.uploadservice.clients;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StoreClientFactory {

    private final S3StoreClient s3StoreClient;

    public StoreClientFactory(@NonNull S3StoreClient s3StoreClient) {
        this.s3StoreClient = s3StoreClient;
    }

    public IStoreClient getClient(@NonNull StoreClients clientType) {
        try {
            if (clientType == StoreClients.S3) {
                return s3StoreClient;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public enum StoreClients {
        S3
    }
}
