package vector.StockManagement.controllers;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import vector.StockManagement.model.dto.PricingRequest;
import vector.StockManagement.model.dto.ContactRequest;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor

public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    private final String toEmail = "info@sales.rw";

    @PostMapping("/request")
    public String contactRequest(@RequestBody ContactRequest contactRequest) {
        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail); //receiver.getEmail()
            helper.setSubject(contactRequest.getSubject());
            helper.setFrom(contactRequest.getEmailAddress()); // sender.getEmail()
            helper.setText(contactRequest.getMessage() + "\n From: "+contactRequest.getFullName(), true); // true indicates HTML content
            mailSender.send(message);
            System.out.println("Email sent");
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());

        }
        return "Email sent";
    }

    @PostMapping("/pricing")
    public String contactPricing(@RequestBody PricingRequest pricingRequest) {
        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail); //receiver.getEmail()
            helper.setSubject("Pricing Request");
            helper.setFrom(pricingRequest.getEmail()); // sender.getEmail()
            helper.setText("Me " + pricingRequest.getFullName() + " with Business called " + pricingRequest.getBusinessName() + "\nAnd phone number" + pricingRequest.getPhoneNumber() + "as well as email of " + pricingRequest.getPhoneNumber() + " with my " + pricingRequest.getBusinessType() + "\nI want to pay " +pricingRequest.getPricingType() +" pricing for your service!" , true); // true indicates HTML content
            mailSender.send(message);
            System.out.println("Email sent");
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());

        }
        return "Email sent";

    }


}
