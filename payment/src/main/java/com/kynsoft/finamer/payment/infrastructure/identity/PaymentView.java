package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.io.Serializable;
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
}