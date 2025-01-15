package com.dropbox.DropboxTest.repositories;

import com.dropbox.DropboxTest.models.User;
import com.dropbox.DropboxTest.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query
    User findByEmail(String email);

    @Transactional
    @Modifying()
    @Query(value = "DELETE FROM users WHERE role <> :role", nativeQuery = true)
    void deleteAllByRoleNotLike(UserRole role);
}
