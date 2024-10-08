package com.kynsof.share.core.domain.service;

import java.io.InputStream;

public interface IFtpService {

    /**
     * Envía un archivo al servidor FTP especificado.
     *
     * @param inputStream El flujo de entrada del archivo que se enviará.
     * @param fileName    El nombre del archivo que se va a guardar en el servidor FTP.
     * @param server      La dirección del servidor FTP.
     * @param user        El nombre de usuario para autenticar en el servidor FTP.
     * @param password    La contraseña para autenticar en el servidor FTP.
     * @param port        El puerto del servidor FTP.
     */
    void sendFile(InputStream inputStream, String fileName, String server, String user, String password, int port, String path);
}
