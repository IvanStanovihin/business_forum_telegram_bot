package irnitu.forum.bot.models.entities;


import lombok.Data;


import javax.persistence.*;


@Data
@Entity(name = "business_experts")
public class BusinessExpert {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "education_block_id")
    private EducationBlock educationBlock;
}
