package mg.federation.federationapi.service;

import jakarta.annotation.PostConstruct;
import mg.federation.federationapi.dto.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataInitializer {

    private final CollectivityService collectivityService;
    private final MemberService memberService;
    private final MembershipService membershipService;

    public DataInitializer(CollectivityService c, MemberService m, MembershipService ms) {
        this.collectivityService = c;
        this.memberService = m;
        this.membershipService = ms;
    }

    @PostConstruct
    public void init() {

        List<CreateCollectivity> list = new ArrayList<>();

        list.add(createCollectivity("Ambatondrazaka"));
        list.add(createCollectivity("Ambatondrazaka"));
        list.add(createCollectivity("Brickaville"));

        List<Collectivity> cs = collectivityService.create(list);

        collectivityService.assignIdentity(cs.get(0).getId(), identity("Mpanorina", "1"));
        collectivityService.assignIdentity(cs.get(1).getId(), identity("Dobo voalohany", "2"));
        collectivityService.assignIdentity(cs.get(2).getId(), identity("Tantely mamy", "3"));

        List<CreateMember> members = new ArrayList<>();

        members.add(member("Nom membre 1", "Prenom membre 1"));
        members.add(member("Nom membre 2", "Prenom membre 2"));
        members.add(member("Nom membre 3", "Prenom membre 3"));
        members.add(member("Nom membre 4", "Prenom membre 4"));
        members.add(member("Nom membre 5", "Prenom membre 5"));
        members.add(member("Nom membre 6", "Prenom membre 6"));
        members.add(member("Nom membre 7", "Prenom membre 7"));
        members.add(member("Nom membre 8", "Prenom membre 8"));

        memberService.create(members);

        membershipService.createFees(cs.get(0).getId(), List.of(fee(100000)));
        membershipService.createFees(cs.get(1).getId(), List.of(fee(100000)));
        membershipService.createFees(cs.get(2).getId(), List.of(fee(50000)));

        membershipService.createPayment("M1", List.of(payment(100000)));
        membershipService.createPayment("M2", List.of(payment(100000)));
    }

    private CreateCollectivity createCollectivity(String loc) {
        CreateCollectivity c = new CreateCollectivity();
        c.setLocation(loc);
        c.setFederationApproval(true);
        c.setStructure(new CreateCollectivityStructure());
        return c;
    }

    private CollectivityIdentity identity(String name, String number) {
        CollectivityIdentity i = new CollectivityIdentity();
        i.setName(name);
        i.setNumber(number);
        return i;
    }

    private CreateMember member(String nom, String prenom) {
        CreateMember m = new CreateMember();
        m.setFirstName(nom);
        m.setLastName(prenom);
        m.setEmail(nom + "@test.com");
        m.setReferees(Arrays.asList("M1", "M2"));
        m.setRegistrationFeePaid(true);
        m.setMembershipDuesPaid(true);
        return m;
    }

    private CreateMembershipFee fee(double amount) {
        CreateMembershipFee f = new CreateMembershipFee();
        f.setAmount(amount);
        f.setLabel("Cotisation annuelle");
        f.setFrequency("ANNUALLY");
        return f;
    }

    private CreateMemberPayment payment(double amount) {
        CreateMemberPayment p = new CreateMemberPayment();
        p.setAmount(amount);
        p.setPaymentMode("CASH");
        return p;
    }
}