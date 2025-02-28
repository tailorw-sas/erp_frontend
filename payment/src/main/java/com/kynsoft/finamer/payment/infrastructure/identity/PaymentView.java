package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Immutable
@Table(name = "payment_view") // Mapea la vista en la BD
public class PaymentView implements Serializable {

    @Id
    @Column(name = "id") // ID del pago
    private UUID id;

    @Column(name = "payment_internal_id") // ID interno del pago
    private Long paymentInternalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status") // Estado del pago
    private Status paymentStatus;

    @Column(name = "hotel_code") // Código del hotel
    private String hotelCode;

    @Column(name = "hotel_name") // Nombre del hotel
    private String hotelName;

    @Column(name = "hotel_id") // ID del hotel
    private UUID hotelId;

    @Column(name = "agency_code") // Código de la agencia
    private String agencyCode;

    @Column(name = "agency_name") // Nombre de la agencia
    private String agencyName;

    @Column(name = "agency_id") // ID de la agencia
    private UUID agencyId;

    @Column(name = "hasattachment") // Indica si tiene adjunto
    private boolean hasAttachment;

    // Estado del pago
    @Column(name = "payment_status_id")
    private UUID paymentStatusId;

    @Column(name = "payment_status_code")
    private String paymentStatusCode;

    @Column(name = "payment_status_name")
    private String paymentStatusName;

    @Column(name = "payment_status_confirmed")
    private Boolean paymentStatusConfirmed;

    @Column(name = "payment_status_applied")
    private Boolean paymentStatusApplied;

    @Column(name = "payment_status_cancelled")
    private Boolean paymentStatusCancelled;

    @Column(name = "payment_status_transit")
    private Boolean paymentStatusTransit;

    @Column(name = "payment_status_status")
    private String paymentStatusStatus;

    @Column(name = "transactiondate") // Fecha de la transacción
    private LocalDate transactionDate;

    @Column(name = "createdat") // Fecha de creación
    private OffsetDateTime createdAt;

    @Column(name = "paymentamount") // Monto del pago
    private Double paymentAmount;

    @Column(name = "depositbalance") // Saldo de depósito
    private Double depositBalance;

    @Column(name = "applied") // Monto aplicado
    private Double applied;

    @Column(name = "notapplied") // Monto no aplicado
    private Double notApplied;

    // Estado del adjunto del pago
    @Column(name = "payment_attachment_status_id")
    private UUID paymentAttachmentStatusId;

    @Column(name = "payment_attachment_status_code")
    private String paymentAttachmentStatusCode;

    @Column(name = "payment_attachment_status_name")
    private String paymentAttachmentStatusName;

    @Column(name = "payment_attachment_status_NonNone")
    private Boolean paymentAttachmentStatusNonNone;

    @Column(name = "payment_attachment_status_patwithattachment")
    private Boolean paymentAttachmentStatusPatWithAttachment;

    @Column(name = "payment_attachment_status_pwawithoutattachment")
    private Boolean paymentAttachmentStatusPwAWithoutAttachment;

    @Column(name = "payment_attachment_status_supported")
    private Boolean paymentAttachmentStatusSupported;

    @Column(name = "payment_attachment_status_status")
    private String paymentAttachmentStatusStatus;
}