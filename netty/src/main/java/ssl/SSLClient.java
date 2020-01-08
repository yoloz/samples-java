package ssl;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;


public final class SSLClient {

    private final boolean SSL = System.getProperty("ssl") != null;
    private final String HOST = System.getProperty("host", "127.0.0.1");
    private final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
    private Channel channel;

    private final Object lock = new Object();

    public Channel getChannel() {
        return channel;
    }

    public void close() {
        this.channel.close();
    }

    public void start() {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // Configure SSL.git
            final SslContext sslCtx;
            if (SSL) sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            else sslCtx = null;

            // Configure the client.
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("正在连接中...");
                            ChannelPipeline p = ch.pipeline();
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
                            }
                            //p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast(new ClientHandler());
                        }
                    });
            // Start the client.
            ChannelFuture f = b.connect(HOST, PORT).sync();
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture arg0) throws Exception {
                    synchronized (lock) {
                        lock.notify();
                    }
                    if (f.isSuccess()) System.out.println("连接服务器成功");
                    else {
                        System.out.println("连接服务器失败");
                        f.cause().printStackTrace();
                        group.shutdownGracefully(); //关闭线程组
                    }
                }
            });
            f.channel().closeFuture().addListener(future -> group.shutdownGracefully());
            System.out.println(System.currentTimeMillis());
            synchronized (lock) {
                lock.wait();
            }
            System.out.println(System.currentTimeMillis());
            channel = f.channel();
        } catch (Exception e) {
            e.printStackTrace();
            group.shutdownGracefully();
        }

    }

}