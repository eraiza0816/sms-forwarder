# ドメイン駆動設計 (DDD) ドキュメント: SMS Forwarder

## 1. コアドメイン (Core Domain)

このアプリケーションのコアドメインは、**SMSメッセージの転送**です。最も重要な関心事は、受信したSMSメッセージを、ユーザーが指定した外部のWebhook URLへ、確実かつ正確に届けることにあります。

## 2. サブドメイン (Subdomains)

コアドメインを支えるいくつかの支援的なサブドメインが存在します。

-   **SMS受信 (SMS Reception):** Android OSから`SMS_RECEIVED`ブロードキャストインテントをリッスンし、SMSメッセージ（送信元、本文）を抽出する役割を担います。
-   **Webhook転送 (Webhook Forwarding):** 抽出されたSMSデータを、指定されたURLへHTTP POSTリクエストとして送信する責務を持ちます。JSONペイロードの構築や通信の実行が含まれます。
-   **設定管理 (Configuration):** ユーザーが転送先のWebhook URLを設定し、それを永続化する機能を提供します。
-   **ロギング (Logging):** アプリの動作状況（成功、失敗、エラー詳細）を記録し、デバッグを容易にするための機能です。

## 3. 境界付けられたコンテキスト (Bounded Context)

このアプリケーションはシンプルであるため、全体で一つの**SMS転送コンテキスト (SMS Forwarding Context)** としてモデル化できます。

### 3.1. ユビキタス言語 (Ubiquitous Language)

-   **転送 (Forward):** SMSをWebhookへ送信する行為。
-   **SMS (Sms):** 転送対象のメッセージ。送信元(Sender)と本文(Body)を持つ。
-   **Webhook:** 転送先のURL。
-   **設定 (Configuration):** アプリの動作に必要な情報。現在はWebhook URLのみ。
-   **ログ (Log):** 転送処理の結果を記録したもの。

### 3.2. モデル

#### 集約 (Aggregates)

-   **転送リクエスト (ForwardingRequest):**
    -   1つのSMS受信から転送までをカプセル化する概念的な集約。
    -   ルートエンティティ: `SmsMessage`
    -   含まれるもの: `SmsMessage`、`Webhook`、転送結果（成功/失敗、HTTPステータス）。
    -   *現状の実装では、この集約は明確なクラスとしてモデル化されておらず、`SmsReceiver`の`onReceive`メソッド内で手続き的に処理されています。*

#### エンティティ (Entities)

-   **SmsMessage:**
    -   一意のID（タイムスタンプやシステムID）を持つことができる、個々のSMSメッセージ。
    -   属性: 送信元アドレス (`originatingAddress`)、メッセージ本文 (`messageBody`)。
    -   *現状では`SmsMessage.createFromPdu`で生成されるAndroidのフレームワークオブジェクトがエンティティとして機能しています。*

#### 値オブジェクト (Value Objects)

-   **WebhookUrl:**
    -   転送先のURLを表します。URL文字列そのものであり、不変です。
    -   *現状では`String`として扱われていますが、URLの妥当性検証などを含んだ`WebhookUrl`クラスとしてモデル化することで、より堅牢になります。*
-   **LogEntry:**
    -   タイムスタンプとメッセージ本文からなるログの各行。一度記録されたら変更されません。

## 4. アーキテクチャ

現状は、DDDのレイヤードアーキテクチャを簡略化した、実用的なAndroidアプリケーションの構造になっています。

-   **UIレイヤー (Presentation Layer):**
    -   `MainActivity`: ユーザーからのWebhook URL入力を受け付け、設定を保存し、ログを表示する責務を持ちます。
-   **アプリケーションレイヤー (Application Layer):**
    -   `SmsReceiver`: システムからのイベントを受け取り、ドメインロジックを呼び出すアプリケーションのエントリーポイントです。
    -   `WebhookSender`: `SmsReceiver`から指示を受け、HTTP通信を行なってWebhookへの転送処理を実行します。
-   **ドメインレイヤー (Domain Layer):**
    -   *現状、明確に分離されたドメインレイヤーは存在しません。* ドメインの知識やロジックは`SmsReceiver`と`WebhookSender`内に混在しています（例: JSONペイロードの構築ロジック）。
    -   よりDDDに近づけるなら、`SmsMessage`や`ForwardingService`のようなドメインオブジェクトを作成し、ビジネスロジックをそこに集約することが考えられます。
-   **インフラストラクチャレイヤー (Infrastructure Layer):**
    -   `HttpURLConnection`: ネットワーク通信という技術的詳細を担います。
    -   `SharedPreferences`: 設定の永続化というストレージの技術的詳細を担います。
    -   `File`: ログの永続化を担います。
