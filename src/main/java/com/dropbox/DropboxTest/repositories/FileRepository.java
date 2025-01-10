package com.dropbox.DropboxTest.repositories;

import com.dropbox.DropboxTest.models.FileModel;
import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileRepository {
    private static final Map<String, FileModel> files = new HashMap<>();

    public static @NonNull FileModel getFile(@NonNull String id) {
        if (!files.containsKey(id)) {
            throw new IllegalArgumentException("File with id " + id + " not found");
        }
        return files.get(id);
    }

    public static @NonNull FileModel addFile(@NonNull String url, @NonNull String name, @NonNull String ownerId, String description) {
        boolean urlExists = files.values().stream().anyMatch(fileModel -> fileModel.getUrl().equals(url));
        if(urlExists) {
            throw new IllegalArgumentException("File with url " + url + " already exists");
        }

        FileModel file = new FileModel(url, name, ownerId);
        file.setDescription(description);
        files.put(url, file);
        return file;
    }

    public static @NonNull FileModel updateFile(@NonNull String id, String name, String description) {
        FileModel file = files.get(id);
        if(file == null) {
            throw new IllegalArgumentException("File with id " + id + " not found");
        }

        if(name != null) {
            file.setName(name);
        }
        if(description != null) {
            file.setDescription(description);
        }

        files.put(id, file);
        return file;
    }

    public static FileModel deleteFile(@NonNull String id) {
        return files.remove(id);
    }

    public static boolean shareFileWithUser(@NonNull String id, @NonNull List<String> userIds) {
        FileModel file = getFile(id);
        List<String> accessUsers = file.getAccessUsersId();
        userIds.forEach(userId -> {
            if(!accessUsers.contains(userId) && !file.getOwnerId().equals(userId)) {
                accessUsers.add(userId);
            }
        });
        files.put(id, file);
        return true;
    }

    public static boolean unShareFileWithUser(@NonNull String id, @NonNull String userId) {
        FileModel file = getFile(id);
        if(file.getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("You can not un-share files with owner.");
        }

        return file.getAccessUsersId().remove(userId);
    }
}
