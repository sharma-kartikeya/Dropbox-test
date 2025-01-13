package com.dropbox.DropboxTest.controllers.file.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FolderCreationRequest {
    @NotBlank(message = "Parent Folder Id not present")
    private String parentFolderId;

    @NotBlank(message = "Folder name not present")
    private String name;
}
