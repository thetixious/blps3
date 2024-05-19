package com.blps.lab2.repo;

import com.blps.lab2.model.DebitOffer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface DebitRepository extends CrudRepository<DebitOffer, Long> {

    @Query("from DebitOffer do where do.card_user.id = :id")
    DebitOffer findByUserId(@Param("id") Long id);
}
