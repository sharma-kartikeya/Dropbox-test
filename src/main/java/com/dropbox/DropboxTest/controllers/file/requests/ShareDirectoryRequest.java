package com.dropbox.DropboxTest.controllers.file.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ShareDirectoryRequest {
    @NotBlank
    private String directoryId;

    @NotBlank
    private List<String> userEmails;

    private boolean readAccess;

    private boolean writeAccess;
}
