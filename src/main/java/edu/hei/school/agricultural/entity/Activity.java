package edu.hei.school.agricultural.entity;

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
public class Activity {
    private String id;
    private String label;
    private String description;
    private LocalDate activityDate;
    private Boolean mandatory;
    private MemberOccupation targetOccupation;
    private Collectivity collectivity;
}
