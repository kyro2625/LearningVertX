package com.example.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.oracleclient.OracleConnectOptions;
import io.vertx.oracleclient.OraclePool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;


public class DatabaseVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.eventBus().consumer("getDataFromDatabase.test", msg -> {
//            OracleConnectOptions connectOptions = new OracleConnectOptions()
//                    .setPort(1521)
//                    .setHost("26.231.160.60")
//                    .setDatabase("TestVertX")
//                    .setUser("nguyennguyen13")
//                    .setPassword("Starone2625");
//
//// Pool options
//            PoolOptions poolOptions = new PoolOptions()
//                    .setMaxSize(5);
//
//// Create the client pool
//            OraclePool client = OraclePool.pool(connectOptions, poolOptions);
//
//// A simple query
//            client
//                    .query("SELECT * FROM DATA WHERE id='1'")
//                    .execute(ar -> {
//                        if (ar.succeeded()) {
//                            RowSet<Row> result = ar.result();
//                            System.out.println("Got " + result.size() + " rows ");
//                        } else {
//                            System.out.println("Failure: " + ar.cause().getMessage());
//                        }
//
//                        // Now close the pool
//                        client.close();
//                    });
            JDBCPool pool = JDBCPool.pool(
                    vertx,
                    // configure the connection
                    new JDBCConnectOptions()
                            // H2 connection string
                            .setJdbcUrl("jdbc:oracle:thin:@26.231.160.60:1521:TestVertX")
                            // username
                            .setUser("nguyennguyen13")
                            // password
                            .setPassword("Starone2625"),
                    // configure the pool
                    new PoolOptions()
                            .setMaxSize(16)
            );
            pool
                    .getConnection()
                    .onFailure(e -> {
                        // failed to get a connection
                        System.out.println("Check the connection");
                        msg.reply("Something when wrong with the connection");

                    })
                    .onSuccess(conn -> conn
                            .query("SELECT * FROM DATA")
                            .execute()
                            .onFailure(e -> {
                                // handle the failure
                                System.out.println("Check the query");
                                msg.reply("Something when wrong with the logic");
                                // very important! don't forget to return the connection
                                conn.close();
                            })
                            .onSuccess(rows -> {
                                msg.reply("Successful on getting the data from database");
                                for (Row row : rows) {
                                    System.out.println(row.getString("NAME"));
                                }
                                // very important! don't forget to return the connection
                                conn.close();
                            }));
            System.out.println("Execute Database Verticle Completed!");

        });

    }
}
