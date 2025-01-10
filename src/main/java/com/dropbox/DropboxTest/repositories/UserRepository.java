package com.dropbox.DropboxTest.repositories;

import com.dropbox.DropboxTest.models.UserModel;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private static final Map<String, UserModel> users = new HashMap<>();

    public static @NonNull UserModel createUser(@NonNull String name, @NonNull String email, String phone) {
        if(users.values().stream().anyMatch(user -> user.getEmail().equals(email))) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        UserModel user = new UserModel(name, email);
        users.put(name, user);
        return user;
    }

    public static @NonNull UserModel getUser(@NonNull String userId) {
        UserModel user = users.get(userId);
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user;
    }

    public static UserModel getUserByEmail(@NonNull String email) {
        return users.values().stream().filter(user -> user.getEmail().equals(email)).findFirst().orElse(null);
    }

    private static boolean updateUser(@NonNull UserModel user) {
        if(!users.containsKey(user.getId())) {
            throw new IllegalArgumentException("User not found");
        }
        return users.put(user.getId(), user) != null;
    }

    public static boolean deleteUser(@NonNull String userId) {
        if(!users.containsKey(userId)) {
            throw new IllegalArgumentException("User not found");
        }
        return users.remove(userId) != null;
    }

    public static boolean appendFile(@NonNull String userId, @NonNull String fileId) {
        UserModel user = getUser(userId);
        if(user.getOwnedFiles().contains(fileId)) {
            throw new IllegalArgumentException("File already exists");
        }

        user.getOwnedFiles().add(fileId);
        return updateUser(user);
    }

    public static boolean removeFile(@NonNull String userId, @NonNull String fileId) {
        UserModel user = getUser(userId);
        if(!user.getOwnedFiles().contains(fileId)) {
            throw new IllegalArgumentException("File does not exist");
        }

        user.getOwnedFiles().remove(fileId);
        return updateUser(user);
    }

    public static boolean shareFile(@NonNull String userId, @NonNull String fileId) {
        UserModel user = getUser(userId);
        if(!user.getSharedFiles().contains(fileId)) {
            user.getSharedFiles().add(fileId);
        }

        return updateUser(user);
    }

    public static boolean unShareFile(@NonNull String userId, @NonNull String fileId) {
        UserModel user = getUser(userId);
        user.getSharedFiles().remove(fileId);
        return updateUser(user);
    }
}
