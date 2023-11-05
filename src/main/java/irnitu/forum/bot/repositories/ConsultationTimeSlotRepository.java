package irnitu.forum.bot.repositories;

import irnitu.forum.bot.models.ConsultationTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationTimeSlotRepository extends JpaRepository<ConsultationTimeSlot, Long> {

    @Query(value = "SELECT * FROM consultation_time_slots WHERE expert_id = ?1 AND " +
            "student_id IS NULL",
            nativeQuery = true)
    List<ConsultationTimeSlot> findByExpertWithNullStudent(Long expertId);
}
