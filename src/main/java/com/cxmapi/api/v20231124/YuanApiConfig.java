package com.cxmapi.api.v20231124;

import com.cxmapi.common.model.Config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("yuanapi.config")
@Data
@ComponentScan
public class YuanApiConfig {

    private String accessKey;
    private String secretKey;
    private String baseurl;

    @Bean
    public Config yuanApiConfig(){
        return new Config.Builder()
                .setBaseurl(baseurl)
                .setAccessKey(accessKey)
                .setSecretKey(secretKey)
                .build();
    }


}
