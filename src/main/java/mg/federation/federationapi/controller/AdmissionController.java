package mg.federation.federationapi.controller;

import mg.federation.federationapi.dto.AdmissionRequest;
import mg.federation.federationapi.service.AdmissionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admissions")
public class AdmissionController {

    private final AdmissionService service;

    public AdmissionController(AdmissionService service) {
        this.service = service;
    }

    @PostMapping
    public String admit(@RequestBody AdmissionRequest request) {
        return service.admettre(request);
    }
}