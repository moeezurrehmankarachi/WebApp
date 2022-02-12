package com.kry.WebApp.service;

import com.kry.WebApp.datatransferobjects.ExternalServiceDTO;
import com.kry.WebApp.domainobject.ExternalService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public interface ExternalServiceService {
    Boolean confirmURLRequest(String url);
    Mono<ExternalServiceDTO> checkURLAndUpdateService(ExternalService externalService);
    Mono<List<ExternalServiceDTO>> fetchAllServices();
    Mono<ExternalServiceDTO> addService(ExternalServiceDTO externalServiceDTO);
    Mono<ExternalServiceDTO> updateService(ExternalServiceDTO externalServiceDTO);
    Mono<Integer> removeServiceCall(String  id);
}
