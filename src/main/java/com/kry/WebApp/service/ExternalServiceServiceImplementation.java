package com.kry.WebApp.service;

import com.kry.WebApp.datatransferobjects.ExternalServiceDTO;
import com.kry.WebApp.domainobject.BackendEndPoints;
import com.kry.WebApp.domainobject.ExternalService;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;

@Service
public class ExternalServiceServiceImplementation implements ExternalServiceService {
    private Logger logger = LoggerFactory.getLogger(ExternalServiceServiceImplementation.class);
//    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);


    @Value("${request.timeout}")
    private Long timeout;

    @Value("${backend.base}")
    private String backendBase;

//    @Value("${backend.getAllServices}")
//    private String getAllServices;
//
//    @Value("${backend.updateService}")
//    private String updateService;
//
//    @Value("${backend.updateServiceStatus}")
//    private String updateServiceStatus;
//
//    @Value("${backend.addService}")
//    private String addService;
//
//    @Value("${backend.removeService}")
//    private String removeService;

    public Mono<ResponseEntity<String>> sendGetRequest(String url) {
        logger.info("Sending Request to url: " + url);
        return WebClient
                .create() //Default Settings
                .get()
                .uri(url)
                .retrieve()
                .toEntity(String.class)
                .timeout(Duration.ofSeconds(timeout))
                .onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"))
                .doOnError(WriteTimeoutException.class, ex -> logger.error("WriteTimeout"))
                .doOnError(Exception.class, ex -> logger.error(ex.getMessage()))
                .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                        .map(s -> ResponseEntity.internalServerError().body(s)));
    }

    @Override
    public Mono<List<ExternalServiceDTO>> fetchAllServices() {
        logger.info("fetchAllServices called");
        return WebClient
                .create()
                .get()
                .uri(backendBase + BackendEndPoints.getAllServices.getAddress())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ExternalServiceDTO>>() {
                })
//                .publishOn(Schedulers.fromExecutor(executorService))
                .timeout(Duration.ofSeconds(timeout))
                .onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"))
                .doOnError(WriteTimeoutException.class, ex -> logger.error("WriteTimeout"))
                .doOnError(Exception.class, ex -> logger.error(ex.getMessage()))
                .onErrorResume(throwable -> Mono.error(throwable));
    }

    @Override
    public Mono<ExternalServiceDTO> addService(ExternalServiceDTO externalServiceDTO) {
        logger.info("Add Services called for: " + externalServiceDTO);
        return WebClient
                .create()
                .post()
                .uri(backendBase + BackendEndPoints.addService.getAddress())
                .body(Mono.just(externalServiceDTO), ExternalServiceDTO.class)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ExternalServiceDTO>() {
                })
//                .publishOn(Schedulers.fromExecutor(executorService))
                .timeout(Duration.ofSeconds(timeout))
                .onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"))
                .doOnError(WriteTimeoutException.class, ex -> logger.error("WriteTimeout"))
                .doOnError(Exception.class, ex -> logger.error(ex.getMessage()))
                .onErrorResume(throwable -> Mono.error(throwable));
    }

    @Override
    public Mono<ExternalServiceDTO> updateService(ExternalServiceDTO externalServiceDTO) {
        logger.info("Update Services called for : " + externalServiceDTO);
        return WebClient
                .create()
                .post()
                .uri(backendBase + BackendEndPoints.updateService.getAddress())
                .body(Mono.just(externalServiceDTO), ExternalServiceDTO.class)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ExternalServiceDTO>() {
                })
//                .publishOn(Schedulers.fromExecutor(executorService))
                .timeout(Duration.ofSeconds(timeout))
                .onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"))
                .doOnError(WriteTimeoutException.class, ex -> logger.error("WriteTimeout"))
                .doOnError(Exception.class, ex -> logger.error(ex.getMessage()))
                .onErrorResume(throwable -> Mono.error(throwable));
    }

    @Override
    public Mono<Integer> removeServiceCall(String  id) {
        logger.info("Remove Services called for id: " + id);
        return WebClient
                .create()
                .delete()
                .uri(backendBase + BackendEndPoints.removeService.getAddress(), id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Integer>() {
                })
//                .publishOn(Schedulers.fromExecutor(executorService))
                .timeout(Duration.ofSeconds(timeout))
                .onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"))
                .doOnError(WriteTimeoutException.class, ex -> logger.error("WriteTimeout"))
                .doOnError(Exception.class, ex -> logger.error(ex.getMessage()))
                .onErrorResume(throwable -> Mono.error(throwable));
    }

    @Override
    public Boolean confirmURLRequest(String url) {
        return sendGetRequest(url)
                .map(stringResponseEntity -> confirmURL(stringResponseEntity, url))
                .block();
    }

    @Override
    public Mono<ExternalServiceDTO> checkURLAndUpdateService(ExternalService externalService) {
        logger.info("Check URL and update service called for: " + externalService);
        return sendGetRequest(externalService.getUrl())
                .map(stringResponseEntity -> ExternalServiceMapper.makeExternalServiceDTO(
                        setStatus(externalService, stringResponseEntity)
                ));
    }

    public ExternalService setStatus(ExternalService externalService, ResponseEntity responseEntity){
        logger.info("Response Received for: " + externalService + " , Status Recieved: " + responseEntity.getStatusCode());
        if (responseEntity.getStatusCode().is2xxSuccessful()) { externalService.setStatus("1"); }
        else { externalService.setStatus("0"); }
        return externalService;
    }

    public Boolean confirmURL(ResponseEntity responseEntity, String url){
        logger.info("Response Received for: " + url + " , Status Recieved: " + responseEntity.getStatusCode());
        return responseEntity.getStatusCode().is2xxSuccessful() ? true : false;
    }
}
