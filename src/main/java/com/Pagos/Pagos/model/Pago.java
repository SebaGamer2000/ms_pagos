package com.Pagos.Pagos.model;


import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}
