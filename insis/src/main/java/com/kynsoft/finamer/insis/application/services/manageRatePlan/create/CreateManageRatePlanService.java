package com.kynsoft.finamer.insis.application.services.manageRatePlan.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.core.domain.rules.ValidateStringNotEmptyRule;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.insis.domain.services.IManageHotelService;
import com.kynsoft.finamer.insis.domain.services.IManageRatePlanService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreateManageRatePlanService {

    private final IManageRatePlanService manageRatePlanService;
    private final IManageHotelService manageHotelService;

    public CreateManageRatePlanService(IManageRatePlanService manageRatePlanService,
                                       IManageHotelService manageHotelService){
        this.manageRatePlanService = manageRatePlanService;
        this.manageHotelService = manageHotelService;
    }

    public List<ManageRatePlanDto> createManageRatePlans(List<String> manageRatePlanCodes, ManageHotelDto hotelDto){
        RulesChecker.checkRule(new ValidateObjectNotNullRule<ManageHotelDto>(hotelDto, "hotelDto", "The manage hotel must not be null"));

        Map<String, UUID> currentManageRatePlanMap = this.getManageRatePlanMapByCode(manageRatePlanCodes, hotelDto.getId());

        List<String> missingRatePlans = manageRatePlanCodes.stream()
                .filter(ratePlan -> !currentManageRatePlanMap.containsKey(ratePlan))
                .toList();

        if(!missingRatePlans.isEmpty()){
            List<ManageRatePlanDto> newRatePlans = missingRatePlans.stream()
                    .map(ratePlanCode -> this.getManageRatePlanDto(
                            ratePlanCode,
                            ratePlanCode,
                            hotelDto
                    ))
                    .collect(Collectors.toList());
            this.manageRatePlanService.createMany(newRatePlans);

            return newRatePlans;
        }

        return Collections.emptyList();
    }

    public List<ManageRatePlanDto> createManageRatePlans(List<String> manageRatePlanCodes, String hotelCode){
        RulesChecker.checkRule(new ValidateObjectNotNullRule<String>(hotelCode, "hotelCode", "The field hotel must not be null"));
        RulesChecker.checkRule(new ValidateStringNotEmptyRule(hotelCode, "hotelCode", "The field hotel must not be empty"));

        ManageHotelDto hotel = this.getManageHotelByCode(hotelCode);

        return this.createManageRatePlans(manageRatePlanCodes, hotel);
    }

    private ManageHotelDto getManageHotelByCode(String hotelCode){
        return this.manageHotelService.findByCode(hotelCode);
    }

    private Map<String, UUID> getManageRatePlanMapByCode(List<String> manageRatePlanCodes, UUID hotelId){
        return this.manageRatePlanService.findIdsByCodes(hotelId, manageRatePlanCodes);
    }

    private ManageRatePlanDto getManageRatePlanDto(String code, String name, ManageHotelDto hotelDto){
        return new ManageRatePlanDto(
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
