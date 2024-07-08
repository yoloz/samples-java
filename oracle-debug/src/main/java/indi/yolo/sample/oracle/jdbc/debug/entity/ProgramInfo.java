package indi.yolo.sample.oracle.jdbc.debug.entity;

/**
 * @author yolo
 */
public class ProgramInfo {

    // The following fields are used when setting a breakpoint
    private final Integer namespace;
    private final String name;
    private final String owner;
    private final String dblink;
    private final Integer line;
    // Read-only fields (set by Probe when doing a stack backtrace)
    private final Integer libunittype;
    private final String entrypointname;

    public ProgramInfo(Integer namespace, String name, String owner, String dblink, Integer line, Integer libunittype, String entrypointname) {
        this.namespace = namespace;
        this.name = name;
        this.owner = owner;
        this.dblink = dblink;
        this.line = line;
        this.libunittype = libunittype;
        this.entrypointname = entrypointname;
    }

    public Integer getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getDblink() {
        return dblink;
    }

    public Integer getLine() {
        return line;
    }

    public Integer getLibunittype() {
        return libunittype;
    }

    public String getEntrypointname() {
        return entrypointname;
    }

    @Override
    public String toString() {
        return "ProgramInfo{" +
                "namespace=" + namespace +
                ", name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", dblink='" + dblink + '\'' +
                ", line=" + line +
                ", libunittype=" + LibunitTypes.getName(libunittype) +
                ", entrypointname='" + entrypointname + '\'' +
                '}';
    }
}
