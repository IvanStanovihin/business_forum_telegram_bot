package irnitu.forum.bot.services;

import irnitu.forum.bot.models.entities.ContestSecret;
import irnitu.forum.bot.models.entities.SecretWord;
import irnitu.forum.bot.repositories.ContestSecretRepository;
import irnitu.forum.bot.repositories.SecretWordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Сервис для логики конкурса "угадай фразу"
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecretPhraseContestService {

    private final ContestWinnerService contestWinnerService;
    private final ContestSecretRepository contestSecretRepository;
    private final SecretWordRepository secretWordRepository;

    /**
     * Метод для проверки введённой фразы
     */
    public boolean checkPhrase(String telegramUserName, String userPhrase) {
        String processedInputPhrase = processUserPhrase(userPhrase);
        ContestSecret contestSecret = contestSecretRepository.findAll().get(0);
        boolean isPhraseCorrect = processUserPhrase(contestSecret.getPhrase()).equals(processedInputPhrase);
        if (isPhraseCorrect) {
            log.info("User with chatId : {} guess phrase!", telegramUserName);
            contestWinnerService.addWinner(telegramUserName);
        }
        return isPhraseCorrect;
    }

    /**
     * Метод для проверки введённого пользователем куска фразы
     */
    public boolean checkWord(String userWords) {
        //Введённое пользователем слово на всякий случай обрезаем и приводим к нижнему регистру
        String processedUserWord = processUserPhrase(userWords);
        List<SecretWord> secretWords = secretWordRepository.findAllNotGuessed();
        //Проверяем совпадает ли введённая пользователем часть фразы с любой из частей хранящихся у нас.
        //Если есть совпадение, то отмечаем у себя в базе слово как угаданное
        boolean isWordCorrect = secretWords.stream().anyMatch(secretWord -> isWordCorrect(processedUserWord, secretWord));
        if (isWordCorrect) {
            boolean isAllWordsGuessed = secretWordRepository.findAllNotGuessed().isEmpty();
            if (isAllWordsGuessed) {
                contestSecretRepository.markAllWordsGuessed();
            }
        }
        return isWordCorrect;
    }

    private boolean isWordCorrect(String userWords, SecretWord secretWord) {
        if (secretWord.getWord().equals(userWords)) {
            secretWord.setIsGuessed(true);
            secretWordRepository.save(secretWord);
            return true;
        }
        return false;
    }

    public Boolean isAllWordsFound() {
        return contestSecretRepository.findAll().get(0).getIsAllWordsFound();
    }

    private String processUserPhrase(String phrase) {
        return phrase.replaceAll("-", "")
                .replaceAll("\\.", "")
                .replaceAll(",", "")
                .replaceAll(" ", "")
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    public void activatePhrase() {
        contestSecretRepository.markAllWordsGuessed();
    }
}
