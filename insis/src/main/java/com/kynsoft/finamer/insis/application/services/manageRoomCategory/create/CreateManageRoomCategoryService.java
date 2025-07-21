package com.kynsoft.finamer.insis.application.services.manageRoomCategory.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.insis.domain.dto.ManageRoomCategoryDto;
import com.kynsoft.finamer.insis.domain.services.IManageRoomCategoryService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreateManageRoomCategoryService {

    private final IManageRoomCategoryService manageRoomCategoryService;

    public CreateManageRoomCategoryService(IManageRoomCategoryService manageRoomCategoryService){
        this.manageRoomCategoryService = manageRoomCategoryService;
    }

    public List<ManageRoomCategoryDto> createRoomCategories(List<String> manageRoomCategoryCodes){
        RulesChecker.checkRule(new ValidateObjectNotNullRule<List<String>>(manageRoomCategoryCodes, "manageRoomCategoryCodes", "The room category list must not be null"));

        Map<String, UUID> currentRoomCategoryMap = this.getManageRoomCategoryMapByCode(manageRoomCategoryCodes);

        List<String> misingRoomCategories = manageRoomCategoryCodes.stream()
                .filter(roomCategoryCode -> !currentRoomCategoryMap.containsKey(roomCategoryCode))
                .toList();

        if(!misingRoomCategories.isEmpty()){
            List<ManageRoomCategoryDto> newRoomCategories = misingRoomCategories.stream()
                    .map(roomCategoryCode -> this.getManageRoomCategoryDto(
                            roomCategoryCode,
                            roomCategoryCode
                    ))
                    .collect(Collectors.toList());

            this.manageRoomCategoryService.createMany(newRoomCategories);
            return newRoomCategories;
        }

        return Collections.emptyList();
    }

    private Map<String, UUID> getManageRoomCategoryMapByCode(List<String> manageRoomCategoryCodes){
        return this.manageRoomCategoryService.findIdsByCodes(manageRoomCategoryCodes);
    }

    private ManageRoomCategoryDto getManageRoomCategoryDto(String code, String name){
        return new ManageRoomCategoryDto(
                UUID.randomUUID(),
                code,
                name,
                "ACTIVE",
                null
        );
    }
}
