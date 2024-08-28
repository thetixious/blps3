package com.blps.lab3.repo.main;

import com.blps.lab3.model.mainDB.CreditOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditRepository extends JpaRepository<CreditOffer,Long> {

    @Query("from CreditOffer co where co.user_id = :id")
    Optional<CreditOffer> findByUserId(@Param("id") Long id);

}
