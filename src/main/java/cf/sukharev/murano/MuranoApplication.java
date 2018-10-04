package cf.sukharev.murano;

import cf.sukharev.murano.repo.custom.DeleteAwareBaseRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.List;

@EnableWs
@EnableJpaRepositories(repositoryBaseClass = DeleteAwareBaseRepositoryImpl.class)
@SpringBootApplication
public class MuranoApplication extends WsConfigurerAdapter {
    public static final String MURANO_NAMESPACE = "https://muranosoft.com/test";

    public static void main(String[] args) {
        SpringApplication.run(MuranoApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "murano")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema muranoTestSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setSchema(muranoTestSchema);
        wsdl11Definition.setPortTypeName("MuranoPort");
        wsdl11Definition.setTargetNamespace(MURANO_NAMESPACE);
        wsdl11Definition.setLocationUri("/ws");
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema muranoTestSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/murano.xml"));
    }

    @Bean
    public PayloadValidatingInterceptor payloadValidatingInterceptor() {
        PayloadValidatingInterceptor interceptor = new PayloadValidatingInterceptor();
        interceptor.setXsdSchema(muranoTestSchema());
        interceptor.setAddValidationErrorDetail(true);
        interceptor.setValidateRequest(true);
        return interceptor;
    }

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        interceptors.add(payloadValidatingInterceptor());
    }
}
