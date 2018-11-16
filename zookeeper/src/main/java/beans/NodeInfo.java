package beans;

/**
 */
public class NodeInfo {


    private static final NodeInfo NO_NODE_INFO = new NodeInfo(-1, "", -1);

    private Integer id;
    private String host;
    private int port;
    private String rack;

    public NodeInfo(Integer id, String host, int port) {
        this(id, host, port, null);
    }

    public NodeInfo(Integer id, String host, int port, String rack) {
        super();
        this.id = id;
        this.host = host;
        this.port = port;
        this.rack = rack;
    }

    public Integer getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getRack() {
        return rack;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }

    public static NodeInfo noNode() {
        return NO_NODE_INFO;
    }

    public boolean isEmpty() {
        return host == null || host.isEmpty() || port < 0;
    }

    public boolean hasRack() {
        return rack != null;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + port;
        result = prime * result + ((rack == null) ? 0 : rack.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NodeInfo other = (NodeInfo) obj;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (port != other.port)
            return false;
        if (rack == null) {
            if (other.rack != null)
                return false;
        } else if (!rack.equals(other.rack))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return host + ":" + port + " (id: " + id + " rack: " + rack + ")";
    }
}
