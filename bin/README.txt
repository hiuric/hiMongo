Caller/WorkerモデルのWorkerサーバです。

ポート番号は8010です。

SimpleServ.shでサーバが起動されます。

SimpleClie.shはサーバ動作の単純な確認です。

./testSetで
　./test_00_ServerMode.sh  socke通信でサーバを呼ぶ
試験のサーバとなります

GitHUB
https://github.com/hiuric/AndroMongo
で公開中のAndroidアプリの通信先ともなります。
host外からのアクセスとなりますので8010ポートの
firewallブロックを解除（ポートを開放)しておく
必要があります。
centOSでポートを開放する手順は次のものです
$ sudo firewall-cmd --zone=public --add-port=8010/tcp --permanent
$ sudo systemctl restart firewalld
