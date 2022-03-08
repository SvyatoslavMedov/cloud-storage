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

public class EchoServerNio {

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ByteBuffer buf;
    private Path currentDir;

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
        if("mkdir".equals(msg)){
            File dir2 = new File("server/testfolder/");
            boolean created = dir2.mkdir();
            if(created){
                out.println("Folder has been created");
            }

//            File newDir2 = new File("testfoldernew");
//            boolean deleted = newDir2.delete();
//            if(deleted){
//                System.out.println("Folder has been deleted");
//            }
        }
        if("touch file".equals(msg)) {
            try {
                String msg2 = reader.toString().trim();
                byte[] name = msg2.getBytes(StandardCharsets.UTF_8);
                out.write(name.length);
                out.write(name);

                File file = new File("testfolder/1.txt");

                long fileSize = file.length();
                ByteBuffer buf = ByteBuffer.allocate(Long.BYTES);
                buf.putLong(fileSize);
                out.write(buf.array());

                try (FileInputStream in = new FileInputStream(file)) {
                    // Читаем файл блоками по килобайту
                    byte[] data = new byte[1024];
                    int read;
                    while ((read = in.read(data)) != -1) {
                        // И отправляем в сокет
                        out.write(data);
                    }
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            }catch (IOException exc){
                exc.printStackTrace();
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
