package dev.isdn.demo.records_dto.app.domain.common;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@MappedSuperclass
public abstract class AbstractEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(generator = "id-generator")
    @GenericGenerator(name = "id-generator", strategy = "dev.isdn.demo.records_dto.app.domain.common.SequenceGenerator")
    private long id;

    @Version
    private Timestamp version;

    public long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id > 0 ? Objects.hashCode(id) : super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return id > 0 ? Objects.equals(id, ((AbstractEntity)obj).id) : super.equals(obj);
    }

}
