package com.ej.hgj.entity.file;

import lombok.Data;

@Data
public class Request {
    private String clientId;
    private String data;
    private String signature;
    private String nonce;
    private long timestamp;
    public Request(String clientId, String data, String signature, String nonce, long timestamp) {
        this.clientId = clientId;
        this.data = data;
        this.signature = signature;
        this.nonce = nonce;
        this.timestamp = timestamp;
    }
}
