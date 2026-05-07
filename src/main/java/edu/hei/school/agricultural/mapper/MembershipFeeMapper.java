package edu.hei.school.agricultural.mapper;

import edu.hei.school.agricultural.entity.ActivityStatus;
import edu.hei.school.agricultural.entity.Frequency;
import edu.hei.school.agricultural.entity.MembershipFee;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MembershipFeeMapper {
    public MembershipFee mapFromResultSet(ResultSet rs) throws SQLException {
        return MembershipFee.builder()
                .id(rs.getString("id"))
                .label(rs.getString("label"))
                .amount(rs.getDouble("amount"))
                .frequency(rs.getString("frequency") == null ? null : Frequency.valueOf(rs.getString("frequency")))
                .eligibleFrom(rs.getDate("eligible_from") == null ? null : rs.getDate("eligible_from").toLocalDate())
                .status(rs.getString("status") == null ? null : ActivityStatus.valueOf(rs.getString("status")))
                .build();
    }
}
