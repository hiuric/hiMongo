# hiMongo : mongoDB-JAVA-driver wrapper
### mongo-shell-like simple [API](https://hiuric.github.io/hiMongo/) as below.
![sample0](docs/img/sample0.png)
click [this](https://hiuric.github.io/hiMongo/) to see [API](https://hiuric.github.io/hiMongo/)-javadoc.
### user class mapping
```
class MyRecord {
   ObjectId _id;
   String   type;
   double   value;
   Date     date;
   }
-----
// get last 3 type-A documents as Record-class list.
hiMongo.DB     db=hiMongo.use("db01");
List<MyRecord> recs
=db.get("coll_01")
   .find("{type:'A'}")
   .sort("{_id:-1}")
   .limit(3)
   .getClassList(MyRecord.class,hiU.REVERSE);
for(MyRecord rec:recs){
   System.out.println("value="+rec.value);
   }
```
### extended JSON input;comment,document array without bracket,ETC.
```
// 作曲家リスト
// 出生国と異なる国に移住し活躍した場合はnationalityに
// 出生国,移住国の順で記述
// ご存命の場合はlifeTimeに出生年のみ記述
{famiryName:'Bach',givenName:['Johann','Sebastian'],
 nationality:['独'],lifeTime:[1685,1750]}
{famiryName:'Bartók',givenName:['Béla'],
 nationality:['ハンガリー','米'],lifeTime:[1881,1945]}
{famiryName:'Beethoven',givenName:['Ludwig','van'],
 nationality:['独'],lifeTime:[1770,1827]}
{famiryName:'Brahms',givenName:['Johannes'],
 nationality:['独'],lifeTime:[1833,1897]}
{famiryName:'Chopin',givenName:['Frédéric-Françoic'],
 nationality:['ポーランド','仏'],lifeTime:[1810,1849]}
```
### run sample programs, test and rebuild library
```
このフォルダはhiMongoのビルド環境および試験環境です。
Javaは8以上が必要です。
サンプル/試験はmongoDBサーバーがローカルに動作していることを前提としています。

|-- configure  サンプル、試験、ビルドの共通設定
|   `-- configure.sh
|-- docs       hiMongo-jdoc
|   |-- index.html
|   ...
|-- lib        使用ライブラリおよびhiMongoライブラリ
|   |-- hiMongo_0_05.jar
|   |-- hiNote_3_09.jar
|   `-- mongo-java-driver-3.12.5.jar
|-- sampleSet  サンプルセット
|   |-- 01simpleAccess
|   ...
|   |-- configure.sh
|   `-- run.sh
|-- src
|   |-- A00_clear.sh
|   |-- A01_build.sh
|   |-- A09_javadoc.sh
|   |-- hiMongo.java
|   `-- hi_style.css
`-- testSet
    |-- TEST10_remote
    ...
    |-- configure.sh
    `-- test.sh

hiMongoライブラリは予めビルドされた形でlibに置いてあります。
lib/hiNote_3_09.jarとlib/mongo-java-driver-3.12.5.jarは
本来は正式な公開元からダウンロードすべきものですが、簡便化
とバージョン安定化のため、置きました。
mongo-java-driver-3.12.5.jar:
   https://mongodb.github.io/mongo-java-driver/
hiNote_3_09.jar:
   http://www.otsu.co.jp/OtsuLibrary/


＝＝＝＝＝ sample実行 ＝＝＝＝＝ 
sampleSetの各ディレクトリで
   ./run.sh
を実行するとサンプルが動きます。
標準エラーは各ディレクトリのmon.logに出ます。
sampleSet直下の./run.shで全サンプルが動きます


＝＝＝＝＝ 試験実行 ＝＝＝＝＝
testSetの各ディレクトリで
   ./test.sh
実行すると試験プログラムが動きます。
結果は予め置かれてあるリファレンスと比較し異なる場合は
エラーとなります。
標準エラーは各ディレクトリのmon.logに出ます。
testSet直下の./test.shで全テストが動きます。ただし、
TEST10_remote/は環境依存なので、予めコメントアウトする
必要があります。
./test_hson.shはhiMongoのパーズ機能を使う試験です。


＝＝＝＝＝ ライブラリのリビルド ＝＝＝＝＝
hiMongoライブラリをビルドしなおすには
   src/
下で
   ./A01_build.sh
を実行します。
lib/下に.jarが置かれます。

docsを作り直すには
   src/
下で
   ./A09_javadoc.sh
を実行します。
../docs/下にドキュメントが作られます。

注意:/var/www/html/hiMongo/下にもコピーが作られます。
     不要の場合A09_javadoc.shの最終行を#コメントアウト
     してください
```
