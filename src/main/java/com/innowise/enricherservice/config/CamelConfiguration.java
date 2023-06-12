package com.innowise.enricherservice.config;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.sqs.Sqs2Component;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class CamelConfiguration {

    @Autowired
    private SqsClient sqsClient;

    @Bean
    public CamelContext camelContext() throws Exception {
        CamelContext context = new DefaultCamelContext();

        // Создание компонента SQS с передачей AmazonSQS
        Sqs2Component sqsComponent = new Sqs2Component();
        sqsComponent.getConfiguration().setAmazonSQSClient(sqsClient);

        // Добавление компонента SQS в контекст
        context.addComponent("aws2-sqs", sqsComponent);

        // Определение маршрута для прослушивания SQS
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("aws2-sqs://file-api-queue")
                        .log("Received Message: ${body}")
                        .process(exchange -> {
                            String message = exchange.getIn().getBody(String.class);
                            System.out.println("Processing message: " + message);
                            // Ваша логика обработки сообщения
                            // ...
                            // Опционально: Можете установить новое значение сообщения перед его возвратом
                            exchange.getIn().setBody("Новое сообщение");
                        })
                        .to("log:output");
            }
        });

        return context;
    }
}
