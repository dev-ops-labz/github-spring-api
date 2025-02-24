package org.git.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

@Configurable
public class OpenApiConfig {

    @Bean
    public OpenAPI customConfig() {
        return new OpenAPI()
                .info(new Info()
                        .title("Github API")
                        .description("Github API access through Github App")
                        .version("1.0.0"));
    }
}
