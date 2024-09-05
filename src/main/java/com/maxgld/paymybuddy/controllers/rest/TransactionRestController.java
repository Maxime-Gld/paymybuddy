package com.maxgld.paymybuddy.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.maxgld.paymybuddy.services.TransactionService;

@RestController
@RequestMapping("/transaction")
public class TransactionRestController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/send")
    public ModelAndView send(@AuthenticationPrincipal UserDetails user, @RequestParam int receiver,
            @RequestParam Double amount, @RequestParam String description, RedirectAttributes redirectAttributes) {

        try {
            transactionService.transfer(user, receiver, amount, description);
            redirectAttributes.addFlashAttribute("messageSuccess", "Virement effectue avec succes");
        }

        catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
        }

        return new ModelAndView("redirect:/transfer");
    }
}
