package com.dropbox.DropboxTest.controllers.user.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "Name can not be empty!")
    private String name;

    @NotBlank(message = "Email can not be empty!")
    @Email(message = "Please provide correct email!")
    private String email;

    @NotBlank(message = "Password can not be empty!")
    private String password;

    @Pattern(regexp = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$",
            message = "Please provide correct mobile number!")
    private String phone;
}
