package largescaleit_demo.repositories;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import largescaleit_demo.models.Cart;
import largescaleit_demo.models.CartItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;
@Service
public class PdfService {

    public byte[] createInvoicePdf(Cart cart, String name, String email, String address, String city, String zip) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(byteArrayOutputStream)) {
            // Initialize PDF document
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            
            //  logo
            // Add it center logo
//            String logoUrl = "https://i.postimg.cc/j2bh9GLg/temp-Image-Oc-Ph-PE.avif";  
//            ImageData logoData = ImageDataFactory.create(logoUrl);
//            Image logo = new Image(logoData);
//            logo.setFixedPosition(50, 750); 
//            logo.scaleToFit(100, 100); 
//            document.add(logo);

            
            document.add(new Paragraph("Invoice  | Abrechnung ©")
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold());
            //buyer informations
            document.add(new Paragraph("Vielen Dank für Ihre Bestellung." ));
            document.add(new Paragraph("  " ));
            document.add(new Paragraph("Name: " + name));
            document.add(new Paragraph("Email: " + email));
            document.add(new Paragraph("Address: " + address + ", " + city + " " + zip));

            // this code for creating a table with cart items
            float[] columnWidths = {3, 1, 1}; // Define column widths
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            //  table headers
            table.addHeaderCell(new Cell().add(new Paragraph("Product Name").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Quantity").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Price").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // table with cart items
            for (CartItem item : cart.getItems()) {
                table.addCell(new Cell().add(new Paragraph(item.getProduct().getNameD())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
                table.addCell(new Cell().add(new Paragraph("$" + item.getProduct().getPrice())));
            }

            // here the total price 
            table.addCell(new Cell(1, 2).add(new Paragraph("Total").setBold()).setTextAlignment(TextAlignment.RIGHT));
            double totalPrice = cart.getItems().stream()
                    .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                    .sum();
            table.addCell(new Cell().add(new Paragraph("$" + totalPrice)));

            
            document.add(table);

            // Close the document
            document.close();
        }

        // Return the PDF as a byte array
        return byteArrayOutputStream.toByteArray();
    }
}
