package test.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import test.server.decoder.MessageEncoder;
import test.server.decoder.MessageEncoder1;
import test.server.decoder.MyDecoder1;

import java.net.InetSocketAddress;

public class TestServer {

    private int port;

    public static void main(String[] args){
        new TestServer(8081).start();
    }

    public TestServer(int port){
        this.port = port;
    }

    public void start(){
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boosGroup,workerGroup)
            .channel(NioServerSocketChannel.class)
            .localAddress(new InetSocketAddress(port))
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception{
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new MyDecoder1(1<<20, 10, 4));
                    pipeline.addLast(new TestServerHandler());
                    pipeline.addLast(new MessageEncoder1());
                }
            })
            .option(ChannelOption.SO_BACKLOG,128)
            .childOption(ChannelOption.SO_KEEPALIVE,true);

            ChannelFuture future = bootstrap.bind().sync();
            System.out.println("服务器启动！正在监听端口");
        }catch (Exception e){}

    }
}
