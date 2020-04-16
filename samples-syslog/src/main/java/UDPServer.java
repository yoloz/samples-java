import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 */
public class UDPServer {

    public static void main(String[] args) throws SocketException {
        DatagramSocket datagramSocket = new DatagramSocket(8088);
        while (true) {
            DatagramPacket packet = new DatagramPacket(new byte[512], 512);
            try {
                datagramSocket.receive(packet);
                String msg = new String(packet.getData(), 0, packet.getLength());
                System.out.println(packet.getAddress() + "/" + packet.getPort() + ":" + msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
