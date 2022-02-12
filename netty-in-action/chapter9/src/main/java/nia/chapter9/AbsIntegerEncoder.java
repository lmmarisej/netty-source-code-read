package nia.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * 对出站数据，以4个字节为一组，编码。
 */
public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) {
        while (in.readableBytes() >= 4) {
            int value = Math.abs(in.readInt());     // 将每4个字节转为一个int，取其绝对值
            out.add(value);     // 写入整数
        }
    }
}
