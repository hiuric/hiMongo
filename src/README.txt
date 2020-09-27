ここでは
＊　hiMongoライブラリのビルド
＊　jdocの作成
を行います。

ライブラリの指定、バージョンの指定などは
  ../configure/configure.sh
で行っています。


================================================
ビルド
$ ./A01_build.sh
でライブラリが作成され
../lib/
に置かれます


================================================
JDOC
$ ./A09_jdoc.sh
でjdocが作成され
../docs
に置かれます。
さらに複製が
/var/www/html/hiMongo/
に置かれます。

1)javaのJDOC内にある<>に対するエスケープ処理を行います
  conv -with propJ.txt  -> hi/hiMongo.java
  propH:表示非表示切替は使っていません
（propHを使って少しJAVADOC部をコンパクトにする予定です）

2)javadoc起動
  --> ../doc/

3)生成されたhtmlに対し次の変更を施します
3-1)<a ref="xxxxx?is-external=true
  を
   <a  target="_blank" rel="noopener noreferrer" href="xxxxx?is-external=true
  にします。
　これはフレーム内にフレームが重層されていくのを防ぐためです
  変換法replace.regexに書いてあります。
  conv -with replace.regex
3-2)/mongodb.github.io/mongo-java-driver/3.7/javadoc/
　を
　 replace=/mongodb.github.io/mongo-java-driver/3.12/javadoc/
  に変更します
　これはjavadocの@linkが3.12ではうまく参照されないため、
　一旦3.7で作成した上で置き換えています。
　3.12のドキュメントがjavadocの@linkで参照できない理由はまだ分かっていません。
  conv -with convDrivVer.regex

なお、Java11ではフレーム無しのとても見づらいドキュメントが生成されますので、
java11環境でライブラリをビルドする場合でもjdocはjava8を使う事をお勧めします。
java11のHTML5対応仕様のようです。(これに限らずHTML5によりwebの表現力がどんど
んと低下していく現状がとても残念です）

================================================
２次ファイルの消去
$ ./A00_clean.sh
で
　.classファイル
　../testSet,../sampleSet下のmon.log(mongoDBのデバグ出力）
　../testSet下のkekka.txt
を消去します。


================================================
ソース規模表示
hiMongoはjdoc部が大きく、規模感がつかみづらいのでjdocを除いたソースの行数を
出すようにしました。
$ ./A08_wc.sh
で
　オリジナルのサイズ
　javadoc部を外したサイズ
　コメントと空行を外したサイズ
が出ます。

ちなみに2020/09/27 version 0_07で
オリジナル    :lines:5036      words:15086     chars:150859
JAVADOC排除   :lines:1037      words:3540      chars:30685
です。JAVADOCがコードのほぼ８割を占めています
