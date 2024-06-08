package com.kynsof.share.core.domain.response;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse implements IResponse, Serializable {

    @Serial
    private static final long serialVersionUID = 1L; // Identificador de versión de clase para la serialización

    private List data;
    private Integer totalPages;
    private Integer totalElementsPage;
    private Long totalElements;
    private Integer size;
    private Integer page;

}
