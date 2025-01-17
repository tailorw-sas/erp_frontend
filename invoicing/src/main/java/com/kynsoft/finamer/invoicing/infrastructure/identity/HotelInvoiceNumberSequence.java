package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.HotelInvoiceNumberSequenceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hotel_invoice_number_sequence")
public class HotelInvoiceNumberSequence {

    @Id
    @Column(name = "id")
    private UUID id;

    private Long invoiceNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_hotel")
    private ManageHotel hotel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trading_company_id")
    private ManageTradingCompanies manageTradingCompanies;

    @Enumerated(EnumType.STRING)
    private EInvoiceType invoiceType;

    public HotelInvoiceNumberSequence(HotelInvoiceNumberSequenceDto dto) {
        this.id = dto.getId();
        this.invoiceNo = dto.getInvoiceNo();
        this.hotel = dto.getHotel() != null ? new ManageHotel(dto.getHotel()) : null;
        this.manageTradingCompanies = dto.getManageTradingCompanies() != null ? new ManageTradingCompanies(dto.getManageTradingCompanies()) : null;
        this.invoiceType = dto.getInvoiceType();
    }

    public HotelInvoiceNumberSequenceDto toAggregate() {
        ManageHotelDto hotelDto = this.hotel != null ? this.hotel.toAggregate() : null;
        ManageTradingCompaniesDto tradingCompaniesDto = this.manageTradingCompanies != null ? this.manageTradingCompanies.toAggregate() : null;
        return new HotelInvoiceNumberSequenceDto(id, invoiceNo, hotelDto, tradingCompaniesDto, invoiceType);
    }

}
