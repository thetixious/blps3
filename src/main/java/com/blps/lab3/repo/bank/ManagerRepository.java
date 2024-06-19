package com.blps.lab3.repo.bank;

import com.blps.lab3.model.bankDB.Manager;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

    Optional<Manager> findFirstByStatusFalse();

}
