package com.dropbox.DropboxTest.services.userdirectorymetaservice;

import com.dropbox.DropboxTest.models.userdirectory.AccessParameter;
import com.dropbox.DropboxTest.models.userdirectory.UserDirectoryAccess;
import com.dropbox.DropboxTest.models.userdirectory.UserDirectoryAccessId;
import com.dropbox.DropboxTest.repositories.UserDirectoryAccessRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserDirectoryAccessServiceImpl implements UserDirectoryAccessService {
    @Autowired
    private UserDirectoryAccessRepository userDirectoryAccessRepository;

    private final Map<UserDirectoryAccessType, List<AccessParameter>> accessParametersMap;

    public UserDirectoryAccessServiceImpl() {
        accessParametersMap = new HashMap<>();
        accessParametersMap.put(UserDirectoryAccessType.READ_ONLY, List.of(AccessParameter.READ));
        accessParametersMap.put(UserDirectoryAccessType.READ_WRITE, List.of(AccessParameter.READ, AccessParameter.WRITE));
        accessParametersMap.put(UserDirectoryAccessType.DOWNLOAD, List.of(AccessParameter.READ, AccessParameter.DOWNLOAD));
        accessParametersMap.put(UserDirectoryAccessType.SHARE, List.of(AccessParameter.READ, AccessParameter.SHARE));
        accessParametersMap.put(UserDirectoryAccessType.DELETE, List.of(AccessParameter.READ, AccessParameter.WRITE, AccessParameter.DELETE));
        accessParametersMap.put(UserDirectoryAccessType.OWNER, List.of(AccessParameter.OWNER));
    }

    private void setUserDirectoryAccessParameter(UserDirectoryAccess userDirectoryAccess, UserDirectoryAccessType type) {
        List<AccessParameter> accessParameters = accessParametersMap.get(type);
        for (AccessParameter accessParameter : accessParameters) {
            switch (accessParameter) {
                case READ:
                    userDirectoryAccess.setReadAccess(true);
                    break;
                case WRITE:
                    userDirectoryAccess.setWriteAccess(true);
                    break;
                case DOWNLOAD:
                    userDirectoryAccess.setDownloadAccess(true);
                    break;
                case SHARE:
                    userDirectoryAccess.setShareAccess(true);
                    break;
                case DELETE:
                    userDirectoryAccess.setDeleteAccess(true);
                    break;
                case OWNER:
                    userDirectoryAccess.setIsOwner(true);
            }
        }
    }

    @Override
    public boolean checkUserDirectoryAccess(@NonNull String userId, @NonNull String directoryId, @NonNull UserDirectoryAccessType accessType) {
        UserDirectoryAccess userDirectoryAccess = userDirectoryAccessRepository.findById(new UserDirectoryAccessId(userId, directoryId)).orElse(null);
        if (userDirectoryAccess == null) {
            return false;
        }

        if (userDirectoryAccess.getIsOwner()) {
            return true;
        }

        List<AccessParameter> accessParameters = accessParametersMap.get(accessType);
        for (AccessParameter accessParameter : accessParameters) {
            switch (accessParameter) {
                case READ:
                    if (!userDirectoryAccess.getReadAccess()) {
                        return false;
                    }
                    break;
                case WRITE:
                    if (!userDirectoryAccess.getWriteAccess()) {
                        return false;
                    }
                    break;
                case DOWNLOAD:
                    if (!userDirectoryAccess.getDownloadAccess()) {
                        return false;
                    }
                    break;
                case SHARE:
                    if (!userDirectoryAccess.getShareAccess()) {
                        return false;
                    }
                    break;
                case DELETE:
                    if (!userDirectoryAccess.getDeleteAccess()) {
                        return false;
                    }
                    break;

            }
        }
        return true;
    }

    @Override
    public UserDirectoryAccess upsertUserDirectoryAccess(String userId, String directoryId, UserDirectoryAccessType access) {
        try {
            UserDirectoryAccess userDirectoryAccess = userDirectoryAccessRepository.findById(new UserDirectoryAccessId(userId, directoryId)).orElse(new UserDirectoryAccess());
            userDirectoryAccess.setUserId(userId);
            userDirectoryAccess.setDirectoryId(directoryId);
            setUserDirectoryAccessParameter(userDirectoryAccess, access);
            return userDirectoryAccessRepository.save(userDirectoryAccess);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<UserDirectoryAccess> upsertAccessByUsersAndDirectories(List<String> userIds, List<String> directoryIds, UserDirectoryAccessType accessType) {
        try {
            List<UserDirectoryAccessId> userDirectoryAccessIds = new ArrayList<>();
            userIds.forEach(userId -> directoryIds
                    .forEach(directoryId -> userDirectoryAccessIds
                            .add(new UserDirectoryAccessId(userId, directoryId))
                    )
            );

            Map<UserDirectoryAccessId, UserDirectoryAccess> existingUserDirectoryMetaMap = userDirectoryAccessRepository
                    .findAllById(userDirectoryAccessIds)
                    .stream()
                    .collect(Collectors.toMap(
                            userDirectoryAccess -> new UserDirectoryAccessId(
                                    userDirectoryAccess.getUserId(),
                                    userDirectoryAccess.getDirectoryId())
                            ,
                            userDirectoryAccess -> userDirectoryAccess)
                    );

            List<UserDirectoryAccess> userDirectoryAccessListToSave = new ArrayList<>();
            userIds.forEach(userId -> directoryIds.forEach(directoryId -> {
                UserDirectoryAccess userDirectoryAccess = existingUserDirectoryMetaMap
                        .getOrDefault(
                                new UserDirectoryAccessId(userId, directoryId),
                                new UserDirectoryAccess(userId, directoryId)
                        );
                setUserDirectoryAccessParameter(userDirectoryAccess, accessType);

            }));
            return userDirectoryAccessRepository.saveAll(userDirectoryAccessListToSave);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<UserDirectoryAccess> getReadAccessForUser(String userId) {
        try {
            List<UserDirectoryAccess> userDirectoryAccessList = userDirectoryAccessRepository.findAllByUserIdAndReadAccess(userId, true);
            return userDirectoryAccessList.stream().filter(userAccessDirectory -> !userAccessDirectory.getIsOwner()).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public Optional<UserDirectoryAccess> getUserDirectoryAccess(@NonNull String userId, @NonNull String directoryId) {
        return userDirectoryAccessRepository.findById(new UserDirectoryAccessId(userId, directoryId));
    }

    @Override
    @Transactional
    public void deleteUserDirectoryAccessByDirectoryIds(List<String> directoryIds) {
        try {
            userDirectoryAccessRepository.deleteAllByDirectoryIds(directoryIds);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
