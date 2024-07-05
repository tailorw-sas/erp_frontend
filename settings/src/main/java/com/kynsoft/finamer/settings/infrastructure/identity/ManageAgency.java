package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.EGenerationType;
import com.kynsoft.finamer.settings.domain.dtoEnum.ESentFileFormat;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_agency")
public class ManageAgency {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;

    private String cif;
    private String agencyAlias;
    private Boolean audit;
    private String zipCode;
    private String address;
    private String mailingAddress;
    private String phone;
    private String alternativePhone;
    private String email;
    private String alternativeEmail;
    private String contactName;
    private Boolean autoReconcile;
    private Integer creditDay;
    private String rfc;
    private Boolean validateCheckout;
    private String bookingCouponFormat;
    private String description;
    private String city;

    @Enumerated(EnumType.STRING)
    private EGenerationType generationType;

    @Enumerated(EnumType.STRING)
    private ESentFileFormat sentFileFormat;


    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "manage_agency_type_id")
    private ManageAgencyType agencyType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_client_id")
    private ManageClient client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_b2bpartner_id")
    private ManageB2BPartner sentB2BPartner;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_country_id")
    private ManageCountry country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_city_state_id")
    private ManageCityState cityState;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String name;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public ManageAgency(ManageAgencyDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.name = dto.getName();
        this.cif = dto.getCif();
        this.agencyAlias = dto.getAgencyAlias();
        this.audit = dto.getAudit();
        this.zipCode = dto.getZipCode();
        this.address = dto.getAddress();
        this.mailingAddress = dto.getMailingAddress();
        this.phone = dto.getPhone();
        this.alternativePhone = dto.getAlternativePhone();
        this.email = dto.getEmail();
        this.alternativeEmail = dto.getAlternativeEmail();
        this.contactName = dto.getContactName();
        this.autoReconcile = dto.getAutoReconcile();
        this.creditDay = dto.getCreditDay();
        this.rfc = dto.getRfc();
        this.validateCheckout = dto.getValidateCheckout();
        this.bookingCouponFormat = dto.getBookingCouponFormat();
        this.description = dto.getDescription();
        this.city = dto.getCity();
        this.generationType = dto.getGenerationType();
        this.sentFileFormat = dto.getSentFileFormat();
        this.agencyType = dto.getAgencyType() != null ? new ManageAgencyType(dto.getAgencyType()) : null;
        this.client = dto.getClient() != null ? new ManageClient(dto.getClient()) : null;
        this.sentB2BPartner = dto.getSentB2BPartner() != null ? new ManageB2BPartner(dto.getSentB2BPartner()) : null;
        this.country = dto.getCountry() != null ? new ManageCountry(dto.getCountry()) : null;
        this.cityState = dto.getCityState() != null ? new ManageCityState(dto.getCityState()) : null;
    }

    public ManageAgencyDto toAggregate() {
        return new ManageAgencyDto(
                id, code, status, name, cif, agencyAlias, audit, zipCode, address, mailingAddress, phone, alternativePhone, email, alternativeEmail, contactName, autoReconcile, creditDay, rfc, validateCheckout, bookingCouponFormat, description, city, generationType, sentFileFormat, agencyType.toAggregate(), client.toAggregate(), sentB2BPartner.toAggregate(), country.toAggregate(), cityState.toAggregate()
        );
    }

    public ManageAgencyDto toAggregateSample() {
        return new ManageAgencyDto(
                id, code, status, name, cif, agencyAlias, audit, zipCode,
                address, mailingAddress, phone, alternativePhone, email, alternativeEmail,
                contactName, autoReconcile, creditDay, rfc, validateCheckout, bookingCouponFormat,
                description, city, generationType, sentFileFormat, agencyType.toAggregate(),
                null, sentB2BPartner.toAggregate(), country.toAggregate(), cityState.toAggregate()
        );
    }
}
