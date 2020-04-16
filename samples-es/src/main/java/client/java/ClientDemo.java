package client.java;

import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequestBuilder;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * es 5.3 java client
 */
public class ClientDemo {

    public static void main(String[] args) {
        TransportClient tClient = null;
        try {
            Map<String, String> settingMap = new HashMap<String, String>();
            settingMap.put("cluster.name", "udb53");
            List<InetSocketTransportAddress> transportAddressList =
                    new ArrayList<InetSocketTransportAddress>();
            transportAddressList.add(new InetSocketTransportAddress(
                    new InetSocketAddress("10.68.23.186", 9300)));
            transportAddressList.add(new InetSocketTransportAddress(
                    new InetSocketAddress("10.68.23.187", 9300)));
            transportAddressList.add(new InetSocketTransportAddress(
                    new InetSocketAddress("10.68.23.188", 9300)));
            Settings settings = Settings.builder().put(settingMap).build();
            tClient = new PreBuiltTransportClient(settings);
            for (InetSocketTransportAddress address : transportAddressList) {
                tClient.addTransportAddress(address);
            }
            ClusterStateRequestBuilder clusterBld = tClient.admin().cluster().prepareState();
            ListenableActionFuture<ClusterStateResponse> lafClu = clusterBld.execute();
            ClusterStateResponse cluResp = lafClu.actionGet();
            String name = cluResp.getClusterName().value();
            ClusterState cluState = cluResp.getState();
            int numNodes = cluState.getNodes().getSize();
            System.out.println(name + "====" + numNodes);

            IndicesExistsResponse indicesExistsResponse = tClient.admin().indices()
                    .exists(Requests.indicesExistsRequest("es53test")).actionGet();
            System.out.println(indicesExistsResponse.isExists());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tClient != null) {
                tClient.close();
            }
        }

    }
}
