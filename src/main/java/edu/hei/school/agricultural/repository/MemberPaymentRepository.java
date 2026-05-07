package edu.hei.school.agricultural.repository;

import edu.hei.school.agricultural.controller.dto.CashAccount;
import edu.hei.school.agricultural.controller.dto.CollectivityTransaction;
import edu.hei.school.agricultural.controller.dto.MemberPayment;
import edu.hei.school.agricultural.controller.dto.PaymentMode;
import edu.hei.school.agricultural.entity.Member;
import edu.hei.school.agricultural.entity.MembershipFee;
import edu.hei.school.agricultural.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberPaymentRepository {
    private final Connection connection;
    private final MemberMapper memberMapper;

    public List<edu.hei.school.agricultural.entity.MemberPayment> saveAll(
            List<edu.hei.school.agricultural.entity.MemberPayment> payments) {
        try (PreparedStatement ps = connection.prepareStatement("""
                insert into member_payment (
                    id, amount, payment_mode, account_credited_identifier, creation_date, member_id, membership_fee_id
                ) values (?, ?, ?::payment_mode, ?, ?, ?, ?)
                on conflict (id) do update set amount = excluded.amount,
                                              payment_mode = excluded.payment_mode,
                                              account_credited_identifier = excluded.account_credited_identifier,
                                              creation_date = excluded.creation_date,
                                              member_id = excluded.member_id,
                                              membership_fee_id = excluded.membership_fee_id
                """)) {
            for (edu.hei.school.agricultural.entity.MemberPayment payment : payments) {
                ps.setString(1, payment.getId());
                ps.setInt(2, payment.getAmount());
                ps.setString(3, payment.getPaymentMode().name());
                ps.setString(4, payment.getAccountCreditedIdentifier());
                ps.setDate(5, Date.valueOf(payment.getCreationDate()));
                ps.setString(6, payment.getMember().getId());
                ps.setString(7, payment.getMembershipFee().getId());
                ps.addBatch();
            }
            ps.executeBatch();
            List<edu.hei.school.agricultural.entity.MemberPayment> saved = new ArrayList<>();
            for (edu.hei.school.agricultural.entity.MemberPayment payment : payments) {
                saved.add(findById(payment.getId()).orElseThrow());
            }
            return saved;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<edu.hei.school.agricultural.entity.MemberPayment> findById(String id) {
        try (PreparedStatement ps = connection.prepareStatement("""
                select member_payment.id,
                       member_payment.amount,
                       payment_mode,
                       account_credited_identifier,
                       creation_date,
                       member_id,
                       membership_fee_id
                from member_payment
                where member_payment.id = ?
                """)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapEntityPayment(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Double> getPaidAmountByMember(String collectivityId, LocalDate from, LocalDate to) {
        Map<String, Double> paidAmountByMember = new HashMap<>();
        try (PreparedStatement ps = connection.prepareStatement("""
                select member_payment.member_id, coalesce(sum(member_payment.amount), 0) as paid_amount
                from member_payment
                    join collectivity_member on member_payment.member_id = collectivity_member.member_id
                    join membership_fee on member_payment.membership_fee_id = membership_fee.id
                where collectivity_member.collectivity_id = ?
                  and membership_fee.collectivity_id = ?
                  and member_payment.creation_date between ? and ?
                group by member_payment.member_id
                """)) {
            ps.setString(1, collectivityId);
            ps.setString(2, collectivityId);
            ps.setDate(3, Date.valueOf(from));
            ps.setDate(4, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                paidAmountByMember.put(rs.getString("member_id"), rs.getDouble("paid_amount"));
            }
            return paidAmountByMember;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Double> getPaidAmountByMemberAndFee(String collectivityId, LocalDate from, LocalDate to) {
        Map<String, Double> paidAmountByMemberAndFee = new HashMap<>();
        try (PreparedStatement ps = connection.prepareStatement("""
                select member_payment.member_id,
                       member_payment.membership_fee_id,
                       coalesce(sum(member_payment.amount), 0) as paid_amount
                from member_payment
                    join collectivity_member on member_payment.member_id = collectivity_member.member_id
                    join membership_fee on member_payment.membership_fee_id = membership_fee.id
                where collectivity_member.collectivity_id = ?
                  and membership_fee.collectivity_id = ?
                  and member_payment.creation_date between ? and ?
                group by member_payment.member_id, member_payment.membership_fee_id
                """)) {
            ps.setString(1, collectivityId);
            ps.setString(2, collectivityId);
            ps.setDate(3, Date.valueOf(from));
            ps.setDate(4, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                paidAmountByMemberAndFee.put(
                        key(rs.getString("member_id"), rs.getString("membership_fee_id")),
                        rs.getDouble("paid_amount"));
            }
            return paidAmountByMemberAndFee;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CollectivityTransaction> findTransactionsByCollectivity(String collectivityId, LocalDate from, LocalDate to) {
        List<CollectivityTransaction> transactions = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("""
                select member_payment.id as payment_id,
                       member_payment.amount,
                       payment_mode,
                       account_credited_identifier,
                       creation_date,
                       "member".id,
                       first_name,
                       last_name,
                       birth_date,
                       gender,
                       phone_number,
                       email,
                       address,
                       profession,
                       occupation,
                       registration_fee_paid,
                       membership_dues_paid
                from member_payment
                    join "member" on member_payment.member_id = "member".id
                    join collectivity_member on "member".id = collectivity_member.member_id
                    join membership_fee on member_payment.membership_fee_id = membership_fee.id
                where collectivity_member.collectivity_id = ?
                  and membership_fee.collectivity_id = ?
                  and member_payment.creation_date between ? and ?
                order by member_payment.creation_date, member_payment.id
                """)) {
            ps.setString(1, collectivityId);
            ps.setString(2, collectivityId);
            ps.setDate(3, Date.valueOf(from));
            ps.setDate(4, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Member member = memberMapper.mapFromResultSet(rs);
                transactions.add(CollectivityTransaction.builder()
                        .id(rs.getString("payment_id"))
                        .amount(rs.getDouble("amount"))
                        .paymentMode(PaymentMode.valueOf(rs.getString("payment_mode")))
                        .creationDate(rs.getDate("creation_date").toLocalDate())
                        .accountCredited(CashAccount.builder()
                                .id(rs.getString("account_credited_identifier"))
                                .amount(rs.getInt("amount"))
                                .build())
                        .memberDebited(edu.hei.school.agricultural.controller.dto.Member.builder()
                                .id(member.getId())
                                .firstName(member.getFirstName())
                                .lastName(member.getLastName())
                                .birthDate(member.getBirthDate())
                                .build())
                        .build());
            }
            return transactions;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Integer> getCashAccountAmountsByCollectivity(String collectivityId, LocalDate at) {
        Map<String, Integer> amountByAccount = new HashMap<>();
        String query = at == null
                ? """
                select account_credited_identifier, coalesce(sum(member_payment.amount), 0) as amount
                from member_payment
                    join collectivity_member on member_payment.member_id = collectivity_member.member_id
                    join membership_fee on member_payment.membership_fee_id = membership_fee.id
                where collectivity_member.collectivity_id = ?
                  and membership_fee.collectivity_id = ?
                group by account_credited_identifier
                """
                : """
                select account_credited_identifier, coalesce(sum(member_payment.amount), 0) as amount
                from member_payment
                    join collectivity_member on member_payment.member_id = collectivity_member.member_id
                    join membership_fee on member_payment.membership_fee_id = membership_fee.id
                where collectivity_member.collectivity_id = ?
                  and membership_fee.collectivity_id = ?
                  and member_payment.creation_date <= ?
                group by account_credited_identifier
                """;
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, collectivityId);
            ps.setString(2, collectivityId);
            if (at != null) {
                ps.setDate(3, Date.valueOf(at));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                amountByAccount.put(rs.getString("account_credited_identifier"), rs.getInt("amount"));
            }
            return amountByAccount;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public MemberPayment mapToDto(edu.hei.school.agricultural.entity.MemberPayment payment) {
        return MemberPayment.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .paymentMode(payment.getPaymentMode())
                .creationDate(payment.getCreationDate())
                .accountCredited(CashAccount.builder()
                        .id(payment.getAccountCreditedIdentifier())
                        .amount(payment.getAmount())
                        .build())
                .build();
    }

    public static String key(String memberId, String feeId) {
        return memberId + ":" + feeId;
    }

    private edu.hei.school.agricultural.entity.MemberPayment mapEntityPayment(ResultSet rs) throws SQLException {
        return edu.hei.school.agricultural.entity.MemberPayment.builder()
                .id(rs.getString("id"))
                .amount(rs.getInt("amount"))
                .paymentMode(PaymentMode.valueOf(rs.getString("payment_mode")))
                .accountCreditedIdentifier(rs.getString("account_credited_identifier"))
                .creationDate(rs.getDate("creation_date").toLocalDate())
                .member(Member.builder().id(rs.getString("member_id")).build())
                .membershipFee(MembershipFee.builder().id(rs.getString("membership_fee_id")).build())
                .build();
    }
}
