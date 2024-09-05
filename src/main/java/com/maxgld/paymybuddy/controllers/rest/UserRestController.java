package com.maxgld.paymybuddy.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.maxgld.paymybuddy.services.UserService;

@RestController
@RequestMapping("/users")
public class UserRestController {

    @Autowired
    private UserService usersService;

    // ajouter un amis
    @PostMapping("/addConnection")
    public ModelAndView addConnection(@AuthenticationPrincipal UserDetails user,
            @RequestParam String email) {

        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("page", "addConnection");

        try {
            usersService.addConnection(user, email);

            modelAndView.addObject("messageSuccess", "Ajout effectue avec succes");
        } catch (Exception e) {
            modelAndView.addObject("messageError", e.getMessage());
        }

        return modelAndView;
    }
}
