package com.dropbox.DropboxTest.controllers.user.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginUserRequest {
    @NotEmpty(message = "Email not present!")
    private String email;

    @NotEmpty(message = "Password not present!")
    private String password;
}
