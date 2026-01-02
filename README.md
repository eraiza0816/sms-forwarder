# SMS Forwarder

## 概要

SMS Forwarderは、Androidデバイスで受信したSMSメッセージを指定されたDiscordのWebhook URLに自動的に転送するアプリケーションです。

## 主な機能

-   受信したSMSをDiscordのWebhookに転送します。
-   Webhook URLを設定するためのシンプルなUIを提供します。
-   デバッグを容易にするためのアプリ内ロギング機能を備えています。

## 使い方

1.  お使いのAndroidデバイスにアプリケーションをインストールします。
2.  アプリを開きます。
3.  テキストフィールドに、あなたのDiscord Webhook URLを入力します。
4.  「保存」ボタンをタップします。
5.  アプリの初回起動時に「SMSの受信」権限を求められるので、許可してください。
6.  以上で設定は完了です。新しいSMSを受信するたびに、指定したWebhookにその内容が自動的に転送されます。

## ソースからのビルド

1.  このリポジトリをクローンします。
2.  Android Studioでプロジェクトを開きます。
3.  `app`モジュールをビルドして実行します。
./gradlew assembleRelease
