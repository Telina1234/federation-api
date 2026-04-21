package mg.federation.federationapi.controller;

import mg.federation.dto.AdmissionRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admissions")
public class AdmissionController {

    @PostMapping
    public String admit(@RequestBody AdmissionRequest request) {
        return "Admission validée";
    }
}