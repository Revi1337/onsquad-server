package revi1337.onsquad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan(basePackages = "revi1337.onsquad")
@SpringBootApplication
public class OnsquadServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnsquadServerApplication.class, args);
    }

}
