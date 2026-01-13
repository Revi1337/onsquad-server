package revi1337.onsquad.common.config.system.properties;

import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import revi1337.onsquad.common.constant.Sign;

@Getter
@ToString
@ConfigurationProperties("onsquad")
public class OnsquadProperties {

    @Value("${spring.application.name:onsquad}")
    private String applicationName;

    @Value("${server.port:8080}")
    private int serverPort;

    private final String frontendBaseUrl;

    public OnsquadProperties(String frontendBaseUrl) {
        this.frontendBaseUrl = frontendBaseUrl;
    }

    public String getIdentifier() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            return String.join(Sign.COLON, ip, String.valueOf(serverPort));
        } catch (UnknownHostException e) {
            return "Unknown-Host";
        }
    }
}
