package edu.hei.school.agricultural.controller.mapper;

import edu.hei.school.agricultural.controller.dto.CreateMemberPayment;
import edu.hei.school.agricultural.entity.*;
import edu.hei.school.agricultural.exception.NotFoundException;
import edu.hei.school.agricultural.repository.FinancialAccountRepository;
import edu.hei.school.agricultural.repository.MemberRepository;
import edu.hei.school.agricultural.repository.MembershipFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberPaymentDtoMaper {
    private final FinancialAccountDtoMapper financialAccountDtoMapper;
    private final MemberRepository memberRepository;
    private final MembershipFeeRepository membershipFeeRepository;
    private final FinancialAccountRepository financialAccountRepository;

    public MemberPayment mapToEntity(String memberIdentifier, CreateMemberPayment createMemberPayment) {
        Member member = memberRepository.findById(memberIdentifier).orElseThrow(
                () -> new NotFoundException("Member.id=" + memberIdentifier + " not found")
        );
        MembershipFee membershipFee = membershipFeeRepository.findById(createMemberPayment.getMembershipFeeIdentifier())
                .orElseThrow(
                        () -> new NotFoundException("MembershipFee.id=" + createMemberPayment.getMembershipFeeIdentifier() + " not found")
                );
        FinancialAccount financialAccount = financialAccountRepository.findFinancialAccountById(createMemberPayment.getAccountCreditedIdentifier())
                .orElseThrow(() -> new NotFoundException("FinancialAccount.id=" + createMemberPayment.getAccountCreditedIdentifier() + " not found"));

        return MemberPayment.builder()
                .paymentMode(createMemberPayment.getPaymentMode() == null ? null : PaymentMode.valueOf(createMemberPayment.getPaymentMode().name()))
                .amount(createMemberPayment.getAmount())
                .memberOwner(member)
                .membershipFee(membershipFee)
                .accountCredited(financialAccount)
                .build();
    }

    public edu.hei.school.agricultural.controller.dto.MemberPayment mapToDto(MemberPayment memberPayment) {
        return edu.hei.school.agricultural.controller.dto.MemberPayment.builder()
                .id(memberPayment.getId())
                .paymentMode(memberPayment.getPaymentMode() == null ? null : edu.hei.school.agricultural.controller.dto.PaymentMode.valueOf(memberPayment.getPaymentMode().name()))
                .accountCredited(financialAccountDtoMapper.mapToDto(memberPayment.getAccountCredited()))
                .creationDate(memberPayment.getCreationDate())
                .amount(memberPayment.getAmount())
                .build();
    }
}
