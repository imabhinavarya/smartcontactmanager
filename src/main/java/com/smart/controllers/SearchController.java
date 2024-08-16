package com.smart.controllers;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class SearchController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ContactRepository contactRepository;

    @RequestMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal p) {
        System.out.println("QUERY: "+ query);
        User user = userRepository.getUserByUserName(p.getName());
        List<Contact> contacts = contactRepository.findByNameContainingAndUser(query, user);

        return ResponseEntity.ok(contacts);


    }
}
