package com.kynsoft.finamer.payment.application.command.replicate.objects;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReplicateRequest {
    private List<ObjectEnum> objects;
}
