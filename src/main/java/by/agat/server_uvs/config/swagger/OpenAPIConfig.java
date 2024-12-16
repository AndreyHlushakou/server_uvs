package by.agat.server_uvs.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${app.openapi.deploy-url}")
    private String deployUrl;

    @Bean
    public OpenAPI myOpenApi() {
        Server deployServer = new Server();
        deployServer.setUrl(deployUrl);
        deployServer.setDescription("Deploy server");

        Contact contact = new Contact();
        contact.setEmail("agat@agat.by");
        contact.setName("AGAT");
        contact.setUrl("https://agat.by/");

        Info info = new Info();
        info.setTitle("Server UVS API");
        info.version("1.0");
        info.contact(contact);
        info.description("This API describe our service");
        return new OpenAPI().info(info).servers(List.of(deployServer));
    }
}
