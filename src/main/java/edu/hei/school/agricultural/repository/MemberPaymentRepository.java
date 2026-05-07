package edu.hei.school.agricultural.repository;

import edu.hei.school.agricultural.entity.MemberPayment;
import edu.hei.school.agricultural.mapper.MemberPaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberPaymentRepository {
    private final Connection connection;
    private final MemberPaymentMapper memberPaymentMapper;

    public List<MemberPayment> saveAll(List<MemberPayment> memberPaymentList) {
        List<MemberPayment> memberPayments = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                insert into member_payment (id, amount, creation_date, member_debited_id, membership_fee_id, payment_mode, financial_account_id)
                values (?, ?, ?, ?, ?, ?::payment_mode, ?)
                on conflict (id) do nothing
                """)) {
            for (MemberPayment memberPayment : memberPaymentList) {
                preparedStatement.setString(1, memberPayment.getId());
                preparedStatement.setDouble(2, memberPayment.getAmount());
                preparedStatement.setDate(3, Date.valueOf(memberPayment.getCreationDate()));
                preparedStatement.setString(4, memberPayment.getMemberOwner().getId());
                preparedStatement.setString(5, memberPayment.getMembershipFee().getId());
                preparedStatement.setString(6, memberPayment.getPaymentMode().name());
                preparedStatement.setString(7, memberPayment.getAccountCredited().getId());
                preparedStatement.addBatch();
            }
            var executedBatch = preparedStatement.executeBatch();
            for (int i = 0; i < executedBatch.length; i++) {
                MemberPayment memberPayment = memberPaymentList.get(i);
                memberPayments.add(findById(memberPayment.getId()).orElseThrow());
            }
            return memberPayments;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<MemberPayment> findById(String id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select id, amount, creation_date, member_debited_id, membership_fee_id, payment_mode, financial_account_id from member_payment
                where id = ?
                """)) {
            preparedStatement.setString(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return Optional.of(memberPaymentMapper.mapFromResultSet(resultSet));
            }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Double> getPaidAmountByMember(String collectivityId, LocalDate from, LocalDate to) {
        Map<String, Double> paidAmountByMember = new HashMap<>();
        try (PreparedStatement ps = connection.prepareStatement("""
                select member_payment.member_debited_id, coalesce(sum(member_payment.amount), 0) as paid_amount
                from member_payment
                    join collectivity_member on member_payment.member_debited_id = collectivity_member.member_id
                    join membership_fee on member_payment.membership_fee_id = membership_fee.id
                where collectivity_member.collectivity_id = ?
                  and membership_fee.collectivity_id = ?
                  and member_payment.creation_date between ? and ?
                group by member_payment.member_debited_id
                """)) {
            ps.setString(1, collectivityId);
            ps.setString(2, collectivityId);
            ps.setDate(3, Date.valueOf(from));
            ps.setDate(4, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                paidAmountByMember.put(rs.getString("member_debited_id"), rs.getDouble("paid_amount"));
            }
            return paidAmountByMember;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Double> getPaidAmountByMemberAndFee(String collectivityId, LocalDate from, LocalDate to) {
        Map<String, Double> paidAmountByMemberAndFee = new HashMap<>();
        try (PreparedStatement ps = connection.prepareStatement("""
                select member_payment.member_debited_id,
                       member_payment.membership_fee_id,
                       coalesce(sum(member_payment.amount), 0) as paid_amount
                from member_payment
                    join collectivity_member on member_payment.member_debited_id = collectivity_member.member_id
                    join membership_fee on member_payment.membership_fee_id = membership_fee.id
                where collectivity_member.collectivity_id = ?
                  and membership_fee.collectivity_id = ?
                  and member_payment.creation_date between ? and ?
                group by member_payment.member_debited_id, member_payment.membership_fee_id
                """)) {
            ps.setString(1, collectivityId);
            ps.setString(2, collectivityId);
            ps.setDate(3, Date.valueOf(from));
            ps.setDate(4, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                paidAmountByMemberAndFee.put(
                        key(rs.getString("member_debited_id"), rs.getString("membership_fee_id")),
                        rs.getDouble("paid_amount"));
            }
            return paidAmountByMemberAndFee;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String key(String memberId, String feeId) {
        return memberId + ":" + feeId;
    }
}
