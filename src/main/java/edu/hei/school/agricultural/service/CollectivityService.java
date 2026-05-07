package edu.hei.school.agricultural.service;

import edu.hei.school.agricultural.controller.dto.CollectivityStatistic;
import edu.hei.school.agricultural.controller.dto.CollectivityTransaction;
import edu.hei.school.agricultural.controller.dto.FinancialAccount;
import edu.hei.school.agricultural.controller.dto.MemberPaymentStatistic;
import edu.hei.school.agricultural.controller.dto.CashAccount;
import edu.hei.school.agricultural.controller.mapper.MemberDtoMapper;
import edu.hei.school.agricultural.entity.Collectivity;
import edu.hei.school.agricultural.entity.Frequency;
import edu.hei.school.agricultural.entity.Member;
import edu.hei.school.agricultural.entity.MembershipFee;
import edu.hei.school.agricultural.exception.BadRequestException;
import edu.hei.school.agricultural.exception.NotFoundException;
import edu.hei.school.agricultural.repository.CollectivityRepository;
import edu.hei.school.agricultural.repository.MemberPaymentRepository;
import edu.hei.school.agricultural.repository.MemberRepository;
import edu.hei.school.agricultural.repository.MembershipFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static edu.hei.school.agricultural.entity.ActivityStatus.ACTIVE;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class CollectivityService {
    private final CollectivityRepository collectivityRepository;
    private final MembershipFeeRepository membershipFeeRepository;
    private final MemberRepository memberRepository;
    private final MemberPaymentRepository memberPaymentRepository;
    private final MemberDtoMapper memberDtoMapper;

    public List<Collectivity> createCollectivities(List<Collectivity> collectivities) {
        for (Collectivity collectivity : collectivities) {
            if (!collectivity.hasEnoughMembers()) {
                throw new BadRequestException("Collectivity must have at least 10 members, otherwise actual is " + collectivity.getMembers().size());
            }
            collectivity.setId(randomUUID().toString());
        }
        return collectivityRepository.saveAll(collectivities);
    }

    public Collectivity getCollectivityById(String id) {
        return collectivityRepository.findById(id).orElseThrow(() -> new NotFoundException("Collectivity.id= " + id + " not found"));
    }

    public Collectivity updateInformations(String collectivityId, String actualName, Integer actualNumber) {
        Collectivity collectivity = collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity.id= " + collectivityId + " not found"));
        if (actualNumber != null && collectivityRepository.isNumberExists(actualNumber)) {
            throw new BadRequestException("Collectivity.number=" + actualNumber + " already exists");
        }
        if (actualName != null && collectivityRepository.isNameExists(actualName)) {
            throw new BadRequestException("Collectivity.name=" + actualName + " already exists");
        }
        collectivity.setName(actualName);
        collectivity.setNumber(actualNumber);
        return collectivityRepository.saveAll(List.of((collectivity))).getFirst();
    }

    public List<MembershipFee> getMembershipFeesByCollectivityIdentifier(String collectivityIdentifier) {
        Collectivity collectivity = collectivityRepository.findById(collectivityIdentifier)
                .orElseThrow(() ->
                        new NotFoundException("Collectivity.id= " + collectivityIdentifier + " not found"));

        return membershipFeeRepository.getMembershipFeesByCollectivityId(collectivity.getId());
    }

    public List<MembershipFee> createMembershipFees(String collectivityIdentifier, List<MembershipFee> membershipFees) {
        Collectivity collectivity = collectivityRepository.findById(collectivityIdentifier)
                .orElseThrow(() ->
                        new NotFoundException("Collectivity.id= " + collectivityIdentifier + " not found"));
        for (MembershipFee membershipFee : membershipFees) {
            membershipFee.setId(randomUUID().toString());
            membershipFee.setStatus(ACTIVE);
            membershipFee.setCollectivityOwner(collectivity);
        }
        return membershipFeeRepository.saveAll(membershipFees);
    }

    public List<MemberPaymentStatistic> getMemberPaymentStatistics(String collectivityId, LocalDate from, LocalDate to) {
        assertValidPeriod(from, to);
        Collectivity collectivity = collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity.id= " + collectivityId + " not found"));
        List<Member> members = memberRepository.findAllByCollectivity(collectivity);
        Map<String, Double> paidAmountByMember = memberPaymentRepository.getPaidAmountByMember(collectivityId, from, to);
        Map<String, Double> paidAmountByMemberAndFee = memberPaymentRepository.getPaidAmountByMemberAndFee(collectivityId, from, to);

        return members.stream()
                .map(member -> MemberPaymentStatistic.builder()
                        .member(memberDtoMapper.mapToDto(member))
                        .paidAmount(paidAmountByMember.getOrDefault(member.getId(), 0.0))
                        .potentialUnpaidAmount(getPotentialUnpaidAmount(
                                member.getId(),
                                getActiveMembershipFeesByCollectivityIdentifier(collectivityId),
                                paidAmountByMemberAndFee,
                                from,
                                to))
                        .build())
                .toList();
    }

    public List<CollectivityStatistic> getCollectivitiesStatistics(LocalDate from, LocalDate to) {
        assertValidPeriod(from, to);
        return collectivityRepository.findAll().stream()
                .map(collectivity -> {
                    List<MemberPaymentStatistic> memberStatistics = getMemberPaymentStatistics(collectivity.getId(), from, to);
                    long upToDateMemberCount = memberStatistics.stream()
                            .filter(statistic -> statistic.getPotentialUnpaidAmount() <= 0)
                            .count();
                    double percentage = memberStatistics.isEmpty()
                            ? 100.0
                            : (upToDateMemberCount * 100.0) / memberStatistics.size();
                    return CollectivityStatistic.builder()
                            .collectivityId(collectivity.getId())
                            .collectivityName(collectivity.getName())
                            .upToDateMemberPercentage(percentage)
                            .newMembersCount(collectivityRepository.countNewMembers(
                                    collectivity.getId(),
                                    Date.valueOf(from),
                                    Date.valueOf(to)))
                            .build();
                })
                .toList();
    }

    public List<FinancialAccount> getFinancialAccounts(String collectivityId, LocalDate at) {
        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity.id= " + collectivityId + " not found"));
        return memberPaymentRepository.getCashAccountAmountsByCollectivity(collectivityId, at).entrySet().stream()
                .map(entry -> (FinancialAccount) CashAccount.builder()
                        .id(entry.getKey())
                        .amount(entry.getValue())
                        .build())
                .toList();
    }

    public List<CollectivityTransaction> getTransactions(String collectivityId, LocalDate from, LocalDate to) {
        assertValidPeriod(from, to);
        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity.id= " + collectivityId + " not found"));
        return memberPaymentRepository.findTransactionsByCollectivity(collectivityId, from, to);
    }

    private List<MembershipFee> getActiveMembershipFeesByCollectivityIdentifier(String collectivityId) {
        return membershipFeeRepository.getMembershipFeesByCollectivityId(collectivityId).stream()
                .filter(membershipFee -> ACTIVE.equals(membershipFee.getStatus()))
                .toList();
    }

    private double getPotentialUnpaidAmount(
            String memberId,
            List<MembershipFee> activeMembershipFees,
            Map<String, Double> paidAmountByMemberAndFee,
            LocalDate from,
            LocalDate to) {
        return activeMembershipFees.stream()
                .map(membershipFee -> {
                    double expectedAmount = membershipFee.getAmount() * countOccurrences(membershipFee, from, to);
                    double paidAmount = paidAmountByMemberAndFee.getOrDefault(
                            MemberPaymentRepository.key(memberId, membershipFee.getId()),
                            0.0);
                    return Math.max(expectedAmount - paidAmount, 0.0);
                })
                .reduce(0.0, Double::sum);
    }

    private long countOccurrences(MembershipFee membershipFee, LocalDate from, LocalDate to) {
        if (membershipFee.getEligibleFrom() == null || membershipFee.getEligibleFrom().isAfter(to)) {
            return 0;
        }
        LocalDate firstDate = membershipFee.getEligibleFrom().isAfter(from) ? membershipFee.getEligibleFrom() : from;
        Frequency frequency = membershipFee.getFrequency();
        if (frequency == Frequency.PUNCTUALLY) {
            return membershipFee.getEligibleFrom().isBefore(from) || membershipFee.getEligibleFrom().isAfter(to) ? 0 : 1;
        }
        if (frequency == Frequency.WEEKLY) {
            return ChronoUnit.WEEKS.between(firstDate, to) + 1;
        }
        if (frequency == Frequency.MONTHLY) {
            return ChronoUnit.MONTHS.between(firstDate.withDayOfMonth(1), to.withDayOfMonth(1)) + 1;
        }
        if (frequency == Frequency.ANNUALLY) {
            return ChronoUnit.YEARS.between(firstDate.withDayOfYear(1), to.withDayOfYear(1)) + 1;
        }
        return 0;
    }

    private void assertValidPeriod(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new BadRequestException("Both from and to query parameters are required");
        }
        if (from.isAfter(to)) {
            throw new BadRequestException("from must be before or equal to to");
        }
    }
}
