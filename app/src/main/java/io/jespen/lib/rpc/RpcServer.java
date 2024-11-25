package io.jespen.lib.rpc;

import io.jespen.lib.handlers.Broadcast;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcServer extends Broadcast {

    HashSet<SocketChannel> allClients = new HashSet<SocketChannel>();

    public void start(final int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.err.println("RPC Server started on port " + port);
            try (ExecutorService rpcSrvrExecutor = Executors.newVirtualThreadPerTaskExecutor();) {
                while (true) {
                    Socket client = serverSocket.accept();
                    rpcSrvrExecutor.submit(() -> {
                        System.err.println("RPC client accepted");
                        int cliPort = client.getPort();
                        try (PipedInputStream pipedInputStream = new PipedInputStream();
                                PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
//                                BufferedReader cliInpStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
//                            PrintWriter cliOut = new PrintWriter(client.getOutputStream(), true)
                        ) {
//                            for (String inpLine = cliInpStream.readLine(); inpLine != null;) {
//                                System.err.println(cliPort + " : Message Received => " + inpLine);
//                                // TODO: Handle the Gossip message here
//                                cliOut.println("OK");
//                            }
                            for (String inpLine = pipedInputStream.; inpLine != null;) {

                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
