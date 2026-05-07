package edu.hei.school.agricultural.service;

import edu.hei.school.agricultural.controller.dto.CreateMemberPayment;
import edu.hei.school.agricultural.controller.dto.MemberPayment;
import edu.hei.school.agricultural.entity.Member;
import edu.hei.school.agricultural.entity.MembershipFee;
import edu.hei.school.agricultural.exception.BadRequestException;
import edu.hei.school.agricultural.exception.NotFoundException;
import edu.hei.school.agricultural.repository.MemberRepository;
import edu.hei.school.agricultural.repository.MemberPaymentRepository;
import edu.hei.school.agricultural.repository.MembershipFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MembershipFeeRepository membershipFeeRepository;
    private final MemberPaymentRepository memberPaymentRepository;

    public List<Member> addNewMembers(List<Member> memberList) {
        for (Member member : memberList) {
            if (!member.refereesAreEligible()) {
                throw new BadRequestException("Member.id=" + member.getId() + " member referees are not eligible");
            }
            if (!member.getMembershipDuesPaid()) {
                throw new BadRequestException("Member.id=" + member.getId() + " membership dues not paid");
            }
            if (!member.getRegistrationFeePaid()) {
                throw new BadRequestException("Member.id=" + member.getId() + " membership fees not paid");
            }
            member.setId(randomUUID().toString());
        }
        return memberRepository.saveAll(memberList);
    }

    public List<MemberPayment> addPayments(String memberId, List<CreateMemberPayment> createMemberPayments) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member.id=" + memberId + " not found"));

        List<edu.hei.school.agricultural.entity.MemberPayment> payments = createMemberPayments.stream()
                .map(createMemberPayment -> {
                    if (createMemberPayment.getAmount() == null || createMemberPayment.getAmount() <= 0) {
                        throw new BadRequestException("Payment amount must be greater than 0");
                    }
                    MembershipFee membershipFee = membershipFeeRepository.findById(createMemberPayment.getMembershipFeeIdentifier())
                            .orElseThrow(() -> new NotFoundException("MembershipFee.id="
                                    + createMemberPayment.getMembershipFeeIdentifier() + " not found"));
                    return edu.hei.school.agricultural.entity.MemberPayment.builder()
                            .id(randomUUID().toString())
                            .amount(createMemberPayment.getAmount())
                            .paymentMode(createMemberPayment.getPaymentMode())
                            .accountCreditedIdentifier(createMemberPayment.getAccountCreditedIdentifier())
                            .creationDate(LocalDate.now())
                            .member(member)
                            .membershipFee(membershipFee)
                            .build();
                })
                .toList();

        return memberPaymentRepository.saveAll(payments).stream()
                .map(memberPaymentRepository::mapToDto)
                .toList();
    }
}
