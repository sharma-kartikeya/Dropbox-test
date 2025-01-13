package com.dropbox.DropboxTest.utils;

import com.dropbox.DropboxTest.models.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {
    public static User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
