package com.home.shine.net.kcp.netty;

import io.netty.buffer.ByteBuf;

/**
 * @author <a href="mailto:szhnet@gmail.com">szh</a>
 */
public interface KcpOutput {

    void out(ByteBuf data, Kcp kcp);

}
