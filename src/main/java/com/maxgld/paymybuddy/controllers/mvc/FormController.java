package com.maxgld.paymybuddy.controllers.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.maxgld.paymybuddy.dto.UserCreateDto;
import com.maxgld.paymybuddy.services.UserService;

@Controller
public class FormController {

    @Autowired
    private UserService usersService;

    /**
     * Affiche le formulaire de login
     * 
     * @param model le model de la vue
     * @return la vue de login
     */
    @GetMapping("/login")
    public ModelAndView login(Model model) {
        model.addAttribute("formulaire", "login");
        model.addAttribute("title", "Login");
        model.addAttribute("textButton", "Se connecter");
        model.addAttribute("textLink", "Je n'ai pas de compte");
        model.addAttribute("action", "/login");
        return new ModelAndView("login-register");
    }

    /**
     * Affiche le formulaire d'inscription
     * 
     * @param model le model de la vue
     * @return la vue de signup
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("formulaire", "register");
        model.addAttribute("title", "S'enregistrer");
        model.addAttribute("textButton", "S'enregistrer");
        model.addAttribute("textLink", "J'ai déja un compte");
        model.addAttribute("action", "/register");
        return "login-register";
    }

    /**
     * Enregistre un utilisateur et le redirige vers la page de login si cela
     * fonctionne.
     * 
     * @param user le formulaire d'inscription
     * @return la page de login si l'enregistrement a fonctionné, sinon la page de
     *         signup avec un message d'erreur
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserCreateDto user) {
        boolean result = usersService.saveUser(user);

        if (!result) {
            return "redirect:/register?existing";
        }

        return "redirect:/login?success";

    }
}
