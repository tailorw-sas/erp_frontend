package com.tailorw.tcaInnsist.domain.services;

public interface ITcaCatalogService {

    void validateCatalog();

    void validateIfManageHotelExists();

    void validateIfTcaConfigurationPropertiesExist();

}
