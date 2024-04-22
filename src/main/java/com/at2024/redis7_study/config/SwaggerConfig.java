package com.at2024.redis7_study.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author lyh
 * @date 2024/4/22  13:49
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private final Boolean enable = true;

    /**
     * 创建API应用
     * apiInfo() 增加API相关信息
     * 通过select()函数返回一个ApiSelectorBuilder实例,用来控制哪些接口暴露给Swagger来展现，
     * 本例采用指定扫描的包路径来定义指定要建立API的目录。
     *
     * @return
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(enable)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.at2024.redis7_study"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 创建该API的基本信息（这些基本信息会展现在文档页面中）
     * 访问地址：http://ip:port/swagger-ui.html
     *
     * @return
     */
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("springBoot利用Swagger2构建api接口文档" + "\t"
                        + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now()))
                .description("springBoot+Redis整合")
                .version("1.0")
                .termsOfServiceUrl("https://blog.csdn.net/2201_75960169")
                .build();
    }

}
