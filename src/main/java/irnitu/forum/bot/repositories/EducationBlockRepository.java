package irnitu.forum.bot.repositories;

import irnitu.forum.bot.models.entities.EducationBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationBlockRepository extends JpaRepository<EducationBlock, Long> {
}
