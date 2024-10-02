package com.kynsof.share.core.infrastructure.services;

import com.kynsof.share.config.StorageConfig;
import com.kynsof.share.core.domain.service.IStorageService;
import com.kynsof.share.core.infrastructure.exceptions.StorageException;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class FileSystemStorageServiceImpl implements IStorageService {
    private Path rootLocation;
    private final StorageConfig config;


    public FileSystemStorageServiceImpl(StorageConfig config) {
        this.config = config;
        init();
    }

    @Override
    public void store(Flux<FilePart> files, String importProcessId) {
        Path importPath = getImportLocation(importProcessId);
        this.createDirectory(importPath);
        files.publishOn(Schedulers.boundedElastic()).flatMap(filePart -> {
            Path transferPath = Paths.get(importPath.toFile().getAbsolutePath(), filePart.filename());
            transferPath.toFile().getParentFile().mkdirs();
          return filePart.transferTo(transferPath.toFile());
        }).doOnError(Throwable::printStackTrace).subscribe();
    }

    @Override
    public Stream<Path> loadAll(String importProcessId) {
        try {
            Path importPath = getImportLocation(importProcessId);
            return Files.walk(importPath, 5)
                    .filter(path -> !path.equals(importPath));
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public InputStream loadAsResource(String filename, String importProcessId) {
        try {
            Path file = getImportLocation(importProcessId);

            return new FileInputStream(Paths.get(file.toFile().getAbsolutePath(),filename).toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll(String importProcessId) {
        Path importPath = getImportLocation(importProcessId);
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
        if (Objects.nonNull(config.getUploadLocation())) {
            this.rootLocation = Paths.get(config.getUploadLocation());
            createDirectory(rootLocation);
        }
    }

    private Path getImportLocation(String importProcessId) {
        return Paths.get(rootLocation.toFile().getAbsolutePath(), importProcessId);
    }

}
