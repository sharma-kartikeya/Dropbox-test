package com.dropbox.DropboxTest.repositories;

import com.dropbox.DropboxTest.models.directory.Directory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DirectoryRepository extends JpaRepository<Directory, String> {
    @Query(
            value = """
                    SELECT * FROM directories WHERE parent_id = :id
                    """,
            nativeQuery = true
    )
    List<Directory> getChildren(String id);

    @Query(value = """
            WITH RECURSIVE Ancestor AS (
                SELECT * FROM directories WHERE id = :id
                UNION ALL
                SELECT d.* FROM directories d, Ancestor a WHERE d.id = a.parent_id
            )
            SELECT * from Ancestor""",
            nativeQuery = true
    )
    List<Directory> getPath(String id);

    @Modifying
    @Query(value = """
            WITH RECURSIVE ToDelete AS (
                SELECT id from directories WHERE id = :id
                UNION ALL
                SELECT d.id FROM directories d, ToDelete t WHERE d.parent_id = t.id
            )
            DELETE FROM directories
            USING ToDelete WHERE directories.id = ToDelete.id;
""", nativeQuery = true)
    void deleteDirectoryTree(String id);

    @Query(
            value = """
                WITH RECURSIVE Descendants AS (
                                    SELECT * FROM directories WHERE id = :id
                                    UNION ALL
                                    SELECT d.* FROM directories d, Descendants de WHERE de.id = d.parent_id
                                )
                SELECT * from Descendants""",
            nativeQuery = true
    )
    List<Directory> getAllDescendantsFiles(String id);
}
