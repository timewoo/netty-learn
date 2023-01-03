package org.example.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @author yanglin
 * @date 2022/12/22 17:25
 */
public class ByteBufTest {

    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(6, 10);
        printByteBufInfo("ByteBufAllocator.buffer(6, 10)",buffer);
        buffer.writeBytes(new byte[]{1,2});
        printByteBufInfo("write 2 Bytes",buffer);
        buffer.writeInt(100);
        printByteBufInfo("write int 100",buffer);
        buffer.writeBytes(new byte[]{3,4,5});
        printByteBufInfo("write 3 Bytes",buffer);
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        printByteBufInfo("read "+buffer.readableBytes(),buffer);
        printByteBufInfo("before getAndSet",buffer);
        System.out.println("get 2 "+buffer.getInt(2));
        buffer.setByte(1,0);
        System.out.println("get 1 "+buffer.getByte(1));
        printByteBufInfo("after getAndSet",buffer);
    }

    private static void printByteBufInfo(String step,ByteBuf byteBuf){
        System.out.println("------"+step+"--------");
        System.out.println("readerIndex():"+byteBuf.readerIndex());
        System.out.println("writerIndex():"+byteBuf.writerIndex());
        System.out.println("isReadable():"+byteBuf.isReadable());
        System.out.println("isWritable():"+byteBuf.isWritable());
        System.out.println("readableBytes():"+byteBuf.readableBytes());
        System.out.println("writableBytes()"+byteBuf.writableBytes());
        System.out.println("maxWritableBytes():"+byteBuf.maxWritableBytes());
        System.out.println("capacity():"+byteBuf.capacity());
        System.out.println("maxCapacity():"+byteBuf.maxCapacity());
    }
}
