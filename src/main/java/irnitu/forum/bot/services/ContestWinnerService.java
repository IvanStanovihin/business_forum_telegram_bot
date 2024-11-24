package irnitu.forum.bot.services;


import irnitu.forum.bot.models.entities.ContestWinner;
import irnitu.forum.bot.models.entities.User;
import irnitu.forum.bot.repositories.ContestWinnerRepository;
import irnitu.forum.bot.repositories.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ContestWinnerService {

    private final ContestWinnerRepository contestWinnerRepository;

    private final UserRepository userRepository;

    public ContestWinnerService(ContestWinnerRepository contestWinnerRepository, UserRepository userRepository) {
        this.contestWinnerRepository = contestWinnerRepository;
        this.userRepository = userRepository;
    }

    /**
     * Метод для фиксиции победителя в конкурсе "Угадай фразу"
     * @param userTelegramName
     * @param time
     */
    public void addWinner(Long chatId) {
        User user = userRepository.findByChatId(chatId.toString());
        ContestWinner winner = new ContestWinner()
            .setStudent(user)
            .setPhraseEntryTime(LocalDateTime.now())
            .setIsWinnerSet(true);

        contestWinnerRepository.save(winner);
    }

    /**
     * Метод для получения победителя в конкурсе "Угадай фразу"
     * @return все участники, которые угадали фразу. Организатор должен сам решить кто точнее
     * ввёл секретную фразу и был раньше.
     */
    public List<ContestWinner> getWinners() {
        return contestWinnerRepository.findAll();
    }

}
