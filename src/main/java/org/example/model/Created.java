package org.example.model;

import java.time.Instant;
import java.util.Objects;

public class Created {

    private Instant at;
    private String by;

    public Instant getAt() {
        return at;
    }

    public Created at(Instant at) {
        this.at = at;
        return this;
    }

    public String getBy() {
        return by;
    }

    public Created by(String by) {
        this.by = by;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Created created = (Created) o;
        return Objects.equals(at, created.at) && Objects.equals(by, created.by);
    }

    @Override
    public int hashCode() {
        return Objects.hash(at, by);
    }
}
