package com.webapp.bankingportal.service;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
		log.debug(">>> Entering EmailServiceImpl(mailSender={})", mailSender);
        this.mailSender = mailSender;
		log.debug("<<< Exiting EmailServiceImpl(mailSender={})", mailSender);
    }

    @Override
    @Async
    public CompletableFuture<Void> sendEmail(String to, String subject, String text) {
		log.debug(">>> Entering sendEmail(to={},subject={},text={})", to, subject, text);
        val future = new CompletableFuture<Void>();

        try {
            val message = mailSender.createMimeMessage();
            val helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
			log.debug("sendEmail(to={},subject={},text={}): helper → {}", to, subject, text, helper);
            // From address is automatically set by Spring Boot based on your properties
            helper.setSubject(subject);
            helper.setText(text, true); // Set the second parameter to true to send HTML content
            mailSender.send(message);

            log.info("Sent email to {}", to);
            future.complete(null);

        } catch (MessagingException | MailException e) {
            log.error("Failed to send email to {}", to, e);
            future.completeExceptionally(e);
        }

		log.debug("<<< Exiting sendEmail(to={},subject={},text={})", to, subject, text);
        return future;
    }

    @Override
    public String getLoginEmailTemplate(String name, String loginTime, String loginLocation) {
		log.debug(">>> Entering getLoginEmailTemplate(name={},loginTime={},loginLocation={})", name, loginTime, loginLocation);
		log.info("getLoginEmailTemplate(name,loginTime,loginLocation)={},{},{}): SOC2-AUDIT [CC6.1]: Authentication operation completed", name, loginTime, loginLocation);
		log.warn("getLoginEmailTemplate(name,loginTime,loginLocation)={},{},{}): SOC2-AUDIT [CC6.1]: Password reset operation performed", name, loginTime, loginLocation);
		log.warn("getLoginEmailTemplate(name,loginTime,loginLocation)={},{},{}): SOC2-AUDIT [CC7.3]: Security incident detected", name, loginTime, loginLocation);
		log.debug("<<< Exiting getLoginEmailTemplate(name={},loginTime={},loginLocation={})", name, loginTime, loginLocation);
        return "<div style=\"font-family: Helvetica, Arial, sans-serif; min-width: 320px; max-width: 1000px; margin: 0 auto; overflow: auto; line-height: 2; background-color: #f1f1f1; padding: 20px;\">"
                + "<div style=\"margin: 50px auto; width: 100%; max-width: 600px; padding: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);\">"
                + "<div style=\"border-bottom: 1px solid #ddd; padding-bottom: 10px; text-align: center;\">"
                + "<a href=\"https://onestopbank.netlify.app/\" style=\"text-decoration: none;\">"
                + "<img src=\"https://onestopbank.netlify.app/assets/onestoplogo.jpg\" alt=\"OneStopBank\" style=\"height: 50px; margin-bottom: 10px;\">"
                + "</a>" + "<h1 style=\"font-size: 1.8em; color: #3f51b5; margin: 10px 0;\">OneStopBank</h1>" + "</div>"
                + "<div style=\"padding: 20px;\">" + "<p style=\"font-size: 1.2em; color: #333;\">Hi, " + name + ",</p>"
                + "<p style=\"font-size: 1em; color: #333;\">A login attempt was made on your account at:</p>"
                + "<p style=\"font-size: 1em; color: #555;\">Time: <strong style=\"color: #3f51b5;\">" + loginTime
                + "</strong></p>"
                + "<p style=\"font-size: 1em; color: #555;\">Location: <strong style=\"color: #3f51b5;\">"
                + loginLocation + "</strong></p>"
                + "<p style=\"font-size: 1em; color: #333;\">If this was you, no further action is required. If you suspect any unauthorized access, please change your password immediately and contact our support team.</p>"
                + "<p style=\"font-size: 1em; color: #555;\">Regards,<br />The OneStopBank Team</p>" + "</div>"
                + "<hr style=\"border: none; border-top: 1px solid #ddd; margin: 20px 0;\" />"
                + "<div style=\"text-align: center; font-size: 0.9em; color: #888;\">"
                + "<p>Need help? Contact our support team:</p>"
                + "<p>Email: <a href=\"mailto:onestopbank@google.com\" style=\"color: #3f51b5; text-decoration: none;\">onestopbank@google.com</a></p>"
                + "<div style=\"margin-top: 20px;\">"
                + "<p style=\"font-size: 1em; color: #333;\">Show your support here ❤️</p>"
                + "<a href=\"https://github.com/abhi9720/BankingPortal-API\" style=\"margin: 0 10px; color: #3f51b5; text-decoration: none;\">GitHub</a>"
                + "</div>" + "</div>" + "</div>" + "</div>";
    }

    @Override
    public String getOtpLoginEmailTemplate(String name, String accountNumber, String otp) {
		log.debug(">>> Entering getOtpLoginEmailTemplate(name={},accountNumber={},otp={})", name, accountNumber, otp);
		log.info("getOtpLoginEmailTemplate(name,accountNumber,otp)={},{},{}): SOC2-AUDIT [CC6.1]: Authentication operation completed", name, accountNumber, otp);
		log.warn("getOtpLoginEmailTemplate(name,accountNumber,otp)={},{},{}): SOC2-AUDIT [CC7.3]: Security incident detected", name, accountNumber, otp);
		log.debug("<<< Exiting getOtpLoginEmailTemplate(name={},accountNumber={},otp={})", name, accountNumber, otp);
        return "<div style=\"font-family: Helvetica, Arial, sans-serif; min-width: 320px; max-width: 1000px; margin: 0 auto; overflow: auto; line-height: 2; background-color: #f1f1f1; padding: 20px;\">"
                + "<div style=\"margin: 50px auto; width: 100%; max-width: 600px; padding: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);\">"
                + "<div style=\"border-bottom: 1px solid #ddd; padding-bottom: 10px; text-align: center;\">"
                + "<a href=\"https://onestopbank.netlify.app/\" style=\"text-decoration: none;\">"
                + "<img src=\"https://onestopbank.netlify.app/assets/onestoplogo.jpg\" alt=\"OneStopBank\" style=\"height: 50px; margin-bottom: 10px;\">"
                + "</a>" + "<h1 style=\"font-size: 1.8em; color: #3f51b5; margin: 10px 0;\">OneStopBank</h1>" + "</div>"
                + "<div style=\"padding: 20px;\">" + "<p style=\"font-size: 1.2em; color: #333;\">Hi, " + name + ",</p>"
                + "<p style=\"font-size: 1em; color: #555;\">Account Number: <strong style=\"color: #3f51b5;\">"
                + accountNumber + "</strong></p>"
                + "<p style=\"font-size: 1em; color: #333;\">Thank you for choosing OneStopBank. Use the following OTP to complete your login procedures. The OTP is valid for "
                + OtpServiceImpl.OTP_EXPIRY_MINUTES + " minutes:</p>"
                + "<h2 style=\"background: #3f51b5; margin: 20px 0; width: max-content; padding: 10px 20px; color: #fff; border-radius: 4px;\">"
                + otp + "</h2>" + "<p style=\"font-size: 1em; color: #555;\">Regards,<br />The OneStopBank Team</p>"
                + "</div>" + "<hr style=\"border: none; border-top: 1px solid #ddd; margin: 20px 0;\" />"
                + "<div style=\"text-align: center; font-size: 0.9em; color: #888;\">"
                + "<p>Need help? Contact our support team:</p>"
                + "<p>Email: <a href=\"mailto:onestopbank@google.com\" style=\"color: #3f51b5; text-decoration: none;\">onestopbank@google.com</a></p>"
                + "<div style=\"margin-top: 20px;\">"
                + "<p style=\"font-size: 1em; color: #333;\">Show your support here ❤️</p>"
                + "<a href=\"https://github.com/abhi9720/BankingPortal-API\" style=\"margin: 0 10px; color: #3f51b5; text-decoration: none;\">GitHub</a>"
                + "</div>" + "</div>" + "</div>" + "</div>";
    }

    @Override
    public String getBankStatementEmailTemplate(String name, String statementText) {
		log.debug(">>> Entering getBankStatementEmailTemplate(name={},statementText={})", name, statementText);
		log.debug("<<< Exiting getBankStatementEmailTemplate(name={},statementText={})", name, statementText);
         return "<div style=\"font-family: Arial, sans-serif; padding: 20px;\">" +
                "<h2>Bank Statement</h2>" +
                "<p>Dear " + name + ",</p>" +
                "<p>Here is your latest bank statement:</p>" +
                "<pre style=\"background: #f4f4f4; padding: 10px; border-radius: 5px;\">" +
                statementText +
                "</pre>" +
                "<p>Regards,<br/>OneStopBank Team</p>" +
                "</div>";
    }

    public void sendEmailWithAttachment(String to, String subject, String text, String attachmentFilePath) {
		log.debug(">>> Entering sendEmailWithAttachment(to={},subject={},text={},attachmentFilePath={})", to, subject, text, attachmentFilePath);
        try {
            val message = mailSender.createMimeMessage();
            val helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
			log.debug("sendEmailWithAttachment(to={},subject={},text={},attachmentFilePath={}): helper → {}", to, subject, text, attachmentFilePath, helper);
            helper.setSubject(subject);
            helper.setText(text, true); // Set the second parameter to true to send HTML content

            // Add an attachment to the email
            val attachmentFile = new File(attachmentFilePath);
            helper.addAttachment(attachmentFile.getName(), attachmentFile);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}", to, e);
        }
		log.debug("<<< Exiting sendEmailWithAttachment(to={},subject={},text={},attachmentFilePath={})", to, subject, text, attachmentFilePath);
    }


}
