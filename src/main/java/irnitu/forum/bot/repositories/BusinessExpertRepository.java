package irnitu.forum.bot.repositories;

import irnitu.forum.bot.models.entities.BusinessExpert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessExpertRepository extends JpaRepository<BusinessExpert, Long> {

    BusinessExpert findByName(String name);
}
