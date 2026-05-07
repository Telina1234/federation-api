package edu.hei.school.agricultural.entity;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MembershipFee {
    private String id;
    private String label;
    private ActivityStatus status;
    private LocalDate eligibleFrom;
    private Frequency frequency;
    private Double amount;
    private Collectivity collectivityOwner;
}
