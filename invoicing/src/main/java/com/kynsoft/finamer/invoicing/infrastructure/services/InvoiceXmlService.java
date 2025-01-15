package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml.*;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml.Tax;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageTradingCompaniesDto;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceXmlService {

    public String generateInvoiceXml(ManageInvoiceDto manageInvoiceDto) {
        InvoiceXml invoiceXml = mapToInvoiceXml(manageInvoiceDto);
        return buildXmlString(invoiceXml);
    }

    private InvoiceXml mapToInvoiceXml(ManageInvoiceDto dto) {
        InvoiceXml invoiceXml = new InvoiceXml();
        ManageTradingCompaniesDto manageTradingCompaniesDto = dto.getHotel().getManageTradingCompanies();
        // General Data
        GeneralData generalData = new GeneralData();
        generalData.setRef(dto.getHotel().getPrefixToInvoice() + dto.getInvoiceNumber());
        generalData.setDate(LocalDate.now());
        generalData.setCurrency("USD");
        generalData.setTaxIncluded(true);
        generalData.setStatus(""); // Default status
        invoiceXml.setGeneralData(generalData);

        // Supplier
        Supplier supplier = new Supplier();
        supplier.setCode(dto.getHotel().getBabelCode() != null ? dto.getHotel().getBabelCode() : "");

        supplier.setCif(dto.getHotel().getBabelCode() != null ? dto.getHotel().getBabelCode() : "");
        supplier.setCompany(manageTradingCompaniesDto.getCompany() != null ? manageTradingCompaniesDto.getCompany() : "");
        supplier.setAddress(manageTradingCompaniesDto.getAddress() != null ? manageTradingCompaniesDto.getAddress() : "");
        supplier.setCity(manageTradingCompaniesDto.getCity() != null ? manageTradingCompaniesDto.getCity() : "");
        supplier.setZipCode("N/A");
        supplier.setCityState(manageTradingCompaniesDto.getCityState() != null ? manageTradingCompaniesDto.getCityState().getName() : "");//TODO
        supplier.setCountry(manageTradingCompaniesDto.getCountry()!= null ? manageTradingCompaniesDto.getCountry().getName() : "");
        invoiceXml.setSupplier(supplier);

        // Client
        Client client = new Client();
        client.setCode(dto.getAgency().getCode() != null ? dto.getAgency().getCode() : "");
        client.setCif(dto.getAgency().getCif() != null ? dto.getAgency().getCif() : "");
        client.setCompany(dto.getAgency().getName() != null ? dto.getAgency().getName() : "");
        client.setAddress(dto.getAgency().getAddress() != null ? dto.getAgency().getAddress() : "");
        client.setCity(dto.getAgency().getCityState().getName() != null ? dto.getAgency().getCityState().getName() : "");
        client.setZipCode(dto.getAgency().getZipCode() != null ? dto.getAgency().getZipCode() : "");
        client.setCityState(dto.getAgency().getCityState().getName());
        client.setCountry(dto.getAgency().getCountry().getCode());
        client.setEmail(dto.getAgency().getMailingAddress() != null ? dto.getAgency().getMailingAddress() : "");
        invoiceXml.setClient(client);

        // Products
        List<Product> products = dto.getBookings().stream()
                .map(booking -> mapBookingToProduct(booking, dto.getHotel())) // Pasar el hotel aquí
                .collect(Collectors.toList());

        invoiceXml.setProductList(products);

        // Total Summary
        TotalSummary totalSummary = new TotalSummary();
        totalSummary.setGrossAmount(dto.getInvoiceAmount());
        totalSummary.setDiscounts(0.0);
        totalSummary.setSubTotal(dto.getInvoiceAmount());
        totalSummary.setTax(0.0);
        totalSummary.setTotal(dto.getInvoiceAmount());
        invoiceXml.setTotalSummary(totalSummary);

        return invoiceXml;
    }

    private Product mapBookingToProduct(ManageBookingDto bookingDto, ManageHotelDto hotelDto) {
        Product product = new Product();
        product.setSupplierSku(bookingDto.getHotelBookingNumber() != null ? bookingDto.getHotelBookingNumber() : "");
        product.setCustomerSku(bookingDto.getReservationNumber() != null ? bookingDto.getReservationNumber().toString() : "");
        product.setItem(bookingDto.getDescription() != null ? bookingDto.getDescription() : "Servicio de alojamiento");

        int days = (int) ChronoUnit.DAYS.between(bookingDto.getCheckIn(), bookingDto.getCheckOut());
        double unitPrice = days > 0 ? bookingDto.getInvoiceAmount() / days : bookingDto.getInvoiceAmount();
        product.setQty(days);
        product.setMu("Unidades");
        product.setUp(Math.round(unitPrice * 100.0) / 100.0);
        product.setTotal(bookingDto.getInvoiceAmount());
        product.setComment("Reserva desde " + bookingDto.getCheckIn().format(DateTimeFormatter.ISO_DATE) +
                " hasta " + bookingDto.getCheckOut().format(DateTimeFormatter.ISO_DATE));

        // Taxes
        Tax tax = new Tax();
        tax.setType("EXENTO");
        tax.setRate(0);
        tax.setAmount(0);
        tax.setDescription("");
        product.setTaxes(Collections.singletonList(tax));

        // Services Data
        ServiceData serviceData = new ServiceData();
        serviceData.setSupplierClientId(hotelDto.getBabelCode() != null ? hotelDto.getBabelCode() : "");
        serviceData.setSupplierId("SCR");
        serviceData.setSupplierName(hotelDto.getName());
        serviceData.setPax(bookingDto.getLastName()+","+bookingDto.getFirstName());
        serviceData.setBeginDate(bookingDto.getCheckIn().toLocalDate());
        serviceData.setEndDate(bookingDto.getCheckOut().toLocalDate());
        serviceData.setPaxNumber(bookingDto.getAdults() + bookingDto.getChildren());
        serviceData.setAdultsNumber(bookingDto.getAdults());
        serviceData.setKidsNumber(bookingDto.getChildren());
        serviceData.setRoomNumber(bookingDto.getRoomNumber());
        serviceData.setRoomCategory(bookingDto.getRoomCategory() != null ? bookingDto.getRoomCategory().getName() : "");
        product.setServiceDatas(Collections.singletonList(serviceData));

        return product;
    }

    private String buildXmlString(InvoiceXml invoiceXml) {
        try {
            JAXBContext context = JAXBContext.newInstance(InvoiceXml.class);
            StringWriter writer = new StringWriter();
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(invoiceXml, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException("Error generating XML", e);
        }
    }
}