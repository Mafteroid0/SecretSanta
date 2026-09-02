package ru.mafteroid.secretsanta.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class LocalImageStorage implements ImageStorage{

    private final Path root;

    public LocalImageStorage(
            @Value("${app.storage.path:./uploads}") String path
    ) {
        this.root = Path.of(path)
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public void save(String key, byte[] data) {
        Path path = resolve(key);
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, data);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot save file", e);
        }

    }

    @Override
    public Resource load(String key) {
        Path path = resolve(key);

        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File not found");
        }

        return new FileSystemResource(path);
    }

    @Override
    public void delete(String key) {
        try {
            Files.deleteIfExists(resolve(key));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot delete file", e);
        }
    }

    @Override
    public boolean exists(String key) {
        return Files.exists(resolve(key));
    }

    private Path resolve(String key) {
        Path path = root.resolve(key).normalize();

        if (!path.startsWith(root)) {
            throw new IllegalArgumentException("Invalid storage key");
        }

        return path;
    }
}
