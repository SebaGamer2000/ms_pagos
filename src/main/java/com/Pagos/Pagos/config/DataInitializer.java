package com.Pagos.Pagos.config;

import com.Pagos.Pagos.model.Pago;
import com.Pagos.Pagos.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final PagoRepository pagoRepository;

    @Override
    public void run(String... args){
        if(pagoRepository.count() > 0){
            log.info("Datos cargados");
            return;
        }

        log.info("No hay datos guardados, creando datos");
    }
}