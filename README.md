# Address-Book
Last State: Nov 2021

# Address-Bookとは何か？

## 種類
Javaで記述したAndroidアプリです。

## 内容
以下、Google Pixel 3で動作確認済みです。
- 「住所一覧画面」では、住所の編集や保存が可能です。
- 「場所、電話、ウェブサイトの住所一覧画面」では、項目ごとの住所を確認できます。
- 「アーカイブ済みの住所一覧画面」では、アーカイブした住所を確認できます。
- 「ゴミ箱にある住所一覧画面」では、削除が可能です。

# だれが、「Address-Book」を使うか？

Androidアプリを作成したい方が参考として使用することを想定します。

# どのように、「Address-Book」を使うか？

以下のAPPLICATION_IDの値(value)を取得し、設定してください。

・AndroidManifest.xml

```
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="キーを設定してください"/>

```

・AdMobFragmentImpl.java

```
    @Override
    protected String getUnitId() {
        return "キーを設定してください";
    }
```
