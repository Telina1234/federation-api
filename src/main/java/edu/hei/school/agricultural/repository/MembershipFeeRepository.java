package edu.hei.school.agricultural.repository;

import edu.hei.school.agricultural.entity.MembershipFee;
import edu.hei.school.agricultural.mapper.MembershipFeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MembershipFeeRepository {
    private final Connection connection;
    private final MembershipFeeMapper membershipFeeMapper;

    public List<MembershipFee> getMembershipFeesByCollectivityId(String collectivityId) {
        List<MembershipFee> membershipFees = new ArrayList<MembershipFee>();
        try (PreparedStatement ps = connection.prepareStatement("""
                select id, label, amount, frequency, status, eligible_from
                from membership_fee where collectivity_id = ?
                """)) {
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MembershipFee membershipFee = membershipFeeMapper.mapFromResultSet(rs);
                membershipFees.add(membershipFee);
            }
            return membershipFees;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MembershipFee> saveAll(List<MembershipFee> membershipFees) {
        List<MembershipFee> membershipFeeList = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                """
                        insert into membership_fee (id, label, amount, eligible_from, status, frequency, collectivity_id)
                        values (?, ?, ?, ?, ?::activity_status, ?::frequency,?) on conflict (id)
                            do update set label=excluded.label,
                                          amount=excluded.amount,
                                          eligible_from=excluded.eligible_from,
                                          status=excluded.status,
                                          frequency=excluded.frequency
                        """)) {
            for (MembershipFee membershipFee : membershipFees) {
                ps.setString(1, membershipFee.getId());
                ps.setString(2, membershipFee.getLabel());
                ps.setDouble(3, membershipFee.getAmount());
                ps.setDate(4, Date.valueOf(membershipFee.getEligibleFrom()));
                ps.setString(5, membershipFee.getStatus().name());
                ps.setString(6, membershipFee.getFrequency().name());
                ps.setString(7, membershipFee.getCollectivityOwner().getId());
                ps.addBatch();
            }
            var executeBatch = ps.executeBatch();
            for (int i = 0; i < executeBatch.length; i++) {
                MembershipFee membershipFee = membershipFees.get(i);
                membershipFeeList.add(findById(membershipFee.getId()).orElseThrow());
            }
            return membershipFeeList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<MembershipFee> findById(String id) {
        try (PreparedStatement ps = connection.prepareStatement("""
                select id, label, amount, frequency, status, eligible_from from membership_fee where id = ?
                """)) {
            ps.setString(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                MembershipFee membershipFee = membershipFeeMapper.mapFromResultSet(resultSet);
                return Optional.of(membershipFee);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
