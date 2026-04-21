package mg.federation.federationapi.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/collectivites")
public class CollectiviteController {

    @PostMapping
    public String create() {
        return "Collectivité créée";
    }
}