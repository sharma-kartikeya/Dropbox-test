package com.dropbox.DropboxTest.repositories;

import com.dropbox.DropboxTest.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query
    User findByEmail(String email);
}
