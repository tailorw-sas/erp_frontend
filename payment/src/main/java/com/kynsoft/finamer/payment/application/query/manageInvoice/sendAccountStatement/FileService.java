package com.kynsoft.finamer.payment.application.query.manageInvoice.sendAccountStatement;

import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
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

    public String convertExcelToBase64(List<UUID> invoiceIds) throws IOException {
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
        //Iniciar en la fila 12
        int rowNumber = 12;
        double totalBalance = 0.0;
        for (int i = 0; i < invoiceIds.size(); i++) {
            ManageInvoiceDto invoiceDto = this.invoiceService.findById(invoiceIds.get(i));
            XSSFRow row = sheet.createRow(rowNumber);

            addPaymentData(invoiceDto, row);
            rowNumber++;
        }

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

        dataRow.createCell(5).setCellValue(decimalFormat.format(0.0));

        dataRow.createCell(6).setCellValue(decimalFormat.format(0.0));

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
