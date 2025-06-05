package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;

@SpringBootApplication
public class SpringApp {
    //ReactorHttpHandlerAdapter
    //HttpWebHandlerAdapter
    //DispatcherHandler
    public static void main(String[] args) {
        SpringApplication.run(SpringApp.class, args);
    }
}
