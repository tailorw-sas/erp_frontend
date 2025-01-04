package com.kynsoft.finamer.invoicing.domain.rules.manageRoomRate;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageRoomRateCheckAdultsAndChildrenRule extends BusinessRule {

    private final Integer adults;
    private final Integer children;

    public ManageRoomRateCheckAdultsAndChildrenRule(Integer adults, Integer children) {
        super(
                DomainErrorMessage.MANAGE_ROOM_RATE_ADULTS_CHILDREN, 
                new ErrorField("adults_children", DomainErrorMessage.MANAGE_ROOM_RATE_ADULTS_CHILDREN.getReasonPhrase())
        );
        this.adults = adults;
        this.children = children;
    }

    @Override
    public boolean isBroken() {
        return adults == 0 && children == 0;
    }

}
