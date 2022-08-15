package com.sihenzhang.realestate.config;

import cn.dev33.satoken.interceptor.SaAnnotationInterceptor;
import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${image.path}")
    private String imagePath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOriginPatterns("*").allowedMethods("*").allowedHeaders("*").allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaAnnotationInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new SaRouteInterceptor((req, res, handler) -> {
            // 预检请求不拦截
            SaRouter.match(SaHttpMethod.OPTIONS).stop();
            // 进行登录校验
            StpUtil.checkLogin();
        })).addPathPatterns("/**").excludePathPatterns("/users/login", "/" + imagePath + "/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path imageDirectoryPath = Paths.get(imagePath);
        String imageDirectoryCanonicalPath = FileUtil.getCanonicalPath(imageDirectoryPath.toFile());
        registry.addResourceHandler("/" + imagePath + "/**")
                .addResourceLocations("file:/" + imageDirectoryCanonicalPath + "/");
    }
}
