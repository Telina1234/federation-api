package mg.federation.federationapi.service;

import mg.federation.federationapi.dto.CreateMember;
import mg.federation.federationapi.dto.Member;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MemberService {

    private Map<String, Member> members = new HashMap<>();
    private int counter = 1;

    public List<Member> create(List<CreateMember> requests) {

        List<Member> result = new ArrayList<>();

        for (CreateMember r : requests) {

            if (r.getReferees() == null || r.getReferees().size() < 2) {
                throw new RuntimeException("Minimum 2 referees");
            }

            if (!r.isRegistrationFeePaid() || !r.isMembershipDuesPaid()) {
                throw new RuntimeException("Payment required");
            }

            String id = "M" + counter++;

            Member m = new Member();
            m.setId(id);
            m.setFirstName(r.getFirstName());
            m.setLastName(r.getLastName());
            m.setEmail(r.getEmail());

            members.put(id, m);
            result.add(m);
        }

        return result;
    }
}