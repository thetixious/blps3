package com.blps.lab2.repo;

import com.blps.lab2.model.CreditOffer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface CreditRepository extends CrudRepository<CreditOffer,Long> {

    @Query("from CreditOffer co where co.card_user.id = :id")
    CreditOffer findByUserId(@Param("id") Long id);

}
