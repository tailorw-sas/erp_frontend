package com.kynsoft.finamer.invoicing.domain.services;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    Flux<?> store(Flux<FilePart> files);

    Stream<Path> loadAll();

    Path load(String filename);

    InputStream loadAsResource(String filename);

    void deleteAll();
}
