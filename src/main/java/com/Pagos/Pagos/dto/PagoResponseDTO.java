package com.Pagos.Pagos.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PagoResponseDTO {
    private Long idpago;
    private String runpagado;
    private Long montopagado;
    private Long membresiapagada;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime fechapago;

}
