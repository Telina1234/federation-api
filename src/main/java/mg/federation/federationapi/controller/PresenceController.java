package mg.federation.federationapi.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/presences")
public class PresenceController {

    @PostMapping
    public String enregistrer() {
        return "Présence enregistrée";
    }
}