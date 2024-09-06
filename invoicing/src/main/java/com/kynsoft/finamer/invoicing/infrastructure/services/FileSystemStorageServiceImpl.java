package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsoft.finamer.invoicing.domain.services.StorageService;
import com.kynsoft.finamer.invoicing.domain.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Flux;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class FileSystemStorageServiceImpl implements StorageService {
    private final Path rootLocation;
    @Value("${upload.location}")
    private String location;


    public FileSystemStorageServiceImpl() {
        this.rootLocation = Paths.get(location);
    }

    @Override
    public Flux<?> store(Flux<FilePart> files, String importProcessId) {
        Path importPath = getImportLocation(importProcessId);
        this.createDirectory(importPath);
        return files.map(filePart -> filePart.transferTo(importPath));
    }

    @Override
    public Stream<Path> loadAll(String importProcessId) {
        try {
            Path importPath=getImportLocation(importProcessId);
            return Files.walk(importPath, 1)
                    .filter(path -> !path.equals(importPath))
                    .map(importPath::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public InputStream loadAsResource(String filename) {
        try {
            Path file = load(filename);
            return new FileInputStream(file.toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll(String importProcessId) {
        Path importPath=getImportLocation(importProcessId);
        FileSystemUtils.deleteRecursively(importPath.toFile());
    }

    @Override
    public void createDirectory(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void init() {
        createDirectory(rootLocation);
    }

    private Path getImportLocation(String importProcessId) {
        return Paths.get(rootLocation.toFile().getAbsolutePath(), importProcessId);
    }

}
