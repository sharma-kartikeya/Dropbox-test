package com.dropbox.DropboxTest.services.userservice;

import com.dropbox.DropboxTest.models.Directory;
import com.dropbox.DropboxTest.models.User;
import lombok.NonNull;

import java.util.List;

public interface UserService {

    @NonNull
    User createUser(@NonNull String name, @NonNull String email, @NonNull String password, String phone);

    User authenticateUser(@NonNull String email, @NonNull String password);

    @NonNull List<User> getAllUsers();

    @NonNull
    User getUser(@NonNull String id);

    User getUserByEmail(@NonNull String email);

    boolean deleteAllUsers();
}
