package com.example.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class OperatorVerticle extends AbstractVerticle {
    private int count = 0;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.eventBus().consumer("postOperator", msg -> {
            count++;
            JsonObject param = (JsonObject) msg.body();
            int a = param.getInteger("a");
            int b = param.getInteger("b");
            String op = param.getString("op");
            int result = 0;
            switch (op) {
                case "+": {
                    result = a + b;
                    break;
                }
                case "-": {
                    result = a - b;
                    break;
                }
                case "*": {
                    result = a * b;
                    break;
                }
                case "/": {
                    result = a / b;
                    break;
                }
                default:
                    break;
            }
            JsonObject resultOperator = new JsonObject();
            resultOperator.put("result", result);
            if (count > 3) {
                resultOperator.put("Notification", "Use more than 3 times");
                msg.reply(resultOperator.encodePrettily());
                System.out.println("Use more than 3 times");
                count = 0;
            } else {
                msg.reply(resultOperator.encodePrettily());
            }
        });

    }
}
