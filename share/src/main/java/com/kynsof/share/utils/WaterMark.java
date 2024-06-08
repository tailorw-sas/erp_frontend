package com.kynsof.share.utils;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;

public class WaterMark {

    public static final String WATER_MARK = "BORRADOR";
    public static final float FONT_SIZE = 70;
    
    public static byte[] manipulatePdf(byte[] bytes) {
        try {
            PdfReader reader = new PdfReader(bytes);
            int n = reader.getNumberOfPages();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PdfStamper stamper = new PdfStamper(reader, bos);
            stamper.setRotateContents(false);
            // text watermark
            Font f = new Font(Font.FontFamily.HELVETICA, FONT_SIZE);
            Phrase p = new Phrase(WATER_MARK, f);
            // transparency
            PdfGState gs1 = new PdfGState();
            gs1.setFillOpacity(0.1f);
            // properties
            PdfContentByte over;
            Rectangle pagesize;
            float x, y;
            // loop over every page
            for (int i = 1; i <= n; i++) {
                pagesize = reader.getPageSize(i);
                x = (pagesize.getLeft() + pagesize.getRight()) / 2;
                y = (pagesize.getTop() + pagesize.getBottom()) / 2;
                over = stamper.getOverContent(i);
                over.saveState();
                over.setGState(gs1);
                ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, 45);
                over.restoreState();
            }
            stamper.close();
            reader.close();

            return bos.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}
