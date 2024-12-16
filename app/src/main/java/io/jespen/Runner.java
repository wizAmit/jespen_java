/*
 * This source file was generated by the Gradle 'init' task
 */
package io.jespen;

import com.eclipsesource.json.JsonObject;
import io.jespen.lib.Message;
import io.jespen.lib.ReqBuilder;
import io.jespen.lib.handlers.Broadcast;
import io.jespen.lib.handlers.NodeV2;
import io.jespen.lib.rpc.SelectorSrvr;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.BiConsumer;

public class Runner {

    static BiConsumer<Message, Throwable> outConsumer = (message, ex) -> {
        if (ex != null) {
            System.err.println("outConsumer Exception: " + ex.getLocalizedMessage());
        }

//        System.err.println("Outputting " + message);
        if (message == null) return;
        JsonObject res = new JsonObject()
                .add("src", message.headers().src())
                .add("dest", message.headers().dest())
                .add("body", message.payload().getJsonObject());
        System.out.println(res.toString());
    };

    static ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    });

    public static void main(String[] args) throws IOException {

//        NodeV2 messageHandler = new EchoNodeV2();
//        NodeV2 messageHandler = new UniqIdNode();
        NodeV2 messageHandler = new Broadcast();
        CompletableFuture<Void> rpcSrvr = null;
        CompletableFuture<Void> rpcClnt = null;

        AsynchronousChannelGroup serverChannelGroup = AsynchronousChannelGroup.withThreadPool(executor);

        try (Scanner scanner = new Scanner(System.in);
             AsynchronousSocketChannel rpcClientChannel = AsynchronousSocketChannel.open(serverChannelGroup);) {

            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                CompletableFuture<Message> resFuture = CompletableFuture
                        .supplyAsync(() -> new ReqBuilder(line).build());

                resFuture.thenApply(messageHandler::handle)
                        .whenComplete(outConsumer)
                        .join();

                rpcSrvr = (rpcSrvr == null) ? CompletableFuture.runAsync(() -> {
                    try {
                        new SelectorSrvr((Broadcast) messageHandler).start();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, executor) : rpcSrvr;


            }
            rpcSrvr.join();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

    }
}
