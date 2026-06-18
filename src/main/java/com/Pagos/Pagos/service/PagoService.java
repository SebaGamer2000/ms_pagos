package com.Pagos.Pagos.service;

import com.Pagos.Pagos.dto.MembresiaDTO;
import com.Pagos.Pagos.dto.PagoRequestDTO;
import com.Pagos.Pagos.dto.PagoResponseDTO;
import com.Pagos.Pagos.dto.PagoTiendaDTO;
import com.Pagos.Pagos.model.Pago;
import com.Pagos.Pagos.model.PagoTienda;
import com.Pagos.Pagos.repository.PagoRepository;
import com.Pagos.Pagos.repository.TiendaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoService {
    @Autowired

    private final WebClient.Builder webClientBuilder;
    private final PagoRepository pagoRepository;

    private final TiendaRepository tiendaRepository;

    private final RestTemplate restTemplate = new RestTemplate();


    //Pagos de membresias
    private PagoResponseDTO mapToDTO(Pago pago){
        PagoResponseDTO response = new PagoResponseDTO();
        response.setIdpago(pago.getIdPago());
        response.setRunpagado(pago.getRunPagado());
        response.setMontopagado(pago.getMontoPagado());
        response.setFechapago(pago.getFechapago());
        response.setTipoPlan(pago.getTipoPlan());
        response.setMembresiapagada(pago.getMembresiaPagada());

        return response;
    }
    //Pagos de tienda
    private PagoTiendaDTO mapToDTOTienda(PagoTienda pagoTienda){
        PagoTiendaDTO responseTienda = new PagoTiendaDTO();
        responseTienda.setIdProducto(pagoTienda.getIdProducto());
        responseTienda.setNombreProducto(pagoTienda.getNombreProducto());
        responseTienda.setPrecioProducto(pagoTienda.getPrecioProducto());

        return responseTienda;
    }


    // Listar todos los pagos
    public List<PagoResponseDTO> listarAllPagos(){
        return pagoRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Buscar pagos por el id de pago
    public Optional<PagoResponseDTO> buscarPorId(Long idpago){
        return pagoRepository.findById(idpago).map(this::mapToDTO);
    }

    // Filtrar pagos por run de usuario
    public List<PagoResponseDTO> filtrarPagosRut(String runpagado){
        return pagoRepository.findByRunPagado(runpagado)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Añadir pagos
    public PagoResponseDTO registrarPago(@Valid @RequestBody PagoRequestDTO dto){
            PagoRequestDTO usuario = webClientBuilder.build()
                    //Aca se busca el socio en el ms-usuarios
                    .get()
                    .uri("http://USUARIO/gym/socios/busqueda/" + dto.getRun())
                    //.header("Authorization", token)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response -> Mono.empty())
                    .onStatus(status -> status.is5xxServerError(), response ->
                            Mono.error(new RuntimeException("Error interno en ms de Usuarios")))
                    .bodyToMono(PagoRequestDTO.class)
                    .onErrorResume(e -> Mono.empty())
                    .block();

            boolean usuarioExiste = usuario != null;

            if(!usuarioExiste){
                throw new RuntimeException("Socio con RUN " + dto.getRun() + " no encontrado");
            }

        // Obtener datos de la membresia
        MembresiaDTO membresia = webClientBuilder.build()
                .get()
                .uri("http://MEMBRESIAS/api/membresias/" + dto.getIdmembresia())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> Mono.empty())
                .onStatus(status -> status.is5xxServerError(), response ->
                        Mono.error(new RuntimeException("Error interno al buscar la membresia el pago")))
                .bodyToMono(MembresiaDTO.class)
                .onErrorResume(e -> Mono.empty())
                .block();

        if(membresia == null) {
            throw new RuntimeException("Membresia con el id: " + dto.getIdmembresia() + " no encontrada");
        }

        // una vez exista el usuario y los datos de la membresia existan tambien, se procesa el pago.
        webClientBuilder.build()
                .put()
                .uri("http://USUARIO/gym/socios/procesarpago/" + dto.getRun())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("nombreMembresia", membresia.getTipoPlan()))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> Mono.empty())
                .onStatus(status -> status.is5xxServerError(), response ->
                        Mono.error(new RuntimeException("Error interno al procesar el pago")))
                .bodyToMono(Void.class)
                .onErrorResume(e -> Mono.empty())
                .block();

        // Aca se guarda el pago.
        Pago pagoNuevo = new Pago();
            pagoNuevo.setRunPagado(dto.getRun());
            pagoNuevo.setMontoPagado(membresia.getPrecio());
            pagoNuevo.setMembresiaPagada(dto.getIdmembresia());
            pagoNuevo.setFechapago(LocalDateTime.now());
            pagoNuevo.setTipoPlan(membresia.getTipoPlan());
            pagoRepository.save(pagoNuevo);
            return mapToDTO(pagoNuevo);
    }

    // Registrar pagos de la tienda
    public PagoTiendaDTO registrarPagoTienda(@Valid @RequestBody PagoTiendaDTO dto) {

        //buscar producto con su id
        PagoTiendaDTO producto = webClientBuilder.build()
                //Aca se busca el socio en el ms-usuarios
                .get()
                .uri("http://MS-TIENDA/gym/tienda/busqueda/id/" + dto.getIdProducto())
                //.header("Authorization", token)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> Mono.empty())
                .onStatus(status -> status.is5xxServerError(), response ->
                        Mono.error(new RuntimeException("Error interno en ms de tienda")))
                .bodyToMono(PagoTiendaDTO.class)
                .onErrorResume(e -> Mono.empty())
                .block();
        System.out.println("JSON TEST" + producto);

        if(producto == null){
            throw new RuntimeException("Producto con el id: " + dto.getIdProducto() + " no encontrado");
        }

        PagoTienda pagoNuevoTienda = new PagoTienda();
        pagoNuevoTienda.setNombreProducto(producto.getNombreProducto());
        pagoNuevoTienda.setIdProducto(dto.getIdProducto());
        pagoNuevoTienda.setPrecioProducto(producto.getPrecioProducto());

        tiendaRepository.save(pagoNuevoTienda);
        return mapToDTOTienda(pagoNuevoTienda);

    }

    // Listar todos los pagos
    public List<PagoTiendaDTO> listarAllPagosTienda(){
        return tiendaRepository.findAll()
                .stream()
                .map(this::mapToDTOTienda)
                .collect(Collectors.toList());
    }
}
