package mg.federation.federationapi.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paiements")
public class PaiementController {

    @PostMapping
    public String payer() {
        return "Paiement enregistré";
    }
}