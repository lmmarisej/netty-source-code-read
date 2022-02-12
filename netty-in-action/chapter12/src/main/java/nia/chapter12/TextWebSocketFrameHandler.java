package nia.chapter12;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * Listing 12.2 Handling text frames
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            // 如果该事件握手成功，则移除 HttpRequestHandler，因为不会再接收到任何 HTTP 消息了
            ctx.pipeline().remove(HttpRequestHandler.class);     // 升级HTTP到websocket
            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));     // 通知其它客户端有新人加入
            group.add(ctx.channel());               // 将当前连接加入房间
            ctx.channel().writeAndFlush(new TextWebSocketFrame("You have joined " + group.name())); // 提示当前用户加入成功
        } else {
            super.userEventTriggered(ctx, evt);     // 其它类型的事件冒泡
        }
    }

    // 接收来自Channel的消息
    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        // 增加消息的引用计数，并将它写到 ChannelGroup 中所有已经连接的客户端
        group.writeAndFlush(msg.retain());      // 增加引用计数，避免channelRead0返回后导致TextWebSocketFrame计数减少
    }
}
