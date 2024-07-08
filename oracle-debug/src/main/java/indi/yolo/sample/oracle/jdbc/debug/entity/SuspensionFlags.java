package indi.yolo.sample.oracle.jdbc.debug.entity;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author yolo
 */
public enum SuspensionFlags {

    reason_none(0, "normal"),
    reason_interpreter_starting(2, "Interpreter is starting"),
    reason_breakpoint(3, "Hit a breakpoint"),
    reason_enter(6, "Procedure entry"),
    reason_return(7, "Procedure is about to return"),
    reason_finish(8, "Procedure is finished"),
    reason_line(9, "Reached a new line"),
    reason_interrupt(10, "An interrupt occurred"),
    reason_exception(11, "An exception was raised"),
    reason_exit(15, "Interpreter is exiting (old form)"),
    reason_handler(16, "Start exception-handler"),
    reason_timeout(17, "A timeout occurred"),
    reason_instantiate(20, "Instantiation block"),
    reason_abort(21, "Interpreter is aborting"),
    reason_knl_exit(25, "Kernel is exiting");

    private final int code;
    private final String reason;

    SuspensionFlags(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int getCode() {
        return code;
    }

    public static String getReason(int status) {
        Optional<SuspensionFlags> breakpointStatus = Arrays.stream(SuspensionFlags.values()).filter(b -> b.code == status).findFirst();
        if (breakpointStatus.isPresent()) {
            return breakpointStatus.get().reason;
        }
        return "Unknown suspension reason[" + status + "]";
    }

}
