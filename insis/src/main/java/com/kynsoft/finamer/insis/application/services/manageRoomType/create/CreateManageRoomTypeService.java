package com.kynsoft.finamer.insis.application.services.manageRoomType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.core.domain.rules.ValidateStringNotEmptyRule;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.insis.domain.services.IManageHotelService;
import com.kynsoft.finamer.insis.domain.services.IManageRoomTypeService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreateManageRoomTypeService {

    private final IManageRoomTypeService manageRoomTypeService;
    private final IManageHotelService manageHotelService;

    public CreateManageRoomTypeService(IManageRoomTypeService manageRoomTypeService,
                                       IManageHotelService manageHotelService){
        this.manageRoomTypeService = manageRoomTypeService;
        this.manageHotelService = manageHotelService;
    }

    public List<ManageRoomTypeDto> createManageRoomTypes(List<String> manageRoomTypeCodes, ManageHotelDto hotelDto){
        RulesChecker.checkRule(new ValidateObjectNotNullRule<ManageHotelDto>(hotelDto, "hotel", "The manage hotel must not be null"));

        Map<String, UUID> currentRoonTypeMap = this.getManageRoomTypeMapByCodes(manageRoomTypeCodes, hotelDto.getId());

        List<String> misingRoomTypes = manageRoomTypeCodes.stream()
                .filter(roomTypeCode -> !currentRoonTypeMap.containsKey(roomTypeCode))
                .toList();

        if(!misingRoomTypes.isEmpty()){
            List<ManageRoomTypeDto> newRoomTypes = misingRoomTypes.stream()
                    .map(roomTypeCode -> this.getManageRoomTypeDto(
                            roomTypeCode,
                            roomTypeCode,
                            hotelDto
                    ))
                    .collect(Collectors.toList());
            this.manageRoomTypeService.createMany(newRoomTypes);
            return newRoomTypes;
        }

        return Collections.emptyList();
    }

    public List<ManageRoomTypeDto> createManageRoomTypes(List<String> manageRoomTypeCodes, String hotelCode){
        RulesChecker.checkRule(new ValidateObjectNotNullRule<String>(hotelCode, "hotelCode", "The field hotel must not be null"));
        RulesChecker.checkRule(new ValidateStringNotEmptyRule(hotelCode, "hotelCode", "The field hotel must not be empty"));

        ManageHotelDto hotel = this.getManageHotelByCode(hotelCode);

        return this.createManageRoomTypes(manageRoomTypeCodes, hotel);
    }

    private ManageHotelDto getManageHotelByCode(String code){
        return this.manageHotelService.findByCode(code);
    }

    private Map<String, UUID> getManageRoomTypeMapByCodes(List<String> codes, UUID hotelId){
        return this.manageRoomTypeService.findIdsByCodes(codes, hotelId);
    }

    private ManageRoomTypeDto getManageRoomTypeDto(String code, String name, ManageHotelDto hotelDto){
        return new ManageRoomTypeDto(
                UUID.randomUUID(),
                code,
                name,
                "ACTIVE",
                false,
                null,
                hotelDto
        );
    }
}
