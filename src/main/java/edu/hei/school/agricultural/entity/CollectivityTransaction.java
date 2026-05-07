package edu.hei.school.agricultural.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class CollectivityTransaction extends Transaction {
    private PaymentMode paymentMode;
    private FinancialAccount accountCredited;
}
