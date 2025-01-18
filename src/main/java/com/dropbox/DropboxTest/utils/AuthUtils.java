package com.dropbox.DropboxTest.utils;

import com.dropbox.DropboxTest.models.user.User;
import com.dropbox.DropboxTest.services.userservice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    @Autowired
    private UserService userService;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        String email = (String) authentication.getPrincipal();
        return userService.getUserByEmail(email);
    }
}
