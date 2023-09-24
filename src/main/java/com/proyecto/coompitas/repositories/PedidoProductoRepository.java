package com.proyecto.coompitas.repositories;

import com.proyecto.coompitas.models.PedidoProducto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoProductoRepository extends CrudRepository<PedidoProducto,Long> {
    List<PedidoProducto> findAll();

    List<PedidoProducto> findAllByPedidoId(Long idPedido);//Buscar tablas por producto y pedido

    PedidoProducto findFirstByPedidoIdAndProductoId(Long idPedido, Long idProducto);//Buscar tablas por producto y pedido

    @Query("SELECT SUM(p.cantidad) FROM PedidoProducto p WHERE p.pedido.id = :pedidoId AND p.producto.id = :productoId")
    Integer sumCantidadByPedidoIdAndProductoId(@Param("pedidoId") Long pedidoId, @Param("productoId") Long productoId);
}
