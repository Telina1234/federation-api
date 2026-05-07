package edu.hei.school.agricultural.repository;

import edu.hei.school.agricultural.entity.Member;
import edu.hei.school.agricultural.entity.Transaction;
import edu.hei.school.agricultural.mapper.FinancialAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TransactionRepository {
    private final Connection connection;
    private final MemberRepository memberRepository;
    private final FinancialAccountMapper financialAccountMapper;
    private final FinancialAccountRepository financialAccountRepository;

    public List<Transaction> saveAll(List<Transaction> transactionList) {
        List<Transaction> savedTransactions = new ArrayList<Transaction>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                insert into "transaction" (id, amount, creation_date, transaction_type, financial_account_id, member_debited_id)
                values (?, ?, ?, ?::transaction_type, ?, ?)
                """)) {
            for (Transaction transaction : transactionList) {
                preparedStatement.setString(1, transaction.getId());
                preparedStatement.setDouble(2, transaction.getAmount());
                preparedStatement.setDate(3, Date.valueOf(transaction.getCreationDate()));
                preparedStatement.setString(4, transaction.getType().name());
                preparedStatement.setString(5, transaction.getAccountCredited().getId());
                preparedStatement.setString(6, transaction.getMemberDebited().getId());
                preparedStatement.addBatch();
            }
            var executedBatch = preparedStatement.executeBatch();
            for (int i = 0; i < executedBatch.length; i++) {
                Transaction transaction = transactionList.get(i);
                savedTransactions.add(findById(transaction.getId()).orElseThrow());
            }
            return savedTransactions;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Transaction> findById(String id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select id, amount, creation_date, transaction_type, member_debited_id, financial_account_id from "transaction"
                where id=?
                """)) {
            preparedStatement.setString(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Member memberDebited = memberRepository.findById(resultSet.getString("member_debited_id")).orElseThrow();
                    Transaction transaction = financialAccountMapper.mapTransactionFromResultSet(resultSet, memberDebited);
                    return Optional.of(transaction);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
