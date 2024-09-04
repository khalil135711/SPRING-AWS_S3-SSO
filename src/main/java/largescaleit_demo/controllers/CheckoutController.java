package largescaleit_demo.controllers;

import largescaleit_demo.models.Cart;
import largescaleit_demo.models.Order;
import largescaleit_demo.repositories.PdfService;
import largescaleit_demo.repositories.S3ProRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.io.source.ByteArrayOutputStream;
import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

    @Autowired
    private S3ProRepository s3ProRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PdfService pdfService;

    @GetMapping("/checkout")
    public String checkoutForm(Model model, HttpSession session) {
        String name = (String) session.getAttribute("userName");
        String email = (String) session.getAttribute("userEmail");
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        return "checkout";
    }

    @PostMapping("/process-checkout")
    public String processCheckout(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("address") String address,
            @RequestParam("city") String city,
            @RequestParam("zip") String zip,
            HttpSession session,
            Model model) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart(); 
        }

        Order order = new Order();
        order.setName(name);
        order.setEmail(email);
        order.setAddress(address);
        order.setCity(city);
        order.setZip(zip);
        order.setCart(cart);
       
        String orderId = UUID.randomUUID().toString();
        String orderKey = "orders/" + orderId + ".json";
        System.out.println("is the last id :" +orderId);
        String orderid = (String) session.getAttribute("orderId");
        try {
            ByteArrayOutputStream orderStream = new ByteArrayOutputStream();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(orderStream, order);
            InputStream orderInputStream = new ByteArrayInputStream(orderStream.toByteArray());
            s3ProRepository.uploadOrderToS3(orderInputStream, orderKey);
            byte[] pdfBytes = pdfService.createInvoicePdf(cart, name, email, address, city, zip);
            model.addAttribute("email", email);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("🍾 MAHL ZEIT " + name + ": Your Invoice / Abrechnung");

            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333; padding: 20px; background-color: #f4f4f4;'>" +
                    "<table width='100%' border='0' cellspacing='0' cellpadding='0' style='background-image: url(\"https://media.gq-magazin.de/photos/5c9cdaf1ffff02d4ce985141/16:9/w_1280,c_limit/_drinks_quer.jpg\"); background-size: cover; background-repeat: no-repeat;'>" +
                    "<tr>" +
                    "<td align='center'>" +
                    "<div style='max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);'>" +
                    "<div style='text-align: center; margin-bottom: 20px;'>" +
                    "<img src='https://i.postimg.cc/j2bh9GLg/temp-Image-Oc-Ph-PE.avif' alt='OnlineDrink Shop' style='max-width: 100%; height: auto;'>" +
                    "</div>" +
                    "<h1 style='color: #007bff; text-align: center;'>Vielen Dank für Ihren Einkauf!</h1>" +
                    "<p>Sehr geehrte/r " + name + ",</p>" +
                    "<p>Wir schätzen Ihr Vertrauen in unseren OnlineDrink Shop und freuen uns, dass Sie bei uns eingekauft haben.</p>" +
                    "<p>Ihre Rechnung finden Sie im Anhang.</p>" +
                    "<p>Lieferung Adresse: " + address + ", " + city + ", " + zip + "</p>" +
                    "<p>Falls Sie Fragen haben, steht Ihnen unser Support-Team jederzeit zur Verfügung.</p>" +
                    "<br>" +
                    "<p>Mit freundlichen Grüßen,<br>Ihr OnlineDrink Shop Team</p>" +
                    "<div style='margin-top: 40px;'>" +
                    "<h2 style='color: #007bff; text-align: center;'>Empfohlene Produkte für Sie</h2>" +
                    "<div style='display: flex; justify-content: space-between; margin-bottom: 20px;'>" +
                    "<div style='flex: 1; text-align: center;'>" +
                    "<img src='https://coscine-s3-01.s3.fds.rwth-aachen.de:9021/fdf49c29-86ae-4402-acb3-b1c3cc0360ad/produkt/b1c8baca-c64c-4214-ba8c-227323e860a3/image' alt='EFFECT 0.33L' style='width: 100px; height: 99px;'>" +
                    "<p style='margin-top: 10px;'><strong>EFFECT 0.33L</strong></p>" +
                    "<p>Ein erfrischendes Getränk für jede Gelegenheit.</p>" +
                    "<p><a href='https://www.onlinedrinkshop.de/product1' style='color: #007bff;'>Jetzt kaufen</a></p>" +
                    "</div>" +
                    "<div style='flex: 1; text-align: center;'>" +
                    "<img src='https://coscine-s3-01.s3.fds.rwth-aachen.de:9021/fdf49c29-86ae-4402-acb3-b1c3cc0360ad/produkt/a474423d-d44a-4ca4-a879-e65cc8826a9d/image' alt='Red Bull 250ml' style='width: 100px; height: 99px;'>" +
                    "<p style='margin-top: 10px;'><strong>Red Bull 250ml</strong></p>" +
                    "<p>Perfekt für besondere Anlässe.</p>" +
                    "<p><a href='https://www.onlinedrinkshop.de/product2' style='color: #007bff;'>Jetzt kaufen</a></p>" +
                    "</div>" +
                    "<div style='flex: 1; text-align: center;'>" +
                    "<img src='https://coscine-s3-01.s3.fds.rwth-aachen.de:9021/fdf49c29-86ae-4402-acb3-b1c3cc0360ad/produkt/08e2e1f5-41f9-4fed-8e99-f7423b4279f0/image' alt='Produkt 3' style='width: 100px; height: 99px;'>" +
                    "<p style='margin-top: 10px;'><strong>Früh Kölsch 0,5l</strong></p>" +
                    "<p>Genießen Sie den Geschmack der Natur.</p>" +
                    "<p><a href='https://www.onlinedrinkshop.de/product3' style='color: #007bff;'>Jetzt kaufen</a></p>" +
                    "</div>" +
                    "</div>" +
                    "</div>" +
                    "<footer style='margin-top: 30px; border-top: 1px solid #ccc; padding-top: 20px; font-size: 12px; color: #555; text-align: center;'>" +
                    "<p><img src='https://i.postimg.cc/j2bh9GLg/temp-Image-Oc-Ph-PE.avif' alt='OnlineDrink Shop Logo' style='width: 100px; height: auto;'></p>" +
                    "<p>OnlineDrink Shop, 1234 Drink Street, Drink City, DC 12345</p>" +
                    "<p>Tel: 0123-456789 | E-Mail: support@onlinedrinkshop.de</p>" +
                    "</footer>" +
                    "</body>" +
                    "</html>";
            mimeMessageHelper.setText(htmlContent, true);
            mimeMessageHelper.addAttachment("Invoice.pdf", new ByteArrayDataSource(pdfBytes, "application/pdf"));
            mailSender.send(mimeMessage);

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Error processing order. Please try again.");
            return "checkout";
        } catch (MessagingException e) {
            e.printStackTrace();
            model.addAttribute("error", "Error sending confirmation email. Please try again.");
            return "checkout";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
            return "checkout";
        }
        session.removeAttribute("cart");

        return "checkout-success";
    }
}
