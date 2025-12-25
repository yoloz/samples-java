package indi.yoloz.sample.jetty;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.Callback;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class RestServer {
    private static Server httpServer;
    private static int port = 7912;

    static {
        InputStream is = RestServer.class.getClassLoader().getResourceAsStream("server.conf");
        if (is != null) {
            Properties properties = new Properties();
            try {
                properties.load(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            port = Integer.parseInt(properties.getProperty("port", "7912"));
        }
    }

    public RestServer() {
        httpServer = new Server(port);
        httpServer.setHandler(new ProxyHandler());
    }

    public void startup() throws Exception {
        httpServer.start();
    }

    public void stop() {
        if (httpServer != null) {
            try {
                httpServer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ProxyHandler extends AbstractHandler {
        @Override
        public boolean handle(Request request, Response response, Callback callback) throws Exception {
            // need todo
            return false;
        }

//		@Override
//		public void handle(String target, Request baseRequest, HttpServletRequest request,
//						   HttpServletResponse response) throws IOException {
//			try {
//				response.setHeader("Pragma", "no-cache");
//				response.setHeader("Cache-Control", "no-cache");
//				response.setHeader("Expires", "0");
//				// text/json = UTF-8
//				response.setHeader("Content-Type", "text/json; charset=utf-8");
//				baseRequest.setHandled(true);
//				String method = baseRequest.getMethod();
//				if (method.equalsIgnoreCase("GET") || "DELETE".equals(method)) {
//					handleGet(target, response);
//				} else {
//					BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
//					String line = null;
//					StringBuilder sb = new StringBuilder();
//					while ((line = br.readLine()) != null) {
//						sb.append(line);
//					}
//					String json = sb.toString();
//					handleModify(target.toLowerCase(), response, method, json);
//				}
//			} catch (Exception e) {
//				System.err.println(e.getClass() + ":" + e.getMessage());
//				e.printStackTrace();
//				try {
//					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//					OutputStream stream = response.getOutputStream();
//					stream.write((e.getClass() + ":" + e.getMessage()).getBytes("UTF-8"));
//				} catch (IOException e1) {
//					System.err.println(e1.getMessage());
//					e1.printStackTrace();
//				}
//			}
//		}

        /**
         * 获取信息
         *
         * @param transmit_str
         * @param response
         * @throws IOException
         */
        private void handleGet(String transmit_str, HttpServletResponse response) throws IOException {
            String outputStr = "get/delete method";
            OutputStream stream = null;
            try {
                stream = response.getOutputStream();
                stream.write((outputStr).getBytes("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         *
         * @param response
         * @param method   POST
         * @param jsonStr
         * @throws Exception
         */
        private void handleModify(String transmit_str, HttpServletResponse response, String method, String jsonStr)
                throws Exception {
            String retMsg = "modify action";
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            OutputStream stream = response.getOutputStream();
            stream.write(retMsg.getBytes("UTF-8"));
        }
    }

    public static void main(String[] args) {
        try {
            new RestServer().startup();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
