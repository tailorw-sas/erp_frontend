package com.kynsoft.finamer.invoicing.domain.services;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(Flux<FilePart> files, String importProcessId);

    Stream<Path> loadAll(String importProcessId);

    Path load(String filename);

    InputStream loadAsResource(String filename,String importProcessId);

    void deleteAll(String importProcessId);

    void createDirectory(Path path);
}
