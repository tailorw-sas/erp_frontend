package com.tailorw.tcaInnsist.application.service.manageHotel;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.tailorw.tcaInnsist.domain.dto.ManageHotelDto;
import com.tailorw.tcaInnsist.domain.services.IManageHotelService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SearchManageHotelService {

    private final IManageHotelService manageHotelService;

    public SearchManageHotelService(IManageHotelService manageHotelService){
        this.manageHotelService = manageHotelService;
    }

    public ManageHotelDto getManageHotel(String code){
        ManageHotelDto hotelDto = this.manageHotelService.getByCode(code);
        if(Objects.isNull(hotelDto)){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_HOTEL_NOT_FOUND, new ErrorField("hotel", DomainErrorMessage.MANAGE_HOTEL_NOT_FOUND.getReasonPhrase())));
        }

        if(Objects.isNull(hotelDto.getTradingCompanyId())){
            throw new IllegalArgumentException(String.format("The hotel %s does not have a trading company associated.", hotelDto.getCode()));
        }

        return hotelDto;
    }
}
