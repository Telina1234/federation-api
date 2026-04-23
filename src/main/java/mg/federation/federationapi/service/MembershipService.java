package mg.federation.federationapi.service;

import mg.federation.federationapi.dto.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MembershipService {

    private Map<String, List<MembershipFee>> fees = new HashMap<>();
    private Map<String, List<MemberPayment>> payments = new HashMap<>();

    private int feeCounter = 1;
    private int paymentCounter = 1;

    public List<MembershipFee> createFees(String collectivityId, List<CreateMembershipFee> reqs) {

        List<MembershipFee> list = new ArrayList<>();

        for (CreateMembershipFee r : reqs) {

            if (r.getAmount() <= 0) {
                throw new RuntimeException("Invalid amount");
            }

            MembershipFee f = new MembershipFee();
            f.setId("F" + feeCounter++);
            f.setAmount(r.getAmount());
            f.setFrequency(r.getFrequency());
            f.setLabel(r.getLabel());
            f.setEligibleFrom(r.getEligibleFrom());
            f.setStatus("ACTIVE");

            list.add(f);
        }

        fees.put(collectivityId, list);
        return list;
    }

    public List<MembershipFee> getFees(String collectivityId) {
        return fees.getOrDefault(collectivityId, new ArrayList<>());
    }

    public List<MemberPayment> createPayment(String memberId, List<CreateMemberPayment> reqs) {

        List<MemberPayment> list = new ArrayList<>();

        for (CreateMemberPayment r : reqs) {

            if (r.getAmount() <= 0) {
                throw new RuntimeException("Invalid payment");
            }

            MemberPayment p = new MemberPayment();
            p.setId("P" + paymentCounter++);
            p.setAmount(r.getAmount());
            p.setPaymentMode(r.getPaymentMode());
            p.setCreationDate(new Date().toString());

            list.add(p);
        }

        payments.put(memberId, list);
        return list;
    }
}