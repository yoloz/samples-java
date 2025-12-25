package indi.yolo.sample.fk.monitor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Created  on 17-2-20.
 */
class HttpImpl {

    private int sleep_time;
    private Set<URL> urls;
    private String basePath;
    private Selector selector = null;

    HttpImpl(String sleep_time, Set<URL> urls, String basePath) {
        this.sleep_time = Integer.parseInt(sleep_time);
        this.urls = urls;
        this.basePath = basePath;
    }

    private void register(Selector selector, SocketAddress address) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        channel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
    }

    private Map<SocketAddress, String> urlToSocketAddress(Set<URL> urls) {
        Map<SocketAddress, String> map = new HashMap<>(urls.size());
        for (URL url : urls) {
            int port = url.getPort() != -1 ? url.getPort() : url.getDefaultPort();
            SocketAddress address = new InetSocketAddress(url.getHost(), port);
            String path = url.getFile();
            map.put(address, path);
        }
        return map;
    }


    void load() throws Exception {
        Map<SocketAddress, String> mapping = this.urlToSocketAddress(urls);
        if (mapping.size() == 0) throw new IOException("no url need to management......");
        Files.createDirectories(Paths.get(basePath));
        ByteBuffer buffer = ByteBuffer.allocate(32 * 1024);
        selector = Selector.open();
        for (SocketAddress address : mapping.keySet()) {
            register(selector, address);
        }
        while (true) {
            selector.select(10000);
            System.out.println("=============开始查询============");
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey selectionKey = keys.next();
                keys.remove();
                try {
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    if (selectionKey.isValid() && selectionKey.isReadable() && channel.isConnected()) {
                        System.out.println("=============查询到结果写进文件============");
                        String fileName = "flume_Http_" + channel.getRemoteAddress().toString().replace("/", "").replace(":", ".");
                        try (FileChannel fileChannel = FileChannel.open(Paths.get(basePath, fileName), StandardOpenOption.APPEND,
                                StandardOpenOption.CREATE)) {
                            buffer.clear();
                            int len;
                            buffer.put(Command.simpleDateFormat.format(new Date()).getBytes("UTF-8"));
                            while ((len = channel.read(buffer)) > 0 || buffer.position() != 0) {
                                buffer.flip();
                                fileChannel.write(buffer);
                                buffer.compact();
                            }
                            fileChannel.write(ByteBuffer.wrap(Command.lineSeparator.getBytes("UTF-8")));
                            if (len == -1) {
                                SocketAddress address = channel.getRemoteAddress();
                                channel.close();
                                selectionKey.cancel();
                                register(selector, address);
                            }
                        }
                    } else if (selectionKey.isValid() && selectionKey.isConnectable()) {
                        boolean success;
                        try {
                            success = channel.finishConnect();
                        } catch (Exception e) {
                            success = false;
                        }
                        if (success) {
                            System.out.println("=============发送请求获取数据============");
                            InetSocketAddress address = (InetSocketAddress) channel.getRemoteAddress();
                            String request = "GET " + mapping.get(address) + " HTTP/1.0\r\n\r\nHost: "
                                    + address.getHostString() + "\r\n\r\n";
                            ByteBuffer header = ByteBuffer.wrap(request.getBytes("UTF-8"));
                            channel.write(header);
                            selectionKey.interestOps(SelectionKey.OP_READ);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    selectionKey.cancel();
                    selectionKey.channel().close();
                    throw e;
                }
            }
            System.out.println("=============休眠等待============");
            Thread.sleep(sleep_time * 1000);
        }
    }

    void destroy() {
        try {
            System.out.println("============准备关闭，倒计时10s...");
            Thread.sleep(10000);
            if (selector != null) selector.close();
        } catch (Exception e) {
            //ignore
        }
    }
}
