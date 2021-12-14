package com.example.demo;

import io.vertx.core.AbstractVerticle;

public class HelloVerticle extends AbstractVerticle {
    @Override
    public void start() {
        vertx.eventBus().consumer("hello.vertx.addr", msg -> {
            int a =4;
            msg.reply("Hello Vertx World" + a);
            System.out.println("Message replied success");
        });
        vertx.eventBus().consumer("hello.name.addr", msg -> {
            String name = (String)msg.body();
            msg.reply(String.format("Hello %s", name));
            System.out.println("Message replied success2");

        });
    }

}
