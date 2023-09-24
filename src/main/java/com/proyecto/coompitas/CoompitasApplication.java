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
        MercadoPagoConfig.setAccessToken("TEST-6874223181035381-091606-e986a9cfbf3c55c4c1a924de61bf8cb9-269860643");
    }

}
