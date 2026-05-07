package edu.hei.school.agricultural.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class CashAccount extends FinancialAccount {
}
