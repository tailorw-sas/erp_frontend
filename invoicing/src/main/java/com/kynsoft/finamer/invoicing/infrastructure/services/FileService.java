package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsoft.finamer.invoicing.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    private final IManageInvoiceService invoiceService;

    public FileService(IManageInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public String convertExcelToBase64(List<UUID> invoiceIds, ManageEmployeeDto employeeDto) throws IOException {
        InputStream inputStream = null;

        try {
            // Intentar cargar el archivo desde el classpath
            ClassPathResource classPathResource = new ClassPathResource("EstadoCuenta.xlsx");
            inputStream = classPathResource.getInputStream();
            System.out.println("Archivo cargado desde el classpath.");
        } catch (IOException e) {
            // Si no se encuentra en el classpath, cargar desde el sistema de archivos (Docker)
            System.out.println("Archivo no encontrado en el classpath, intentando cargar desde el sistema de archivos.");
            inputStream = new FileInputStream("/app/resources/EstadoCuenta.xlsx");
        }

        //Crea un excel con el archivo que se lee
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        // Obtiene la primera hoja
        XSSFSheet sheet = workbook.getSheetAt(0);

        //Recorre desde la 12 hasta donde este la lista de invoices
        String hotelName = "";
        String agencyName = "";
        int rowNumber = 12;
        double totalBalance = 0.0;
        for (int i = 0; i < invoiceIds.size(); i++) {
            ManageInvoiceDto invoiceDto = this.invoiceService.findById(invoiceIds.get(i));
            hotelName = invoiceDto.getHotel().getName();
            agencyName = invoiceDto.getAgency().getName();
            XSSFRow row = sheet.createRow(rowNumber);

            addPaymentData(invoiceDto, row);
            totalBalance = totalBalance + invoiceDto.getDueAmount();
            rowNumber++;
        }

        //Esto viene de la invoice
        XSSFRow rowHotel = sheet.getRow(7);
        rowHotel.getCell(1).setCellValue(hotelName);

        //Esto viene de la invoice
        XSSFRow rowAgency = sheet.getRow(8);
        rowAgency.getCell(1).setCellValue(agencyName);

        //Esto viene del Employee
        XSSFRow rowContactTel = sheet.getRow(9);
        rowContactTel.getCell(1).setCellValue(employeeDto.getPhoneExtension());
        rowContactTel.getCell(4).setCellValue("Email: " + employeeDto.getEmail());

        //Esto viene del Employee
        XSSFRow rowFax = sheet.getRow(10);
        rowFax.getCell(1).setCellValue("N/A");
        rowFax.getCell(4).setCellValue("Contact Name: " + employeeDto.getFirstName() + " " + employeeDto.getLastName());

        XSSFRow row = sheet.createRow(rowNumber++);
        addBalance(totalBalance, row);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        workbook.write(outputStream);

        return Base64.getEncoder()
                .encodeToString(outputStream.toByteArray());
    }

    /**
     * Adicionando la informacion de las facturas.
     * @param entity
     * @param dataRow 
     */
    private void addPaymentData(ManageInvoiceDto entity, XSSFRow dataRow) {

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        dataRow.createCell(0).setCellValue(entity.getInvoiceDate().toLocalDate().toString());

        dataRow.createCell(1).setCellValue(entity.getInvoiceNumber());

        dataRow.createCell(2).setCellValue(entity.getBookings().get(0).getFullName());

        dataRow.createCell(3).setCellValue("Booking #");

        dataRow.createCell(4).setCellValue(decimalFormat.format(entity.getInvoiceAmount()));

        dataRow.createCell(5).setCellValue(decimalFormat.format(entity.getInvoiceAmount() - entity.getDueAmount()));

        dataRow.createCell(6).setCellValue(decimalFormat.format(entity.getDueAmount()));

        dataRow.createCell(7).setCellValue("");

    }

    /**
     * Adicionando el balance.
     * @param entity
     * @param dataRow 
     */
    private void addBalance(Double balance, XSSFRow dataRow) {

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        dataRow.createCell(5).setCellValue("Balance");

        dataRow.createCell(6).setCellValue(decimalFormat.format(balance));

    }
}
