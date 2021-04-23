package com.yoloz.sample.sqlparser;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TableInfo {

    @Getter
    private final boolean virtual;
    @Setter
    private String owner;
    @Getter
    @Setter
    private String name;
    @Setter
    private String alias;

    private List<ColumnInfo> selects;
    private List<ColumnInfo> columns;

    public TableInfo() {
        this.virtual = false;
    }

    public TableInfo(boolean virtual) {
        this.virtual = virtual;
    }

    public Optional<String> getOwner() {
        return Optional.ofNullable(owner);
    }

    public String getAlias() {
        if (alias == null) {
            return name;
        }
        return alias;
    }

    public Optional<List<ColumnInfo>> getSelects() {
        return Optional.ofNullable(selects);
    }

    public Optional<List<ColumnInfo>> getColumns() {
        return Optional.ofNullable(columns);
    }

    public void fillSelect(List<ColumnInfo> cols) {
        for (ColumnInfo col : cols) {
            fillSelect(col);
        }
    }

    public void fillSelect(ColumnInfo col) {
        if (selects == null) {
            selects = new ArrayList<>();
        }
        selects.add(col);
        if (!"*".equals(col.getName())) {
            fillColumn(col);
        }
    }

    public void fillColumn(List<ColumnInfo> cols) {
        for (ColumnInfo col : cols) {
            fillColumn(col);
        }
    }

    public void fillColumn(ColumnInfo col) {
        if (columns == null) {
            columns = new ArrayList<>();
        }
        boolean exist = false;
        for (ColumnInfo column : columns) {
            if (column.getOwner().orElse("").equalsIgnoreCase(col.getOwner().orElse("")) &&
                    column.getName().equalsIgnoreCase(col.getName()) &&
                    column.getAlias().equalsIgnoreCase(col.getAlias())) {
                exist = true;
            }
        }
        if (!exist) columns.add(col);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TableInfo)) {
            return false;
        }
        TableInfo dst = (TableInfo) obj;
        if (this.owner == null && dst.getOwner().isPresent()) {
            return false;
        }
        if (this.owner != null && !dst.getOwner().isPresent()) {
            return false;
        }
        if (this.owner != null && dst.getOwner().isPresent() && !this.owner.equalsIgnoreCase(dst.getOwner().get())) {
            return false;
        }
        if (!this.name.equalsIgnoreCase(dst.getName())) {
            return false;
        }
        return this.alias.equalsIgnoreCase(dst.getAlias());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(this.getClass().getSimpleName())
                .append("(owner=").append(getOwner().orElse(null))
                .append(", name=").append(getName()).append(", alias=").append(getAlias());
        if (getColumns().isPresent()) {
            builder.append(", columns=[");
            for (int i = 0; i < columns.size(); i++) {
                ColumnInfo columnInfo = columns.get(i);
                builder.append(columnInfo);
                if (i != columns.size() - 1) {
                    builder.append(",");
                }
            }
            builder.append("]");
        }
        if (getSelects().isPresent()) {
            builder.append(", selects=[");
            for (int i = 0; i < selects.size(); i++) {
                ColumnInfo columnInfo = selects.get(i);
                builder.append(columnInfo);
                if (i != selects.size() - 1) {
                    builder.append(",");
                }
            }
            builder.append("]");
        }
        builder.append(")");
        return builder.toString();
    }

    public String toSQLString() {
        StringBuilder builder = new StringBuilder();
        if (getOwner().isPresent()) {
            builder.append(owner).append(".");
        }
        builder.append(alias);
        return builder.toString();
    }

    public static void merge(List<TableInfo> src, List<TableInfo> dst) {
        if (dst == null || dst.isEmpty()) {
            return;
        }
        for (TableInfo dt : dst) {
            boolean exist = false;
            for (TableInfo st : src) {
                if (dt.equals(st)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                src.add(dt);
            }
        }
    }
}
