package mg.federation.federationapi.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stats/collectivites")
public class StatsCollectiviteController {

    @GetMapping("/{id}")
    public String stats() {
        return "Stats collectivité";
    }
}