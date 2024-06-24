package com.bin.client.network.udp;

import com.bin.webmonitor.common.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RetryAckSend {
    private static Logger logger = LoggerFactory.getLogger(RetryAckSend.class);

    private String fluxServerDomain = "127.0.0.1";

    private int fluxServerPort = 35559;

    private DatagramSocket sock;

    private InetSocketAddress addr;


    public RetryAckSend(String host, int port)  {
        this.fluxServerDomain = host;
        this.fluxServerPort = port;
        try {
            this.sock = new DatagramSocket();
            this.addr = new InetSocketAddress(fluxServerDomain, fluxServerPort);
            this.sock.setSoTimeout(3000);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }



    public void send(byte[] content,  boolean ack) {
        byte[] receive = new byte[1];
        try {
            send(content, ack, receive);
        } catch (IOException e) {
            if (ack) {
                retry(content);
            }
            return;
        }
        if (ack && receive[0] != 1) {
            retry(content);
        }
    }

    private void retry(byte[] content) {
        logger.info("[ARCH_SDK_retry_acck_send]");
        for (int i = 0;i < 2;i++) {
            byte[] re = new byte[1];
            try {
                send(content, true, re);
                if (re[0] == 1) {
                    break;
                }
            } catch (IOException e) {
                logger.info("[ARCH_SDK_ignore_retry_ack_send]", e);
            }
        }
    }


    public void send(byte[] buf, boolean ack, byte[] ackByte)
            throws IOException {
        DatagramPacket dp = new DatagramPacket(buf, buf.length, addr);
        synchronized (this) {
            sock.send(dp);
            if (ack) {
                sock.receive(new DatagramPacket(ackByte, 0, ackByte.length));
            }
        }
    }
}
