package com.mytypeworldcup.mytypeworldcup.global.auth.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProviderType {
    NATIVE("native"),
    GOOGLE("google"),
    NAVER("naver");

    private String providerType;

}
