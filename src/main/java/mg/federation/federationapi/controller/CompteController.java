package mg.federation.federationapi.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comptes")
public class CompteController {

    @GetMapping
    public String get() {
        return "Liste comptes";
    }
}