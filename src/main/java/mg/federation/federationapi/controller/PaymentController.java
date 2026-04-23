package mg.federation.federationapi.controller;

import mg.federation.federationapi.dto.*;
import mg.federation.federationapi.service.MembershipService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class PaymentController {

    private final MembershipService service;

    public PaymentController(MembershipService service) {
        this.service = service;
    }

    @PostMapping("/{id}/payments")
    public List<MemberPayment> createPayments(@PathVariable String id,
                                              @RequestBody List<CreateMemberPayment> reqs) {
        return service.createPayment(id, reqs);
    }
}