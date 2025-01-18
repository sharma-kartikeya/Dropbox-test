package com.dropbox.DropboxTest.repositories;

import com.dropbox.DropboxTest.models.userdirectory.UserDirectoryAccess;
import com.dropbox.DropboxTest.models.userdirectory.UserDirectoryAccessId;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserDirectoryAccessRepository extends JpaRepository<UserDirectoryAccess, UserDirectoryAccessId> {

    @Override
    <S extends UserDirectoryAccess> @NonNull List<S> saveAll(@NonNull Iterable<S> entities);

    List<UserDirectoryAccess> findAllByUserIdAndReadAccess(@NonNull String userId, @NonNull Boolean readAccess);

    @Modifying
    @Query(value = "DELETE FROM user_directory_meta WHERE directory_id IN (:directoryIds)", nativeQuery = true)
    void deleteAllByDirectoryIds(@NonNull List<String> directoryIds);
}
