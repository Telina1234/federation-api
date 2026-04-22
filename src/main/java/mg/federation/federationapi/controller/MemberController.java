package mg.federation.federationapi.controller;

import mg.federation.federationapi.dto.CreateMember;
import mg.federation.federationapi.dto.Member;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    @PostMapping
    public List<Member> createMembers(@RequestBody List<CreateMember> requests) {

        List<Member> response = new ArrayList<>();

        for (CreateMember req : requests) {

            if (req.getReferees() == null || req.getReferees().size() < 2) {
                throw new RuntimeException("Minimum 2 referees required");
            }

            if (!req.isRegistrationFeePaid() || !req.isMembershipDuesPaid()) {
                throw new RuntimeException("Paiement non effectué");
            }

            Member m = new Member();
            m.setId("M" + System.currentTimeMillis());
            m.setFirstName(req.getFirstName());
            m.setLastName(req.getLastName());

            response.add(m);
        }

        return response;
    }
}