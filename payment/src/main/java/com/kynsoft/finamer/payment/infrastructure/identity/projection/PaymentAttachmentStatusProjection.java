package com.kynsoft.finamer.payment.infrastructure.identity.projection;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PaymentAttachmentStatusProjection {
    private UUID id;
    private String code;
    private String name;
    private String status;
    private  Boolean defaults;
    private  boolean nonNone;
    private  boolean patWithAttachment;
    private  boolean pwaWithOutAttachment;
    private  boolean supported;
    private  boolean otherSupport;

    public PaymentAttachmentStatusProjection(UUID id, String code, String name, String status, Boolean defaults,
                                             boolean nonNone, boolean patWithAttachment, boolean pwaWithOutAttachment,
                                             boolean supported, boolean otherSupport) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.defaults = defaults;
        this.nonNone = nonNone;
        this.patWithAttachment = patWithAttachment;
        this.pwaWithOutAttachment = pwaWithOutAttachment;
        this.supported = supported;
        this.otherSupport = otherSupport;
    }
    // getters...
}