package com.example.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
        vertx.deployVerticle(new HelloVerticle());
        vertx.deployVerticle(new OperatorVerticle());
        vertx.deployVerticle(new DatabaseVerticle());

        Router router = Router.router(vertx);
        router.get("/api/v1/hello").handler(this::helloVerx);
        router.get("/api/v1/hello/:name").handler(this::helloName);
        router.get("/getOperator").handler(this::getOperator);
        router.post("/postOperator").handler(BodyHandler.create()).handler(this::postOperator);
        router.get("/getDataFromDatabase").handler(this::getDataFromDatabase);
        vertx.createHttpServer().requestHandler(router).listen(8888);
        vertx.setPeriodic(1000000, id -> {
            System.out.println("time fired");
            System.out.println("Do something!!!");
        });
    }

    void helloVerx(RoutingContext context) {
        vertx.eventBus().request("hello.vertx.addr","", reply -> context.request().response().end((String)reply.result().body()));
    }

    void helloName(RoutingContext context) {
        String name = context.pathParam("name");
        vertx.eventBus().request("hello.name.addr", name, reply -> context.request().response().end((String)reply.result().body()));
    }

    void getOperator(RoutingContext context) {
        String a = context.request().getParam("a");
        String b = context.request().getParam("b");
        if (!a.isEmpty() && !b.isEmpty()) {
            int result = Integer.parseInt(a) + Integer.parseInt(b);
            context.json(new JsonObject()
                    .put("a", a)
                    .put("b", b)
                    .put("result= a+b", result));
        } else if (a.equals("") || b.equals("")) {
            context.request().response().end("Invaild input");
        }
    }

    void postOperator(RoutingContext context) {
        JsonObject param = context.getBodyAsJson();
        vertx.eventBus().request("postOperator",param, reply->{
            context.request().response().end((String)reply.result().body());
        });
    }
    void getDataFromDatabase(RoutingContext context) {
        vertx.eventBus().request("getDataFromDatabase.test","", reply -> {
            context.request().response().end((String)reply.result().body());
        });
//        vertx.eventBus().request("getDataFromDatabase.test","");
    }
}
