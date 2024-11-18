/*
 * This source file was generated by the Gradle 'init' task
 */
package io.jespen;

import com.eclipsesource.json.JsonObject;
import io.jespen.lib.Message;
import io.jespen.lib.ReqBuilder;
import io.jespen.lib.handlers.EchoNode;
import io.jespen.lib.handlers.EchoNodeV2;
import io.jespen.lib.handlers.MessageHandler;
import io.jespen.lib.handlers.NodeV2;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class Runner {

    static BiConsumer<Message, Throwable> outConsumer = (message,ex) -> {
//        System.err.println("Outputting " + message);
        JsonObject res = new JsonObject()
                .add("src", message.headers().src())
                .add("dest", message.headers().dest())
                .add("body", message.payload().getJsonObject());
        System.out.println(res.toString());
    };

    public static void main(String[] args) throws IOException {

        NodeV2 messageHandler = new EchoNodeV2();
//        MessageHandler messageHandler = new EchoNode();

        try (Scanner scanner = new Scanner(System.in);) {
            
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                CompletableFuture<Message> resFuture = CompletableFuture
                        .supplyAsync(() -> new ReqBuilder(line).build());

//                System.out.println("here");

                resFuture.thenApply(messageHandler::handle)
                        .whenComplete(outConsumer)
                        .join();
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
