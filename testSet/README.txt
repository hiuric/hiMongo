hiMongoの試験プログラムセットです。

複数のプログラムがそれぞれのフォルダに置かれています。

run.shまたはrun.batで全プログラムのビルド/起動ができます。

個々のフォルダでrun.shまたはrun.batを動かすこともできます。

実行結果は表示され各フォルダ下のkekka.txtに置かれます。
kekka.txtは予め置かれているref_kekka.txtと比較され
差異がある場合エラーとなります。ただし、ObjectIdや
Dateなど一定値とならないものは差異チェックの対象では
有りません。
標準エラー出力がmon.logに出力されます。mongoDBのログが
出ています。
kekka.txtの文字コードはUTF-8です。
mon.logの文字コードはシステム標準です。

試験は３つのモードがあります。
./test_00_DirectMode.sh  : mongo-java-driverの直呼び
./test_00_WorkerMode.sh  : Caler/Workerモデル
./test_00_ServerMode.sh  : socke通信でサーバを呼ぶ
ServerMode実行にはlocalhostで
../bin/simpleServ.sh
を起動しておく必要があります
.sh上のlocalhostを他ホストのipアドレスに変えると
他ホストと接続することもできます。
