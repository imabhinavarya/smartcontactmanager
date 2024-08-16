package com.smart.controllers;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Controller
public class ForgotController {

    Random random = new Random(1000);
    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/forget")
    public String openEmailForm(){
        return "forgot_email_form";
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam("email") String email, HttpSession session){


        int otp = random.nextInt(999999);

        System.out.println("EMAIL: "+email);
        System.out.println(otp);

        //sending otp to email
        String subject = "OTP verification from Smart Contact Manager.";
//        String message = "<h3>OTP: "+otp+ " </h3>";

        String message=  "<div style='border: 2px solid #e2e2e2; padding:20px;'>"
                +"<h3 style='color: blue;'>"
                +"Your OTP for password change is "
                +"<b>"+otp
                +"</n>"
                +"</h3>"
                +"</div>";
        String to = email;

        boolean flag = this.emailService.sendEmail(subject,message,to);

        if(flag) {
            session.setAttribute("myotp",otp);
            session.setAttribute("email",email);
            return "verify_otp";
        } else {
            session.setAttribute("message", "Check your email Id!!!");
            return "forgot_email_form";
        }
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") Integer otp, HttpSession session){
        Integer myOtp = (Integer)session.getAttribute("myotp");
        String email = (String)session.getAttribute("email");

        if(otp.equals(myOtp)){
            User user = this.userRepository.getUserByUserName(email);
            if(user == null){
                session.setAttribute("message", "No User exist with this email");
                return "forgot_email_form";
            } else {
                return "password_change_form";
            }
        } else {
            session.setAttribute("message", "You have entered wrong OTP !!");
            return "verify_otp";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newPassword") String newPassword,
                                 HttpSession session){
        String email = (String) session.getAttribute("email");
        User user = this.userRepository.getUserByUserName(email);
        user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
        this.userRepository.save(user);

        return "redirect:/signin?change=Password Changed Successfully.";

    }
}
