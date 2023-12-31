package com.proyecto.coompitas.controllers;

import com.proyecto.coompitas.models.Camara;
import com.proyecto.coompitas.models.Pedido;
import com.proyecto.coompitas.models.PedidoProducto;
import com.proyecto.coompitas.models.User;
import com.proyecto.coompitas.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Controller
public class PagosController {
    private final CamaraService camaraService;
    private final UserService userService;
    private final PedidoProductoService pedidoProductoService;
    private final PedidoService pedidoService;
    private final MercadoPagoService mercadoPagoService;

    public PagosController
            (CamaraService camaraService,
             UserService userService,
             PedidoProductoService pedidoProductoService,
             PedidoService pedidoService,
             MercadoPagoService mercadoPagoService)
    {
        this.camaraService = camaraService;
        this.userService = userService;
        this.pedidoProductoService = pedidoProductoService;
        this.pedidoService = pedidoService;
        this.mercadoPagoService = mercadoPagoService;
    }

    //POST PARA CAMBIAR EL ESTADO DE LA CAMARA PARA QUE SE PUEDA PAGAR
    @PostMapping("/camara/{idCamara}/estadoDePago")
    public String cambiarEstadoDePago
    (
            @PathVariable("idCamara") Long idCamara,
            HttpSession session
    )
    {
        Long idLogueado = (Long) session.getAttribute("idLogueado");
        if (idLogueado != null) {
            Camara camara = camaraService.findCamara(idCamara);
            if(camara.getEstadoDeLaCamara() == 3){
                camara.setEstadoDeLaCamara(2);
            }else{
                camara.setEstadoDeLaCamara(3);
            }
            camaraService.createCamara(camara);
            return "redirect:/camara/"+idCamara;
        }else{
            System.out.println("No hay usuario logueado");
            return "redirect:/login";
        }
    }

    //GET PARA RENDERIZAR LA PAGINA DE PAGO DEL PEDIDO
    @GetMapping("/pagar/{idCamara}/{idPedido}")
    public String renderPagarPedido
    (
            @PathVariable("idCamara") Long idCamara,
            @PathVariable("idPedido") Long idPedido,
            HttpSession session,
            Model viewModel
    )
    {
        Long idLogueado = (Long) session.getAttribute("idLogueado");
        if (idLogueado == null) {
            System.out.println("No hay usuario logueado");
            return "redirect:/login";
        }

        Camara camara = camaraService.findCamara(idCamara);
        Pedido pedido = pedidoService.buscarPedidoPorId(idPedido);
        List<PedidoProducto> relacionesPedido = pedidoProductoService.buscarPorPedido(idPedido);
        User usuarioLogueado = userService.findUserById(idLogueado);

        // System.out.println(((relacionesPedido.get(0).getProducto().getPrecio() * relacionesPedido.get(0).getCantidad()) - (((relacionesPedido.get(0).getProducto().getPrecio() * relacionesPedido.get(0).getCantidad()) * relacionesPedido.get(0).getDescuentoVigente()) / 100)) / relacionesPedido.get(0).getCantidad());

        viewModel.addAttribute("userLogueado", usuarioLogueado);
        viewModel.addAttribute("camara", camara);
        viewModel.addAttribute("pedido", pedido);
        viewModel.addAttribute("carrito", relacionesPedido);

        return "paginas_pagos/pagarPedidoPage";
    }

    //POST PARA ENVIAR LA CÁMARA DE PEDIDOS (ULTIMO ESTADO)
    @PostMapping("/camara/{idCamara}/enviar")
    public String enviarCamara
    (
            @PathVariable("idCamara") Long idCamara,
            @RequestParam("fechaDeLlegada")
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDeLlegada,//Esta es la forma de aplicarle el formato
            HttpSession session
    )
    {
        Long idLogueado = (Long) session.getAttribute("idLogueado");
        if (idLogueado != null) {
            Camara camara = camaraService.findCamara(idCamara);
            camara.setEstadoDeLaCamara(6);
            camara.setFechaDeLlegada(fechaDeLlegada);
            camaraService.createCamara(camara);
            return "redirect:/perfil";
        }else{
            System.out.println("No hay usuario logueado");
            return "redirect:/login";
        }
    }

    //POST PARA MARCAR QUE SE RECIBIÓ LA CÁMARA CORRECTAMENTE EN LA DIRECCIÓN ESTIPULADA
    //@PostMapping("/camara/{idCamara}/recibida") continuará...

    //POST PARA MARCAR QUE CADA PARTICIPANTE RETIRÓ EL PEDIDO DE LA UBICACIÓN DESIGNADA

}
