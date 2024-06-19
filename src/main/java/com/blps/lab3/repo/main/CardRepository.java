package com.blps.lab3.repo.main;

import com.blps.lab3.model.mainDB.Cards;
import com.blps.lab3.utils.Bonus;
import com.blps.lab3.utils.CardType;
import com.blps.lab3.utils.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface CardRepository extends JpaRepository<Cards,Long> {

//    Set<Cards> findAllByTypeAndGoalOrBonus(CardType cardType, Goal goal, Bonus bonus);
    @Query("SELECT c FROM Cards c WHERE c.type = :type AND (c.goal = :goal OR c.bonus = :bonus)")
    Set<Cards> findAllByTypeAndGoalOrBonus(@Param("type") CardType type, @Param("goal") Goal goal, @Param("bonus") Bonus bonus);

    Set<Cards> findAllByIdIn(List<Long> cardIds);
}
