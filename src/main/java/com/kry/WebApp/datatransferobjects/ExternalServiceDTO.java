package com.kry.WebApp.datatransferobjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExternalServiceDTO {
    private Long id;

    @NotNull(message = "Name can not be null!")
    private String name;

    @NotNull(message = "URL can not be null!")
    private String url;

    @NotNull(message = "Creation date time can not be null!")
    private OffsetDateTime creation_datetime;

    private OffsetDateTime update_datetime;

    private OffsetDateTime last_verified_datetime;

    private String status;

    public ExternalServiceDTO(Long id, String name, String url, OffsetDateTime creation_datetime, String status) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.creation_datetime = creation_datetime;
        this.status = status;
    }

    public static ExternalServiceDTOBuilder newBuilder() {
        return new ExternalServiceDTOBuilder();
    }

    public static class ExternalServiceDTOBuilder {
        private Long id;
        private String name;
        private String url;
        private OffsetDateTime creation_datetime;
        private OffsetDateTime update_datetime;
        private OffsetDateTime last_verified_datetime;
        private String status;

        public ExternalServiceDTO createExternalServiceDTO() {
            return new ExternalServiceDTO(id, name, url, creation_datetime, update_datetime, last_verified_datetime, status);
        }

        public ExternalServiceDTOBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public ExternalServiceDTOBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public ExternalServiceDTOBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public ExternalServiceDTOBuilder setCreation_datetime(OffsetDateTime creation_datetime) {
            this.creation_datetime = creation_datetime;
            return this;
        }

        public ExternalServiceDTOBuilder setUpdate_datetime(OffsetDateTime update_datetime) {
            this.update_datetime = update_datetime;
            return this;
        }

        public ExternalServiceDTOBuilder setLast_verified_datetime(OffsetDateTime last_verified_datetime) {
            this.last_verified_datetime = last_verified_datetime;
            return this;
        }

        public ExternalServiceDTOBuilder setStatus(String status) {
            this.status = status;
            return this;
        }
    }
}
