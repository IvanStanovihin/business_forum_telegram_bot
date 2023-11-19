package irnitu.forum.bot.services;

import irnitu.forum.bot.models.entities.EducationBlock;
import irnitu.forum.bot.models.entities.Feedback;
import irnitu.forum.bot.models.entities.User;
import irnitu.forum.bot.repositories.EducationBlockRepository;
import irnitu.forum.bot.repositories.FeedbackRepository;
import irnitu.forum.bot.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final EducationBlockRepository educationBlockRepository;

    public FeedbackService(FeedbackRepository feedbackRepository,
                           UserRepository userRepository,
                           EducationBlockRepository educationBlockRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.educationBlockRepository = educationBlockRepository;
    }

    /**
     * Метод для сохранения отзыва пользователя в БД.
     */
    public void addFeedback(String userTelegramName, Long educationBlockId, String message){
        User user = userRepository.findByTelegramUserName(userTelegramName);
        EducationBlock educationBlock = educationBlockRepository.findById(educationBlockId).get();
        Feedback feedback = new Feedback()
                .setMessage(message)
                .setEducationBlock(educationBlock)
                .setStudent(user);
        feedbackRepository.save(feedback);
    }
}
