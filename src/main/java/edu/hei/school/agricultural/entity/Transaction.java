package edu.hei.school.agricultural.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "accountCredited")
@EqualsAndHashCode(exclude = "accountCredited")
public class Transaction {
    private String id;
    private TransactionType type;
    private Double amount;
    private LocalDate creationDate;
    private Member memberDebited;
    private FinancialAccount accountCredited;
}
