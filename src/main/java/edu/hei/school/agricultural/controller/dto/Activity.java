package edu.hei.school.agricultural.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Activity {
    private String id;
    private String label;
    private String description;
    private LocalDate activityDate;
    private Boolean mandatory;
    private MemberOccupation targetOccupation;
}
