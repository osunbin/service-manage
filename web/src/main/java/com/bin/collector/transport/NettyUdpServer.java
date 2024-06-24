package com.bin.collector.transport;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

@Component
public class NettyUdpServer {

    private static final Logger log = LoggerFactory.getLogger(NettyUdpServer.class);

    @Autowired
    private Environment env;

    @Autowired
    private UdpMessageHandler udpMessageHandler;

    private EventLoopGroup group;

    private ChannelFuture channelFuture;

    public static Channel channel;



    public void start() {
        log.info("start collector server");
        log.info("env is {}", env);
        group = new NioEventLoopGroup(16, new DefaultThreadFactory("server_manage_Collector"));
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(ChannelOption.SO_RCVBUF, Integer.valueOf(env.getProperty("server.udp.recivebuf")))
                    .option(ChannelOption.SO_SNDBUF, Integer.valueOf(env.getProperty("server.udp.sendbuf")))
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024, 65536))
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        public void initChannel(NioDatagramChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("handler", udpMessageHandler);
                        }
                    });

            // 绑定端口，开始接收进来的连接
            channelFuture = bootstrap.bind(Integer.valueOf(env.getProperty("server.udp.port"))).sync();
            channel = channelFuture.channel();

            channel.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(Integer.valueOf(env.getProperty("server.udp.recivebuf"))));
            // 等待关闭
            channel.closeFuture().await();
        } catch (Throwable e) {
            log.error("", e);
        } finally {
            group.shutdownGracefully();
        }
    }

}
