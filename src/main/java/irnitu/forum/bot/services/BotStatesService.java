package irnitu.forum.bot.services;

import irnitu.forum.bot.constants.BotStateNames;
import irnitu.forum.bot.models.entities.BotState;
import irnitu.forum.bot.repositories.BotStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Сервис для управления состояниями бота. Состояния нужны для работы с текстом,
 * который вводит пользователь.
 */
@Slf4j
@Service
public class BotStatesService {

    private final BotStateRepository botStateRepository;

    public BotStatesService(BotStateRepository botStateRepository) {
        this.botStateRepository = botStateRepository;
    }


    /**
     * Метод в котором устанавливается состояние ожидающее ввода регистрационных данных
     * для указанного пользователя.
     */
    public void setRegistrationState(String userTelegramName){
        BotState botState = botStateRepository.findByUserTelegramName(userTelegramName);
        if (botState == null) {
            botState = new BotState()
                    .setUserTelegramName(userTelegramName);
        }
            botState.setState(BotStateNames.WAIT_REGISTRATION);
            botStateRepository.save(botState);
    }

    public BotState getState(String telegramUserName){
        return botStateRepository.findByUserTelegramName(telegramUserName);
    }

    /**
     * Очистка состояния бота для конкретного пользователя.
     */
    public void resetState(String telegramUsername){
        log.info("Reset bot state for user {}", telegramUsername);
        BotState botState = botStateRepository.findByUserTelegramName(telegramUsername);
        if (botState == null){
            botState = new BotState()
                    .setUserTelegramName(telegramUsername);
        }
        botState.setState(null);
        botStateRepository.save(botState);
    }

}
