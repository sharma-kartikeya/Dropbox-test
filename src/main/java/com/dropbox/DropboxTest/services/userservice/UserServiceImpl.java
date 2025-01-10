package com.dropbox.DropboxTest.services.userservice;

import com.dropbox.DropboxTest.models.UserModel;
import com.dropbox.DropboxTest.repositories.UserRepository;
import lombok.NonNull;

import java.util.List;

public class UserServiceImpl implements UserService {

    @Override
    public @NonNull UserModel createUser(@NonNull String name, @NonNull String email, String phone) {
        return UserRepository.createUser(name, email, phone);
    }

    @Override
    public @NonNull UserModel getUser(@NonNull String id) {
        return UserRepository.getUser(id);
    }

    @Override
    public UserModel getUserByEmail(@NonNull String email) {
        return UserRepository.getUserByEmail(email);
    }

    @Override
    public @NonNull List<String> getAllUserFiles(@NonNull String id) {
        return UserRepository.getUser(id).getOwnedFiles();
    }

    @Override
    public @NonNull List<String> getAllSharedFiles(@NonNull String id) {
        return UserRepository.getUser(id).getSharedFiles();
    }
}
