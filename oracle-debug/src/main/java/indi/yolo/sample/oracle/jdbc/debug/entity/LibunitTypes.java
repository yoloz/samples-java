package indi.yolo.sample.oracle.jdbc.debug.entity;

import indi.yolo.sample.oracle.jdbc.debug.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author yolo
 */
public enum LibunitTypes {
    LibunitType_cursor(0),
    LibunitType_procedure(7),
    LibunitType_function(8),
    LibunitType_package(9),
    LibunitType_package_body(11),
    LibunitType_trigger(12),
    LibunitType_Unknown(-1);

    private final int code;

    LibunitTypes(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static String getName(int status) {
        Optional<LibunitTypes> libunitType = Arrays.stream(LibunitTypes.values()).filter(t -> t.code == status).findFirst();
        return libunitType.map(Enum::name).orElseGet(() -> "Unknown libunittype[" + status + "]");
    }

    public static void main(String[] args) {
        List<String> list = Arrays.stream(LibunitTypes.values()).map(Enum::name).toList();
        System.out.println(Utils.outputConstants(list));
    }
}
