package com.example.authenticationservice.util;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import com.example.authenticationservice.dto.EMail;
import com.example.authenticationservice.entity.Otp;
import com.example.authenticationservice.entity.VerifyToken;
import com.example.authenticationservice.error.AuthException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import freemarker.template.Configuration;

@Service
public class ServiceUtil {
    
    @Autowired     
    private JavaMailSender javaMailSender; 

    @Autowired
    private ServerProperties serverProperties;

    @Autowired     
    Configuration fmConfiguration;

    @Value("${sp.notification.from}")
    private String notificationFrom;

    public void sendVerificationMail(String email,VerifyToken verifyToken,String userName) throws Exception {
        EMail mail = new EMail();
        mail.setFrom(notificationFrom);
        mail.setTo(email);
        mail.setSubject("Email Verification");
        StringBuilder builder = new StringBuilder();
        builder.append(serverProperties.getAddress().getHostAddress()).append(":").append(serverProperties.getPort());
        builder.append("/").append("/auth/verifyEmail?token=").append(verifyToken.getToken()).append("&email=").append(email);
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("verificationLink",builder.toString());
        model.put("userName",userName);
        sendEmailWithAttachment(mail,model,"email-verify" + ".ftl");
    }

    public void sendOtpMail(String email,Otp otp) throws Exception {
        EMail mail = new EMail();
        mail.setFrom(notificationFrom);
        mail.setTo(email);
        mail.setSubject("OTP Verification");
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("otp",otp.getOtpCode());
        sendEmailWithAttachment(mail, model,"otp-verify" + ".ftl");
    }

    public void sendChangePasswordMail(String email,VerifyToken verifyToken)  throws Exception{
        EMail mail = new EMail();
        mail.setFrom(notificationFrom);
        mail.setTo(email);
        mail.setSubject("Password Change Request");
        Map<String,Object> model = new HashMap<String,Object>();
        sendEmailWithAttachment(mail, model,"change-password" + ".ftl");
        // to be looked upon later
    }

    public synchronized void sendEmailWithAttachment(EMail mail,Map<String,Object> model,String template) throws AuthException, Exception  {
        try {
            MimeMessage msg = javaMailSender.createMimeMessage();// true = multipart message
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setTo(mail.getTo());
            helper.setFrom(mail.getFrom());
            helper.setSubject(mail.getSubject());
            helper.setText(getContentFromTemplate(model,template));
            javaMailSender.send(msg);
        } catch (MessagingException e) {
            // TODO: handle exception
            throw new AuthException(new Exception(e.getMessage()));
        }
    }
    
    public synchronized String getContentFromTemplate(Map < String, Object >model,String templateName)     { 
        StringBuffer content = new StringBuffer();
        try {
            content.append(
                FreeMarkerTemplateUtils.processTemplateIntoString(
                    fmConfiguration.getTemplate(templateName), model
                )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
    
}
