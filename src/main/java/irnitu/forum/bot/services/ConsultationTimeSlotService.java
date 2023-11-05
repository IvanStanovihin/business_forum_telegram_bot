package irnitu.forum.bot.services;

import irnitu.forum.bot.models.BusinessExpert;
import irnitu.forum.bot.models.ConsultationTimeSlot;
import irnitu.forum.bot.repositories.BusinessExpertRepository;
import irnitu.forum.bot.repositories.ConsultationTimeSlotRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ConsultationTimeSlotService {

    private final BusinessExpertRepository businessExpertRepository;
    private final ConsultationTimeSlotRepository consultationTimeSlotRepository;

    public ConsultationTimeSlotService(ConsultationTimeSlotRepository consultationTimeSlotRepository,
                                       BusinessExpertRepository businessExpertRepository) {
        this.consultationTimeSlotRepository = consultationTimeSlotRepository;
        this.businessExpertRepository = businessExpertRepository;
    }

    /**
     * Метод для поиска свободных тайм слотов для записи на консультацию к эксперту.
     * @param expertName эксперт для которого выполняется поиск свободных тайм слотов
     * @return список свободных для консультации тайм слотов
     */
    public List<ConsultationTimeSlot> getFreeSlot(String expertName){
        BusinessExpert expert = businessExpertRepository.findByName(expertName);
        log.info("GetFreeSlot found expert: {}", expert);
        return consultationTimeSlotRepository.findByExpertWithNullStudent(expert.getId());
    }

    public void takeTimeSlot(String expert, String timeInterval, Long student_id){

    }
}
