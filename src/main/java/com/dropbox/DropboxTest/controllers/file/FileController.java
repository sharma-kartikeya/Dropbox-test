package com.dropbox.DropboxTest.controllers.file;

import com.dropbox.DropboxTest.controllers.file.requests.FolderCreationRequest;
import com.dropbox.DropboxTest.models.Directory;
import com.dropbox.DropboxTest.models.User;
import com.dropbox.DropboxTest.models.UserRole;
import com.dropbox.DropboxTest.services.fileservice.DirectoryService;
import com.dropbox.DropboxTest.services.uploadservice.UploadService;
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

    @PostMapping(path = "upload")
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file,
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
            return ResponseEntity.ok(directory.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "create-folder")
    public ResponseEntity<String> createFolder(@RequestBody @Valid FolderCreationRequest request) {
        try {
            Directory directory = directoryService.createDirectory(request.getName(), request.getParentFolderId());
            return ResponseEntity.ok(directory.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping(path = "all")
    public ResponseEntity<List<Directory>> getAll() {
        try {
            User user = AuthUtils.getCurrentUser();
            if(!user.getRole().equals(UserRole.ADMIN)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            return ResponseEntity.ok(directoryService.getAllDirectories());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping(path = "children")
    public ResponseEntity<List<Directory>> getChildren(@RequestParam String folderId) {
        try {
            List<Directory> children = directoryService.getChildren(folderId);
            return ResponseEntity.ok(children);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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
