package com.Pagos.Pagos.controller;

import com.Pagos.Pagos.dto.PagoRequestDTO;
import com.Pagos.Pagos.dto.PagoResponseDTO;
import com.Pagos.Pagos.model.Pago;
import com.Pagos.Pagos.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gym/pagos")
@RequiredArgsConstructor
public class PagoController {
    private final PagoService pagoService;

    // Listar todos los pagos de la bd
    @GetMapping
    ResponseEntity<List<PagoResponseDTO>> listarPagos(){
        return ResponseEntity.ok(pagoService.listarAllPagos());
    }


    // Buscar pagos por el id pago
    @GetMapping("{idPago}")
    ResponseEntity<PagoResponseDTO> buscarPagoId(@PathVariable Long idPago){
        return pagoService.buscarPorId(idPago)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Filtrar todos los pagos de un run asociado
    @GetMapping("/filtrorun/{runpagado}")
    ResponseEntity<List<PagoResponseDTO>> buscarPagoRUN(@PathVariable String runpagado){
        return ResponseEntity.ok(pagoService.filtrarPagosRut(runpagado));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> crearPago(@Valid @RequestBody PagoRequestDTO dto){
        return ResponseEntity.ok(pagoService.subirPago(dto));
    }



}
