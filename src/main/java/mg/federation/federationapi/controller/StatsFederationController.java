package mg.federation.federationapi.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stats/federation")
public class StatsFederationController {

    @GetMapping
    public String stats() {
        return "Stats fédération";
    }
}