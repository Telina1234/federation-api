package edu.hei.school.agricultural.entity;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MemberPayment {
    private String id;
    private Double amount;
    private MembershipFee membershipFee;
    private PaymentMode paymentMode;
    private FinancialAccount accountCredited;
    private Member memberOwner;
    private LocalDate creationDate;
}
