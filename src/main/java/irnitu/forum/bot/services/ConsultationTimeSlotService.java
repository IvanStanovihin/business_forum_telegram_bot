package irnitu.forum.bot.services;

import irnitu.forum.bot.models.entities.BusinessExpert;
import irnitu.forum.bot.models.entities.ConsultationTimeSlot;
import irnitu.forum.bot.models.entities.User;
import irnitu.forum.bot.repositories.BusinessExpertRepository;
import irnitu.forum.bot.repositories.ConsultationTimeSlotRepository;
import irnitu.forum.bot.repositories.UserRepository;
import irnitu.forum.bot.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ConsultationTimeSlotService {

    private final UserRepository userRepository;
    private final BusinessExpertRepository businessExpertRepository;
    private final ConsultationTimeSlotRepository consultationTimeSlotRepository;

    public ConsultationTimeSlotService(ConsultationTimeSlotRepository consultationTimeSlotRepository,
                                       BusinessExpertRepository businessExpertRepository,
                                       UserRepository userRepository) {
        this.consultationTimeSlotRepository = consultationTimeSlotRepository;
        this.businessExpertRepository = businessExpertRepository;
        this.userRepository = userRepository;
    }

    /**
     * Метод для поиска свободных тайм слотов для записи на консультацию к эксперту.
     * @param expertName эксперт для которого выполняется поиск свободных тайм слотов
     * @return список свободных для консультации тайм слотов
     */
    public List<ConsultationTimeSlot> getExpertFreeSlot(String expertName){
        BusinessExpert expert = businessExpertRepository.findByName(expertName);
        log.info("GetFreeSlot found expert: {}", expert);
        return consultationTimeSlotRepository.findByExpertWithNullStudent(expert.getId());
    }

    /**
     * Метод для проверки записи пользователя на консультацию.
     * Если пользователь уже записан к данному эксперту - true
     */
    public boolean isUserAlreadyTakeTimeslot(String username, Long expertId){
        User user = userRepository.findByTelegramUserName(username);
        ConsultationTimeSlot timeSlot = consultationTimeSlotRepository
                .findByStudentIdAndExpertId(user.getId(), expertId);
        if (timeSlot != null){
            log.info("Take timeslot error. User already take expert timeslot studentId: {} expertId: {}",
                    user.getId(), expertId);
            return true;
        }
        return false;
    }


    /**
     * Метод для записи студента на консультацию к эксперту.
     * В методе выполняется проверка. Если пользователь уже записан на консультацию к эксперту,
     * то повторная запись не производится
     * @param timeslotId свободный таймслот на консультацию к эксперту
     */
    public void takeTimeslot(Long timeslotId, String userName, Long expertId){
        User student = userRepository.findByTelegramUserName(userName);
        ConsultationTimeSlot freeTimeslot = consultationTimeSlotRepository.findById(timeslotId).get();
        freeTimeslot.setStudent(student);
        consultationTimeSlotRepository.save(freeTimeslot);
        log.info("Take time slot successfully timeSlotId {} userName {}", timeslotId, userName);
    }


    /**
     * Метод для поиска всех консультаций на которые записался пользователь
     */
    public String getAllUserConsultations(String telegramUsername) {
        User user = userRepository.findByTelegramUserName(telegramUsername);
        List<ConsultationTimeSlot> userConsultations = consultationTimeSlotRepository
                .findAllByUserId(user.getId());
        if (userConsultations.size() == 0){
            return "Вы еще не записались ни на одну консультацию. Для записи на консультацию, используйте команду /consultation";
        }
        StringBuilder stringBuilder = new StringBuilder("Вы записаны на следующие консультации: ");
        for (ConsultationTimeSlot consultation : userConsultations){
            String expertInfo = consultation.getExpert().getName();
            String consultationTime = TimeUtil.getTimeInterval(consultation.getStartConsultationTime(), consultation.getEndConsultationTime());
            stringBuilder.append("\n").append(consultationTime).append(" ").append(expertInfo);
        }
        return stringBuilder.toString();
    }

    /**
     * Формирование текста в котором содержится информация о расписании всех консультаций с экспертами
     */
    public String getAllExpertsTimeSlots(){
        StringBuilder timeSlotsInfo = new StringBuilder("РАССПИСАНИЕ ВСЕХ КОНСУЛЬТАЦИЙ:\n");
        List<ConsultationTimeSlot> allTimeSlots = consultationTimeSlotRepository.findAllGroupByExpert();
        for(ConsultationTimeSlot timeSlot : allTimeSlots){
            String expertInfo = String.format("ЭКСПЕРТ: %s", timeSlot.getExpert().getName());
            String studentInfo = "\nСВОБОДНО";
            if (timeSlot.getStudent() != null) {
                studentInfo = "\nУЧАСТНИК:"
                    + timeSlot.getStudent().getRegistrationInformation()
                    + "\nTELEGRAM: @"
                    + timeSlot.getStudent().getTelegramUserName()
                    + "\nЗАНЯТО";
            }
            String timeInfo = String.format("СЛОТ: %s", TimeUtil.getTimeInterval(timeSlot.getStartConsultationTime(), timeSlot.getEndConsultationTime()));
            timeSlotsInfo.append("\n").append(expertInfo).append("\n").append(timeInfo).append(" ").append(studentInfo).append("\n-----------------");
        }
        return timeSlotsInfo.toString();
    }
}
