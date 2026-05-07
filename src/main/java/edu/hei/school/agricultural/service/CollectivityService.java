package edu.hei.school.agricultural.service;

import edu.hei.school.agricultural.controller.dto.Activity;
import edu.hei.school.agricultural.controller.dto.Attendance;
import edu.hei.school.agricultural.controller.dto.AttendanceStatus;
import edu.hei.school.agricultural.controller.dto.CollectivityStatistic;
import edu.hei.school.agricultural.controller.dto.CreateActivity;
import edu.hei.school.agricultural.controller.dto.CreateAttendance;
import edu.hei.school.agricultural.controller.dto.MemberPaymentStatistic;
import edu.hei.school.agricultural.controller.mapper.MemberDtoMapper;
import edu.hei.school.agricultural.entity.*;
import edu.hei.school.agricultural.exception.BadRequestException;
import edu.hei.school.agricultural.exception.NotFoundException;
import edu.hei.school.agricultural.repository.ActivityRepository;
import edu.hei.school.agricultural.repository.CollectivityRepository;
import edu.hei.school.agricultural.repository.FinancialAccountRepository;
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
import java.util.stream.Stream;

import static edu.hei.school.agricultural.entity.ActivityStatus.ACTIVE;
import static edu.hei.school.agricultural.entity.PaymentMode.*;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class CollectivityService {
    private final CollectivityRepository collectivityRepository;
    private final MembershipFeeRepository membershipFeeRepository;
    private final FinancialAccountRepository financialAccountRepository;
    private final MemberRepository memberRepository;
    private final MemberPaymentRepository memberPaymentRepository;
    private final MemberDtoMapper memberDtoMapper;
    private final ActivityRepository activityRepository;

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

    public List<FinancialAccount> getFinancialAccounts(String collectivityIdentifier) {
        Collectivity collectivity = collectivityRepository.findById(collectivityIdentifier)
                .orElseThrow(() ->
                        new NotFoundException("Collectivity.id= " + collectivityIdentifier + " not found"));

        CashAccount cashAccount = financialAccountRepository.getCashAccountByCollectivityId(collectivity.getId());
        List<BankAccount> bankAccounts = financialAccountRepository.getBankAccountsByCollectivityId(collectivity.getId());
        List<MobileBankingAccount> mobileBankingAccountsByCollectivityId = financialAccountRepository.getMobileBankingAccountsByCollectivityId(collectivity.getId());

        return Stream.concat(
                Stream.concat(
                        Stream.of(cashAccount),
                        bankAccounts.stream()),
                mobileBankingAccountsByCollectivityId.stream()
        ).toList();
    }

    public List<CollectivityTransaction> getTransactionsByCollectivity(String collectivityIdentifier, LocalDate from, LocalDate to) {
        assertValidPeriod(from, to);
        List<FinancialAccount> financialAccounts = getFinancialAccounts(collectivityIdentifier);

        return financialAccounts.stream()
                .map(financialAccount -> {
                    var transactionList = financialAccount.getTransactions().stream()
                            .filter(transaction -> (transaction.getCreationDate().isAfter(from) || transaction.getCreationDate().equals(from))
                                    && (transaction.getCreationDate().isBefore(to) || transaction.getCreationDate().equals(to)))
                            .toList();
                    var paymentMode = getPaymentMode(financialAccount);
                    return transactionList.stream()
                            .map(transaction -> {
                                CollectivityTransaction collectivityTransaction = CollectivityTransaction.builder()
                                        .id(transaction.getId())
                                        .type(transaction.getType())
                                        .amount(transaction.getAmount())
                                        .creationDate(transaction.getCreationDate())
                                        .accountCredited(financialAccount)
                                        .paymentMode(paymentMode)
                                        .memberDebited(transaction.getMemberDebited())
                                        .build();
                                return collectivityTransaction;
                            })
                            .toList();
                })
                .flatMap(List::stream)
                .toList();
    }

    public List<MemberPaymentStatistic> getMemberPaymentStatistics(String collectivityId, LocalDate from, LocalDate to) {
        assertValidPeriod(from, to);
        Collectivity collectivity = collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity.id= " + collectivityId + " not found"));
        List<Member> members = memberRepository.findAllByCollectivity(collectivity);
        Map<String, Double> paidAmountByMember = memberPaymentRepository.getPaidAmountByMember(collectivityId, from, to);
        Map<String, Double> paidAmountByMemberAndFee = memberPaymentRepository.getPaidAmountByMemberAndFee(collectivityId, from, to);
        Map<String, Double> attendanceRateByMember = activityRepository.getAttendanceRateByMember(collectivityId, from, to);
        List<MembershipFee> activeMembershipFees = getActiveMembershipFeesByCollectivityIdentifier(collectivityId);

        return members.stream()
                .map(member -> MemberPaymentStatistic.builder()
                        .member(memberDtoMapper.mapToDto(member))
                        .paidAmount(paidAmountByMember.getOrDefault(member.getId(), 0.0))
                        .potentialUnpaidAmount(getPotentialUnpaidAmount(
                                member.getId(),
                                activeMembershipFees,
                                paidAmountByMemberAndFee,
                                from,
                                to))
                        .attendanceRate(attendanceRateByMember.getOrDefault(member.getId(), 100.0))
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
                    double upToDatePercentage = memberStatistics.isEmpty()
                            ? 100.0
                            : (upToDateMemberCount * 100.0) / memberStatistics.size();
                    return CollectivityStatistic.builder()
                            .collectivityId(collectivity.getId())
                            .collectivityName(collectivity.getName())
                            .upToDateMemberPercentage(upToDatePercentage)
                            .newMembersCount(collectivityRepository.countNewMembers(
                                    collectivity.getId(),
                                    Date.valueOf(from),
                                    Date.valueOf(to)))
                            .attendanceRate(activityRepository.getCollectivityAttendanceRate(collectivity.getId(), from, to))
                            .build();
                })
                .toList();
    }

    public List<Activity> createActivities(String collectivityId, List<CreateActivity> createActivities) {
        Collectivity collectivity = collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity.id= " + collectivityId + " not found"));
        List<edu.hei.school.agricultural.entity.Activity> activities = createActivities.stream()
                .map(createActivity -> {
                    if (createActivity.getLabel() == null || createActivity.getActivityDate() == null) {
                        throw new BadRequestException("Activity label and activityDate are required");
                    }
                    return edu.hei.school.agricultural.entity.Activity.builder()
                            .id(randomUUID().toString())
                            .label(createActivity.getLabel())
                            .description(createActivity.getDescription())
                            .activityDate(createActivity.getActivityDate())
                            .mandatory(Boolean.TRUE.equals(createActivity.getMandatory()))
                            .targetOccupation(createActivity.getTargetOccupation() == null ? null
                                    : MemberOccupation.valueOf(createActivity.getTargetOccupation().name()))
                            .collectivity(collectivity)
                            .build();
                })
                .toList();
        return activityRepository.saveAll(activities).stream()
                .map(this::mapActivityToDto)
                .toList();
    }

    public List<Activity> getActivities(String collectivityId) {
        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity.id= " + collectivityId + " not found"));
        return activityRepository.findAllByCollectivityId(collectivityId).stream()
                .map(this::mapActivityToDto)
                .toList();
    }

    public List<Attendance> createAttendance(String collectivityId, String activityId, List<CreateAttendance> createAttendances) {
        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity.id= " + collectivityId + " not found"));
        if (!activityRepository.belongsToCollectivity(activityId, collectivityId)) {
            throw new NotFoundException("Activity.id=" + activityId + " not found for collectivity.id=" + collectivityId);
        }
        List<Attendance> attendances = createAttendances.stream()
                .map(createAttendance -> {
                    if (!memberRepository.belongsToCollectivity(createAttendance.getMemberIdentifier(), collectivityId)) {
                        throw new BadRequestException("Member.id=" + createAttendance.getMemberIdentifier()
                                + " does not belong to collectivity.id=" + collectivityId);
                    }
                    if (activityRepository.attendanceExists(activityId, createAttendance.getMemberIdentifier())) {
                        throw new BadRequestException("Attendance for member.id="
                                + createAttendance.getMemberIdentifier() + " already exists");
                    }
                    Member member = memberRepository
                            .findById(createAttendance.getMemberIdentifier())
                            .orElseThrow(() -> new NotFoundException("Member.id="
                                    + createAttendance.getMemberIdentifier() + " not found"));
                    return Attendance.builder()
                            .id(randomUUID().toString())
                            .status(createAttendance.getStatus() == null ? AttendanceStatus.ABSENT : createAttendance.getStatus())
                            .member(memberDtoMapper.mapToDto(member))
                            .build();
                })
                .toList();
        return activityRepository.saveAttendance(activityId, attendances);
    }

    public List<Attendance> getPresentAttendance(String collectivityId, String activityId) {
        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity.id= " + collectivityId + " not found"));
        if (!activityRepository.belongsToCollectivity(activityId, collectivityId)) {
            throw new NotFoundException("Activity.id=" + activityId + " not found for collectivity.id=" + collectivityId);
        }
        return activityRepository.findAttendanceByActivity(activityId).stream()
                .filter(attendance -> AttendanceStatus.PRESENT.equals(attendance.getStatus()))
                .toList();
    }

    private PaymentMode getPaymentMode(FinancialAccount financialAccount) {
        PaymentMode paymentMode;
        paymentMode = switch (financialAccount) {
            case BankAccount ignored -> BANK_TRANSFER;
            case MobileBankingAccount ignored -> MOBILE_BANKING;
            case CashAccount ignored -> CASH;
            default ->
                    throw new IllegalArgumentException("Unknown financial account type " + financialAccount.getClass().getTypeName());
        };
        return paymentMode;
    }

    private Activity mapActivityToDto(edu.hei.school.agricultural.entity.Activity activity) {
        return Activity.builder()
                .id(activity.getId())
                .label(activity.getLabel())
                .description(activity.getDescription())
                .activityDate(activity.getActivityDate())
                .mandatory(activity.getMandatory())
                .targetOccupation(activity.getTargetOccupation() == null ? null
                        : edu.hei.school.agricultural.controller.dto.MemberOccupation.valueOf(activity.getTargetOccupation().name()))
                .build();
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
