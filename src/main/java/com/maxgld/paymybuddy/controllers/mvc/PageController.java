package com.maxgld.paymybuddy.controllers.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.maxgld.paymybuddy.dto.TransactionDto;
import com.maxgld.paymybuddy.dto.UserDto;
import com.maxgld.paymybuddy.services.TransactionService;
import com.maxgld.paymybuddy.services.UserService;

@Controller
public class PageController {

    @Autowired
    private UserService usersService;

    @Autowired
    private TransactionService transactionService;

    /**
     * Affiche la page de transfert, avec la liste des virements,
     * le solde, le nom d'utilisateur et la liste des relations.
     *
     * @param model le model qui contiendra les attributs
     * @param user  le détails de l'utilisateur connecté
     * @return le nom de la page html
     */
    @GetMapping("/transfer")
    public String transfer(Model model, @AuthenticationPrincipal UserDetails user) {

        List<UserDto> connections = usersService.getConnections(user);
        double balance = usersService.getBalance(user);
        String username = usersService.getUsername(user);
        List<TransactionDto> transactions = transactionService.getAllTransaction(user);

        model.addAttribute("transactions", transactions);
        model.addAttribute("username", username);
        model.addAttribute("users", connections);
        model.addAttribute("balance", balance);
        model.addAttribute("title", "Transfer");
        model.addAttribute("page", "transfer");
        return "index";
    }

    /**
     * Affiche la page du profil.
     *
     * @param model le model qui contiendra les attributs
     * @return le nom de la page html
     */
    @GetMapping("/profile")
    public String profile(Model model) {

        model.addAttribute("title", "Profile");
        model.addAttribute("page", "profile");
        return "index";
    }

    /**
     * Affiche la page d'ajout de relation.
     *
     * @param model le model qui contiendra les attributs
     * @return le nom de la page html
     */
    @GetMapping("/addConnection")
    public String addConnection(Model model) {

        model.addAttribute("title", "Add Connection");
        model.addAttribute("page", "addConnection");
        return "index";
    }

}
