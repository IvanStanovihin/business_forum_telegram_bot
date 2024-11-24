package irnitu.forum.bot.models.entities;


import lombok.Data;
import lombok.experimental.Accessors;


import javax.persistence.*;

@Data
@Accessors(chain = true)
@Entity(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "message", length = 2048)
    private String message;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "education_block_id")
    private EducationBlock educationBlock;
}
