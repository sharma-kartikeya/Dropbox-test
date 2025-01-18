package com.dropbox.DropboxTest.repositories;

import com.dropbox.DropboxTest.models.user.User;
import com.dropbox.DropboxTest.models.user.UserRole;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(@NonNull String email);

    @Query(value = "SELECT * FROM users WHERE email IN (:emails)", nativeQuery = true)
    List<User> findAllByEmail(@NonNull List<String> emails);

    @Transactional
    @Modifying()
    @Query(value = "DELETE FROM users WHERE role <> :role", nativeQuery = true)
    void deleteAllByRoleNotLike(UserRole role);
}
