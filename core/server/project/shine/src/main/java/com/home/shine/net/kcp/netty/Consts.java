package com.home.shine.net.kcp.netty;

import io.netty.util.internal.SystemPropertyUtil;

/**
 * @author <a href="mailto:szhnet@gmail.com">szh</a>
 */
public class Consts {

    public static final int FIXED_RECV_BYTEBUF_ALLOCATE_SIZE = SystemPropertyUtil.getInt("com.home.shine.net.kcp" +
            ".udpRecvAllocateSize", 2048);

    public static final int CLOSE_WAIT_TIME = 5 * 1000;

}
