package ru.mafteroid.secretsanta.storage;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.util.ResourceBundle;
import java.util.UUID;

public interface ImageStorage {
    void save(String key, byte[] data);

    Resource load(String key);

    void delete(String key);

    boolean exists(String key);


}
