package com.dropbox.DropboxTest.services.userdirectorymetaservice;

import com.dropbox.DropboxTest.models.userdirectory.UserDirectoryAccess;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface UserDirectoryAccessService {
    boolean checkUserDirectoryAccess(@NonNull String userId, @NonNull String directoryId, @NonNull UserDirectoryAccessType accessType);

    UserDirectoryAccess upsertUserDirectoryAccess(String userId,
                                                String directoryId,
                                                UserDirectoryAccessType accessType);

    List<UserDirectoryAccess> upsertAccessByUsersAndDirectories(List<String> userIds,
                                               List<String> directoryIds,
                                               UserDirectoryAccessType accessType);

    List<UserDirectoryAccess> getReadAccessForUser(String userId);

    Optional<UserDirectoryAccess> getUserDirectoryAccess(@NonNull String userId, @NonNull String directoryId);

    void deleteUserDirectoryAccessByDirectoryIds(List<String> directoryIds);
}
