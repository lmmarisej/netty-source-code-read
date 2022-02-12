package nia.chapter8;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * Listing 8.7 Using attributes
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class BootstrapClientWithOptionsAndAttrs {

    /**
     * Listing 8.7 Using attributes
     * */
    public void bootstrap() {
        // 可以用客户端和服务端Channels安全地关联任何类型的数据对象
        final AttributeKey<Integer> id = AttributeKey.newInstance("ID");
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
            .channel(NioSocketChannel.class)
            .handler(
                new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    public void channelRegistered(ChannelHandlerContext ctx) {
                        Integer idValue = ctx.channel().attr(id).get();
                        // do something with the idValue
                    }

                    @Override
                    protected void channelRead0(
                        ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
                        System.out.println("Received data");
                    }
                }
            );
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        bootstrap.attr(id, 123456);     // 配置一个Channel和一个属性来存储一个整数值
        ChannelFuture future = bootstrap.connect(
            new InetSocketAddress("www.manning.com", 80));
        future.syncUninterruptibly();
    }
}
