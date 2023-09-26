package com.proyecto.coompitas;

import com.mercadopago.MercadoPagoConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoompitasApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(CoompitasApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception{
        MercadoPagoConfig.setAccessToken("TEST-2361479918763548-092502-f1cd3e46a05997c58ccdb95111242015-269860643");
    }

}
