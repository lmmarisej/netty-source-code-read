package nia.chapter2.echoclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Listing 2.4 Main class for the client
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();     // 用来处理客户端事件
        try {
            Bootstrap b = new Bootstrap();          // 客户端启动器
            b.group(group)
                .channel(NioSocketChannel.class)        // 异步 TCP 客户端
                .remoteAddress(new InetSocketAddress(host, port))       // 连接到目标服务器
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new EchoClientHandler());     // 在创建Channel时，给ChannelPipeline中添加一个Handler
                    }
                });
            ChannelFuture f = b.connect().sync();   // 连接到远程节点，直到连接完成
            f.channel().closeFuture().sync();       // 阻塞直到Channel关闭
        } finally {
            group.shutdownGracefully().sync();      // 关闭连接释放资源
        }
    }

    public static void main(String[] args) throws Exception {
        final String host = "localhost";
        final int port = 1222;
        new EchoClient(host, port).start();
    }
}

