package edu.hei.school.agricultural.mapper;

import edu.hei.school.agricultural.entity.MemberPayment;
import edu.hei.school.agricultural.entity.PaymentMode;
import edu.hei.school.agricultural.repository.FinancialAccountRepository;
import edu.hei.school.agricultural.repository.MemberRepository;
import edu.hei.school.agricultural.repository.MembershipFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class MemberPaymentMapper {
    private final MembershipFeeRepository membershipFeeRepository;
    private final MemberRepository memberRepository;
    private final FinancialAccountRepository financialAccountRepository;

    public MemberPayment mapFromResultSet(ResultSet resultSet) throws SQLException {
        return MemberPayment.builder()
                .id(resultSet.getString("id"))
                .amount(resultSet.getDouble("amount"))
                .creationDate(resultSet.getDate("creation_date").toLocalDate())
                .paymentMode(PaymentMode.valueOf(resultSet.getString("payment_mode")))
                .memberOwner(memberRepository.findById(resultSet.getString("member_debited_id")).orElseThrow())
                .accountCredited(financialAccountRepository.findFinancialAccountById(resultSet.getString("financial_account_id")).orElseThrow())
                .membershipFee(membershipFeeRepository.findById(resultSet.getString("membership_fee_id")).orElseThrow())
                .build();
    }
}
