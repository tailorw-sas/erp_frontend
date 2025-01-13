package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml.*;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml.Tax;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
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

        // General Data
        GeneralData generalData = new GeneralData();
        generalData.setRef(dto.getInvoiceNumber());
        generalData.setType(dto.getInvoiceType().name());
        generalData.setDate(dto.getInvoiceDate().toLocalDate().format(DateTimeFormatter.ISO_DATE));
        generalData.setCurrency("USD");
        generalData.setTaxIncluded(true);
        generalData.setStatus(""); // Default status
        invoiceXml.setGeneralData(generalData);

        // Supplier
        Supplier supplier = new Supplier();
        supplier.setSupplierID("SCR");
        supplier.setCIF(dto.getHotel().getBabelCode());
        supplier.setCompany(dto.getHotel().getName());
        supplier.setAddress(dto.getHotel().getAddress());
        supplier.setCity(dto.getHotel().getManageCityState().getName());
        supplier.setPc("N/A");
        supplier.setProvince(dto.getHotel().getManageCityState().getName());
        supplier.setCountry(dto.getHotel().getManageCountry().getCode());
        invoiceXml.setSupplier(supplier);

        // Client
        Client client = new Client();
        client.setSupplierClientID(dto.getAgency().getCode());
        client.setCIF(dto.getAgency().getCif());
        client.setCompany(dto.getAgency().getName());
        client.setAddress(dto.getAgency().getAddress());
        client.setCity(dto.getAgency().getCityState().getName());
        client.setPostalCode(dto.getAgency().getZipCode());
        client.setProvince(dto.getAgency().getCityState().getName());
        client.setCountry(dto.getAgency().getCountry().getCode());
        client.setEmail(dto.getAgency().getMailingAddress());
        invoiceXml.setClient(client);

        // Products
        List<Product> products = dto.getBookings().stream()
                .map(this::mapBookingToProduct)
                .collect(Collectors.toList());
        invoiceXml.setProductList(products);

        // Total Summary
        TotalSummary totalSummary = new TotalSummary();
        totalSummary.setGrossAmount(dto.getInvoiceAmount());
        totalSummary.setDiscounts(0.0);
        totalSummary.setSubTotal(dto.getInvoiceAmount());
        totalSummary.setTotal(dto.getInvoiceAmount());
        invoiceXml.setTotalSummary(totalSummary);

        return invoiceXml;
    }

    private Product mapBookingToProduct(ManageBookingDto bookingDto) {
        Product product = new Product();
        product.setSupplierSKU(bookingDto.getHotelBookingNumber());
        product.setCustomerSKU(String.valueOf(bookingDto.getReservationNumber()));
        product.setItem(bookingDto.getDescription() != null ? bookingDto.getDescription() : "Servicio de alojamiento");
        product.setQty(1.0);
        product.setMU("Unidades");
        product.setUP(bookingDto.getInvoiceAmount());
        product.setTotal(bookingDto.getInvoiceAmount());
        product.setComment("Reserva desde " + bookingDto.getCheckIn().format(DateTimeFormatter.ISO_DATE) +
                " hasta " + bookingDto.getCheckOut().format(DateTimeFormatter.ISO_DATE));

        // Taxes
        Tax tax = new Tax("EXENTO", 0, 0, "");
        product.setTaxes(List.of(tax));

        // Services Data
        ServiceData serviceData = new ServiceData();
        serviceData.setSupplierClientID("I " + bookingDto.getHotelBookingNumber());
        serviceData.setSupplierID("SCR");
        serviceData.setSupplierName("Sunscape Curacao Resort, Spa & Casino");
        serviceData.setPax(bookingDto.getFullName());
        serviceData.setBeginDate(bookingDto.getCheckIn().format(DateTimeFormatter.ISO_DATE));
        serviceData.setEndDate(bookingDto.getCheckOut().format(DateTimeFormatter.ISO_DATE));
        serviceData.setPaxNumber(bookingDto.getAdults() + bookingDto.getChildren());
        serviceData.setAdultsNumber(bookingDto.getAdults());
        serviceData.setKidsNumber(bookingDto.getChildren());
        serviceData.setRoomNumber(bookingDto.getRoomNumber());
        serviceData.setRoomCategory(bookingDto.getRoomCategory() != null ? bookingDto.getRoomCategory().getName() : "");
        product.setServicesData(List.of(serviceData));

        return product;
    }

    private String buildXmlString(InvoiceXml invoiceXml) {
        StringBuilder xmlBuilder = new StringBuilder();
        // Agregar cabecera XML
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        xmlBuilder.append("<Transaction xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
        xmlBuilder.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");

        // General Data
        GeneralData generalData = invoiceXml.getGeneralData();
        xmlBuilder.append("<GeneralData ");
        xmlBuilder.append("Ref=\"").append(generalData.getRef()).append("\" ");
        xmlBuilder.append("Type=\"").append(generalData.getType()).append("\" ");
        xmlBuilder.append("Date=\"").append(generalData.getDate()).append("\" ");
        xmlBuilder.append("Currency=\"").append(generalData.getCurrency()).append("\" ");
        xmlBuilder.append("TaxIncluded=\"").append(generalData.isTaxIncluded()).append("\" ");
        xmlBuilder.append("Status=\"").append(generalData.getStatus()).append("\" ");
        xmlBuilder.append("/>");

        // Supplier
        Supplier supplier = invoiceXml.getSupplier();
        xmlBuilder.append("<Supplier ");
        xmlBuilder.append("SupplierID=\"").append(supplier.getSupplierID()).append("\" ");
        xmlBuilder.append("CIF=\"").append(supplier.getCIF()).append("\" ");
        xmlBuilder.append("Company=\"").append(supplier.getCompany()).append("\" ");
        xmlBuilder.append("Address=\"").append(supplier.getAddress()).append("\" ");
        xmlBuilder.append("City=\"").append(supplier.getCity()).append("\" ");
        xmlBuilder.append("PC=\"").append(supplier.getPc()).append("\" ");
        xmlBuilder.append("Province=\"").append(supplier.getProvince()).append("\" ");
        xmlBuilder.append("Country=\"").append(supplier.getCountry()).append("\" ");
        xmlBuilder.append("/>");

        // Client
        Client client = invoiceXml.getClient();
        xmlBuilder.append("<Client ");
        xmlBuilder.append("SupplierClientID=\"").append(client.getSupplierClientID()).append("\" ");
        xmlBuilder.append("CIF=\"").append(client.getCIF()).append("\" ");
        xmlBuilder.append("Company=\"").append(client.getCompany()).append("\" ");
        xmlBuilder.append("Address=\"").append(client.getAddress()).append("\" ");
        xmlBuilder.append("City=\"").append(client.getCity()).append("\" ");
        xmlBuilder.append("PostalCode=\"").append(client.getPostalCode()).append("\" ");
        xmlBuilder.append("Province=\"").append(client.getProvince()).append("\" ");
        xmlBuilder.append("Country=\"").append(client.getCountry()).append("\" ");
        xmlBuilder.append("Email=\"").append(client.getEmail()).append("\" ");
        xmlBuilder.append("/>");

        // Product List
        xmlBuilder.append("<ProductList>");
        for (Product product : invoiceXml.getProductList()) {
            xmlBuilder.append("<Product ");
            xmlBuilder.append("SupplierSKU=\"").append(product.getSupplierSKU()).append("\" ");
            xmlBuilder.append("CustomerSKU=\"").append(product.getCustomerSKU()).append("\" ");
            xmlBuilder.append("Item=\"").append(product.getItem()).append("\" ");
            xmlBuilder.append("Qty=\"").append(product.getQty()).append("\" ");
            xmlBuilder.append("MU=\"").append(product.getMU()).append("\" ");
            xmlBuilder.append("UP=\"").append(product.getUP()).append("\" ");
            xmlBuilder.append("Total=\"").append(product.getTotal()).append("\" ");
            xmlBuilder.append("Comment=\"").append(product.getComment()).append("\" >");

            // Taxes
            xmlBuilder.append("<Taxes>");
            for (Tax tax : product.getTaxes()) {
                xmlBuilder.append("<Tax ");
                xmlBuilder.append("Type=\"").append(tax.getType()).append("\" ");
                xmlBuilder.append("Rate=\"").append(tax.getRate()).append("\" ");
                xmlBuilder.append("Amount=\"").append(tax.getAmount()).append("\" ");
                xmlBuilder.append("Description=\"").append(tax.getDescription()).append("\" ");
                xmlBuilder.append("/>");
            }
            xmlBuilder.append("</Taxes>");

            // Services Data
            xmlBuilder.append("<ServicesData>");
            for (ServiceData service : product.getServicesData()) {
                xmlBuilder.append("<ServiceData ");
                xmlBuilder.append("SupplierClientID=\"").append(service.getSupplierClientID()).append("\" ");
                xmlBuilder.append("SupplierID=\"").append(service.getSupplierID()).append("\" ");
                xmlBuilder.append("SupplierName=\"").append(service.getSupplierName()).append("\" ");
                xmlBuilder.append("Pax=\"").append(service.getPax()).append("\" ");
                xmlBuilder.append("BeginDate=\"").append(service.getBeginDate()).append("\" ");
                xmlBuilder.append("EndDate=\"").append(service.getEndDate()).append("\" ");
                xmlBuilder.append("PaxNumber=\"").append(service.getPaxNumber()).append("\" ");
                xmlBuilder.append("AdultsNumber=\"").append(service.getAdultsNumber()).append("\" ");
                xmlBuilder.append("KidsNumber=\"").append(service.getKidsNumber()).append("\" ");
                xmlBuilder.append("RoomNumber=\"").append(service.getRoomNumber()).append("\" ");
                xmlBuilder.append("RoomCategory=\"").append(service.getRoomCategory()).append("\" ");
                xmlBuilder.append("/>");
            }
            xmlBuilder.append("</ServicesData>");

            xmlBuilder.append("</Product>");
        }
        xmlBuilder.append("</ProductList>");

        // Total Summary
        TotalSummary totalSummary = invoiceXml.getTotalSummary();
        xmlBuilder.append("<TotalSummary ");
        xmlBuilder.append("GrossAmount=\"").append(totalSummary.getGrossAmount()).append("\" ");
        xmlBuilder.append("Discounts=\"").append(totalSummary.getDiscounts()).append("\" ");
        xmlBuilder.append("SubTotal=\"").append(totalSummary.getSubTotal()).append("\" ");
        xmlBuilder.append("Tax=\"0\" ");
        xmlBuilder.append("Total=\"").append(totalSummary.getTotal()).append("\" ");
        xmlBuilder.append("/>");

        xmlBuilder.append("</Transaction>");
        return xmlBuilder.toString();
    }
}