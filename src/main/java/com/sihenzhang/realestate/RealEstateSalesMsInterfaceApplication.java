package com.sihenzhang.realestate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class RealEstateSalesMsInterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealEstateSalesMsInterfaceApplication.class, args);
    }

}
