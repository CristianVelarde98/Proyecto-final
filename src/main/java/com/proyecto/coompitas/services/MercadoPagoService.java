package com.proyecto.coompitas.services;

import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.proyecto.coompitas.models.Pedido;
import com.proyecto.coompitas.models.PedidoProducto;
import com.proyecto.coompitas.models.Producto;
import com.proyecto.coompitas.repositories.PedidoProductoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class MercadoPagoService {

    private final PedidoProductoRepository pedidoProductoRepository;

    public MercadoPagoService(PedidoProductoRepository pedidoProductoRepository) {
        this.pedidoProductoRepository = pedidoProductoRepository;
    }

    public String crearPreferenciaDePago(Pedido pedido, Long id) {
        try {

            // Obtener la lista de relaciones de pedido a través del servicio
            List<PedidoProducto> relacionesPedido = pedidoProductoRepository.findAllByPedidoId(id);

            // Crear la lista de elementos (productos) para la preferencia
            List<PreferenceItemRequest> items = new ArrayList<>();

            // Iterar a través de las relaciones de pedido
            for (PedidoProducto relacion : relacionesPedido) {
                Producto producto = relacion.getProducto();
                int cantidadTotal = relacion.getCantidad();

                // Calcular el precio del producto con descuento
                double precioUnitarioConDescuento = ((producto.getPrecio() * cantidadTotal) - ((producto.getPrecio() * cantidadTotal * relacion.getDescuentoVigente()) / 100)) / cantidadTotal;

                // Crear un elemento (producto) para la preferencia
                PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                        .id(producto.getId().toString()) // Usa el ID del producto como ID del elemento
                        .title(producto.getNombre()) // Nombre del producto
                        .description(producto.getDescripcion()) // Descripción del producto
                        .pictureUrl(producto.getImagen()) // URL de la imagen del producto
                        .categoryId(producto.getCategoria()) // Categoría del producto
                        .quantity(cantidadTotal) // Cantidad total de este producto en el pedido
                        .currencyId("AR")
                        .unitPrice(BigDecimal.valueOf(precioUnitarioConDescuento)) // Precio unitario del producto con descuento aplicado
                        .build();

                items.add(itemRequest);
            }

            // agregamos el costo de envio
            double costoDeEnvio = pedido.getCamara().getPrecioEnvio() / pedido.getCamara().getParticipantes().size();
            PreferenceItemRequest envioItemRequest = PreferenceItemRequest.builder()
                    .id("envio")
                    .title("Costo de Envío")
                    .quantity(1)
                    .currencyId("AR")
                    .unitPrice(BigDecimal.valueOf(costoDeEnvio))
                    .build();
            items.add(envioItemRequest);

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(PreferenceBackUrlsRequest.builder()
                            .success("http://localhost:8080/pedido/pagado/" + id)
                            .failure("http://localhost:8080/pedido/rechazado/" + id)
                            .build())
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            return preference.getInitPoint();
        } catch (MPException exception) {
            // Manejo de excepciones de Mercado Pago, por ejemplo, MPException
            exception.printStackTrace();
            return null;
        } catch (MPApiException ex){
            return null;
        }
    }
}


