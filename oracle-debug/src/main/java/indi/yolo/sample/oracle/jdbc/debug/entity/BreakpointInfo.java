package indi.yolo.sample.oracle.jdbc.debug.entity;

/**
 * @author yolo
 */
public class BreakpointInfo {

    private String name;  // Name of the program unit
    private String owner; // Owner of the program unit
    private String dblink; // Database link, if remote
    private Integer line; // Line number
    private Integer libunittype; //NULL, unless this is a nested procedure or function
    private Integer status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDblink() {
        return dblink;
    }

    public void setDblink(String dblink) {
        this.dblink = dblink;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public Integer getLibunittype() {
        return libunittype;
    }

    public void setLibunittype(Integer libunittype) {
        this.libunittype = libunittype;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BreakpointInfo{" +
                "name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", dblink='" + dblink + '\'' +
                ", line=" + line +
                ", libunittype=" + LibunitTypes.getName(libunittype) +
                ", status=" + BreakpointStatus.getName(status) +
                '}';
    }
}
