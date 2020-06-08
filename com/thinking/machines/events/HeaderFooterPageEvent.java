package com.thinking.machines.events;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class HeaderFooterPageEvent extends PdfPageEventHelper {
    public int x=0;
    public void onStartPage(PdfWriter writer,Document document)
     {
        x++;
    	Rectangle rect = writer.getBoxSize("art");
        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Services:-"), rect.getLeft(), rect.getTop(), 0);
        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("page:-"+x), rect.getRight(), rect.getTop(), 0);
        try{
        document.add(new Paragraph("          "));
   }catch(Exception e){e.printStackTrace();} }
    public void onEndPage(PdfWriter writer,Document document) {
    }
} 