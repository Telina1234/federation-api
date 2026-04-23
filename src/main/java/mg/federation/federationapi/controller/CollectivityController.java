package mg.federation.federationapi.controller;

import mg.federation.federationapi.dto.CreateCollectivity;
import mg.federation.federationapi.dto.Collectivity;
import mg.federation.federationapi.dto.CollectivityIdentity;
import mg.federation.federationapi.dto.FinancialAccount;
import mg.federation.federationapi.service.CollectivityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class CollectivityController {

    private final CollectivityService service;

    public CollectivityController(CollectivityService service) {
        this.service = service;
    }

    @PostMapping
    public List<Collectivity> create(@RequestBody List<CreateCollectivity> requests) {
        return service.create(requests);
    }

    // ✅ CORRECTION ICI
    @PutMapping("/{id}/informations")
    public Collectivity assignIdentity(@PathVariable String id,
                                       @RequestBody CollectivityIdentity request) {
        return service.assignIdentity(id, request);
    }

    @GetMapping("/{id}")
    public Collectivity getById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping("/{id}/financialAccounts")
    public List<FinancialAccount> getAccounts(
            @PathVariable String id,
            @RequestParam String at) {

        return service.getAccounts(id);
    }
}