package com.smart.controllers;

import com.razorpay.Order;
import com.razorpay.*;
import com.smart.dao.ContactRepository;
import com.smart.dao.MyOrderRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.MyOrder;
import com.smart.entities.User;
import com.smart.helper.Message;
import org.hibernate.Session;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private MyOrderRepository myOrderRepository;

    @ModelAttribute
    public void addCommonData(Model m, Principal p) {
        String name = p.getName();
        System.out.println("User: "+name);
        User user = userRepository.getUserByUserName(name);

        System.out.println("USER: "+user);
        m.addAttribute("user",user);
    }
    @RequestMapping("/index")
    public String dashboard(Model m, Principal p) {
        m.addAttribute("title","User Dashboard - Smart Contact Manager");
        return "normal/user_dashboard";
    }

    @RequestMapping("/add-contact")
    public String openAddContactForm(Model m) {
        m.addAttribute("title","Add Contact - Smart Contact Manager");
        m.addAttribute("contact",new Contact());
        return "normal/add_contact_form";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/process-contact")
    public String processContact(@ModelAttribute("contact") Contact contact, @RequestParam("profileImage") MultipartFile multipartFile, HttpSession session, Principal p) {
        try {
            String name = p.getName();
            User user = this.userRepository.getUserByUserName(name);

            if(multipartFile.isEmpty()) {
                //Show message
                System.out.println("File is Empty");
                contact.setImage("contact.png");

            } else {
                //upload the file to folder and update name to contact
                contact.setImage(multipartFile.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());

                Files.copy(multipartFile.getInputStream(), path , StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File is Uploaded");
            }
            user.getContacts().add(contact);
            contact.setUser(user);

            this.userRepository.save(user);
            System.out.println("Contact :" +contact);
            System.out.println("Added to database.");
            session.setAttribute("message", new Message("Your Contact is successfully Added!", "success"));

        } catch (Exception e) {
            session.setAttribute("message", new Message("Something went wrong.", "danger"));
            System.out.println("ERROR: "+e.getMessage());
            e.printStackTrace();
        }
        return "normal/add_contact_form";
    }

    @RequestMapping("/show-contacts/{page}")
    public String showContacts(Model m, Principal p, @PathVariable("page") Integer page) {
        m.addAttribute("title","View Contact - Smart Contact Manager");
        String name = p.getName();
        User user = userRepository.getUserByUserName(name);



        Pageable pageable = PageRequest.of(page, 3);

        Page<Contact> contacts = contactRepository.findContactsByUser(user.getId(),pageable);

        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", contacts.getTotalPages());


        return "normal/show_contacts";
    }

    @RequestMapping("/contact/{cId}")
    public String showContactDetail(@PathVariable("cId") Integer cId, Model m, Principal p){
        Optional<Contact> contactOptional = contactRepository.findById(cId);
        Contact contact = contactOptional.get();
        String name = p.getName();
        User user = userRepository.getUserByUserName(name);

        if(user.getId() == contact.getUser().getId()) {
            m.addAttribute("contact", contact);
            m.addAttribute("title", contact.getName());
        }

        return "normal/contact_details";
    }

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, Model m, HttpSession session, Principal principal){
        Contact contact = this.contactRepository.findById(id).get();
        //authenticate if user is same (ToDo...)

        User user = this.userRepository.getUserByUserName(principal.getName());
        user.getContacts().remove(contact);
        this.userRepository.save(user);

        session.setAttribute("message",new Message("Contact Deleted Successfully","success"));


        return "redirect:/user/show-contacts/0";
    }

    @PostMapping("/update-contact/{cid}")
    public String updateContact(@PathVariable("cid") int cid, Model m){
        m.addAttribute("title", "Update Contact");
        Contact contact = contactRepository.findById(cid).get();
        m.addAttribute("contact", contact);

        return "normal/update_form";
    }

    @RequestMapping(method = RequestMethod.POST, value ="/process-update")
    public String processUpdate(@ModelAttribute("contact") Contact contact, @RequestParam("profileImage") MultipartFile multipartFile,
                                HttpSession session, Principal p) {
        try{
            Contact oldContact = contactRepository.findById(contact.getcId()).get();
            if(!multipartFile.isEmpty()){
                //image
                //rewrite
                //delete old photo
                File deleteFile = new ClassPathResource("static/img").getFile();
                File file1 = new File(deleteFile, oldContact.getImage());
                file1.delete();


                //update new photo
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());

                Files.copy(multipartFile.getInputStream(), path , StandardCopyOption.REPLACE_EXISTING);

                contact.setImage(multipartFile.getOriginalFilename());

            } else {
                contact.setImage(oldContact.getImage());
            }

            User user = userRepository.getUserByUserName(p.getName());
            contact.setUser(user);

            this.contactRepository.save(contact);

            session.setAttribute("message",new Message("Your Contact is Updated...","success"));

        } catch (Exception e){
            e.printStackTrace();
        }

        return "redirect:/user/contact/"+contact.getcId();
    }

    @RequestMapping("/profile")
    public String yourProfile(Model m) {
        m.addAttribute("title","Your Profile");

        return "normal/profile";
    }

    @RequestMapping("/settings")
    public String openSetting(){
        return "normal/settings";
    }

    @RequestMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword,
                                 Model m, Principal p, HttpSession session){

        System.out.println(oldPassword);
        System.out.println(newPassword);

        String name = p.getName();
        User currentUser = userRepository.getUserByUserName(name);
        System.out.println(currentUser.getPassword());

        if(bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
            currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);
            session.setAttribute("message", new Message("Password Changed Successfully","success"));
        } else {
            session.setAttribute("message", new Message("Please Enter Correct Password!!","danger"));
        }

        return "redirect:/user/index";
    }


    //CREATING ORDER FOR PAYMENT
    @ResponseBody
    @PostMapping("/create_order")
    public String createOrder(@RequestBody Map<String, Object> data, Principal p) throws RazorpayException {
        System.out.println(data);

        int amt = Integer.parseInt( data.get("amount").toString());

        var client = new RazorpayClient("rzp_test_tg9UVFB0LzVFXG", "6P4U3Sy4hhUHzGmzZhXoI9lP");

        JSONObject ob = new JSONObject();
        ob.put("amount", amt*100);
        ob.put("currency", "INR");
        ob.put("receipt", "txn_235425");

        //creating new order
        Order order  = client.orders.create(ob);
        System.out.println(order);

        //save the order in db
        MyOrder myOrder = new MyOrder();
        myOrder.setAmount(order.get("amount")+"");
        myOrder.setOrderId(order.get("id"));
        myOrder.setPaymentId(null);
        myOrder.setStatus("created");
        myOrder.setUser(this.userRepository.getUserByUserName(p.getName()));
        myOrder.setReceipt(order.get("receipt"));

        this.myOrderRepository.save(myOrder);

        return order.toString();
    }


    //UPdating order
    @ResponseBody
    @PostMapping("/update_order")
    public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data) throws RazorpayException {
        System.out.println(data);

        MyOrder myOrder = this.myOrderRepository.findByOrderId(data.get("order_id").toString());
        System.out.println("===============");
        System.out.println(myOrder);
        myOrder.setPaymentId(data.get("payment_id").toString());
        myOrder.setStatus(data.get("status").toString());
        myOrderRepository.save(myOrder);

        return ResponseEntity.ok(Map.of("msg","updated"));
    }
    }
