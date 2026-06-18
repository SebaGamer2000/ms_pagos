package com.Pagos.Pagos.controller;

import com.Pagos.Pagos.dto.PagoRequestDTO;
import com.Pagos.Pagos.dto.PagoResponseDTO;
import com.Pagos.Pagos.dto.PagoTiendaDTO;
import com.Pagos.Pagos.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gym/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Operaciones relacionadas a los pagos del gym")
public class PagoController {
    private final PagoService pagoService;

    // Listar todos los pagos de la bd
    @GetMapping("/listarpagos")
    //Swagger
    @Operation(summary = "Listar todos los pagos de MEMBRESIAS", description = "Obtiene una lista de todos los pagos realizados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagos listados correctamente")
    })
    ResponseEntity<List<PagoResponseDTO>> listarPagos(){
        return ResponseEntity.ok(pagoService.listarAllPagos());
    }


    // Buscar pagos por el id pago
    @GetMapping("{idPago}")
    @Operation(summary = "Buscar un pago por su ID de pago", description = "Buscar cierto pago por su ID de pago.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error del servidor")
    })
    ResponseEntity<PagoResponseDTO> buscarPagoId(@PathVariable Long idPago){
        return pagoService.buscarPorId(idPago)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Filtrar todos los pagos de un run asociado
    @GetMapping("/filtrorun/{runpagado}")
    @Operation(summary = "Buscar todos los pagos de algún RUN", description = "Busca y lista los pagos filtrandolos por el RUN buscado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagos encontrados"),
            @ApiResponse(responseCode = "404", description = "Pagos no encontrados"),
            @ApiResponse(responseCode = "500", description = "Error del servidor")
    })
    ResponseEntity<List<PagoResponseDTO>> buscarPagoRUN(@PathVariable String runpagado){
        return ResponseEntity.ok(pagoService.filtrarPagosRut(runpagado));
    }

    // Añadir pago en BD de tienda
    @PostMapping("/registrar")
    @Operation(summary = "Registrar un pago nuevo de MEMBRESIAS", description = "Utilizado para agregar un nuevo pago al sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago añadido correctamente"),
            @ApiResponse(responseCode = "404", description = "Error al añadir al pago"),
            @ApiResponse(responseCode = "500", description = "Error del servidor")
    })
    public ResponseEntity<?> crearPago(@Valid @RequestBody PagoRequestDTO dto){
        return ResponseEntity.ok(pagoService.registrarPago(dto));
    }

    // --------------- PAGOS TIENDA -----------------------
    @PostMapping("/registrar/tienda")
    @Operation(summary = "Registrar un pago nuevo de la TIENDA", description = "Utilizado para agregar un nuevo pago al sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago añadido correctamente"),
            @ApiResponse(responseCode = "404", description = "Error al añadir al pago"),
            @ApiResponse(responseCode = "500", description = "Error del servidor")
    })
    public ResponseEntity<PagoTiendaDTO> registrarPago(@Valid @RequestBody PagoTiendaDTO dto){
        return ResponseEntity.ok(pagoService.registrarPagoTienda(dto));
    }

    // Listar todos los pagos de la bd
    @GetMapping("/listarpagos/tienda")
    //Swagger
    @Operation(summary = "Listar todos los pagos de la TIENDA", description = "Obtiene una lista de todos los pagos realizados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagos listados correctamente")
    })
    ResponseEntity<List<PagoTiendaDTO>> listarPagosTienda(){
        return ResponseEntity.ok(pagoService.listarAllPagosTienda());
    }
}
