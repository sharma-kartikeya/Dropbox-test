package com.dropbox.DropboxTest.controllers.file;

import com.dropbox.DropboxTest.controllers.BaseResponse;
import com.dropbox.DropboxTest.controllers.file.requests.FolderCreationRequest;
import com.dropbox.DropboxTest.controllers.file.requests.ShareDirectoryRequest;
import com.dropbox.DropboxTest.controllers.file.responses.DirectoryResponse;
import com.dropbox.DropboxTest.models.directory.Directory;
import com.dropbox.DropboxTest.models.user.User;
import com.dropbox.DropboxTest.models.user.UserRole;
import com.dropbox.DropboxTest.services.directoryservice.DirectoryService;
import com.dropbox.DropboxTest.services.uploadservice.UploadService;
import com.dropbox.DropboxTest.services.userdirectorymetaservice.UserDirectoryAccessService;
import com.dropbox.DropboxTest.services.userdirectorymetaservice.UserDirectoryAccessType;
import com.dropbox.DropboxTest.services.userservice.UserService;
import com.dropbox.DropboxTest.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(path = "/api/v1/file")
public class FileController {

    @Autowired
    private DirectoryService directoryService;
    @Autowired
    private UserDirectoryAccessService userDirectoryAccessService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private AuthUtils authUtils;
    @Autowired
    private UserService userService;

    @PostMapping(path = "upload")
    public ResponseEntity<BaseResponse<DirectoryResponse>> uploadFile(@RequestParam @NonNull MultipartFile file,
                                                                      @RequestParam @NonNull String folderId) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(BaseResponse.<DirectoryResponse>builder()
                                .setError("File not present!")
                                .build()
                        );
            }
            User currentUser = authUtils.getCurrentUser();
            if (!userDirectoryAccessService.checkUserDirectoryAccess(currentUser.getId(), folderId, UserDirectoryAccessType.OWNER)) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(BaseResponse.<DirectoryResponse>builder()
                                .setError("Unauthorized access!")
                                .build());
            }
            String key = uploadService.uploadFile(file);
            Directory directory;
            try {
                directory = directoryService.createFile(Objects.requireNonNull(file.getOriginalFilename()), folderId, key);
                userDirectoryAccessService.upsertUserDirectoryAccess(currentUser.getId(), directory.getId(), UserDirectoryAccessType.OWNER);
            } catch (Exception e) {
                uploadService.deleteFile(key);
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

    @GetMapping(path = "url")
    public ResponseEntity<BaseResponse<String>> getSignedUrl(@RequestParam String fileId) {
        try {
            User currentUser = authUtils.getCurrentUser();
            if (!userDirectoryAccessService.checkUserDirectoryAccess(currentUser.getId(), fileId, UserDirectoryAccessType.DOWNLOAD)) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(BaseResponse.<String>builder()
                                .setError("Unauthorized access!")
                                .build());
            }
            Directory directory = directoryService.getDirectory(fileId);
            if (directory == null) {
                throw new IllegalArgumentException("No such File");
            }

            if (!directory.getIsFile()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(BaseResponse.<String>builder()
                                .setError("Directory is not a file")
                                .build()
                        );
            }

            String fileUrl = uploadService.getSignedUrl(directory.getKey());

            return ResponseEntity.ok(BaseResponse.<String>builder().setData(fileUrl).build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.<String>builder()
                            .setError(e.getMessage())
                            .build()
                    );
        }
    }

    @PostMapping(path = "upload-url")
    public ResponseEntity<BaseResponse<String>> getPutSignedUrl(@RequestParam String fileName,
                                                                @RequestParam Map<String, String> metaData) {
        try {
            String url = uploadService.putSignedUrl(fileName, metaData);
            return ResponseEntity.ok(BaseResponse.<String>builder().setData(url).build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.<String>builder()
                            .setError(e.getMessage())
                            .build()
                    );
        }
    }

    @PostMapping(path = "create-folder")
    public ResponseEntity<BaseResponse<DirectoryResponse>> createFolder(@RequestBody @Valid FolderCreationRequest request) {
        try {
            User currentUser = authUtils.getCurrentUser();
            if (!userDirectoryAccessService.checkUserDirectoryAccess(currentUser.getId(), request.getParentFolderId(), UserDirectoryAccessType.OWNER)) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(BaseResponse.<DirectoryResponse>builder()
                                .setError("Unauthorized access!")
                                .build());
            }
            Directory directory = directoryService.createDirectory(
                    request.getName(),
                    request.getParentFolderId()
            );

            userDirectoryAccessService.upsertUserDirectoryAccess(currentUser.getId(), directory.getId(), UserDirectoryAccessType.OWNER);

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
            User user = authUtils.getCurrentUser();
            if (folderID == null) {
                folderID = user.getRootDirectoryId();
            } else if (!userDirectoryAccessService.checkUserDirectoryAccess(user.getId(), folderID, UserDirectoryAccessType.READ_ONLY)) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(BaseResponse.<List<DirectoryResponse>>builder()
                                .setError("Unauthorized access!")
                                .build());
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

    @PostMapping(path = "share")
    public ResponseEntity<BaseResponse<String>> shareDirectory(@RequestBody @Valid ShareDirectoryRequest request) {
        try {
            User user = authUtils.getCurrentUser();
            if (!userDirectoryAccessService.checkUserDirectoryAccess(user.getId(), request.getDirectoryId(), UserDirectoryAccessType.SHARE)) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(BaseResponse.<String>builder()
                                .setError("Unauthorized access!")
                                .build());
            }
            List<String> userIds = userService.getUsersByEmails(request.getUserEmails()).stream().map(User::getId).toList();

            if (!request.isReadAccess() && !request.isWriteAccess()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(BaseResponse.<String>builder()
                                .setError("Bad request!")
                                .build());
            }

            UserDirectoryAccessType accessType = request.isWriteAccess() ? UserDirectoryAccessType.READ_WRITE : UserDirectoryAccessType.READ_ONLY;
            userDirectoryAccessService.upsertAccessByUsersAndDirectories(userIds, List.of(request.getDirectoryId()), accessType);

            return ResponseEntity.ok(BaseResponse.<String>builder().setData("Shared").build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<String>builder()
                            .setError("Something went wrong!")
                            .build()
                    );
        }


    }

    @PostMapping(path = "delete")
    public ResponseEntity<BaseResponse<String>> deleteDirectory(@RequestParam String folderId) {
        try {
            User user = authUtils.getCurrentUser();
            if (!userDirectoryAccessService.checkUserDirectoryAccess(user.getId(), folderId, UserDirectoryAccessType.DELETE)) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(BaseResponse.<String>builder()
                                .setError("Unauthorized access!")
                                .build()
                        );
            }

            List<Directory> directories = directoryService.getAllDescendantsDirectories(folderId);

            List<String> keysToDelete = new ArrayList<>();
            List<String> directoryIds = new ArrayList<>();
            directories.forEach(directory -> {
                directoryIds.add(directory.getId());
                if (directory.getIsFile()) {
                    keysToDelete.add(directory.getKey());
                }
            });
            uploadService.deleteFilesByKeys(keysToDelete);
            directoryService.deleteDirectoryByCascade(folderId);
            userDirectoryAccessService.deleteUserDirectoryAccessByDirectoryIds(directoryIds);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(BaseResponse.<String>builder()
                            .setData("Deleted Successfully")
                            .build()
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<String>builder()
                            .setError(e.getMessage())
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
