package com.kry.WebApp.domainobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalService {
    private Long id;
    private String name;
    private String url;
    private LocalDateTime creation_datetime;
    private LocalDateTime update_datetime;
    private LocalDateTime last_verified_datetime;
    private String status;

    public ExternalService(Long id, String name, String url, String status) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.status = status;
    }

    public ExternalService(Long id, String name, String url, String status, LocalDateTime creation_datetime) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.creation_datetime = creation_datetime;
        this.status = status;
    }

}