package edu.hei.school.agricultural.repository;

import edu.hei.school.agricultural.controller.dto.Attendance;
import edu.hei.school.agricultural.controller.dto.AttendanceStatus;
import edu.hei.school.agricultural.controller.dto.MemberOccupation;
import edu.hei.school.agricultural.entity.Activity;
import edu.hei.school.agricultural.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ActivityRepository {
    private final Connection connection;
    private final MemberMapper memberMapper;

    public List<Activity> saveAll(List<Activity> activities) {
        try (PreparedStatement ps = connection.prepareStatement("""
                insert into collectivity_activity (
                    id, label, description, activity_date, mandatory, target_occupation, collectivity_id
                ) values (?, ?, ?, ?, ?, ?::member_occupation, ?)
                on conflict (id) do update set label = excluded.label,
                                              description = excluded.description,
                                              activity_date = excluded.activity_date,
                                              mandatory = excluded.mandatory,
                                              target_occupation = excluded.target_occupation,
                                              collectivity_id = excluded.collectivity_id
                """)) {
            for (Activity activity : activities) {
                ps.setString(1, activity.getId());
                ps.setString(2, activity.getLabel());
                ps.setString(3, activity.getDescription());
                ps.setDate(4, Date.valueOf(activity.getActivityDate()));
                ps.setBoolean(5, Boolean.TRUE.equals(activity.getMandatory()));
                if (activity.getTargetOccupation() == null) {
                    ps.setNull(6, Types.VARCHAR);
                } else {
                    ps.setString(6, activity.getTargetOccupation().name());
                }
                ps.setString(7, activity.getCollectivity().getId());
                ps.addBatch();
            }
            ps.executeBatch();
            List<Activity> saved = new ArrayList<>();
            for (Activity activity : activities) {
                saved.add(findById(activity.getId()).orElseThrow());
            }
            return saved;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Activity> findAllByCollectivityId(String collectivityId) {
        List<Activity> activities = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("""
                select id, label, description, activity_date, mandatory, target_occupation, collectivity_id
                from collectivity_activity
                where collectivity_id = ?
                order by activity_date, id
                """)) {
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                activities.add(mapActivity(rs));
            }
            return activities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Activity> findById(String activityId) {
        try (PreparedStatement ps = connection.prepareStatement("""
                select id, label, description, activity_date, mandatory, target_occupation, collectivity_id
                from collectivity_activity
                where id = ?
                """)) {
            ps.setString(1, activityId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapActivity(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean belongsToCollectivity(String activityId, String collectivityId) {
        try (PreparedStatement ps = connection.prepareStatement("""
                select id
                from collectivity_activity
                where id = ?
                  and collectivity_id = ?
                """)) {
            ps.setString(1, activityId);
            ps.setString(2, collectivityId);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean attendanceExists(String activityId, String memberId) {
        try (PreparedStatement ps = connection.prepareStatement("""
                select id
                from activity_attendance
                where activity_id = ?
                  and member_id = ?
                """)) {
            ps.setString(1, activityId);
            ps.setString(2, memberId);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Attendance> saveAttendance(String activityId, List<Attendance> attendances) {
        try (PreparedStatement ps = connection.prepareStatement("""
                insert into activity_attendance (id, activity_id, member_id, status)
                values (?, ?, ?, ?::attendance_status)
                """)) {
            for (Attendance attendance : attendances) {
                ps.setString(1, attendance.getId());
                ps.setString(2, activityId);
                ps.setString(3, attendance.getMember().getId());
                ps.setString(4, attendance.getStatus().name());
                ps.addBatch();
            }
            ps.executeBatch();
            return findAttendanceByActivity(activityId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Attendance> findAttendanceByActivity(String activityId) {
        List<Attendance> attendances = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("""
                select activity_attendance.id as attendance_id,
                       activity_attendance.status,
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
                from activity_attendance
                    join "member" on activity_attendance.member_id = "member".id
                where activity_attendance.activity_id = ?
                order by "member".id
                """)) {
            ps.setString(1, activityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                edu.hei.school.agricultural.entity.Member member = memberMapper.mapFromResultSet(rs);
                attendances.add(Attendance.builder()
                        .id(rs.getString("attendance_id"))
                        .status(AttendanceStatus.valueOf(rs.getString("status")))
                        .member(edu.hei.school.agricultural.controller.dto.Member.builder()
                                .id(member.getId())
                                .firstName(member.getFirstName())
                                .lastName(member.getLastName())
                                .birthDate(member.getBirthDate())
                                .gender(member.getGender() == null ? null : edu.hei.school.agricultural.controller.dto.Gender.valueOf(member.getGender().name()))
                                .occupation(member.getOccupation() == null ? null : MemberOccupation.valueOf(member.getOccupation().name()))
                                .address(member.getAddress())
                                .profession(member.getProfession())
                                .phoneNumber(member.getPhoneNumber())
                                .email(member.getEmail())
                                .build())
                        .build());
            }
            return attendances;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Double> getAttendanceRateByMember(String collectivityId, LocalDate from, LocalDate to) {
        Map<String, AttendanceCounter> countersByMember = new HashMap<>();
        try (PreparedStatement ps = connection.prepareStatement("""
                select "member".id as member_id,
                       activity_attendance.status
                from "member"
                    join collectivity_member on "member".id = collectivity_member.member_id
                    join collectivity_activity on collectivity_member.collectivity_id = collectivity_activity.collectivity_id
                    left join activity_attendance on collectivity_activity.id = activity_attendance.activity_id
                        and "member".id = activity_attendance.member_id
                where collectivity_member.collectivity_id = ?
                  and collectivity_activity.activity_date between ? and ?
                  and (
                      collectivity_activity.target_occupation is null
                      or collectivity_activity.target_occupation = "member".occupation
                  )
                """)) {
            ps.setString(1, collectivityId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AttendanceCounter counter = countersByMember.computeIfAbsent(
                        rs.getString("member_id"),
                        ignored -> new AttendanceCounter());
                counter.expected++;
                if (AttendanceStatus.PRESENT.name().equals(rs.getString("status"))) {
                    counter.present++;
                }
            }
            return mapCountersToRates(countersByMember);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public double getCollectivityAttendanceRate(String collectivityId, LocalDate from, LocalDate to) {
        Map<String, Double> attendanceRateByMember = getAttendanceRateByMember(collectivityId, from, to);
        if (attendanceRateByMember.isEmpty()) {
            return 100.0;
        }
        return attendanceRateByMember.values().stream()
                .reduce(0.0, Double::sum) / attendanceRateByMember.size();
    }

    private Activity mapActivity(ResultSet rs) throws SQLException {
        return Activity.builder()
                .id(rs.getString("id"))
                .label(rs.getString("label"))
                .description(rs.getString("description"))
                .activityDate(rs.getDate("activity_date") == null ? null : rs.getDate("activity_date").toLocalDate())
                .mandatory(rs.getBoolean("mandatory"))
                .targetOccupation(rs.getString("target_occupation") == null ? null
                        : edu.hei.school.agricultural.entity.MemberOccupation.valueOf(rs.getString("target_occupation")))
                .collectivity(edu.hei.school.agricultural.entity.Collectivity.builder()
                        .id(rs.getString("collectivity_id"))
                        .build())
                .build();
    }

    private Map<String, Double> mapCountersToRates(Map<String, AttendanceCounter> countersByMember) {
        Map<String, Double> ratesByMember = new HashMap<>();
        countersByMember.forEach((memberId, counter) -> ratesByMember.put(
                memberId,
                counter.expected == 0 ? 100.0 : (counter.present * 100.0) / counter.expected));
        return ratesByMember;
    }

    private static class AttendanceCounter {
        private int expected;
        private int present;
    }
}
