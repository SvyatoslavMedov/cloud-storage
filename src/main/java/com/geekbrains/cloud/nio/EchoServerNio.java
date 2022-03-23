package com.geekbrains.cloud.nio;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import sun.jvm.hotspot.tools.jcore.ClassWriter;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.System.out;
import static java.lang.System.setOut;

public class EchoServerNio {

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ByteBuffer buf;
    private Path currentDir;
    private String filename;
    private String pathName;

    public EchoServerNio() throws Exception {

        buf = ByteBuffer.allocate(5);
        currentDir = Paths.get("server");
        serverSocketChannel = ServerSocketChannel.open();
        selector = Selector.open();
        serverSocketChannel.bind(new InetSocketAddress(8189));
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while(serverSocketChannel.isOpen()){
            selector.select();

            Set<SelectionKey> keys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = keys.iterator();
            while(iterator.hasNext()) {
                SelectionKey currentKey = iterator.next();
                if(currentKey.isAcceptable()){
                    handleAccept();
                }
                if(currentKey.isReadable()) {
                    handleRead(currentKey);
                }
                iterator.remove();
            }
        }

    }

    private void handleRead(SelectionKey currentKey) throws IOException {
        SocketChannel channel = (SocketChannel) currentKey.channel();

        StringBuilder reader = new StringBuilder();

        while(true) {
            int count = channel.read(buf);
            if(count == 0) {
                break;
            }
            if(count == -1) {
                channel.close();
                return;
            }
            buf.flip();
            while(buf.hasRemaining()) {
                reader.append((char)buf.get());
            }

            buf.clear();
        }

        String msg = reader.toString().trim();
        if("ls".equals(msg)){
            channel.write(ByteBuffer.wrap(getFiles(currentDir).getBytes(StandardCharsets.UTF_8)));
        }
        if("mkdir".equals(msg)) {
            int i ;
            String fileName = null;
            String pathName = null;
            for (i = 0; i < 100; i++) {


                fileName = i  + ".txt";

                pathName = "server/" + fileName;
                File dir2 = new File(pathName);
                boolean created = dir2.mkdir();
                if (created) {
                    out.println("Folder has been created");
                    break;
                    }
                }

        }

        if("touch file".equals(msg)) {
            try {
                File f = new File(pathName);
                if (f.createNewFile()) {
                    System.out.println("File created");
                } else {
                    System.out.println("File already exists");
                }
            }catch(Exception e){
                    System.err.println(e);
            }
        }
        if("cat file".equals(msg)) {
            try (BufferedReader in = new BufferedReader(new FileReader("server/testfolder/1.txt"))) {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
            }
        }





        printPrelude(channel);
//        System.out.println("Received: " + msg);
        channel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));

    }

    private String getFiles(Path path) throws IOException {
        return Files.list(path)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.joining("\n")) + "\n\r";
    }
    private void printPrelude(SocketChannel channel) throws IOException {
        channel.write(ByteBuffer.wrap("->".getBytes(StandardCharsets.UTF_8)));
    }

    private void handleAccept() throws Exception {
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        socketChannel.write(ByteBuffer.wrap("Hello in Slava terminal\n\r".getBytes(StandardCharsets.UTF_8)));
        printPrelude(socketChannel);
        out.println("Client accepted....");
    }

    public static void main(String[] args) throws Exception {
        new EchoServerNio();

    }
}
