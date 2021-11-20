package jp.osaka.cherry.addressbook.android;

/**
 * クライアントのインタフェース
 */
public interface IClient {

    /**
     * 接続
     */
    void connect();

    /**
     * 非接続
     */
    void disconnect();
}
