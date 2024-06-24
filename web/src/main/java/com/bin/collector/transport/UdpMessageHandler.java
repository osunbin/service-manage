package com.bin.collector.transport;

import com.bin.collector.CircuitBreakEventCollector;
import com.bin.collector.CircuitBreakMonitorCollector;
import com.bin.collector.ServiceFunctionsCollector;
import com.bin.collector.request.CircuitBreakEventData;
import com.bin.collector.request.CircuitBreakMonitorData;
import com.bin.collector.request.ServiceFunctions;
import com.bin.webmonitor.enums.ReportType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class UdpMessageHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger log = LoggerFactory.getLogger(UdpMessageHandler.class);

    public static ExecutorService executorService = Executors.newFixedThreadPool(20);

    @Autowired
    ServiceFunctionsCollector serviceFunctionsProcessor;

    @Autowired
    CircuitBreakMonitorCollector circuitBreakMonitorCollector;

    @Autowired
    CircuitBreakEventCollector circuitBreakEventCollector;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet)
            throws Exception {

        byte[] request = packet.getData();

        if (request == null || request.length == 0) {
            return;
        }

        InetSocketAddress address = (InetSocketAddress) packet.getSocketAddress();
        executorService.execute(() -> {
            byte requestType = request[0];
            String ip = address.getAddress().getHostAddress();
            if (requestType == ReportType.serviceFunction.getValue()) {
                ServiceFunctions sfr = new ServiceFunctions().toRequest(request);
                serviceFunctionsProcessor.collect(sfr, ip);
            } else if (requestType == ReportType.circuitBreakMonitorData.getValue()) {
                // 熔断监控数据
                CircuitBreakMonitorData circuitBreakMonitorData = new CircuitBreakMonitorData().toRequest(request);
                circuitBreakMonitorCollector.collect(circuitBreakMonitorData, ip);
            } else if (requestType == ReportType.circuitBreakEventData.getValue()) {
                // 熔断事件
                CircuitBreakEventData circuitBreakEventData = new CircuitBreakEventData().toRequest(request);
                circuitBreakEventCollector.collect(circuitBreakEventData, ip);
            }
        });
        ctx.writeAndFlush(new DatagramPacket(new byte[]{1}, 1, address));
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx)
            throws Exception {
        super.channelRegistered(ctx);
    }


}
