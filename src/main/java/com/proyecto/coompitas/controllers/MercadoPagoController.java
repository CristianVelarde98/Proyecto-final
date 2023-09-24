package com.proyecto.coompitas.controllers;

import com.proyecto.coompitas.models.Pedido;
import com.proyecto.coompitas.services.MercadoPagoService;
import com.proyecto.coompitas.services.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MercadoPagoController {
    private final MercadoPagoService mercadoPagoService;
    private final PedidoService pedidoService;
    private boolean pasoPorMp = false;


    public MercadoPagoController(MercadoPagoService mercadoPagoService, PedidoService pedidoService) {
        this.mercadoPagoService = mercadoPagoService;
        this.pedidoService = pedidoService;
    }

    @PostMapping("/crear-preferencia/{idPedido}")
    public String crearPreferenciaDePago(
            @PathVariable("idPedido") Long idPedido,
            HttpSession session
    )
    {
        Long idLogueado = (Long) session.getAttribute("idLogueado");
        if (idLogueado == null){
            return "redirect:/login";
        }

        // Obtén el pedido u otros datos necesarios
        Pedido pedido = pedidoService.buscarPedidoPorId(idPedido);

        // Llama al servicio de Mercado Pago para crear la preferencia de pago
        String linkDePago = mercadoPagoService.crearPreferenciaDePago(pedido, idPedido);

        if (linkDePago != null) {
            pasoPorMp = true;
            return "redirect:" + linkDePago;
        }

        // caso algo salga mal
        return "/manejo_de_errores/errorPageMP";
    }

    @GetMapping("/pedido/pagado/{idPedido}")
    public String pedidoPagado(
            @PathVariable("idPedido") Long idPedido,
            HttpSession session
    )
    {
        Long idLogueado = (Long) session.getAttribute("idLogueado");
        if (idLogueado == null || !pasoPorMp){
            System.out.println("No hay usuario logueado");
            return "redirect:/login";
        }

        Pedido pedido = pedidoService.buscarPedidoPorId(idPedido);

        if(pedido.getEstadoDelPedido() == 1){
           pedido.setEstadoDelPedido(2);
        }

        pedidoService.crearPedido(pedido); // es para salvar el cambio del estado
        return "redirect:/camara/"+ pedido.getCamara().getId();
    }

    @GetMapping("/pedido/rechazado/{idPedido}")
    public String pedidoRechazado(
            @PathVariable("idPedido") Long idPedido,
            HttpSession session
    )
    {
        Long idLogueado = (Long) session.getAttribute("idLogueado");
        if (idLogueado == null){
            return "redirect:/login";
        }

        pasoPorMp = false;

        Pedido pedido = pedidoService.buscarPedidoPorId(idPedido);

        return "redirect:/camara/"+pedido.getCamara().getId();
    }
}
