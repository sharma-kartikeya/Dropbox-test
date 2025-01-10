package com.dropbox.DropboxTest.services.fileservice;

import com.dropbox.DropboxTest.models.FileModel;
import com.dropbox.DropboxTest.repositories.FileRepository;
import lombok.NonNull;

import java.util.List;

public class FileServiceImpl implements FileService {

    @Override
    public @NonNull String getFileUrl(String fileId) {
        return FileRepository.getFile(fileId).getUrl();
    }

    @Override
    public @NonNull FileModel addFile(@NonNull String name, @NonNull String url, @NonNull String ownerId, String description) {
        return FileRepository.addFile(name, url, ownerId, description);
    }

    @Override
    public FileModel removeFile(@NonNull String fileId) {
        return FileRepository.deleteFile(fileId);
    }

    @Override
    public @NonNull FileModel updateFile(@NonNull String fileId, String name, String description) {
        return FileRepository.updateFile(fileId, name, description);
    }

    @Override
    public boolean shareFile(@NonNull String fileId, @NonNull List<String> userIds) {
        return FileRepository.shareFileWithUser(fileId, userIds);
    }

    @Override
    public boolean unShareFile(@NonNull String fileId, @NonNull String userId) {
        return FileRepository.unShareFileWithUser(fileId, userId);
    }
}