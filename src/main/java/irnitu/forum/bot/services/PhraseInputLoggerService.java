package irnitu.forum.bot.services;

import irnitu.forum.bot.models.entities.PhraseInputLog;
import irnitu.forum.bot.repositories.PhraseInputLoggerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhraseInputLoggerService {

    private final PhraseInputLoggerRepository phraseInputLoggerRepository;

    public PhraseInputLoggerService(
            PhraseInputLoggerRepository phraseInputLoggerRepository) {
        this.phraseInputLoggerRepository = phraseInputLoggerRepository;
    }

    public List<PhraseInputLog> getInputPhraseLog() {
        return phraseInputLoggerRepository.findAll();
    }

    public void addInputPhraseLog(PhraseInputLog phraseInputLog) { phraseInputLoggerRepository.save(phraseInputLog); }
}
