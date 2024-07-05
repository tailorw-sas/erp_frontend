package com.kynsoft.notification.application;

import com.kynsof.share.core.domain.bus.query.IResponse;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SaveFileS3Response implements IResponse {
    private UUID path; // Es la primary key que contiene el path de amazon para recuperar el archivo.
    private String fileName; // File Name
}
