package Models;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by jebes on 12/1/2017.
 */

/**
 * Class : PDG Header an extension class model that supplies Event Handler while constructing PDF Document
 */
public class PDFHeader extends PdfPageEventHelper {

    Font ffont = new Font(Font.FontFamily.UNDEFINED, 25, Font.ITALIC);

    /**
     * OnEndPage Event handler that will be executed while drawing the Header and Footer of th PDF Document
     * @param writer - PDF writer
     * @param document - Document object
     */
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        Phrase header = new Phrase("EventBook"+getCurrentTimeStamp(), ffont);
        Phrase footer = new Phrase("EventBook", ffont);
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                header,
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.top() + 10, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                footer,
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.bottom() - 10, 0);
    }

    /**
     * Method to return the Current TimeStamp for using as a File Name
     * @return String date that contains sequence number representing the date
     */
    private String getCurrentTimeStamp()
    {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
    }
}
