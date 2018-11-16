package assignor;


/**
 */
public interface ServiceAssignor {

//    @Deprecated
//    Integer assign(ClusterInfo metadata, String serviceId);

    Integer assign(String serviceId);

    String name();

}
