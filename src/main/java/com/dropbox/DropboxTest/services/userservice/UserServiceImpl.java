package com.dropbox.DropboxTest.services.userservice;

import com.dropbox.DropboxTest.models.user.User;
import com.dropbox.DropboxTest.models.user.UserRole;
import com.dropbox.DropboxTest.repositories.UserRepository;
import com.dropbox.DropboxTest.services.directoryservice.DirectoryService;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public @NonNull User createUser(@NonNull String name,
                                    @NonNull String email,
                                    @NonNull String password,
                                    String phone,
                                    @NonNull String rootDirectoryId) {
        try {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setPhone(phone);
            user.setRootDirectoryId(rootDirectoryId);
            if (email.equals("s.kartikeya18@gmail.com")) {
                user.setRole(UserRole.ADMIN);
            } else {
                user.setRole(UserRole.USER);
            }
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User authenticateUser(@NonNull String email, @NonNull String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return userRepository.findByEmail(email).orElse(null);
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
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public List<User> getUsersByEmails(@NonNull List<String> emails) {
        return userRepository.findAllByEmail(emails);
    }

    @Override
    public boolean deleteAllUsers() {
        try {
            userRepository.deleteAllByRoleNotLike(UserRole.ADMIN);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
