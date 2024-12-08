package org.example.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = CredentialsRequest.Builder.class)
public record CredentialsRequest(String username, String password, String authority, boolean isEnabled) {

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
        @JsonProperty
        private boolean isEnabled = true; // Defaults to true if not specified

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder authority(String authority) {
            this.authority = authority;
            return this;
        }

        public Builder isEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
            return this;
        }

        public CredentialsRequest build() {
            return new CredentialsRequest(username, password, authority, isEnabled);
        }
    }
}
