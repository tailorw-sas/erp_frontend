package com.kynsoft.finamer.payment.application.command.replicate.objects;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateReplicateRequest {
    private List<ObjectEnum> objects;
}
