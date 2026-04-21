package mg.federation.federationapi.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activites")
public class ActiviteController {

    @PostMapping
    public String create() {
        return "Activité créée";
    }
}