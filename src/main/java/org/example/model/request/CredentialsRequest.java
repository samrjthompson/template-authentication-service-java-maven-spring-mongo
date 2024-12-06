package org.example.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = CredentialsRequest.Builder.class)
public record CredentialsRequest(String username, String password, String authority) {

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder
    public static class Builder {

        @JsonProperty
        private String username;
        @JsonProperty
        private String password;
        @JsonProperty
        private String authority;

        private Builder username(String username) {
            this.username = username;
            return this;
        }

        private Builder password(String password) {
            this.password = password;
            return this;
        }

        private Builder authority(String authority) {
            this.authority = authority;
            return this;
        }

        public CredentialsRequest build() {
            return new CredentialsRequest(username, password, authority);
        }
    }
}
