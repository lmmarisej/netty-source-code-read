package nia.chapter2.echoclient;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * Listing 2.3 ChannelHandler for the client
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
@Sharable       // 标记该类的实例可以被多个Channel共享
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    // 到服务器的链接建立之后将调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 当被通知Channel是活跃的时候，发送一条消息
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
    }

    // 当从服务器接收到一条消息时被调用，可能服务器发送的数据被分块接收，TCP保证按照顺序接收
    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {       // 接收的数据被放入ByteBuf，当方法返回时，SimpleChannelInboundHandler负责释放
        System.out.println("Client received: " + in.toString(CharsetUtil.UTF_8));
    }

    // 在处理过程中引发异常调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
