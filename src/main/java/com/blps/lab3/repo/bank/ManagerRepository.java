package com.blps.lab3.repo.bank;

import com.blps.lab3.model.bankDB.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager,Long> {

    Optional<Manager> findFirstByStatusFalse();
    @Modifying
    @Query("UPDATE Manager m SET m.data = :data WHERE m.id = :managerId")
    void updateUserNameById(@Param("managerId") Long managerId,@Param("data") String data);
}
