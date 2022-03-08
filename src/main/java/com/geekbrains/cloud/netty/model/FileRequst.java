package com.geekbrains.cloud.netty.model;

import lombok.Data;

@Data
public class FileRequst implements CloudMessage {

    private final String name;

    public FileRequst(String name) {
        this.name = name;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_REQUEST;
    }


}
