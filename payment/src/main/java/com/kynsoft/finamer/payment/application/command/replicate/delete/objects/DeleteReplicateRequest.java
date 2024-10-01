package com.kynsoft.finamer.payment.application.command.replicate.delete.objects;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeleteReplicateRequest {
    private List<DeleteObjectEnum> objects;
}
