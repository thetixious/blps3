package com.blps.lab2.repo;

import com.blps.lab2.model.Cards;
import com.blps.lab2.utils.Bonus;
import com.blps.lab2.utils.CardType;
import com.blps.lab2.utils.Goal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CardRepository extends CrudRepository<Cards,Long> {

//    Set<Cards> findAllByTypeAndGoalOrBonus(CardType cardType, Goal goal, Bonus bonus);
    @Query("SELECT c FROM Cards c WHERE c.type = :type AND (c.goal = :goal OR c.bonus = :bonus)")
    Set<Cards> findAllByTypeAndGoalOrBonus(@Param("type") CardType type, @Param("goal") Goal goal, @Param("bonus") Bonus bonus);

    Set<Cards> findAllByIdIn(List<Long> cardIds);
}
