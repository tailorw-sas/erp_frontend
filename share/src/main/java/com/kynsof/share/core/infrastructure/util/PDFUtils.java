package com.kynsof.share.core.infrastructure.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PDFUtils {

public static ByteArrayOutputStream mergePDF(List<InputStream> pdfToMerge) throws DocumentException, IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Document document = new Document();
    PdfCopy copy = new PdfCopy(document, outputStream);
    document.open();
    PdfReader reader;
    int n;
    for (int i = 0; i < pdfToMerge.size(); i++) {
        reader = new PdfReader(pdfToMerge.get(i));
        n = reader.getNumberOfPages();
        for (int page = 0; page < n; ) {
            copy.addPage(copy.getImportedPage(reader, ++page));
        }
        copy.freeReader(reader);
        reader.close();
    }
    outputStream.flush();
    document.close();
    outputStream.close();
    return outputStream;
}
}
