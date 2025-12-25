package indi.yoloz.sample.sm4;


class SM4Context {

    private final int mode;    //加密1,解密0
    private final long[] sk = new long[32]; //子密钥,也称作轮密钥
    private final boolean padding;

    SM4Context(int mode, boolean padding) {
        this.mode = mode;
        this.padding = padding;
    }

    int getMode() {
        return mode;
    }

    long[] getSk() {
        return sk;
    }

    boolean isPadding() {
        return padding;
    }
}