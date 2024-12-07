package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.application.query.resourceType.search.GetSearchResourceTypeResponse;
import com.kynsoft.finamer.creditcard.domain.dto.AttachmentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AttachmentResponse implements IResponse {
    private UUID id;
    private Long attachmentId;
    private Long resource;
    private String filename;
    private String file;
    private String remark;
    private ManageAttachmentTypeResponse type;
    private TransactionBasicResponse transaction;
    private String employee;
    private UUID employeeId;
    private LocalDateTime createdAt;
    private GetSearchResourceTypeResponse paymentResourceType;
    private HotelPaymentBasicResponse hotelPayment;

    public AttachmentResponse(AttachmentDto dto) {
        this.id = dto.getId();
        this.remark = dto.getRemark();
        this.file = dto.getFile();
        this.filename = dto.getFilename();
        this.type = dto.getType() != null ? new ManageAttachmentTypeResponse(dto.getType()) : null;
        this.transaction = dto.getTransaction() != null ? new TransactionBasicResponse(dto.getTransaction()) : null;
        this.attachmentId = dto.getAttachmentId();
        this.employee = dto.getEmployee();
        this.employeeId = dto.getEmployeeId();
        this.createdAt = dto.getCreatedAt();
        this.paymentResourceType = dto.getPaymentResourceType() != null
                ? GetSearchResourceTypeResponse.builder()
                    .id(dto.getPaymentResourceType().getId())
                    .code(dto.getPaymentResourceType().getCode())
                    .name(dto.getPaymentResourceType().getName()).build()
                : null;
        this.hotelPayment = dto.getHotelPayment() != null ? new HotelPaymentBasicResponse(dto.getHotelPayment()) : null;
    }
}
