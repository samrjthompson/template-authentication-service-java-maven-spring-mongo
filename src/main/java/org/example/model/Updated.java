package org.example.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class Updated implements Serializable {

    private Instant at;
    private String by;

    public String getBy() {
        return by;
    }

    public Updated by(String by) {
        this.by = by;
        return this;
    }

    public Instant getAt() {
        return at;
    }

    public Updated at(Instant at) {
        this.at = at;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Updated updated = (Updated) o;
        return Objects.equals(at, updated.at) && Objects.equals(by, updated.by);
    }

    @Override
    public int hashCode() {
        return Objects.hash(at, by);
    }
}
