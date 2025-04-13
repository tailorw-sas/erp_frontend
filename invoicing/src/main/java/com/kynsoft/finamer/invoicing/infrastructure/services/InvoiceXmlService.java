package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml.*;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml.Tax;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InvoiceXmlService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceXmlService.class);

    public String generateInvoiceXml(ManageInvoiceDto manageInvoiceDto) {
        InvoiceXml invoiceXml = mapToInvoiceXml(manageInvoiceDto);
        log.warn(invoiceXml.toString());
        return buildXmlString(invoiceXml);
    }

    private InvoiceXml mapToInvoiceXml(ManageInvoiceDto dto) {
        InvoiceXml invoiceXml = new InvoiceXml();
        ManageTradingCompaniesDto manageTradingCompaniesDto = dto.getHotel().getManageTradingCompanies();
        // General Data
        GeneralData generalData = new GeneralData();
        generalData.setRef(dto.getHotel().getPrefixToInvoice() + dto.getInvoiceNumberPrefix());
        generalData.setDate(dto.getInvoiceDate().toLocalDate());
        invoiceXml.setGeneralData(generalData);
        log.warn("Se genero la data base");
        // Supplier
        Supplier supplier = new Supplier();
        supplier.setAddress(manageTradingCompaniesDto.getAddress() != null ? manageTradingCompaniesDto.getAddress() : StringUtils.EMPTY);
        supplier.setCif(manageTradingCompaniesDto.getCif() != null ? manageTradingCompaniesDto.getCif() : StringUtils.EMPTY);
        supplier.setCity(manageTradingCompaniesDto.getCity() != null ? manageTradingCompaniesDto.getCity() : StringUtils.EMPTY);
        supplier.setCompany(manageTradingCompaniesDto.getCompany() != null ? manageTradingCompaniesDto.getCompany() : StringUtils.EMPTY);
        supplier.setCountry(manageTradingCompaniesDto.getCountry()!= null ? manageTradingCompaniesDto.getCountry().getIso3() : StringUtils.EMPTY);
        supplier.setCityState(manageTradingCompaniesDto.getCityState() != null ? manageTradingCompaniesDto.getCityState().getName() : StringUtils.EMPTY);
        supplier.setZipCode(manageTradingCompaniesDto.getZipCode() != null ? manageTradingCompaniesDto.getZipCode() : StringUtils.EMPTY);
        supplier.setCode(dto.getHotel().getBabelCode() != null ? dto.getHotel().getBabelCode() : StringUtils.EMPTY);
        invoiceXml.setSupplier(supplier);
        log.warn("Se genero la data supplier");

        // Client
        Client client = new Client();
        client.setAddress(dto.getAgency().getAddress() != null ? dto.getAgency().getAddress() : StringUtils.EMPTY);
        client.setCity(dto.getAgency().getCity() != null ? dto.getAgency().getCity() : StringUtils.EMPTY);
        client.setCompany(dto.getAgency().getName() != null ? dto.getAgency().getName() : StringUtils.EMPTY);
        client.setCountry(dto.getAgency().getCountry() != null ? dto.getAgency().getCountry().getIso3() : StringUtils.EMPTY);
        client.setCityState( dto.getAgency().getCityState() != null ? dto.getAgency().getCityState().getName() : StringUtils.EMPTY);
        client.setEmail(dto.getAgency().getMailingAddress() != null ? dto.getAgency().getMailingAddress() : StringUtils.EMPTY);
        client.setCode(dto.getAgency().getCode() != null ? dto.getAgency().getCode() : StringUtils.EMPTY);
        client.setZipCode(dto.getAgency().getZipCode() != null ? dto.getAgency().getZipCode() : StringUtils.EMPTY);
        invoiceXml.setClient(client);
        log.warn("Se genero la data de client");

        // Products
        List<Product> products = Optional.ofNullable(dto.getBookings())
                .orElse(Collections.emptyList())
                .stream()
                .flatMap(booking -> mapBookingToProduct(booking, Optional.ofNullable(dto.getHotel()).orElse(new ManageHotelDto())).stream())
                .collect(Collectors.toList());

        invoiceXml.setProductList(products);
        log.warn("Se genero la data de productos");

        // Total Summary
        TotalSummary totalSummary = new TotalSummary();
        totalSummary.setGrossAmount(BankerRounding.round(dto.getInvoiceAmount()));
        totalSummary.setSubTotal(BankerRounding.round(totalSummary.getGrossAmount() - totalSummary.getDiscounts()));
        totalSummary.setTotal(BankerRounding.round(totalSummary.getSubTotal() + totalSummary.getTax()));
        invoiceXml.setTotalSummary(totalSummary);
        log.warn("Se genero la data de summary");

        return invoiceXml;
    }

    private List<Product> mapBookingToProduct(ManageBookingDto bookingDto, ManageHotelDto hotelDto) {
        List<Product> products = new ArrayList<>();
        List<ManageRoomRateDto> roomRates = Optional.ofNullable(bookingDto.getRoomRates()).orElse(Collections.emptyList());

        for (ManageRoomRateDto roomRate : roomRates) {
            int days = (roomRate.getCheckIn() != null && roomRate.getCheckOut() != null)
                    ? (int) ChronoUnit.DAYS.between(roomRate.getCheckIn(), roomRate.getCheckOut())
                    : 1;
            String[] supplierSplit = Optional.ofNullable(bookingDto.getHotelBookingNumber())
                    .map(s -> s.split("\\s+"))
                    .orElse(new String[]{StringUtils.EMPTY});
            Product product = new Product();
            product.setItem(Optional.ofNullable(roomRate.getRemark()).orElse(StringUtils.EMPTY));
            product.setTotal(BankerRounding.round(roomRate.getInvoiceAmount()));
            product.setUp(BankerRounding.round(days > 0 ? roomRate.getInvoiceAmount() / days : roomRate.getInvoiceAmount()));
            product.setSupplierSku(supplierSplit.length > 1 ? supplierSplit[1] : supplierSplit[0]);
            product.setCustomerSku(Optional.ofNullable(bookingDto.getCouponNumber()).orElse(StringUtils.EMPTY));
            product.setQty(days);

            // Taxes
            product.setTaxes(new ArrayList<>(List.of(new Tax())));

            // Services Data
            ServiceData serviceData = new ServiceData();
            serviceData.setAdultsNumber(roomRate.getAdults());
            serviceData.setKidsNumber(roomRate.getChildren());
            serviceData.setSupplierClientId(Optional.ofNullable(bookingDto.getHotelBookingNumber()).orElse(StringUtils.EMPTY));
            serviceData.setSupplierId(Optional.ofNullable(hotelDto.getBabelCode()).orElse(StringUtils.EMPTY));
            serviceData.setSupplierName(hotelDto.getName());
            serviceData.setPax(
                    (bookingDto.getLastName() != null ? bookingDto.getLastName() : "") + "," +
                    (bookingDto.getFirstName() != null ? bookingDto.getFirstName() : "")
            );
            serviceData.setPaxNumber(roomRate.getAdults() + roomRate.getChildren());
            serviceData.setBeginDate(roomRate.getCheckIn() != null ? roomRate.getCheckIn().toLocalDate() : null);
            serviceData.setEndDate(roomRate.getCheckOut() != null ? roomRate.getCheckOut().toLocalDate() : null);
            serviceData.setRoomNumber(roomRate.getRoomNumber());
            product.setServiceDatas(Collections.singletonList(serviceData));
            products.add(product);
        }

        return products;
    }

    private String buildXmlString(InvoiceXml invoiceXml) {
        try {
            JAXBContext context = JAXBContext.newInstance(InvoiceXml.class);
            StringWriter writer = new StringWriter();
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
            marshaller.marshal(invoiceXml, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException("Error generating XML", e);
        }
    }
}