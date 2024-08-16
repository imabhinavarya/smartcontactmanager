package com.smart.controllers;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    @RequestMapping("/")
    public String home(Model m) {
        m.addAttribute("title",  "Home - Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model m) {
        m.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    @RequestMapping("/signup")
    public String signUp(Model m) {
        m.addAttribute("title", "SignUp - Smart Contact Manager");
        m.addAttribute("user", new User());
        return "signup";
    }

    //handler for registering user
    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model m,
                                HttpSession session) {
        try {
            if(!agreement) {
                System.out.println("Please Agree on terms and conditions.");
                throw new Exception("Please Agree on terms and conditions.");
            }

            if(result.hasErrors()) {
                System.out.println("ERRORS: "+ result.toString());
                m.addAttribute("user",user);
                return "signup";
            }
            user.setRole("ROLE_USER");
            user.setImageUrl("image.png");
            user.setEnabled(true);
            user.setPassword(passwordEncoder.encode(user.getPassword()));


            System.out.println("AGREEMENT: "+ agreement);


            User res = this.userRepository.save(user);
            m.addAttribute("user", new User());

            session.setAttribute("message", new Message("Successfully Registered ", "alert-success"));

            System.out.println(user);
            return "signup";
        } catch (Exception e) {
            e.printStackTrace();
            m.addAttribute("user", user);
            session.setAttribute("message", new Message("Something Went Wrong!! " + e.getMessage(), "alert-danger"));
            return "signup";
        }

    }



    @RequestMapping("/signin")
    public String customLogin(Model m) {
        m.addAttribute("title", "LogIn - Smart Contact Manager");
        m.addAttribute("about", "This is about page.");
        return "login";
    }
}
