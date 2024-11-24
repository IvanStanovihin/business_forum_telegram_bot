package irnitu.forum.bot.services;

import irnitu.forum.bot.models.entities.Contest;
import irnitu.forum.bot.repositories.ContestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@Service
public class ContestService {

    private final ContestRepository contestRepository;

    public ContestService(ContestRepository contestRepository) {
        this.contestRepository = contestRepository;
    }

    public Contest getContestService(Long id) {
        return contestRepository.findContestById(id);
    }

    /**
     * Метод для проверки введеной фразы
     * @param phraseInput
     * @return
     */
    public boolean checkPhrase(String phraseInput) {
        String processedInputPhrase = processPhrase(phraseInput);

        String correctPhrase = contestRepository.findContestById(100l).getPhrase();

        return processedInputPhrase.equals(processPhrase(correctPhrase));
    }

    /**
     * Метод для проверки введеного слова
     * @param
     * @return
     */
    public boolean checkWord(String wordInput) {
        String processedWordInput = wordInput.replaceAll(" ", "").toLowerCase(Locale.ROOT);
        String correctPhrase = contestRepository.findContestById(100l).getPhrase().toLowerCase(Locale.ROOT);

        return correctPhrase.contains(processedWordInput);
    }

    private String processPhrase(String phrase) {
        return phrase.replaceAll("-", "")
                .replaceAll("\\.", "")
                .replaceAll(",", "")
                .replaceAll(" ", "")
                .toLowerCase(Locale.ROOT);
    }
}
