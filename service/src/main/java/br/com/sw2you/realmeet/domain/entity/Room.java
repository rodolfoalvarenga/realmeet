package br.com.sw2you.realmeet.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.Objects;

import static java.util.Objects.isNull;

@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "seats", nullable = false)
    private Integer seats;

    @Column(name = "active", nullable = false)
    private Boolean active;

    public Room() {}

    private Room(Builder builder) {
        id = builder.id;
        name = builder.name;
        seats = builder.seats;
        active = builder.active;
    }

    @PrePersist
    public void prePersist() {
        if (isNull(active)) {
            active = true;
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getSeats() {
        return seats;
    }

    public Boolean getActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(getId(), room.getId()) && Objects.equals(getName(), room.getName())
                && Objects.equals(getSeats(), room.getSeats()) && Objects.equals(getActive(), room.getActive());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getSeats(), getActive());
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", seats=" + seats +
                ", active=" + active +
                '}';
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String name;
        private Integer seats;

        private Boolean active;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder seats(Integer seats) {
            this.seats = seats;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public Room build() {
            return new Room(this);
        }
    }
}
