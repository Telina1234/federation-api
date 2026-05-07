package edu.hei.school.agricultural.repository;

import edu.hei.school.agricultural.entity.*;
import edu.hei.school.agricultural.exception.InternalServerErrorException;
import edu.hei.school.agricultural.mapper.FinancialAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FinancialAccountRepository {
    private final Connection connection;
    private final FinancialAccountMapper financialAccountMapper;
    private final MemberRepository memberRepository;

    public Optional<FinancialAccount> findFinancialAccountById(String financialAccountId) {
        Optional<CashAccount> optionalCashAccount = findCashAccountById(financialAccountId);
        if (optionalCashAccount.isPresent()) {
            return Optional.of(optionalCashAccount.get());
        } else {
            Optional<BankAccount> optionalBankAccount = findBankAccountById(financialAccountId);
            if (optionalBankAccount.isPresent()) {
                return Optional.of(optionalBankAccount.get());
            }
            Optional<MobileBankingAccount> optionalMobileBankingAccount = findMobileBankingAccountById(financialAccountId);
            if (optionalMobileBankingAccount.isPresent()) {
                return Optional.of(optionalMobileBankingAccount.get());
            }
        }
        return Optional.empty();
    }

    public List<BankAccount> getBankAccountsByCollectivityId(String collectivityId) {
        List<BankAccount> bankAccounts = new ArrayList<BankAccount>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select id, holder_name, bank_name, bank_code, branch_code, account_number, key from "bank_account" where collectivity_id = ?
                """)) {
            preparedStatement.setString(1, collectivityId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                BankAccount bankAccount = financialAccountMapper.mapBankFromResultSet(resultSet);
                List<Transaction> transactionList = getTransactionsByFinancialAccountId(bankAccount.getId());
                bankAccount.addTransactions(transactionList);
                bankAccounts.add(bankAccount);
            }
            return bankAccounts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MobileBankingAccount> getMobileBankingAccountsByCollectivityId(String collectivityId) {
        List<MobileBankingAccount> mobileBankingAccounts = new ArrayList<MobileBankingAccount>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select id, holder_name, service, mobile_number from "mobile_banking_account" where collectivity_id = ?
                """)) {
            preparedStatement.setString(1, collectivityId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                MobileBankingAccount mobileBankingAccount = financialAccountMapper.mapMobileBankingFromResultSet(resultSet);
                List<Transaction> transactionList = getTransactionsByFinancialAccountId(mobileBankingAccount.getId());
                mobileBankingAccount.addTransactions(transactionList);
                mobileBankingAccounts.add(mobileBankingAccount);
            }
            return mobileBankingAccounts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CashAccount getCashAccountByCollectivityId(String collectivityId) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select id from "cash_account" where collectivity_id = ?
                """)) {
            preparedStatement.setString(1, collectivityId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                CashAccount cashAccount = financialAccountMapper.mapCashFromResultSet(resultSet);
                List<Transaction> transactionList = getTransactionsByFinancialAccountId(cashAccount.getId());
                cashAccount.addTransactions(transactionList);
                return cashAccount;
            }
            throw new InternalServerErrorException("Unable to retrieve cash account for collectivity.id= " + collectivityId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Transaction> getTransactionsByFinancialAccountId(String financialAccountId) {
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                """
                        select id, amount, creation_date, transaction_type, member_debited_id from "transaction" where financial_account_id = ?
                        """
        )) {
            preparedStatement.setString(1, financialAccountId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Member memberDebited = memberRepository.findById(resultSet.getString("member_debited_id")).orElseThrow();
                Transaction transaction = financialAccountMapper.mapTransactionFromResultSet(resultSet, memberDebited);
                transactions.add(transaction);
            }
            return transactions;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<CashAccount> findCashAccountById(String id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select id from "cash_account" where id = ?
                """)) {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                CashAccount cashAccount = financialAccountMapper.mapCashFromResultSet(resultSet);
                List<Transaction> transactionList = getTransactionsByFinancialAccountId(cashAccount.getId());
                cashAccount.addTransactions(transactionList);
                return Optional.of(cashAccount);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<BankAccount> findBankAccountById(String id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select id, holder_name, bank_name, bank_code, branch_code, account_number, key from "bank_account" where id = ?
                """)) {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                BankAccount account = financialAccountMapper.mapBankFromResultSet(resultSet);
                List<Transaction> transactionList = getTransactionsByFinancialAccountId(account.getId());
                account.addTransactions(transactionList);
                return Optional.of(account);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<MobileBankingAccount> findMobileBankingAccountById(String id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select id, holder_name, service, mobile_number from "mobile_banking_account" where id = ?
                """)) {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                MobileBankingAccount account = financialAccountMapper.mapMobileBankingFromResultSet(resultSet);
                List<Transaction> transactionList = getTransactionsByFinancialAccountId(account.getId());
                account.addTransactions(transactionList);
                return Optional.of(account);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
