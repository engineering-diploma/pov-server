package cloud.ptl.povserver.service.resource;

import cloud.ptl.povserver.data.model.ResolutionDAO;
import cloud.ptl.povserver.data.repositories.ResolutionRepository;
import org.springframework.stereotype.Service;

/**
 * Each resource stored in system can be represented by many resolutions as each call possibly generate new one on demand
 * To track this we store information about available resolutions in proper Resolution entity
 * This class us used to manage this behaviour and entities
 */
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
