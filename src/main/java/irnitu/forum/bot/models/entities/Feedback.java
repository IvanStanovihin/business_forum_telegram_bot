package irnitu.forum.bot.models.entities;


import lombok.Data;


import javax.persistence.*;

@Data
@Entity(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String message;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "education_block_id")
    private EducationBlock educationBlock;
}
