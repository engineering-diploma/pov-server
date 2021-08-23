package cloud.ptl.povserver.service.resource;

import cloud.ptl.povserver.data.model.ResolutionDAO;
import cloud.ptl.povserver.data.repositories.ResolutionRepository;
import org.springframework.stereotype.Service;

@Service
public class ResolutionService {
    private final ResolutionRepository resolutionRepository;

    public ResolutionService(ResolutionRepository resolutionRepository) {
        this.resolutionRepository = resolutionRepository;
    }

    public ResolutionDAO save(ResolutionDAO resolutionDAO) {
        return this.resolutionRepository.save(resolutionDAO);
    }
}
