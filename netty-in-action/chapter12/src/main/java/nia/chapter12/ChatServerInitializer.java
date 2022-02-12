package nia.chapter12;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Listing 12.3 Initializing the ChannelPipeline
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class ChatServerInitializer extends ChannelInitializer<Channel> {
    private final ChannelGroup group;

    public ChatServerInitializer(ChannelGroup group) {
        this.group = group;
    }

    // 当Channel建立时，才会被调用
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 将字节解码为HttpRequest、HttpContent和LastHttp-Content
        pipeline.addLast(new HttpServerCodec());            // 并将HttpRequest、HttpContent和Last-HttpContent编码为字节
        pipeline.addLast(new ChunkedWriteHandler());        // 写入一个文件的内容
        // 将一个HttpMessage和跟随它的多个HttpContent聚合为单个FullHttpRequest或者FullHttpResponse
        // 安装了这个之后，ChannelPipeline中的下一个ChannelHandler将只会收到完整的HTTP请求或响应
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
        pipeline.addLast(new HttpRequestHandler("/ws"));    // 处理FullHttpRequest（那些不发送到/ws URI的请求）
        // 按照WebSocket规范的要求，处理WebSocket升级握手、PingWebSocketFrame、PongWebSocketFrame和CloseWebSocketFrame
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));        // 封装为websocket事件对象给下一个handler
        pipeline.addLast(new TextWebSocketFrameHandler(group));     // 处理TextWebSocketFrame和握手完成事件
    }
}
