package com.smart.service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;


import java.util.Properties;

@Service
public class EmailService {
    public boolean sendEmail(String subject, String message, String to) {
        //rest of the code
        boolean f= false;
        String from = "abhinavarya331@gmail.com";

        //variable for gmail
        String host = "smtp.gmail.com";

        //get the system properties
        Properties properties = System.getProperties();
        System.out.println("PROPERTIES "+properties);

        //setting imp info to properties object

        //host set
        properties.put("mail.smtp.host",host);
        //properties.put("mail.smtp.port","465");
        //properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.port","587");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.auth","true");

        //Step:1 To get the session object
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("abhinavarya331@gmail.com","lfinbhkhxhxdvngk");
            }
        });

        session.setDebug(true);

        //Step:2 Compose the message[text,multimedia]
        MimeMessage m = new MimeMessage(session);

        try {
            //from email
            //m.setFrom(from);
            m.setFrom(from);

            //adding recipent to message
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            //adding subject to message
            m.setSubject(subject);

            //adding text to message
//            m.setText(message);
            m.setContent(message, "text/html");

            //send

            //Step:3 send the message using transpose class
            Transport.send(m);
            System.out.println("Sent Success.......");
            f=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }
}
