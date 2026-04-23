package mg.federation.federationapi.controller;

import mg.federation.federationapi.dto.*;
import mg.federation.federationapi.service.MembershipService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class MembershipController {

    private final MembershipService service;

    public MembershipController(MembershipService service) {
        this.service = service;
    }

    @PostMapping("/{id}/membershipFees")
    public List<MembershipFee> createFees(@PathVariable String id,
                                          @RequestBody List<CreateMembershipFee> reqs) {
        return service.createFees(id, reqs);
    }

    @GetMapping("/{id}/membershipFees")
    public List<MembershipFee> getFees(@PathVariable String id) {
        return service.getFees(id);
    }
}