package com.kynsoft.finamer.creditcard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling  // Habilitar la programación
@SpringBootApplication
public class CreditCardApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditCardApplication.class, args);
    }

}
