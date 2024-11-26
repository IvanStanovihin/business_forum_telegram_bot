package irnitu.forum.bot.repositories;

import irnitu.forum.bot.models.entities.PhraseInputLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhraseInputLoggerRepository extends JpaRepository<PhraseInputLog, Long> { }
