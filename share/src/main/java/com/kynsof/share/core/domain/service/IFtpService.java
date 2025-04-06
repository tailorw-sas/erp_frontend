package com.kynsof.share.core.domain.service;
import com.kynsof.share.core.application.FileRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IFtpService {

    /**
     * Envía un archivo al servidor FTP especificado.
     *
     * @param fileBytes   El flujo de entrada del archivo que se enviará.
     * @param fileName    El nombre del archivo que se va a guardar en el servidor FTP.
     * @param server      La dirección del servidor FTP.
     * @param user        El nombre de usuario para autenticar en el servidor FTP.
     * @param password    La contraseña para autenticar en el servidor FTP.
     * @param port        El puerto del servidor FTP.
     */
    CompletableFuture<String> sendFile(byte[] fileBytes, String fileName, String server, String user, String password, int port, String path);

    public CompletableFuture<String> sendFiles(List<FileRequest> files, String server, String user, String password, int port, String path);
}
