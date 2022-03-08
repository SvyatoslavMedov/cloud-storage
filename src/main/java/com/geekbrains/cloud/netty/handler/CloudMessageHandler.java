package com.geekbrains.cloud.netty.handler;

import com.geekbrains.cloud.netty.model.CloudMessage;
import com.geekbrains.cloud.netty.model.FileMessage;
import com.geekbrains.cloud.netty.model.FileRequst;
import com.geekbrains.cloud.netty.model.ListMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CloudMessageHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private Path serverDir;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        serverDir = Paths.get("server");
        ctx.writeAndFlush(new ListMessage(serverDir));
    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        switch (cloudMessage.getMessageType()) {
            case FILE:
                FileMessage fm = (FileMessage) cloudMessage;
                Files.write(serverDir.resolve(fm.getName()), fm.getBytes());
                ctx.writeAndFlush(new ListMessage(serverDir));
                break;
             case FILE_REQUEST:
                 FileRequst fr = (FileRequst) cloudMessage;
                 ctx.writeAndFlush(new FileMessage(serverDir.resolve(fr.getName())));
                 break;
        }

    }
}
