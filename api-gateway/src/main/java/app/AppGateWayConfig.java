package app;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppGateWayConfig {

    @Bean
    public HttpClientCustomizer httpClientResolverCustomizer() {
        return httpClient -> httpClient
                .resolver(DefaultAddressResolverGroup.INSTANCE);
    }

    @Bean
    AuthorizationHeaderFilter authorizationHeaderFilter() {
        return new AuthorizationHeaderFilter();
    }

    @Bean
    AppPreFilter appPreFilter() {
        return new AppPreFilter();
    }


}
