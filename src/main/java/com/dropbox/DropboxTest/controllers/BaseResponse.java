package com.dropbox.DropboxTest.controllers;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class BaseResponse<T> {
    private T data;

    private String error;

    private BaseResponse(Builder<T> builder) {
        this.data = builder.data;
        this.error = builder.error;
    }

    @NoArgsConstructor
    public static class Builder<K> {
        private K data;

        private String error;

        public Builder<K> setData(K data) {
            this.data = data;
            return this;
        }

        public Builder<K> setError(String error) {
            this.error = error;
            return this;
        }

        public BaseResponse<K> build() {
            return new BaseResponse<>(this);
        }
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
}
