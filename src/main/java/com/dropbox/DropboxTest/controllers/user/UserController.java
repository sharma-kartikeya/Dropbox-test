package com.dropbox.DropboxTest.controllers.user;

import com.dropbox.DropboxTest.controllers.BaseResponse;
import com.dropbox.DropboxTest.controllers.user.requests.CreateUserRequest;
import com.dropbox.DropboxTest.controllers.user.requests.LoginUserRequest;
import com.dropbox.DropboxTest.controllers.user.responses.UserResponse;
import com.dropbox.DropboxTest.models.User;
import com.dropbox.DropboxTest.models.UserRole;
import com.dropbox.DropboxTest.services.userservice.UserService;
import com.dropbox.DropboxTest.utils.AuthUtils;
import com.dropbox.DropboxTest.utils.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping(path = "/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthUtils authUtils;

    @PostMapping(path = "signup")
    public ResponseEntity<BaseResponse<UserResponse>> createUser(@RequestBody @Valid CreateUserRequest createUserRequest, HttpServletResponse response) {
        try {
            User user = userService.createUser(
                    createUserRequest.getName(),
                    createUserRequest.getEmail(),
                    createUserRequest.getPassword(),
                    createUserRequest.getPhone());

            String token = jwtUtils.createToken(createUserRequest.getEmail());

            Cookie cookie = new Cookie("at", token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setMaxAge(360000000);
            cookie.setDomain("localhost");

            response.addCookie(cookie);

            UserResponse userResponse = new UserResponse(user);

            return new ResponseEntity<>(
                    BaseResponse.<UserResponse>builder()
                            .setData(userResponse)
                            .build(),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BaseResponse.<UserResponse>builder().setError("Something went wrong!").build());
        }
    }

    @PostMapping(path = "login")
    public ResponseEntity<BaseResponse<UserResponse>> LoginUser(@RequestBody @Valid LoginUserRequest loginUserRequest, HttpServletResponse response) {
        try {
            User user = userService.authenticateUser(loginUserRequest.getEmail(), loginUserRequest.getPassword());
            if (user == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(BaseResponse.<UserResponse>builder()
                                .setError("Invalid username or password")
                                .build()
                        );
            }

            String token = jwtUtils.createToken(user.getEmail());

            Cookie cookie = new Cookie("at", token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setMaxAge(360000000);
            cookie.setDomain("localhost");

            response.addCookie(cookie);

            UserResponse userResponse = new UserResponse(user);

            return new ResponseEntity<>(
                    BaseResponse.<UserResponse>builder()
                            .setData(userResponse)
                            .build(),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = e.getMessage();
            if (e instanceof BadCredentialsException) {
                errorMessage = "Incorrect Email or Password";
            }
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            BaseResponse.<UserResponse>builder()
                                    .setError(errorMessage)
                                    .build()
                    );
        }
    }

    @GetMapping(path = "all")
    public ResponseEntity<BaseResponse<List<User>>> getAllUsers() {
        try {
            User user = authUtils.getCurrentUser();
            if (!user.getRole().equals(UserRole.ADMIN)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(BaseResponse.<List<User>>builder()
                    .setData(users)
                    .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            BaseResponse.<List<User>>builder()
                                    .setError("Something went wrong!")
                                    .build()
                    );
        }
    }

    @PostMapping(path = "delete-all")
    public ResponseEntity<String> deleteAllUsers() {
        try {
            if (!authUtils.getCurrentUser().getRole().equals(UserRole.ADMIN)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Admin User");
            }
            if (userService.deleteAllUsers()) {
                return ResponseEntity.status(HttpStatus.OK).body("Deleted all users");
            }

            throw new Exception("Something went wrong");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping(path = "me")
    public ResponseEntity<BaseResponse<UserResponse>> userState() {
        try {
            User user = authUtils.getCurrentUser();
            return ResponseEntity.ok(BaseResponse.<UserResponse>builder().setData(new UserResponse(user)).build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body(BaseResponse.
                            <UserResponse>builder().
                            setError("Something went wrong!").
                            build()
                    );
        }
    }

    @PostMapping(path = "logout")
    public ResponseEntity<BaseResponse<String>> logout(HttpServletResponse response) {
        try {
            User user = authUtils.getCurrentUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(
                                BaseResponse.<String>builder().
                                        setError("Unauthorised User")
                                        .build()
                        );
            }
            Cookie cookie = new Cookie("at", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.<String>builder().setData("User Logged out").build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            BaseResponse.<String>builder()
                                    .setError("Something went wrong")
                                    .build()
                    );
        }


    }


}
