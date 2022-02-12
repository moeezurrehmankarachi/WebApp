package com.kry.WebApp.service;


import com.kry.WebApp.datatransferobjects.ExternalServiceDTO;
import com.kry.WebApp.domainobject.ExternalService;

import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ExternalServiceMapper {
    public static ExternalService makeExternalService(ExternalServiceDTO externalServiceDTO) {
        ExternalService externalService = new ExternalService(
                externalServiceDTO.getId(),
                externalServiceDTO.getName(),
                externalServiceDTO.getUrl(),
                externalServiceDTO.getStatus(),
                externalServiceDTO.getCreation_datetime().toLocalDateTime()
        );

        if(externalServiceDTO.getLast_verified_datetime()!=null){
            externalService.setLast_verified_datetime(externalServiceDTO.getLast_verified_datetime().toLocalDateTime());
        }

        if(externalServiceDTO.getUpdate_datetime()!=null){
            externalService.setUpdate_datetime(externalServiceDTO.getUpdate_datetime().toLocalDateTime());
        }

        return externalService;
    }

    public static ExternalServiceDTO makeExternalServiceDTO(ExternalService externalService) {
        ExternalServiceDTO.ExternalServiceDTOBuilder dtoBuilder = ExternalServiceDTO.newBuilder()

                .setId(externalService.getId())
                .setName(externalService.getName())
                .setUrl(externalService.getUrl())
                .setStatus(externalService.getStatus())
                .setCreation_datetime(externalService.getCreation_datetime().atZone(ZoneId.systemDefault()).toOffsetDateTime());

        if (externalService.getUpdate_datetime() != null) {
            dtoBuilder.setUpdate_datetime(externalService.getUpdate_datetime().atZone(ZoneId.systemDefault()).toOffsetDateTime());
        }
        if (externalService.getLast_verified_datetime() != null) {
            dtoBuilder.setLast_verified_datetime(externalService.getLast_verified_datetime().atZone(ZoneId.systemDefault()).toOffsetDateTime());
        }
        return dtoBuilder.createExternalServiceDTO();
    }

    public static List<ExternalServiceDTO> makeExternalServiceDTOList(Collection<ExternalService> externalServices) {
        return externalServices.stream()
                .map(ExternalServiceMapper::makeExternalServiceDTO)
                .collect(Collectors.toList());
    }

    public static List<ExternalService> makeExternalServiceList(Collection<ExternalServiceDTO> externalServiceDTOS) {
        return externalServiceDTOS.stream()
                .map(ExternalServiceMapper::makeExternalService)
                .collect(Collectors.toList());
    }
}
