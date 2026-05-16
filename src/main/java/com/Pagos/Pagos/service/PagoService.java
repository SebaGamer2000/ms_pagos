package com.Pagos.Pagos.service;

import com.Pagos.Pagos.clientes.ProductoFeignClient;
import com.Pagos.Pagos.dto.PagoRequestDTO;
import com.Pagos.Pagos.dto.PagoResponseDTO;
import com.Pagos.Pagos.model.Pago;
import com.Pagos.Pagos.repository.PagoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

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
    private final PagoRepository pagoRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private final ProductoFeignClient productoFeignClient;

    private PagoResponseDTO mapToDTO(Pago pago){
        PagoResponseDTO response = new PagoResponseDTO();
                response.setIdpago(pago.getIdPago());
                response.setRunpagado(pago.getRunPagado());
                response.setMontopagado(pago.getMontoPagado());
                response.setFechapago(pago.getFechapago());
                response.setMembresiapagada(pago.getMembresiaPagada());

                return response;
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
    public Map<String, Object> subirPago(@Valid @RequestBody PagoRequestDTO dto){
        // conectado con el ms de usuarios, se debe chequear si ese run está asociado a algún socio.
        String url = "http://localhost:8081/gym/socios/busqueda/" + dto.getRun();

        // url con el procesamiento de pago
        String urlActualizar = "http://localhost:8081/gym/socios/procesarpago/" + dto.getRun();
        try {
            Map<String, Object> usuarioMap = restTemplate.getForObject(url, Map.class);

            if(usuarioMap != null){
                Pago pago = new Pago();
                pago.setIdPago(null);
                pago.setRunPagado(dto.getRun());
                pago.setFechapago(LocalDateTime.now());
                // aca se debe sacar el valor de la membresia con el ms de membresias y la id
                pago.setMontoPagado(15000L);
                pago.setMembresiaPagada(dto.getIdmembresia());

                pagoRepository.save(pago);
                restTemplate.put(urlActualizar,null);

                String nombresocio = (String) usuarioMap.get("nombre");

                Map<String, Object> respuesta = new HashMap<>();
                respuesta.put("MENSAJE","Pago registrado para el socio "+nombresocio);
                respuesta.put("PAGO",mapToDTO(pago));

                return respuesta;

            }
        }catch (Exception e){
            throw new RuntimeException("El run no está asociado a ningun socio.");
        //throw new ResponseStatusException(HttpStatus.NOT_FOUND,"El run "+ dto.getRun() +" no está asociado a ningun socio.");
        }
        return null;
    }
}
