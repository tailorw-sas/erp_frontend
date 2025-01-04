package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml.*;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceXmlService {

    public String generateInvoiceXml(ManageInvoiceDto manageInvoiceDto) {
        // Mapea los datos a la entidad InvoiceXml
        InvoiceXml invoiceXml = mapToInvoiceXml(manageInvoiceDto);
        // Construye el XML manualmente
        return buildXmlString(invoiceXml);
    }

    private InvoiceXml mapToInvoiceXml(ManageInvoiceDto dto) {
        InvoiceXml invoiceXml = new InvoiceXml();

        // Mapear los datos generales
        GeneralData generalData = new GeneralData();
        generalData.setRef(dto.getInvoiceNumber());
        generalData.setType(dto.getInvoiceType().name());
        generalData.setDate(dto.getInvoiceDate().toLocalDate().format(DateTimeFormatter.ISO_DATE));
        generalData.setLanguage("es-ES");
        generalData.setCurrency("USD");
        generalData.setTaxIncluded(true);
        generalData.setTaxSystemText("IVA General");
        generalData.setOnlyArchive(false);
        generalData.setPrintAsCopy(false);

        // Mapear los datos del proveedor
        Supplier supplier = new Supplier();
        supplier.setCompany(dto.getHotel().getName());
        supplier.setCIF(dto.getHotel().getBabelCode());
        supplier.setAddress(dto.getHotel().getAddress());
        supplier.setCity(dto.getHotel().getManageCityState().getName() != null ? dto.getHotel().getManageCityState().getName() : "");
        supplier.setProvince(dto.getHotel().getManageCityState().getName() != null ? dto.getHotel().getManageCityState().getName() : "");
        supplier.setCountry(dto.getHotel().getManageCountry() != null ? dto.getHotel().getManageCountry().getName() : "");
        supplier.setEmail(dto.getHotel().getManageTradingCompanies().getAddress() != null ? dto.getHotel().getManageTradingCompanies().getAddress() : "");

        // Mapear los datos del cliente
        Client client = new Client();
        client.setCompany(dto.getAgency().getName());
        client.setCIF(dto.getAgency().getCif());
        client.setAddress(dto.getAgency().getAddress());
        client.setCity(dto.getAgency().getCityState() != null ? dto.getAgency().getCityState().getName() : "");
        client.setPostalCode(dto.getAgency().getZipCode() != null ? dto.getAgency().getZipCode() : "");
        client.setProvince(dto.getAgency().getCityState().getName() != null ? dto.getAgency().getCityState().getName() : "");
        client.setCountry(dto.getAgency().getCountry() != null ? dto.getAgency().getCountry().getCode() : "");
        client.setEmail(dto.getAgency().getMailingAddress());
        client.setSupplierClientID(dto.getAgency().getCode());

        // Mapear la lista de productos
        List<Product> products = dto.getBookings().stream().map(this::mapBookingToProduct).collect(Collectors.toList());

        // Mapear el resumen total
        TotalSummary totalSummary = new TotalSummary();
        totalSummary.setGrossAmount(dto.getInvoiceAmount());
        totalSummary.setDiscounts(0.0);
        totalSummary.setSubTotal(dto.getInvoiceAmount());
        totalSummary.setTotal(dto.getInvoiceAmount());

        // Asignar los datos mapeados a la entidad principal
        invoiceXml.setGeneralData(generalData);
        invoiceXml.setSupplier(supplier);
        invoiceXml.setClient(client);
        invoiceXml.setProductList(products);
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
        product.setNetAmount(bookingDto.getInvoiceAmount() - (bookingDto.getDueAmount() != null ? bookingDto.getDueAmount() : 0.0));
        product.setServiceStartDate(bookingDto.getCheckIn().format(DateTimeFormatter.ISO_DATE));
        product.setServiceEndDate(bookingDto.getCheckOut().format(DateTimeFormatter.ISO_DATE));
        product.setComment("Reserva desde " + bookingDto.getCheckIn().format(DateTimeFormatter.ISO_DATE) +
                " hasta " + bookingDto.getCheckOut().format(DateTimeFormatter.ISO_DATE));
        product.setAdultsNumber(bookingDto.getAdults());
        product.setChildrenNumber(bookingDto.getChildren());
        product.setRoomType(bookingDto.getRoomType() != null ? bookingDto.getRoomType().getName() : "");
        product.setRatePlan(bookingDto.getRatePlan() != null ? bookingDto.getRatePlan().getName() : "");
        product.setRoomCategory(bookingDto.getRoomCategory() != null ? bookingDto.getRoomCategory().getName() : "");
        return product;
    }

    private String buildXmlString(InvoiceXml invoiceXml) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<Transaction>");

        // General Data
        xmlBuilder.append("<GeneralData>");
        xmlBuilder.append("<Ref>").append(invoiceXml.getGeneralData().getRef()).append("</Ref>");
        xmlBuilder.append("<Type>").append(invoiceXml.getGeneralData().getType()).append("</Type>");
        xmlBuilder.append("<Date>").append(invoiceXml.getGeneralData().getDate()).append("</Date>");
        xmlBuilder.append("<Language>").append(invoiceXml.getGeneralData().getLanguage()).append("</Language>");
        xmlBuilder.append("<Currency>").append(invoiceXml.getGeneralData().getCurrency()).append("</Currency>");
    //    xmlBuilder.append("<TaxIncluded>").append(invoiceXml.getGeneralData().isTaxIncluded()).append("</TaxIncluded>");
        xmlBuilder.append("<TaxSystemText>").append(invoiceXml.getGeneralData().getTaxSystemText()).append("</TaxSystemText>");
      //  xmlBuilder.append("<OnlyArchive>").append(invoiceXml.getGeneralData().isOnlyArchive()).append("</OnlyArchive>");
      //  xmlBuilder.append("<PrintAsCopy>").append(invoiceXml.getGeneralData().isPrintAsCopy()).append("</PrintAsCopy>");
        xmlBuilder.append("</GeneralData>");

        // Supplier
        xmlBuilder.append("<Supplier>");
        xmlBuilder.append("<Company>").append(invoiceXml.getSupplier().getCompany()).append("</Company>");
        xmlBuilder.append("<CIF>").append(invoiceXml.getSupplier().getCIF()).append("</CIF>");
        xmlBuilder.append("<Address>").append(invoiceXml.getSupplier().getAddress()).append("</Address>");
        xmlBuilder.append("<City>").append(invoiceXml.getSupplier().getCity()).append("</City>");
        xmlBuilder.append("<Province>").append(invoiceXml.getSupplier().getProvince()).append("</Province>");
        xmlBuilder.append("<Country>").append(invoiceXml.getSupplier().getCountry()).append("</Country>");
        xmlBuilder.append("<Email>").append(invoiceXml.getSupplier().getEmail()).append("</Email>");
        xmlBuilder.append("</Supplier>");

        // Client
        xmlBuilder.append("<Client>");
        xmlBuilder.append("<Company>").append(invoiceXml.getClient().getCompany()).append("</Company>");
        xmlBuilder.append("<CIF>").append(invoiceXml.getClient().getCIF()).append("</CIF>");
        xmlBuilder.append("<Address>").append(invoiceXml.getClient().getAddress()).append("</Address>");
        xmlBuilder.append("<City>").append(invoiceXml.getClient().getCity()).append("</City>");
        xmlBuilder.append("<PostalCode>").append(invoiceXml.getClient().getPostalCode()).append("</PostalCode>");
        xmlBuilder.append("<Province>").append(invoiceXml.getClient().getProvince()).append("</Province>");
        xmlBuilder.append("<Country>").append(invoiceXml.getClient().getCountry()).append("</Country>");
        xmlBuilder.append("<Email>").append(invoiceXml.getClient().getEmail()).append("</Email>");
        xmlBuilder.append("<SupplierClientID>").append(invoiceXml.getClient().getSupplierClientID()).append("</SupplierClientID>");
        xmlBuilder.append("</Client>");

        // Product List
        xmlBuilder.append("<ProductList>");
        for (Product product : invoiceXml.getProductList()) {
            xmlBuilder.append("<Product>");
            xmlBuilder.append("<SupplierSKU>").append(product.getSupplierSKU()).append("</SupplierSKU>");
            xmlBuilder.append("<CustomerSKU>").append(product.getCustomerSKU()).append("</CustomerSKU>");
            xmlBuilder.append("<Item>").append(product.getItem()).append("</Item>");
            xmlBuilder.append("<Qty>").append(product.getQty()).append("</Qty>");
            xmlBuilder.append("<MU>").append(product.getMU()).append("</MU>");
            xmlBuilder.append("<UP>").append(product.getUP()).append("</UP>");
            xmlBuilder.append("<Total>").append(product.getTotal()).append("</Total>");
            xmlBuilder.append("<NetAmount>").append(product.getNetAmount()).append("</NetAmount>");
            xmlBuilder.append("<ServiceStartDate>").append(product.getServiceStartDate()).append("</ServiceStartDate>");
            xmlBuilder.append("<ServiceEndDate>").append(product.getServiceEndDate()).append("</ServiceEndDate>");
            xmlBuilder.append("<Comment>").append(product.getComment()).append("</Comment>");
            xmlBuilder.append("<AdultsNumber>").append(product.getAdultsNumber()).append("</AdultsNumber>");
            xmlBuilder.append("<ChildrenNumber>").append(product.getChildrenNumber()).append("</ChildrenNumber>");
            xmlBuilder.append("<RoomType>").append(product.getRoomType()).append("</RoomType>");
            xmlBuilder.append("<RatePlan>").append(product.getRatePlan()).append("</RatePlan>");
            xmlBuilder.append("<RoomCategory>").append(product.getRoomCategory()).append("</RoomCategory>");
            xmlBuilder.append("</Product>");
        }
        xmlBuilder.append("</ProductList>");

        // Total Summary
        xmlBuilder.append("<TotalSummary>");
        xmlBuilder.append("<GrossAmount>").append(invoiceXml.getTotalSummary().getGrossAmount()).append("</GrossAmount>");
        xmlBuilder.append("<Discounts>").append(invoiceXml.getTotalSummary().getDiscounts()).append("</Discounts>");
        xmlBuilder.append("<SubTotal>").append(invoiceXml.getTotalSummary().getSubTotal()).append("</SubTotal>");
        xmlBuilder.append("<Total>").append(invoiceXml.getTotalSummary().getTotal()).append("</Total>");
        xmlBuilder.append("</TotalSummary>");

        xmlBuilder.append("</Transaction>");
        return xmlBuilder.toString();
    }
}