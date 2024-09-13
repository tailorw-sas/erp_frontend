package com.kynsoft.finamer.invoicing.infrastructure.services;


import com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml.*;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;


import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;

@Service
public class InvoiceXmlService {

    public String generateInvoiceXml(ManageInvoiceDto manageInvoiceDto) throws JAXBException {
        // Mapear ManageInvoiceDto a la clase XML InvoiceXml
        InvoiceXml invoiceXml = mapToInvoiceXml(manageInvoiceDto);

        // Crear el contexto JAXB y el marshaller
        JAXBContext jaxbContext = JAXBContext.newInstance(InvoiceXml.class);
        Marshaller marshaller = jaxbContext.createMarshaller();

        // Formatear el XML para que sea más legible
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        // Convertir el objeto InvoiceXml a XML
        StringWriter xmlWriter = new StringWriter();
        marshaller.marshal(invoiceXml, xmlWriter);

        return xmlWriter.toString();
    }

    private InvoiceXml mapToInvoiceXml(ManageInvoiceDto dto) {
        InvoiceXml invoiceXml = new InvoiceXml();

        // Mapear GeneralData
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

        // Mapear Supplier
        Supplier supplier = new Supplier();
        supplier.setCompany(dto.getHotel().getName());
        supplier.setCIF(dto.getHotel().getCode());//BavelCode
//        supplier.setAddress(dto.getHotel().getAddress());
//        supplier.setCity(dto.getHotel().getCityState() != null ? dto.getHotel().getCityState().getName() : "");
//        supplier.setPostalCode(dto.getHotel().getPostalCode());
//        supplier.setProvince(dto.getHotel().getProvince());
//        supplier.setCountry(dto.getHotel().getCountry() != null ? dto.getHotel().getCountry().getCode() : "");
//        supplier.setEmail(dto.getHotel().getManageTradingCompanies().getEmail());

        // Mapear Client
        Client client = new Client();
        client.setCompany(dto.getAgency().getName());
        client.setCIF(dto.getAgency().getCif());
        client.setAddress(dto.getAgency().getAddress());
        client.setCity(dto.getAgency().getCityState() != null ? dto.getAgency().getCityState().getName() : "");
//        client.setPostalCode(dto.getAgency().getPostalCode());
//        client.setProvince(dto.getAgency().getProvince());
        client.setCountry(dto.getAgency().getCountry() != null ? dto.getAgency().getCountry().getCode() : "");
        client.setEmail(dto.getAgency().getMailingAddress());
        client.setSupplierClientID(dto.getAgency().getCode());

        // Mapear ProductList desde Bookings
        List<Product> products = dto.getBookings().stream().map(this::mapBookingToProduct).collect(Collectors.toList());

        // Mapear TaxSummary
        TaxSummary taxSummary = new TaxSummary();
        taxSummary.setType("IVA");
        taxSummary.setRate(21.0); // Ajusta la tasa de IVA según sea necesario
        taxSummary.setBase(dto.getInvoiceAmount());
        taxSummary.setAmount(dto.getInvoiceAmount() * 0.21);

        // Mapear TotalSummary
        TotalSummary totalSummary = new TotalSummary();
        totalSummary.setGrossAmount(dto.getInvoiceAmount());
        totalSummary.setDiscounts(0.0); // Ajustar si hay descuentos
        totalSummary.setSubTotal(dto.getInvoiceAmount());
        totalSummary.setTax(taxSummary.getAmount());
        totalSummary.setTotal(dto.getInvoiceAmount() + taxSummary.getAmount());

        // Asignar los datos mapeados a la clase principal InvoiceXml
        invoiceXml.setGeneralData(generalData);
        invoiceXml.setSupplier(supplier);
        invoiceXml.setClient(client);
        invoiceXml.setProductList(products);
        invoiceXml.setTaxSummary(taxSummary);
        invoiceXml.setTotalSummary(totalSummary);

        return invoiceXml;
    }

    private Product mapBookingToProduct(ManageBookingDto bookingDto) {
        Product product = new Product();

        // Mapeo de identificadores y descripciones
        product.setSupplierSKU(bookingDto.getHotelBookingNumber()); // Código del proveedor
        product.setCustomerSKU(String.valueOf(bookingDto.getReservationNumber())); // Número de referencia del cliente
        product.setItem(bookingDto.getDescription() != null ? bookingDto.getDescription() : "Servicio de alojamiento");

        // Mapeo de cantidades y precios
        product.setQty(1.0); // Consideramos 1 unidad por servicio de reserva
        product.setMU("Unidades"); // Unidad de medida
        product.setUP(bookingDto.getInvoiceAmount()); // Precio unitario bruto
        product.setTotal(bookingDto.getInvoiceAmount()); // Importe bruto total
        product.setNetAmount(bookingDto.getInvoiceAmount() - (bookingDto.getDueAmount() != null ? bookingDto.getDueAmount() : 0.0)); // Importe neto

//        // Mapeo de descuentos y cargos
//        product.setDiscount(bookingDto.getCouponNumber() != null ? bookingDto.getRateAdult() * 0.1 : 0.0); // Ejemplo de descuento si aplica un cupón
//        product.setCharge(0.0); // Cargos adicionales, si hay alguno
//
//        // Mapeo de impuestos (ejemplo de cálculo)
//        product.setTaxAmount(product.getNetAmount() * 0.21); // Aplicar un 21% de impuestos

        // Mapeo de fechas del servicio
        product.setServiceStartDate(bookingDto.getCheckIn().format(DateTimeFormatter.ISO_DATE)); // Fecha de inicio del servicio
        product.setServiceEndDate(bookingDto.getCheckOut().format(DateTimeFormatter.ISO_DATE)); // Fecha de fin del servicio

        // Mapeo de información adicional y comentarios
        product.setComment("Reserva desde " + bookingDto.getCheckIn().format(DateTimeFormatter.ISO_DATE) +
                " hasta " + bookingDto.getCheckOut().format(DateTimeFormatter.ISO_DATE));

        // Mapeo de datos específicos del alojamiento
        product.setAdultsNumber(bookingDto.getAdults()); // Número de adultos
        product.setChildrenNumber(bookingDto.getChildren()); // Número de niños
        product.setRoomType(bookingDto.getRoomType() != null ? bookingDto.getRoomType().getName() : ""); // Tipo de habitación
        product.setRatePlan(bookingDto.getRatePlan() != null ? bookingDto.getRatePlan().getName() : ""); // Plan de tarifa
        product.setRoomCategory(bookingDto.getRoomCategory() != null ? bookingDto.getRoomCategory().getName() : ""); // Categoría de habitación

        return product;
    }

}