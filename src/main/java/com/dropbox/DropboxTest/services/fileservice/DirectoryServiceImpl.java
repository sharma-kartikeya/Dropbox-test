package com.dropbox.DropboxTest.services.fileservice;

import com.dropbox.DropboxTest.models.Directory;
import com.dropbox.DropboxTest.repositories.DirectoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DirectoryServiceImpl implements DirectoryService {
    @Autowired
    private DirectoryRepository directoryRepository;

    @Override
    public Directory createDirectory(String name, String parentId) {
        try {
            Directory parentDirectory = null;
            if(parentId != null) {
                parentDirectory = directoryRepository.findById(parentId).orElse(null);
                if(parentDirectory == null) {
                    throw new RuntimeException("Parent directory not found");
                }
            }
            Directory directory = new Directory();
            directory.setName(name);
            directory.setParent(parentDirectory);
            directoryRepository.save(directory);
            return directory;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Directory createFile(String name, String parentId, String key) {
        try {
            Directory directory = directoryRepository.findById(parentId).orElse(null);
            if(directory == null) {
                throw new RuntimeException("Parent directory not found");
            }
            Directory file = new Directory();
            file.setName(name);
            file.setParent(directory);
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
    public boolean checkRelated(String directoryId, String parentId) {
        return false;
    }

    @Override
    public void moveDirectory(String id, String targetParentId) {
        try {
            Directory directory = directoryRepository.findById(id).orElse(null);
            if(directory == null) {
                throw new RuntimeException("Directory not found");
            }
            Directory targetDirectory = directoryRepository.findById(targetParentId).orElse(null);
            if(targetDirectory == null) {
                throw new RuntimeException("Target directory not found");
            }
            directory.setParent(targetDirectory);
            directoryRepository.save(directory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteDirectory(String id, boolean cascade) {
        directoryRepository.deleteById(id);
    }

    @Override
    public List<Directory> getChildren(String parentId) {
        return directoryRepository.getChildren(parentId);
    }

    @Override
    public List<Directory> getAllChildren(String id) {
        return List.of();
    }

    @Override
    public List<Directory> getPathDirectories(String id) {
        return directoryRepository.getPath(id);
    }

    @Override
    public List<Directory> getAllDescendantsFiles(String id) {
        return directoryRepository.getAllDescendantsFiles(id);
    }

    @Override
    public List<Directory> getAllDirectories() {
        return directoryRepository.findAll();
    }
}