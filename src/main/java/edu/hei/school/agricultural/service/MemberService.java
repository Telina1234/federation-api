package edu.hei.school.agricultural.service;

import edu.hei.school.agricultural.entity.Member;
import edu.hei.school.agricultural.entity.MemberPayment;
import edu.hei.school.agricultural.entity.Transaction;
import edu.hei.school.agricultural.exception.BadRequestException;
import edu.hei.school.agricultural.repository.MemberPaymentRepository;
import edu.hei.school.agricultural.repository.MemberRepository;
import edu.hei.school.agricultural.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static edu.hei.school.agricultural.entity.TransactionType.IN;
import static java.time.LocalDate.now;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberPaymentRepository memberPaymentRepository;
    private final TransactionRepository transactionRepository;

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

    public List<MemberPayment> createPayments(List<MemberPayment> memberPaymentList) {
        for (MemberPayment member : memberPaymentList) {
            member.setId(randomUUID().toString());
            member.setCreationDate(now());
        }
        List<MemberPayment> savedMemberPayments = memberPaymentRepository.saveAll(memberPaymentList);

        List<Transaction> newTransactionList = savedMemberPayments.stream()
                .map(memberPayment -> {
                    Transaction transaction = Transaction.builder()
                            .id(randomUUID().toString())
                            .memberDebited(memberPayment.getMemberOwner())
                            .amount(memberPayment.getAmount())
                            .type(IN)
                            .creationDate(memberPayment.getCreationDate())
                            .accountCredited(memberPayment.getAccountCredited())
                            .build();
                    return transaction;
                })
                .toList();

        transactionRepository.saveAll(newTransactionList);

        return savedMemberPayments;
    }
}
