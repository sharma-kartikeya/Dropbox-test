package com.dropbox.DropboxTest.services.userservice;

import com.dropbox.DropboxTest.models.Directory;
import com.dropbox.DropboxTest.models.User;
import com.dropbox.DropboxTest.models.UserRole;
import com.dropbox.DropboxTest.repositories.UserRepository;
import com.dropbox.DropboxTest.services.fileservice.DirectoryService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public @NonNull User createUser(@NonNull String name,
                                    @NonNull String email,
                                    @NonNull String password,
                                    String phone) {
        try {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setPhone(phone);
            if(email.equals("s.kartikeya18@gmail.com")) {
                user.setRole(UserRole.ADMIN);
            } else {
                user.setRole(UserRole.USER);
            }

            Directory rootDirectory = directoryService.createDirectory("Root|" + user.getId() + "|" + user.getName(), null);
            user.setRootDirectory(rootDirectory);
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User authenticateUser(@NonNull String email, @NonNull String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return userRepository.findByEmail(email);
    }

    @Override
    public @NonNull List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public @NonNull User getUser(@NonNull String id) {
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            return user;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserByEmail(@NonNull String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public @NonNull List<String> getAllUserFiles(@NonNull String id) {
        return List.of();
    }

    @Override
    public @NonNull List<String> getAllSharedFiles(@NonNull String id) {
        return List.of();
    }
}
