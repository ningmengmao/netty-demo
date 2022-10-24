package com.ningmeng.netty.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import static com.ningmeng.netty.demo.PrintBuf.log;

/**
 * @since 2022-10-23 19:38:18
 */
public class TestEmbeddedChannel {
   private static final Logger log = LoggerFactory.getLogger(TestEmbeddedChannel.class);
    
    @Test
    public void test(){
        ChannelInboundHandler h1 = getIn(() -> log.info("1"));
        ChannelInboundHandler h2 = getIn(() -> log.info("2"));
        ChannelInboundHandler h3 = getIn(() -> log.info("3"));
        ChannelInboundHandler h4 = getIn(() -> log.info("4"));
    
        ChannelOutboundHandler o5 = getOut(() -> log.info("5"));
        ChannelOutboundHandler o6 = getOut(() -> log.info("6"));
        ChannelOutboundHandler o7 = getOut(() -> log.info("7"));
    
        EmbeddedChannel channel = new EmbeddedChannel(h1, h2, h3, h4, o5, o6, o7);
        channel.config().setOption(ChannelOption.SO_RCVBUF, 10);
        ByteBuf buf = Unpooled.copiedBuffer("hello1234567890000000000000000000000000".getBytes(StandardCharsets.UTF_8));
        channel.writeInbound(buf);
        log(buf);
        channel.writeOutbound(Unpooled.copiedBuffer("world".getBytes(StandardCharsets.UTF_8)));
    }
    
    
    ChannelInboundHandler getIn(Runnable runnable) {
        return new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                runnable.run();
                super.channelRead(ctx, msg);
            }
        };
    }
    
    ChannelOutboundHandler getOut(Runnable runnable) {
        return new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                runnable.run();
                super.write(ctx, msg, promise);
            }
        };
    }
    
    

}
