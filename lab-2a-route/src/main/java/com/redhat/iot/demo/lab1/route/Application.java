package com.redhat.iot.demo.simulator.route;

import com.redhat.iot.demo.lab1.route.CsvProcess;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath:spring/camel-context.xml"})
public class Application extends RouteBuilder {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void configure() throws Exception {

        final CsvDataFormat csv =
                new CsvDataFormat()
                        .setIgnoreEmptyLines(true)
                        .setUseMaps(true)
                        .setCommentMarker('#')
                        .setHeader(new String[]{"offset","sensor0","sensor1","sensor2","sensor3","sensor4","sensor5","sensor6"});

        from("file:/deployments/data/")
                .split(body().tokenize("\n")).streaming()

.unmarshal(csv)
.delay(1000)
.process(new CsvProcess())
.marshal().json(JsonLibrary.Jackson)
.setHeader(Exchange.HTTP_METHOD,constant("POST"))
.setHeader(Exchange.CONTENT_TYPE,constant("application/json"))

                .to("http://lab-1-web/iot");
    }
}
