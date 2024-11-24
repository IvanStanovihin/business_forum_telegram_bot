package irnitu.forum.bot.models.entities;


import lombok.Data;


import javax.persistence.*;


@Data
@Entity(name = "business_experts")
public class BusinessExpert {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", length = 1024)
    private String name;

    @Column(name = "description", length = 2048)
    private String description;
}
