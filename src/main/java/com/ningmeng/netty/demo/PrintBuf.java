package com.ningmeng.netty.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * @since 2022-10-23 20:28:48
 */
public class PrintBuf {
    
    public static void log(ByteBuf buf) {
        int len = buf.readableBytes();
        int row = len / 16 + (len % 15 == 0 ? 0 : 1) + 4;
        StringBuilder sb = new StringBuilder(row * 80 * 2);
        sb.append("read index:").append(buf.readerIndex())
                .append(" write index:").append(buf.writerIndex())
                .append(" capacity:").append(buf.capacity())
                .append(NEWLINE);
        ByteBufUtil.appendPrettyHexDump(sb, buf);
        System.out.println(sb);
    }
}
