package edu.hei.school.agricultural.mapper;

import edu.hei.school.agricultural.entity.*;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FinancialAccountMapper {

    public CashAccount mapCashFromResultSet(ResultSet resultSet) throws SQLException {
        return CashAccount.builder()
                .id(resultSet.getString("id"))
                .build();
    }

    public BankAccount mapBankFromResultSet(ResultSet resultSet) throws SQLException {
        return BankAccount.builder()
                .id(resultSet.getString("id"))
                .bankCode(resultSet.getInt("bank_code"))
                .branchCode(resultSet.getInt("branch_code"))
                .accountNumber(resultSet.getInt("account_number"))
                .accountKey(resultSet.getInt("key"))
                .build();
    }

    public MobileBankingAccount mapMobileBankingFromResultSet(ResultSet resultSet) throws SQLException {
        return MobileBankingAccount.builder()
                .id(resultSet.getString("id"))
                .holderName(resultSet.getString("holder_name"))
                .mobileNumber(resultSet.getString("mobile_number"))
                .mobileBankingService(resultSet.getString("service") == null ? null : MobileBankingService.valueOf(resultSet.getString("service")))
                .build();
    }

    public Transaction mapTransactionFromResultSet(ResultSet resultSet, Member member) throws SQLException {
        return Transaction.builder()
                .id(resultSet.getString("id"))
                .amount(resultSet.getDouble("amount"))
                .creationDate(resultSet.getDate("creation_date").toLocalDate())
                .type(TransactionType.valueOf(resultSet.getString("transaction_type")))
                .memberDebited(member)
                .build();
    }
}
