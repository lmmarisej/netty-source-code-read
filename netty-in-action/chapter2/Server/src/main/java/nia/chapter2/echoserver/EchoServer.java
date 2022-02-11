package nia.chapter2.echoserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Listing 2.2 EchoServer class
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 *
 * 引导服务器。
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        new EchoServer(1222).start();
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();     // 指定接收和处理新连接
        try {
            ServerBootstrap b = new ServerBootstrap();      // server引导器
            // 配置引导
            b.group(group)
                .channel(NioServerSocketChannel.class)      // 异步 TCP 服务端
                .localAddress(new InetSocketAddress(port))  // 指定端口，绑定到指定地址以监听请求
                    // 当新的连接被接受时，一个新的Channel将被创建，通过ChannelInitializer可以将自定义的EchoServerHandler添加到Channel的pipeline中
                .childHandler(new ChannelInitializer<SocketChannel>() {     // 添加一个Channel到ChannelPipeline
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(serverHandler);       // 对于所有的客户端来说，使用同一个EchoServerHandler
                    }
                });

            ChannelFuture f = b.bind().sync();      // 绑定服务器，调用sync阻塞，直到绑定完成
            System.out.println(EchoServer.class.getName() + " started and listening for connections on " + f.channel().localAddress());
            f.channel().closeFuture().sync();       // 阻塞当前线程，直到channel关闭
        } finally {
            group.shutdownGracefully().sync();      // 关闭EventLoopGroup，释放所有资源
        }
    }
}
