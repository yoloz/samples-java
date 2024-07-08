package indi.yolo.sample.oracle.jdbc.debug.entity;


import java.util.Arrays;
import java.util.Optional;

/**
 * @author yolo
 */
public enum ExceptionFlags {
    success(0, "Normal termination"),
    //  GET_VALUE
    error_bogus_frame(1, "No such entrypoint on the stack"),
    error_no_debug_info(2, "Program was compiled without debug symbols"),
    error_no_such_object(3, "No such variable or parameter"),
    error_unknown_type(4, "Debug information is unreadable"),
    error_indexed_table(18, "Returned by GET_VALUE if the object is a table, but no index was provided"),
    error_illegal_index(19, "No such element exists in the collection"),
    error_nullcollection(40, "Table is atomically NULL"),
    error_nullvalue(32, "Value is NULL"),
    // SET_VALUE
    error_illegal_value(5, "Constraint violation"),
    error_illegal_null(6, "Constraint violation"),
    error_value_malformed(7, "Unable to decipher the given value"),
    error_other(8, "Some other error"),
    error_name_incomplete(11, "Name did not resolve to a scalar"),
    // Breakpoint Functions
    error_no_such_breakpt(13, "No such breakpoint"),
    error_idle_breakpt(14, "Cannot enable or disable an unused breakpoint"),
    error_bad_handle(16, "Unable to set breakpoint in given program (nonexistent or security violation)"),
    // Subprograms Error Codes
    error_unimplemented(17, "Functionality is not yet implemented"),
    error_deferred(27, "No program running; operation deferred"),
    error_exception(28, "An exception was raised in the DBMS_DEBUG or Probe packages on the server"),
    error_communication(29, "Some error other than a timeout occurred"),
    error_timeout(31, "Timout occurred");

    private final int code;
    private final String msg;

    ExceptionFlags(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public static String getMessage(int status) {
        Optional<ExceptionFlags> breakpointStatus = Arrays.stream(ExceptionFlags.values()).filter(b -> b.code == status).findFirst();
        if (breakpointStatus.isPresent()) {
            return breakpointStatus.get().msg;
        }
        return "Unknown Exception code[" + status + "]";
    }

}
