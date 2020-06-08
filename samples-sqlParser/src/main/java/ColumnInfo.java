import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@NoArgsConstructor
public class ColumnInfo {

    @Setter
    private String owner;
    @Getter
    @Setter
    private String name;
    @Setter
    private String alias;

    public Optional<String> getOwner() {
        return Optional.ofNullable(owner);
    }

    public String getAlias() {
        if (alias == null) {
            return name;
        }
        return alias;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "(owner=" + getOwner().orElse(null) +
                ", name=" + getName() + ", alias=" + getAlias() +
                ")";
    }

    public String toSQLString() {
        StringBuilder builder = new StringBuilder();
        if (getOwner().isPresent()) {
            builder.append(getOwner().get()).append(".");
        }
        builder.append(getAlias());
        return builder.toString();
    }
}
