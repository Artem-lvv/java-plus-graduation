package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.model.UserAction;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
    @Query("SELECT usact FROM UserAction usact WHERE usact.userId = :userId ORDER BY usact.timestamp DESC")
    List<UserAction> findRecentInteractionsByUser(@Param("userId") int userId);

    @Query("SELECT CASE WHEN COUNT(usact) > 0 THEN true ELSE false END FROM UserAction usact " +
            "WHERE usact.userId = :userId AND usact.eventId = :eventId")
    boolean hasUserInteractedWithEvent(@Param("userId") int userId,@Param("eventId") int eventId);

    @Query("SELECT usact.eventId FROM UserAction usact " +
            "WHERE usact.userId = :userId")
    List<Integer> findInteractedEventsByUser(@Param("userId") int userId);

    @Query("SELECT COUNT(usact) FROM UserAction usact " +
            "WHERE usact.eventId = :eventId")
    int countInteractionsForEvent(@Param("eventId") int eventId);
}