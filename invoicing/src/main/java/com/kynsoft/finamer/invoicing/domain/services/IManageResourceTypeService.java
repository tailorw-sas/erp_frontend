package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsoft.finamer.invoicing.domain.dto.ResourceTypeDto;

import java.util.UUID;

public interface IManageResourceTypeService {
    UUID create(ResourceTypeDto dto);

    void update(ResourceTypeDto dto);

    void delete(ResourceTypeDto dto);

    ResourceTypeDto findById(UUID id);

    

    Long countByCodeAndNotId(String code, UUID id);



  
}
