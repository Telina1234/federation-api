package edu.hei.school.agricultural.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CollectivityStatistic {
    private String collectivityId;
    private String collectivityName;
    private Double upToDateMemberPercentage;
    private Integer newMembersCount;
    private Double attendanceRate;
}
