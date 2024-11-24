package irnitu.forum.bot.services;


import irnitu.forum.bot.models.entities.ContestWinner;
import irnitu.forum.bot.models.entities.User;
import irnitu.forum.bot.repositories.ContestWinnerRepository;
import irnitu.forum.bot.repositories.UserRepository;
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
    public void addWinner(String userTelegramName, String time) {
        User user = userRepository.findByTelegramUserName(userTelegramName);
        ContestWinner winner = new ContestWinner().setStudent(user).setPhraseEntryTime(time).setIsWinnerSet(true);

        contestWinnerRepository.save(winner);
    }

    /**
     * Метод для получения победителя в конкурсе "Угадай фразу"
     * @return
     */
    public ContestWinner getWinner() {
        //КАК ТУТ БЫТЬ С ID??
        ContestWinner contestWinner = contestWinnerRepository.findWinner(1l);

        //Если в БД еще нет записи с победителем то вернется null и тогда возвращаем
        //ContestWinner с IsWinnerSet = false, на UI потом делаем проверку
        if (contestWinner == null) {
            contestWinner = new ContestWinner().setIsWinnerSet(false);
        }

        return contestWinner;
    }

}
