package indi.yolo.sample.oracle.jdbc.debug.entity;


import indi.yolo.sample.oracle.jdbc.debug.Utils;

import java.util.Arrays;
import java.util.List;

/**
 * Namespace_cursor contains cursors (anonymous blocks).
 * <br/>
 * Namespace_pkgspec_or_toplevel contains:
 * Package specifications.
 * Procedures and functions that are not nested inside other packages, procedures, or functions.
 * Object types.
 * <br/>
 * Namespace_pkg_body contains package bodies and type bodies.
 * <br/>
 * Namespace_trigger contains triggers.
 *
 * @author yolo
 */
public enum Namespaces {

    Namespace_cursor(0),
    Namespace_pkgspec_or_toplevel(1),
    Namespace_pkg_body(2),
    Namespace_trigger(3);

    private final int code;

    Namespaces(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static void main(String[] args) {
        List<String> list = Arrays.stream(Namespaces.values()).map(Enum::name).toList();
        System.out.println(Utils.outputConstants(list));
    }
}
