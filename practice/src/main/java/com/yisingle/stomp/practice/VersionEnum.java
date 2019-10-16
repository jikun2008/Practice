package com.yisingle.stomp.practice;

public enum VersionEnum {

    VERSION_1_0("1.0"), VERSION_1_1("1.1"), VERSION_1_2("1.2");
    private String version;

    VersionEnum(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }


}
