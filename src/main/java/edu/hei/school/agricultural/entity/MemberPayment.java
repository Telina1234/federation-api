package edu.hei.school.agricultural.entity;

import edu.hei.school.agricultural.controller.dto.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MemberPayment {
    private String id;
    private Integer amount;
    private PaymentMode paymentMode;
    private String accountCreditedIdentifier;
    private LocalDate creationDate;
    private Member member;
    private MembershipFee membershipFee;
}
