package com.dropbox.DropboxTest.controllers.file;

import com.dropbox.DropboxTest.controllers.BaseResponse;
import com.dropbox.DropboxTest.controllers.file.requests.FolderCreationRequest;
import com.dropbox.DropboxTest.controllers.file.responses.DirectoryResponse;
import com.dropbox.DropboxTest.models.Directory;
import com.dropbox.DropboxTest.models.User;
import com.dropbox.DropboxTest.models.UserRole;
import com.dropbox.DropboxTest.services.fileservice.DirectoryService;
import com.dropbox.DropboxTest.services.uploadservice.UploadService;
import com.dropbox.DropboxTest.services.userservice.UserService;
import com.dropbox.DropboxTest.utils.AuthUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/file")
public class FileController {

    @Autowired
    private DirectoryService directoryService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private AuthUtils authUtils;
    @Autowired
    private UserService userService;

    @PostMapping(path = "upload")
    public ResponseEntity<BaseResponse<DirectoryResponse>> uploadFile(@RequestParam MultipartFile file,
                                                                      @RequestParam String folderId) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            String key = uploadService.upload(file);
            Directory directory;
            try {
                directory = directoryService.createFile(file.getOriginalFilename(), folderId, key);
            } catch (Exception e) {
                uploadService.removeFile(key);
                throw e;
            }
            return ResponseEntity.ok(BaseResponse.<DirectoryResponse>builder()
                    .setData(new DirectoryResponse(directory))
                    .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BaseResponse.<DirectoryResponse>builder()
                    .setError("Something went wrong!")
                    .build()
            );
        }
    }

    @PostMapping(path = "create-folder")
    public ResponseEntity<BaseResponse<DirectoryResponse>> createFolder(@RequestBody @Valid FolderCreationRequest request) {
        try {
            Directory directory = directoryService.createDirectory(request.getName(), request.getParentFolderId());
            return ResponseEntity.ok(BaseResponse.<DirectoryResponse>builder()
                    .setData(new DirectoryResponse(directory))
                    .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<DirectoryResponse>builder()
                            .setError("Some thing went wrong!")
                            .build()
                    );
        }
    }

    @GetMapping(path = "folder")
    public ResponseEntity<BaseResponse<List<DirectoryResponse>>> getFolders(@RequestParam String folderId) {
        try {
            String folderID = folderId;
            if (folderID == null) {
                User user = authUtils.getCurrentUser();
                folderID = user.getRootDirectory().getId();
            }
            List<Directory> directories = directoryService.getChildren(folderID);
            List<DirectoryResponse> directoriesResponse = directories
                    .stream()
                    .map(DirectoryResponse::new)
                    .toList();
            return ResponseEntity.ok(
                    BaseResponse.<List<DirectoryResponse>>builder()
                            .setData(directoriesResponse)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<List<DirectoryResponse>>builder()
                            .setError("Something went wrong!")
                            .build()
                    );
        }
    }

    @GetMapping(path = "all")
    public ResponseEntity<BaseResponse<List<DirectoryResponse>>> getAll() {
        try {
            User user = authUtils.getCurrentUser();
            if (!user.getRole().equals(UserRole.ADMIN)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            List<Directory> directories = directoryService.getAllDirectories();
            List<DirectoryResponse> directoryResponses = directories.stream().map(DirectoryResponse::new).toList();
            return ResponseEntity.ok(
                    BaseResponse.<List<DirectoryResponse>>builder()
                            .setData(directoryResponses)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<List<DirectoryResponse>>builder()
                            .setError("Something went wrong").build()
                    );
        }
    }

    @PostMapping(path = "path")
    public ResponseEntity<List<Directory>> getPath(@RequestParam String id) {
        try {
            List<Directory> path = directoryService.getPathDirectories(id);
            return ResponseEntity.ok(path);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
