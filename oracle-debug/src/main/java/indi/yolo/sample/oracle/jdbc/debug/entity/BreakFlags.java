package indi.yolo.sample.oracle.jdbc.debug.entity;

/**
 * Breakflags:
 * break_continue(0): Run until the program hits a breakpoint
 * break_next_line(32): Break at next source line (step over calls)
 * break_any_call(12): Break at next source line (step into calls)
 * break_any_return(512): Break after returning from current entrypoint (skip over any entrypoints called from the current routine)
 * break_return(16): Break the next time an entrypoint gets ready to return. (This includes entrypoints called from the current one. If interpreter is running Proc1, which calls Proc2, then break_return stops at the end of Proc2.)
 * break_exception(2): Break when an exception is raised
 * break_handler(2048): Break when an exception handler is executed
 * abort_execution(8192): Stop execution and force an 'exit' event as soon as DBMS_DEBUG.CONTINUE is called
 *
 * @author yolo
 */
public enum BreakFlags {

    break_continue(0),
    break_next_line(32),
    break_any_call(12),
    break_any_return(512),
    break_return(16),
    break_exception(2),
    break_handler(2048),
    abort_execution(8192);

    private final int code;

    BreakFlags(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
