package edu.hei.school.agricultural.repository;

import edu.hei.school.agricultural.entity.Collectivity;
import edu.hei.school.agricultural.entity.CollectivityStructure;
import edu.hei.school.agricultural.mapper.CollectivityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CollectivityRepository {
    private final Connection connection;
    private final CollectivityMapper collectivityMapper;

    public List<Collectivity> saveAll(List<Collectivity> collectivities) {
        List<Collectivity> memberList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                """
                        insert into "collectivity" (id, name, number, location, president_id, vice_president_id, treasurer_id, secretary_id) 
                        values (?, ?, ?, ?, ?, ?, ?, ?) 
                        on conflict (id) do update set name = excluded.name,
                                                       number = excluded.number,
                                                       location = excluded.location,
                                                       president_id = excluded.president_id,
                                                       treasurer_id = excluded.treasurer_id,
                                                       secretary_id = excluded.secretary_id 
                        """)) {
            for (Collectivity collectivity : collectivities) {
                CollectivityStructure collectivityStructure = collectivity.getCollectivityStructure();
                preparedStatement.setString(1, collectivity.getId());
                preparedStatement.setString(2, collectivity.getName());
                Integer number = collectivity.getNumber();
                if (number == null) {
                    preparedStatement.setNull(3, Types.INTEGER);
                } else {
                    preparedStatement.setInt(3, number);
                }
                preparedStatement.setObject(4, collectivity.getLocation());
                preparedStatement.setString(5, collectivityStructure.getPresident().getId());
                preparedStatement.setString(6, collectivityStructure.getVicePresident().getId());
                preparedStatement.setString(7, collectivityStructure.getTreasurer().getId());
                preparedStatement.setString(8, collectivityStructure.getSecretary().getId());
                preparedStatement.addBatch();
            }
            var executedRow = preparedStatement.executeBatch();
            for (int i = 0; i < executedRow.length; i++) {
                memberList.add(findById(collectivities.get(i).getId()).orElseThrow());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return memberList;
    }


    public boolean isNumberExists(Integer number) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select id
                from "collectivity"
                where number = ?
                """)) {
            preparedStatement.setInt(1, number);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean isNameExists(String name) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select id
                from "collectivity"
                where name = ?
                """)) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public Optional<Collectivity> findById(String id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select id, name, number, location, president_id, vice_president_id, treasurer_id, secretary_id
                from "collectivity"
                where id = ?
                """)) {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(collectivityMapper.mapFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public List<Collectivity> findAll() {
        List<Collectivity> collectivities = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select id, name, number, location, president_id, vice_president_id, treasurer_id, secretary_id
                from "collectivity"
                order by number, id
                """)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                collectivities.add(collectivityMapper.mapFromResultSet(resultSet));
            }
            return collectivities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int countNewMembers(String collectivityId, Date from, Date to) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select count(*) as new_members_count
                from collectivity_member
                where collectivity_id = ?
                  and membership_date between ? and ?
                """)) {
            preparedStatement.setString(1, collectivityId);
            preparedStatement.setDate(2, from);
            preparedStatement.setDate(3, to);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("new_members_count");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Collectivity> findAllByMemberId(String memberId) {
        List<Collectivity> collectivities = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select id, name, number, location, president_id, vice_president_id, treasurer_id, secretary_id
                from "collectivity" 
                join "collectivity_member" on collectivity.id = collectivity_member.collectivity_id
                where collectivity_member.member_id = ?
                """)) {
            preparedStatement.setString(1, memberId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                collectivities.add(collectivityMapper.mapFromResultSet(resultSet));
            }
            return collectivities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
