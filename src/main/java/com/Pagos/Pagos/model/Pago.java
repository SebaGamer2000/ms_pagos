package com.Pagos.Pagos.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pagos")

public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;

    @Column(nullable = false, length = 9)
    private String runPagado;

    @Column(nullable = false)
    private Long montoPagado;

    @Column(nullable = false)
    private Long membresiaPagada;

    @Column

    private LocalDateTime fechapago;

}
