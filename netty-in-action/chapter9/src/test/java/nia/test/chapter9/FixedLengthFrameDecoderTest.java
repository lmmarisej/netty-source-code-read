package nia.test.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import nia.chapter9.FixedLengthFrameDecoder;
import org.junit.Test;

import static org.junit.Assert.*;

public class FixedLengthFrameDecoderTest {
    @Test
    public void testFramesDecoded() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);       // 存储9个字节
        }
        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));  // 嵌入解码器
        // write bytes
        assertTrue(channel.writeInbound(input.retain()));       // 数据写入Channel
        assertTrue(channel.finish());       // 标记channel完成，将无法写数据

        // read messages
        ByteBuf read = channel.readInbound();       // 读一次，经过解码后，每次读取应该是有3个字节
        assertEquals(buf.readSlice(3), read);       // 验证本次读的一帧是否有3字节
        read.release();

        read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        assertNull(channel.readInbound());      // 读完了
        buf.release();      // 销毁buf
    }

    @Test
    public void testFramesDecoded2() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();        // 拷贝引用

        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        // 有decoder的存在需要一次性写入3个字节才算成功
        assertFalse(channel.writeInbound(input.readBytes(2)));      // 读两个字节出来写入，未返回任何数据将返回false
        assertTrue(channel.writeInbound(input.readBytes(1)));
        assertTrue(channel.writeInbound(input.readBytes(6)));

        assertTrue(channel.finish());
        ByteBuf read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        assertNull(channel.readInbound());
        buf.release();
    }
}
