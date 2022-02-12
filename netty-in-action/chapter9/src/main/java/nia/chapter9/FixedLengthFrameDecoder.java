package nia.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 对入站数据，以3个字节为一组，转为一个对象。
 */
public class FixedLengthFrameDecoder extends ByteToMessageDecoder {
    private final int frameLength;

    public FixedLengthFrameDecoder(int frameLength) {
        if (frameLength <= 0) {
            throw new IllegalArgumentException("frameLength must be a positive integer: " + frameLength);
        }
        this.frameLength = frameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 循环处理所有可读的数据
        while (in.readableBytes() >= frameLength) {     // 是否有足够的字节可被读取，如若未达到可读长度，将不会进来
            ByteBuf buf = in.readBytes(frameLength);    // 读取3个字节到缓存
            out.add(buf);
        }
    }
}
