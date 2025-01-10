package com.dropbox.DropboxTest.services.uploadservice;

import com.dropbox.DropboxTest.models.FileModel;

import java.io.File;
import java.util.List;

public interface UploadService {
    String upload(File file, String fileName);

    boolean removeFile(String fileId);

    String getSignedUrl(String fileId);
}
