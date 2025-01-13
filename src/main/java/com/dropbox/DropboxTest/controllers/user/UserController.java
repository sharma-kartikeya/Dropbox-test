package com.dropbox.DropboxTest.controllers.user;

import com.dropbox.DropboxTest.controllers.user.requests.CreateUserRequest;
import com.dropbox.DropboxTest.controllers.user.requests.LoginUserRequest;
import com.dropbox.DropboxTest.models.User;
import com.dropbox.DropboxTest.models.UserRole;
import com.dropbox.DropboxTest.services.userservice.UserService;
import com.dropbox.DropboxTest.utils.AuthUtils;
import com.dropbox.DropboxTest.utils.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping(path="/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping(path = "signup")
    public ResponseEntity<String> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        try {
            User user = userService.createUser(
                    createUserRequest.getName(),
                    createUserRequest.getEmail(),
                    createUserRequest.getPassword(),
                    createUserRequest.getPhone());
            String token = jwtUtils.createToken(createUserRequest.getEmail());
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("auth-token", token);
            return new ResponseEntity<>(user.getId(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "login")
    public ResponseEntity<String> LoginUser(@RequestBody @Valid LoginUserRequest loginUserRequest) {
        try {
            User user = userService.authenticateUser(loginUserRequest.getEmail(), loginUserRequest.getPassword());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
            String token = jwtUtils.createToken(user.getEmail());
            MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
            headers.add("auth-token", token);

            return new ResponseEntity<>(user.getId(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping(path = "all")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            User user = AuthUtils.getCurrentUser();
            if(!user.getRole().equals(UserRole.ADMIN)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
