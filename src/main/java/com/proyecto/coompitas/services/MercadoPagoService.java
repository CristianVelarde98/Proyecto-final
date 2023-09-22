package com.proyecto.coompitas.services;

import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.proyecto.coompitas.models.Pedido;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class MercadoPagoService {

    public String crearPreferenciaDePago(Pedido pedido) {
        try {
            // Construye los detalles de la preferencia de pago
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id("1")
                    .title("Consola de Videojuegos")
                    .description("PS5")
                    .pictureUrl("https://acdn.mitiendanube.com/stores/186/318/products/chupa-chups-individual1-fec4a0bf51f373f85c16251518373905-640-0.png")
                    .categoryId("games")
                    .quantity(1)
                    .currencyId("AR")
                    .unitPrice(new BigDecimal("40000"))
                    .build();

            List<PreferenceItemRequest> items = Collections.singletonList(itemRequest);

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(PreferenceBackUrlsRequest.builder()
                            .success("http://localhost:8080/")
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
