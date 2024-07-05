package com.kynsoft.finamer.settings.domain.rules.manageAgency;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageAgencyCifMustNotBeNullRule extends BusinessRule {

    private final String cif;

    public ManageAgencyCifMustNotBeNullRule(String cif) {
        super(
                DomainErrorMessage.MANAGE_AGENCY_CIF_CANNOT_BE_EMPTY,
                new ErrorField("name", "The cif cannot be empty.")
        );
        this.cif = cif;
    }

    @Override
    public boolean isBroken() {
        return this.cif == null || this.cif.isEmpty();
    }
}
