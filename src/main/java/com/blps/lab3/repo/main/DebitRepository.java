package com.blps.lab3.repo.main;

import com.blps.lab3.model.mainDB.DebitOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DebitRepository extends JpaRepository<DebitOffer, Long> {

    @Query("SELECT do FROM DebitOffer do WHERE do.user_id = :id")
    Optional<DebitOffer> findByUserId(@Param("id") Long id);
}
