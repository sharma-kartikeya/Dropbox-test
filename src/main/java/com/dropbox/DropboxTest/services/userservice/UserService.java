package com.dropbox.DropboxTest.services.userservice;

import com.dropbox.DropboxTest.models.UserModel;
import lombok.NonNull;

import java.util.List;

public interface UserService {

    @NonNull UserModel createUser(@NonNull String name, @NonNull String email, String phone);

    @NonNull UserModel getUser(@NonNull String id);

    UserModel getUserByEmail(@NonNull String email);

    @NonNull List<String> getAllUserFiles(@NonNull String id);

    @NonNull List<String> getAllSharedFiles(@NonNull String id);
}
