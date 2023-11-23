package irnitu.forum.bot.services;

import irnitu.forum.bot.models.entities.BusinessExpert;
import irnitu.forum.bot.repositories.BusinessExpertRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessExpertService {

    private final BusinessExpertRepository businessExpertRepository;

    public BusinessExpertService(BusinessExpertRepository businessExpertRepository) {
        this.businessExpertRepository = businessExpertRepository;
    }

    public List<String> getExpertNamesAndDescriptions(){
        return businessExpertRepository.findAll()
                .stream()
                .map(BusinessExpert::getName)
                .collect(Collectors.toList());
    }

    public List<BusinessExpert> getAll(){
        return businessExpertRepository.findAll();
    }
}
