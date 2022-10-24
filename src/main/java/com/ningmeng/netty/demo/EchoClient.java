package com.ningmeng.netty.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @since 2022-09-18 20:21:46
 */
public class EchoClient {
  private final static Logger log = LoggerFactory.getLogger(EchoClient.class);
  public static void main(String[] args) throws InterruptedException {
    new EchoClient()
        .start("127.0.0.1", 8080);
  }

  void start(String host, int port) throws InterruptedException {
    NioEventLoopGroup loopGroup = new NioEventLoopGroup();
    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(loopGroup)
          .channel(NioSocketChannel.class)
          .remoteAddress(new InetSocketAddress(host, port))
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ch.pipeline().addLast(new EchoClientHandler());
            }
          });
      ChannelFuture future = bootstrap.connect();
      // sync
      // future.sync();
      // log.info("connected");
      future.addListener((ChannelFutureListener) f -> {
        if (f.isDone()) {
          log.info("done");
          if (f.isSuccess()) {
            log.info("connected success");
          }
        } else {
         log.info("connected error");
        }
      });
      future.channel().closeFuture().sync();
    } finally {
      loopGroup.shutdownGracefully().sync();
    }
  }

  static class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      ctx.writeAndFlush(Unpooled.copiedBuffer("hello world", StandardCharsets.UTF_8));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
      log.info("received: {}", msg.toString(StandardCharsets.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      log.error(cause.getMessage(), cause);
      ctx.close();
    }
  }
}
