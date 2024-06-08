package com.kynsoft.finamer.settings.domain.rules.manageContact;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageContactPhoneRule extends BusinessRule {

    private final String phone;

    public ManageContactPhoneRule(String phone) {
        super(
                DomainErrorMessage.MANAGE_CONTACT_PHONE_INVALID,
                new ErrorField("phone", "The phone is not accepted.")
        );
        this.phone = phone;
    }

    @Override
    public boolean isBroken() {
        return !validateCode(phone);
    }

    /**
     * Este patrón acepta números de teléfono con o sin paréntesis, separados por espacios,
     * guiones o ningún separador. Es útil para validar números de teléfono en varios países.
     *
     * (+53)12345678 no es válido,
     * (53)12345678 es válido,
     * (123)4567890 es válido,
     * 1234567890 es válido,
     * 123-456-7890 es válido,
     * (123)456-7890 es válido,
     * 12345678901 es válido,
     * (1)234567890 no es válido,
     * (123)-4567890 no es válido,
     * 1 no es válido,
     * 12-3456-7890 es válido,
     * 123-4567 es válido
     *
     * @param phone
     * @return
     */
    private boolean validateCode(String phone) {
        Pattern pattern = Pattern.compile("(?:\\(\\d{1,5}\\)|\\d{2,3}[-]*)\\d{2,4}[-]*\\d{2,4}");
        Matcher matcher = pattern.matcher(phone);

        return matcher.matches();
    }
}
