package com.ningmeng.netty.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @since 2022-09-18 20:38:46
 */
public class EchoServer {

  private static Logger log = LoggerFactory.getLogger(EchoServer.class);
  static int port = 8080;

  public static void main(String[] args) throws InterruptedException {
    EchoServer server = new EchoServer();
    server.start(port);
  }


  void start(int port) throws InterruptedException {
    EchoServerHandler serverHandler = new EchoServerHandler();
    NioEventLoopGroup loopGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(loopGroup)
          .channel(NioServerSocketChannel.class)
          .localAddress(new InetSocketAddress(port))
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ch.pipeline().addLast(serverHandler);
            }
          });
      ChannelFuture future = serverBootstrap.bind().sync();
      future.channel().closeFuture().sync();
    } finally {
      loopGroup.shutdownGracefully().sync();
    }
  }

  static class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      ByteBuf in = (ByteBuf) msg;
      log.info("received: {}", in.toString(StandardCharsets.UTF_8));
      ctx.write(in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
      ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
          .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      log.error(cause.getMessage(), cause);
      ctx.close();
    }
  }
}
