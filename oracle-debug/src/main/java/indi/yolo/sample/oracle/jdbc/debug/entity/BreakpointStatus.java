package indi.yolo.sample.oracle.jdbc.debug.entity;

import java.util.Arrays;
import java.util.Optional;

/**
 * breakpoint_status_active(1, "a line breakpoint")
 * breakpoint_status_disabled(4, "breakpoint is currently disabled")
 * breakpoint_status_remote(8, "a shadow breakpoint (a local representation of a remote breakpoint)")
 * <br/>
 *
 * @author yolo
 */
public enum BreakpointStatus {

    breakpoint_status_active(1),
    breakpoint_status_disabled(4),
    breakpoint_status_remote(8);

    private final int code;

    BreakpointStatus(int code) {
        this.code = code;
    }

    public static String getName(int status) {
        Optional<BreakpointStatus> breakpointStatus = Arrays.stream(BreakpointStatus.values()).filter(t -> t.code == status).findFirst();
        return breakpointStatus.map(Enum::name).orElseGet(() -> "Unknown breakpoint status[" + status + "]");
    }
}
