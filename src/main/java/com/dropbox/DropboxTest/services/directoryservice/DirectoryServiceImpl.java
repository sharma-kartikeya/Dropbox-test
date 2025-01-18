package com.dropbox.DropboxTest.services.directoryservice;

import com.dropbox.DropboxTest.models.directory.Directory;
import com.dropbox.DropboxTest.repositories.DirectoryRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DirectoryServiceImpl implements DirectoryService {
    @Autowired
    private DirectoryRepository directoryRepository;

    @Override
    public Directory createRootDirectory() {
        try {
            Directory directory = new Directory();
            directory.setName("Root");
            directory.setParentId(null);
            directory.setIsFile(false);
            return directoryRepository.save(directory);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Directory createDirectory(@NonNull String name, @NonNull String parentId) {
        try {
            Directory parentDirectory = directoryRepository.findById(parentId).orElse(null);
            if (parentDirectory == null) {
                throw new RuntimeException("Parent directory not found");
            }

            Directory directory = new Directory();
            directory.setName(name);
            String parentDirectoryId = parentDirectory.getId();
            directory.setParentId(parentDirectoryId);
            directoryRepository.save(directory);
            return directory;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Directory createFile(@NonNull String name, @NonNull String parentId, @NonNull String key) {
        try {
            Directory directory = directoryRepository.findById(parentId).orElse(null);
            if (directory == null) {
                throw new RuntimeException("Parent directory not found");
            }
            Directory file = new Directory();
            file.setName(name);
            file.setParentId(parentId);
            file.setKey(key);
            file.setIsFile(true);
            directoryRepository.save(file);
            return file;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Directory getDirectory(String id) {
        return directoryRepository.findById(id).orElse(null);
    }

    @Override
    public void moveDirectory(String id, String targetParentId) {
        try {
            Directory directory = directoryRepository.findById(id).orElse(null);
            if (directory == null) {
                throw new RuntimeException("Directory not found");
            }
            Directory targetDirectory = directoryRepository.findById(targetParentId).orElse(null);
            if (targetDirectory == null) {
                throw new RuntimeException("Target directory not found");
            }
            directory.setParentId(targetDirectory.getId());
            directoryRepository.save(directory);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    public void deleteDirectoryByCascade(String id) {
        try {
            directoryRepository.deleteDirectoryTree(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteDirectories(List<String> id) {
        try {
            directoryRepository.deleteAllByIdInBatch(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Directory> getChildren(String parentId) {
        return directoryRepository.getChildren(parentId);
    }

    @Override
    public List<Directory> getPathDirectories(String id) {
        return directoryRepository.getPath(id);
    }

    @Override
    public List<Directory> getAllDescendantsDirectories(String id) {
        try {
            return directoryRepository.getAllDescendantsFiles(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Directory> getAllDirectories() {
        return directoryRepository.findAll();
    }
}