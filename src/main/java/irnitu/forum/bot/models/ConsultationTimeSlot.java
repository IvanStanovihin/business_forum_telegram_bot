package irnitu.forum.bot.models;

import lombok.Data;


import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity(name = "consultation_time_slots")
public class ConsultationTimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDateTime startConsultationTime;
    private LocalDateTime endConsultationTime;

    @ManyToOne
    @JoinColumn(name = "expert_id")
    private BusinessExpert expert;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;
}
