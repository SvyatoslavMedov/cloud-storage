package com.geekbrains.cloud.nio;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BufferExamples {
    public static void main(String[] args) {

        ByteBuffer buf = ByteBuffer.allocate(7);
        buf.put("Hello".getBytes(StandardCharsets.UTF_8));


        buf.flip();

        while(buf.hasRemaining()) {
            byte b = buf.get();
            System.out.print((char)b);
        }
        System.out.println();

        buf.flip();

        while(buf.hasRemaining()) {
            byte b = buf.get();
            System.out.print((char)b);
        }
        System.out.println();


        buf.rewind();
        buf.get();
        buf.get();
        buf.get();
        buf.mark();
        buf.get();
        buf.get();
        buf.reset();

        while(buf.hasRemaining()) {
            byte b = buf.get();
            System.out.print((char )b);
        }
        System.out.println();
    }
}
