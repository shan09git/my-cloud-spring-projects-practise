package app;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientResolverFixConfig {

    @Bean
    public HttpClientCustomizer httpClientResolverCustomizer() {
        return httpClient -> httpClient
                .resolver(DefaultAddressResolverGroup.INSTANCE);
    }

}
