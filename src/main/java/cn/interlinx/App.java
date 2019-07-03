package cn.interlinx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.stereotype.Repository;
import org.springframework.web.WebApplicationInitializer;

@ServletComponentScan
@SpringBootApplication
@MapperScan(basePackages = "cn.interlinx.dao", annotationClass = Repository.class)
public class App extends SpringBootServletInitializer implements WebApplicationInitializer {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
//        SpringApplication app = new SpringApplication(App.class);
//        app.addListeners(new ApplicationStartup());
//        app.run(args);
    }

    @Override//为了打包springboot项目
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }
}
