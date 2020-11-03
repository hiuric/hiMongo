package hi.db;

import otsu.hiNote.*;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.*;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.bson.types.Decimal128;
import org.bson.BsonRegularExpression;

/*
win8  java version "1.8.0_211"
linux openjdk version "1.8.0_265"
*/
/**
mongoDBアクセス機.
<!--
JDOCに関する注意点：<,>はconv -with propJ.txtでエスケープしますので&lt;化は不要です。
-->
<pre id="top" class=quote10 style="background-color:black;color:#eeffff">

 // JAVAプログラム
 hiMongo.DB db=hiMongo.use("db01");   // database   選択
 db.in("coll_01")                    // collection 選択
   .find("{type:'A'}","{_id:0}")      // typeが'A'のレコード
   .sort("{_id:-1}")                  // _idで逆向きにソート
   .limit(3)                          // 個数制限
   .getJsonList(hiU.REVERSE)          // 反転したリスト取得
   .forEach(Rd->System.out.println(Rd));// レコード表示

</pre>

<p>
hiMongoはドキュメント指向のDataBaseであるmongoDBにアクセスするjavaライブラリです。<br>
hiMongoはmongo-java-driverのラッパーです。
</p>
<hr>
<ul>
<li><a class=A1 href="#hiMongo_base">JavaでmongoDB;hiMongo基本</a>
   <ul>
   <li><a class=A1 href="#mongoDB">mongoDBとは</a>
   <li><a class=A1 href="#api_find">hiMongo-APIの基本;レコード取得</a>
   <li><a class=A1 href="#api_insert">hiMongo-APIの基本;レコード挿入</a>
   <li><a class=A1 href="#api_cascade">hiMongo-APIの基本;カスケード式</a>
   <li><a class=A1 href="#api_values">APIの基本；Java変数、辞書管理変数(#変数)</a>
   </ul>
</li>
<li><a class=A1 href="#find">find(検索)の引数と結果</a>
   <ul>
   <li><a class=A1 href="#find_filter">検索条件</a></li>
   <li><a class=A1 href="#find_field">取得フィールド</a></li>
   <li><a class=A1 href="#find_list">検索結果の全レコードをArrayListで取得</a>
      <ul>
      <li><a class=A1 href="#getDocument">Documentで取得</a>
      <li><a class=A1 href="#getClass">利用者定義クラス・インスタンスとして取得</a>
      <li><a class=A1 href="#getProbe">汎用データ探索機hiJSON.Probeで取得</a>
      <li><a class=A1 href="#getJsonMson">JSON文字列、拡張JSON文字列で取得</a>
      </ul>
   </li>
   <li><a class=A1 href="#forEachType_lambda">検索結果１レコード毎に取得しラムダ式の引数とする</a>
   </li>
   </ul>
<li><a class=A1 href="#dict_and_lambda">辞書管理変数、パラメタ内変数展開、再帰探索</a>
   <ul>
   <li><a class=A1 href="#forEach_lambda">検索結果をラムダスコープ変数(#変数)に入れラムダ式実行
   </li>
   <li><a class=A1 href="#readOne_lambda">検索結果をDBスコープ変数(#変数)に取り込む
   </li>
   <li><a class=A1 href="#recursive_lambda">再帰探索と変数
   </li>
   <li><a class=A1 href="#the_value">特別変数the_value
   </li>
   </ul>
</li>
<li><a class=A1 href="#insert_and_others">insert等のレコード処理</a>
   <ul>
   <li><a class=A1 href="#col_insert">insert</a>
   <li><a class=A1 href="#col_drop">drop</a>
   <li><a class=A1 href="#col_delete">delete</a>
   <li><a class=A1 href="#col_update">update</a>
   <li><a class=A1 href="#col_replace">replace</a>
   <li><a class=A1 href="#class_insert">利用者定義クラス・インスタンスをinsertする</a>
   </ul>
</li>
<li><a class=A1 href="#cap_index">cap指定（最大容量)、index設定</a>
   <ul>
   <li><a class=A1 href="#cap">キャップ指定</a></li>
   <li><a class=A1 href="#index">インデックス指定,ユニーク性保証,生存時間制限</a></li>
   </ul>
</li>
<li><a class=A1 href="#AG">aggregate(集計）</a>
   <ul>
   <li><a class=A1 href="#aggre">単純集計($match,$group)</a></li>
   <li><a class=A1 href="#lookup">$lookupによるフィールド結合</a></li>
   </ul>
</li>
<li><a class=A1 href="#mson">mongoDB拡張JSON(mson)記述</a>
   <ul>
   <li><a class=A1 href="#quote">引用符</a>
   <li><a class=A1 href="#date">ObjectId,Dateと数値</a>
   <!--<li><a class=A1 href="#regex">正規表現</a>-->
   <li><a class=A1 href="#comment">コメント等のhiMongo拡張(hson)</a>
   </ul>
</li>
<li><a class=A1 href="#remote">remote接続</a></li>
<li><a class=A1 href="#worker">Caller(API)とWorker(deiver呼び出し)</a></li>
<!--
<li><a class=A1 href="#driver">driver-APIを使う</a></li>
<li><a class=A1 href="#node">node(Object,Document)の取り扱い</a></li>
-->
<li><a class=A1 href="#build">build</a></li>
<li><a class=A1 href="#log">log</a></li>
<li><a class=A1 href="#version">更新履歴</a></li>
<li><a class=A1 href="#API">API</a></li>
</ul>


<hr>
<table style="line-height:100%" class=t0>
<tr><td colspan=2>用語:</td></tr>
<tr><td>mson</td><td>: mongoDB用の拡張JSON記述;ObjectId(...),ISODate(...)あり</td><tr>
<tr><td>json</td><td>: 標準JSON記述</td><tr>
<tr><td>hson</td><td>: msonにコメント等の入力解析拡張を行ったもの</td><tr>
<tr><td>Database</td><td>: データベース。名前付きcollectionの集合</td><tr>
<tr><td>Collection</td><td>: レコードの集合</td><tr>
<tr><td>レコード</td><td>: 入出力データの基本単位,名前付きフィールドの集合</td><tr>
<tr><td>フィールド</td><td>: レコード内の名前付きの値</td><tr>
<tr><td>ノード</td><td>: プログラム上でのデータ要素。データはノードのツリー構造となる</td><tr>
<tr><td>Document</td><td>: プログラムでのmsonのノード実装クラスおよびノードデータ<br>
mongoDBではレコードの事を'ドキュメント'と呼ぶとされているが、ここではプログラム上で限定される{@link org.bson.Document Document}クラスを指す</td></tr>
<tr><td>bson</td><td>: msonのバイナリ表現(プログラム上およびDB上)<br>
{@link org.bson.conversions.Bson Bson}クラスはJSON式の構造は採っておらず、hiMongoでは対象外としています。
</td><tr>
<!-- memo: a内のrel="noopener noreferrer"はjava8神経質jdocエラーとなる -->
<tr><td>
hiJSON
</td><td>:hiMongoが使用している
<a class=A1 target="_blank" rel="noopener noreferrer" href=
"http://www.otsu.co.jp/OtsuLibrary"
><i>Otsu</i>ラリブラリ(エントリ)
</a>
/
<a class=A1 target="_blank" rel="noopener noreferrer" href=
"http://www.otsu.co.jp/OtsuLibrary/jdoc/index.html"
>hiNoteパッケージ
</a>
に属するクラスです。
<td></tr>
</table>
<hr>

<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<!-- =================================================================== -->
<p class=B1 id="hiMongo_base">
&emsp;JavaでmongoDB;hiMongoの基本
</p>
<p class=B1_2 id="mongoDB">
&emsp;mongoDBとは
</p>
<div id="divMongoDB_1">
<p><input type="button" value="説明を表示する" style="WIDTH:12em"
   onClick="document.getElementById('divMongoDB_2').style.display='block';
            document.getElementById('divMongoDB_1').style.display='none'"></p>
</div>
<div id="divMongoDB_2" style="display:none">
<p><input type="button" value="説明を隠す" style="WIDTH:12em"
   onClick="document.getElementById('divMongoDB_2').style.display='none';
            document.getElementById('divMongoDB_1').style.display='block'"></p>
<p>
mongoDBはドキュメント指向DBです。SQLで扱うテーブル指向のリレーショナルDBとは異なるアプローチを採っています。
</p>
<p class=c>
&emsp;データモデル、階層
</p>
<p>
mongoDBでは次のようにデータがモデル化されます。
</p>
<pre class=prog10>
データベース(名前付き)
|
+-- コレクション(名前付き)
    |
    +-- レコード        概念上JSON文字列で表現される。
        |                  {"フィールド名":値、"フィールド名":値・・・}
        |
        +-- フィールド(名前付き)  jsonの一要素　中にさらにjson構造を含んでも良い
                                  名前と値で構成される
                                  "_id"という名称のデフォルトのフィールドがある
                                  "_id"値は通常重複が無い形で自動生成される
</pre>
<p>
レコードの形式を限定するスキーマはなく、内容はJSON形式に表せるものであれば何でも構いません。<br>
レコードには名前は付きません。フィールド値を指定して検索できます。<br>
mongoDBではレコードをDocumentと呼びますが、ここではコレクションに含まれる単位をレコードと呼びます。
</p>
<p>
コレクションはレコードの集合です。通常はコレクション中のレコードは同じ形式を与えます。ただし、そのような制限がある訳ではありません。
</p>
<p>
次のような形となります。
</p>
<pre class=prog10>
データベース "db01"
+-- コレクション "coll_A"
|   +-- {value1:'A' ,value2:1.57 ,value3:true <span class=gray>,_id:'????'</span>}
|   +-- {value1:'C' ,value2:3.78 ,value3:false<span class=gray>,_id:'????'</span>}
|   +-- {value1:'XX',value2:0    ,value3:true <span class=gray>,_id:'????'</span>}
|   +-- {value1:'Y' ,value2:2e326,value3:true <span class=gray>,_id:'????'</span>}
+-- コレクション "coll_X"
    +-- {name:'佐藤',age:50,趣味:'ゴルフ'  <span class=gray>,_id:'????'</span>}
    +-- {name:'田中',age:25,趣味:'音楽鑑賞'<span class=gray>,_id:'????'</span>}
    +-- {name:'鈴木',age:30,趣味:'読書'    <span class=gray>,_id:'????'</span>}
（_idは利用者が意識して挿入したものではありません）
</pre>
<p>
データ表現はJSONを基本とし、引用符などを取り扱いやすいものにしてあります。
</p>

<p class=c>
&emsp;レコード検索と挿入
</p>
<p>
レコードの取得には
</p>
<ul>
<li>検索条件</li>
<li>取得するフィールド</li>
</ul>
<p>
をJSON形式で指定します。
</p>
<pre class=prog10>
    .find("{val_n:'A'}","{_id:0,val_n:1,val_01:1}")
    //     検索条件      取得するフィールド
</pre>
<p>
条件はJSON形式です。
</p>
<pre class=prog10>
   { フィールド名:値 }
</pre>
<p>
の形です。
</p>
<p>
値は単純値の他、大小や正規表現条件を付加することが出来ます。
(<a class=A1 href="#compare">フィールド値の大小判断</a>参照)
</p>
<pre class=prog10>
  { val_01        : { $gt : 100} }
  // フィル―ド名 : 値（100より大きい)
  //                大なり: 数値
</pre>
<p>
論理積(and)論理和(or)で複数の条件を組み合わせることも出来ます。
(<a class=A1 href="#logic_ope">条件の組み合わせ</a>参照)
</p>
<pre class=prog10>
   { $and : [ // 配列で要素並びを設定する
        { val_n : 'A' },        // val_nフィールドの値が'A'
        { val_01 : {$gt : 100} }// かつval_01フィールドの値が100以上
        ]}
</pre>
<p>
取得するフィールドはフィールド名に値１または０で取得、取得しないを示します。
</p>
<pre class=prog10>
   { _id:0,val_01:0 } // _idフィールドとval_01フィールド以外を取得する。
</pre>
<p>
_idフィールドと他フィールドの扱いは異なっています。
<a class=A1 href="#get_fileld">取得フィールド</a>を参照してください。
</p>
<p>
レコードの追加にはレコード情報のJSON形式を与えます。
</p>
<pre class=prog10>
   .insertOne("{name:'佐藤',age:50,趣味:'ゴルフ'}");
</pre>
<p>
_idフィールドは自動で付加されます。
</p>

<p class=c>
&emsp;システム構成；サーバとクライアント
</p>
<p>
mongoDBはサーバとクライアント構成をとります。
</p>
<p>
サーバはクライアントプログラムと同じ装置でもリモート装置でも構いません。
</p>
<p>
クライアント側は用意されているドライバを用いアプリケーションプログラムを作成しアクセスする他、mongo-shellとよぶCUIプログラム(mongo)で端末から対話式にアクセスすることも出来ます。<br>
本ライブラリはクライアントプログラムを作成するためのものです。
</p>



<p class=c>
&emsp;制限
</p>
<p>
mongoDBは精密なスキーマ設計を行うことなく、簡便にDBを構築できます。
</p>
<p>
ある程度複雑な検索を行うことが出来ます。<br>
フィールド値をアトミックに増減させる機能も用意されています。<br>
現実的DB応用の広い範囲で利用できると考えられますし、リレーショナル・データベースに比べ高速であるため巨大な通信バッファのような応用も出来ます。
</p>
<p>
ただしコレクション間の連携処理は弱く、サブクエリといったものも用意されていません。<br>
必要な場合プログラムによる手続きによって実現することになります。
</p>
<p>
また、トランザクション機能を持たないことも注意すべき点です。
</p>
<p>
なお、最も気を付ける必要があるのが「名称を間違えると新たな要素の追加となる」という仕様です。フィールド値をupdateしたつもりが新なフィールドの追加となったといった障害が多発します。
</p>

<p><input type="button" value="説明を隠す△" style="WIDTH:12em"
   onClick="document.getElementById('divMongoDB_2').style.display='none';
            document.getElementById('divMongoDB_1').style.display='block';
            document.location='#divMongoDB_1'"></p>
</div>



<p class=B1_2 id="api_find">
&emsp;JavaでmongoDB;hiMongo-APIの基本;レコード取得
</p>
<p>
次のプログラムはJava用のmongoDBライブラリhiMongoの使用例です。
</p>
<pre class=quote10>
 // JAVAプログラム
 {@link hi.db.hiMongo.DB hiMongo.DB} db={@link hi.db.hiMongo#use(String) hiMongo.use("db01")};   // database   選択
 db{@link hi.db.hiMongo.DB#in(String) .in("coll_01")}                    // collection 選択
   {@link hi.db.hiMongo.Collection#find(Object,Object) .find("{type:'A'}","{_id:0}")}      // typeが'A'のレコード
   {@link hi.db.hiMongo.Finder#sort(Object) .sort("{_id:-1}")}                  // _idで逆向きにソート
   {@link hi.db.hiMongo.Finder#limit(int) .limit(3)}                          // 個数制限
   {@link hi.db.hiMongo.Accessor#getJsonList(long) .getJsonList(hiU.REVERSE)}          // 反転したリスト取得
   {@link java.lang.Iterable#forEach .forEach(Rj->System.out.println(Rj))};// レコード表示
</pre>
<p>
mongoDBのCUIであるmongo-shellの次の手続きに相当します。
</p>
<pre class=quote10>
 // mongo shell script
 use db01;
 db.coll_01.                             // collection 選択
    find({type:'A'},{_id:0}).            // typeが'A'のレコード
    sort({_id:-1}).                      // _idで逆向きにソート
    limit(3).                            // 個数制限
    toArray().reverse().                 // 反転したリスト取得
    forEach(Rd=>print(JSON.stringify(Rd)));// 要素表示
</pre>
<p>
typeフィールドが'A'であるレコードの最新３個を取得しています。
</p>
<p>
実際にビルド・実行できるフルコードと実行結果を示します
</p>
<div id="divSimple_1">
<p><input type="button" value="フルコードを表示する" style="WIDTH:12em"
   onClick="document.getElementById('divSimple_2').style.display='block';
            document.getElementById('divSimple_1').style.display='none'"></p>
</div>
<div id="divSimple_2" style="display:none">
<p><input type="button" value="フルコードを隠す" style="WIDTH:12em"
   onClick="document.getElementById('divSimple_2').style.display='none';
            document.getElementById('divSimple_1').style.display='block'"></p>
<pre class=quote8>
// このプログラムではtypeフィールドが'A'のレコードの最新３個を表示しています
// sort({_id-1})で一旦並びを逆にしていますので、リストは再反転して取得します
// ここではmongo-shellの出力形式と揃えるためgetMsonListを使っています
import hi.db.hiMongo;
import otsu.hiNote.*;
public class Test {
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");  // database   'db01'選択
      db.in("coll_01")                     // collection 選択
        .find("{type:'A'}","{_id:0}")      // typeが'A'のレコード,
        .sort("{_id:-1}")                  // _idで逆向きにソート
        .limit(3)                          // 個数制限
        .getMsonList(hiU.REVERSE)          // 反転したリスト取得
        .forEach(Rm->System.out.println(Rm)) // レコード表示
        ;
      }
   }
＊＊＊＊＊＊　dbデータ
> use db01
switched to db db01
> db.coll_01.find({},{_id:0})
{ "type" : "A", "value" : 12.3, "date" : ISODate("2020-08-17T07:07:00Z") }
{ "type" : "A", "value" : 4.56, "date" : ISODate("2020-08-17T07:07:10Z") }
{ "type" : "B", "value" : 2001, "date" : ISODate("2020-08-17T07:07:20Z") }
{ "type" : "A", "value" : 7.89, "date" : ISODate("2020-08-17T07:07:30Z") }
{ "type" : "A", "value" : 0.12, "date" : ISODate("2020-08-17T07:07:40Z") }
＊＊＊＊＊＊　ビルド実行結果
$ ./run.sh
----- 01simpleAccess -----
{"type":"A", "value":4.56, "date":ISODate("2020-08-17T07:07:10.000Z")}
{"type":"A", "value":7.89, "date":ISODate("2020-08-17T07:07:30.000Z")}
{"type":"A", "value":0.12, "date":ISODate("2020-08-17T07:07:40.000Z")}
OK
</pre>
<p><input type="button" value="フルコードを隠す△" style="WIDTH:12em"
   onClick="document.getElementById('divSimple_2').style.display='none';
            document.getElementById('divSimple_1').style.display='block';
            document.location='#div_1'"></p>
</div>
<p>
本文書に掲載するプログラムおよびプログラムの断片は次の習慣に沿って書かれています。
</p>
<div id="divCoding_1">
<p><input type="button" value="説明を表示する" style="WIDTH:12em"
   onClick="document.getElementById('divCoding_2').style.display='block';
            document.getElementById('divCoding_1').style.display='none'"></p>
</div>
<div id="divCoding_2" style="display:none">
<p><input type="button" value="説明を隠す" style="WIDTH:12em"
   onClick="document.getElementById('divCoding_2').style.display='none';
            document.getElementById('divCoding_1').style.display='block'"></p>

<p class=c>
&emsp;変数名;dbは例外
</p>
<p>
変数名は次の様に付けられます。
</p>
<table class=t0>
<tr>
<td style="width:8em">ローカル変数</td><td style="width:7em">先頭に_</td><td><pre class=prog10>MyRecord _rec=null;</pre></td>
</tr>
<tr>
<td>メソッド引数</td><td>最後に_</td><td><pre class=prog10>void func(String&emsp;arg_){...}</pre></td>
</tr>
<tr>
<td>クラス変数</td><td>_は付けない</td><td><pre class=prog10>class MyClass{String type,double value,Date date}</pre></td>
</tr>
</table>
<p>
例外："db"は例外的にローカル変数でも_を付加しません。ほぼ予約語に近い扱いとします。
</p>
<pre class=prog10>
    hiMongo.DB <span class=red>db</span>=hiMongo.use("db01");
</pre>

<p class=c>
&emsp;ラムダ式引数(hiMongoで扱う範囲のみ)
</p>
<p>
ラムダ式の引数名は大文字+小文字の2文字とします。<br>
データの表すものと型により次のように定めます。
</p>
<table class=t0>
<tr>
<td style="width:13em">レコードのDocument表現</td><td style="width:3em">Rd</td><td>forEachDocument(Rd->System.out.println(Rd))</td>
</tr>
<tr>
<td>レコードのClass表現&emsp;</td><td>Rc&emsp;</td><td>forEachClass(MyRecord.class,Rc->System.out.println(Rc.value))</td>
</tr>
<tr>
<td>レコードのProbe表現&emsp;</td><td>Rp&emsp;</td><td>forEachProbe(Rp->System.out.println(Rp.to("value").get())</td>
</tr>
<tr>
<td>レコードのJson表現&emsp;</td><td>Rj&emsp;</td><td>forEachJson(Rj->System.out.println(Rj))</td>
</tr>
<tr>
<td>レコードのMson表現&emsp;</td><td>Rm&emsp;</td><td>forEachMson(Rm->System.out.println(Rm))</td>
</tr>
<tr>
<td>Collection&emsp;</td><td>Co&emsp;</td><td>forThis(Co->System.out.println(Co.count())</td>
</tr>
<tr>
<td>Finder&emsp;</td><td>Fi&emsp;</td><td>forThis(Fi->Fi.getIterable().showRecordId(true))</td>
</tr>
<tr>
<td>Aggregator&emsp;</td><td>Ag&emsp;</td><td>forThis(Ag->System.out.println("some message"))</td>
</tr>
<tr>
<td>その他のDocumet&emsp;</td><td>Do&emsp;</td><td>getIndexList().forEach(Do->System.out.println(Do))</td>
</tr>
</table>

<p class=c>
&emsp;カスケード式のインデント
</p>
<p>
カスケード式では.を垂直にならべ、最後に.と同じ位置に終端子;を置きます。<br>
連続数が少ない場合は終端子は最終項の行端に置いても構いません。
</p>
<pre class=quote10>
      hiMongo.DB db=hiMongo.use("db01");
      db.in("coll_01")
        .find("{type:'A'}","{_id:0}")
        .sort("{_id:-1}")
        .limit(3)
        .getMsonList(hiU.REVERSE)
        .forEach(Rm->System.out.println(Rm)) // ;はここに置いても良い
        ;
        ^ 縦に揃える
</pre>
<p>
代入がある場合は変数と行を変え=を先頭に置きます。クラス変数アクセスは行を分けません。
</p>
<pre class=quote10>
      hiMongo.DB db=hiMongo.use("db01");
      long _last_date
      =db.in("coll_01")
         .find("{type:'A'}","{_id:0,date:1}")
         .sort("{_id:-1}").limit(1)
         .getClassList(WithDate.class)
         .get(0).date.getTime(); //クラス変数にアクセスする場合は行は分けない
</pre>

<p class=c>
&emsp;ブレイスとインデント
</p>
<p>
開始ブレイス{が有ると次の行を１インデント下げ、終了ブレイス}があると次の行を１インデント戻します。<br>
1インデントは３個の空白です。タブは厳禁です。<br>
この単純なルールによりif-elseやtry-catchなどの組が見やすくなります。
</p>
<pre class=quote10>
   if(...) {
      //...
      //...
      }
   else {
      //...
      //...
      }
   try{
      //...
      //..
      }
   catch(Exception _ex){
      //...
      }
</pre>

<p class=c>
&emsp;他
</p>
<p>
データのインデント、関数引数部のインデントなどは特に定めません。
</p>




<p><input type="button" value="説明を隠す△" style="WIDTH:12em"
   onClick="document.getElementById('divCoding_2').style.display='none';
            document.getElementById('divCoding_1').style.display='block';
            document.location='#divCoding_1'"></p>
</div>


<p class=B1_2 id="api_insert">
&emsp;APIの基本；レコード挿入
</p>
<p>
レコードの挿入では次の様にレコード内容を拡張JSON(mson)で与えます。
</p>
<pre class=quote10>
 // JAVAプログラム
 hiMongo.DB db=hiMongo.use("db01"));  // database   選択
 db.in("coll_01")                    // collection 選択
   {@link hi.db.hiMongo.Collection#insertOne(Object...) .insertOne("{type:'A',value:21,date:{$date:1597648050000}}")};
</pre>
<p>
時刻は{$date:<i>Unix-Epoch</i>}またはISODate('<i>グリニッジ日時</i>')の形です。<br>
mongo-shellでは<b>new Date</b>(<i>Unix-Epoch</i>)またはISODate('<i>グリニッジ日時</i>')となります。
</p>
<pre class=quote10>
 // mongo-shell
use db01
db.coll_01.
   insertOne({type:'A',value:21,date:<b>new Date</b>(1597648050000)});
</pre>
</p>
<p class=B1_2 id="api_cascade">
&emsp;APIの基本；カスケード式
</p>
<p>
次のようなAPIが用意されており、冒頭のようなカスケード式で呼び出すことも各型の変数で結果を受けて改めて呼び出すことも出来ます
</p>
<pre class=quote10>
hiMongo#use                    => DB
   DB#in                       => Collection
      Collection#find          => Finder
         Finder#sort           => Finder
         Finder#limit          => Finder
         Finder#skip           => Finder
         Finder#forEachXXX     => Finder
         Finder#getXXXList     => ArrayList<XXX>
      Collection#aggregate     => Aggregator
         Aggregator#forEachXXX => Aggregator
         Aggregator#getXXXList => ArrayList<XXX>
      Collection#insertOne     => Collection
      Collection#insertMany    => Collection
      Collection#updateOne     => Collection
      Collection#updateMany    => Collection
      Collection#replaceOne    => Collection
      Collection#deleteOne     => Collection
      Collection#deleteMany    => Collection
</pre>
<p>
各型のインスタンスで受けて操作する場合は次のような形です。
</p>
<pre class=quote10>
hiMongo.DB         db  = hiMongo.use("db01");
hiMongo.Collection _coll= db.in("coll_01");
_coll.find("{}","{_id:0}");
_coll.sort("{_id:-1}");
_coll.limit(3);
ArrayList&lr;String> _recs=coll.getJsonList(hiU.REVERT);
for(Document _rec:_recs)System.out.println(_rec);
</pre>
<p>
カスケード式では次の様になります。
</p>
<pre class=quote10>
hiMongo.DB db = hiMongo.use("db01");
db.in("coll_01")                     // DBのget()
  .find("{}","{_id:0}")               // Collectionのfind()
  .sort("{_id:-1}")                   // Finderのsort()
  .limit(3)                           // Finderのlimit()
  .getJsonList(hiU.REVERT)            // Finder(Accessor)のgetJsonList()
  .forEach(Rj->System.out.println(Rj)); // ArrayListのforEach()
// use()から続けることも可能ですが、習慣上DBにはdbという変数を割り当てます。
</pre>

<p class=B1_2 id="api_values">
&emsp;APIの基本；Java変数、辞書管理変数(#変数)
</p>
<p>
レコード情報、フィールド情報は
</p>
<ul>
<li>複数レコード情報をArrayList-Java変数で取得</li>
<li>１レコード毎にラムダ式引数(Java変数)として取得しラムダ式で処理</li>
<li>レコード、フィールド情報を辞書変数(#変数）に保持しラムダ式で処理</li>
</ul>
<p>
の形態でＤＢから読み取られます。
</p>
<p>
Java変数は直接Javaプログラムで取り扱います。
</p>
<pre class=quote10>
import hi.db.hiMongo;
public class Test {
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      ArrayList<String> _recs=db.in("coll_01").find("{type:'B'}").getJsonList();
      // 変数_recsをJavaで処理
</pre>
<p>
辞書変数にはDBスコープとforスコープがあります。forスコープはforOne,forEachなどのラムダ式内でアクセス可能でラムダ式では参照できません。
</p>
<p>
辞書変数は先頭が'#'である名前で特定されます。<br>
get()などの手続きで取り出すことができる他、find()引数内に置かれた#変数もfind実行時に展開されます。
</p>
<pre class=quote10>
import hi.db.hiMongo;
public class Test {
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      db.in("coll_01")
        .find("{type:'B'}")                             // type=='B'のレコードの
        .forOne("{#B_id:_id}",Fi->                      //   _idを#B_idとして取得
            Fi.find("{_id:{$gte:#B_id}}","{_id:0}")     // _id#B_id以上のレコード検索
              .forEachMson(Rm->System.out.println(Rm)));// 表示(Rmは値のラムダ式引数)
      }
   }
// forOneのラムダ引数はFinderです。FinderのfindはカレントのCollectionに対して出されます。
＊＊＊　ＤＢ
   {type:'A',value:12.3,date:ISODate('2020-08-17T07:07:00.000Z')}
  ,{type:'A',value:4.56,date:ISODate('2020-08-17T07:07:10.000Z')}
  ,{type:'B',value:2001,date:ISODate('2020-08-17T07:07:20.000Z')} // これ以降が欲しい
  ,{type:'A',value:7.89,date:ISODate('2020-08-17T07:07:30.000Z')}
  ,{type:'A',value:0.12,date:ISODate('2020-08-17T07:07:40.000Z')}
＊＊＊　実行結果
----- 14sharpValue -----
{'type':'B', 'value':2001, 'date':ISODate('2020-08-17T07:07:20.000Z')}
{'type':'A', 'value':7.89, 'date':ISODate('2020-08-17T07:07:30.000Z')}
{'type':'A', 'value':0.12, 'date':ISODate('2020-08-17T07:07:40.000Z')}
</pre>



<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<!-- ======================================================================= -->
<p class=B1 id=find>
&emsp;find(検索)の引数と結果
</p>
<p>
{@link hi.db.hiMongo.Collection#find(Object,Object) find}は２つの引数を持ちます。検索条件と取得フィールドです。<br>
</p>
<p class=B1_2 id="find_filter">
&emsp;検索条件
</p>
<p>
検索条件には次の様な記述が出来ます。
</p>

<table class=t>
<tr>
<td>条件</td>
<td>記述例</td>
</tr>
<tr>
<td>無条件</td>
<td>find()またはfind("{}")</td>
</tr>
<tr>
<td>フィールド値の単純一致</td>
<td>find("{name:'A'}")</td>
</tr>
<tr id="compare">
<td>フィールド値の大小判断</td>
<td>find("{value:{$gt:100}}")
<pre class=quot8>
形式:
{<i>field</i>:{$eq:<i>値</i>}}        ==
{<i>field</i>:{$ne:<i>値</i>}}        !=
{<i>field</i>:{$lt:<i>値</i>}}        <
{<i>field</i>:{$lte:<i>値</i>}}       <=
{<i>field</i>:{$gt:<i>値</i>}}        >
{<i>field</i>:{$gte:<i>値</i>}}       >=
{<i>field</i>:{$in:[<i>値,..</i>]}}   含まれる
{<i>field</i>:{$nin:[<i>値,...</i>]}} 含まれない
</pre>
</td>
</tr>
<tr id="logic_ope">
<td>条件の組み合わせ</td>
<td>find("{$and:[{name:'A'},{value:{$gt:100}}]}")
<pre class=quot8>
形式:
{$and:[{条件},{条件},...]}
{$or:[{条件},{条件},...]}
{$not:{条件}}
</pre>

</td>
</tr>
<tr>
<td>部分一致、正規表現</td>
<td>
find("{name:/et/}")       nameがetを含む<br> 
find("{name:/^T.*sky$/}") nameがTで始まりskyで終わる<br>
find("{name:/(ba|ab)/}")  nameがbaまたはabを含む
</td>
</tr>
</table>

<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<!-- --------------------------------------- -->
<p class=B1_2 id="find_field">
&emsp;取得フィールド
</p>
<p>
取得フィールドの指定は{}内に「フィールド名：0または１」を必要分カンマで繋いで記述します。<br>
0は非表示（取得しない)を1は表示(取得する)を表します。<br>
指定は_idフィールドとその他の利用者フィールドでは意味が少し異なります。<br>
利用者フィールドに1を指定した場合、指定しないフィールドは非表示となります。0を指定した場合指定フィールドが非表示となります。<br>
利用者フィールドの指定に0と1が混在することは許されません。
</p>
<pre class=quote10>
find()                       // 全て取得
find("{}")                   // 全て取得
find("{}","{}")              // 全て取得
find("{}","{_id:0}")         // _idフィールド以外全て取得
<span class=gray>{ "A" : 10, "B" : 20, "C" : 30, "D" : 40 }
{ "A" : 11, "B" : 21, "C" : 31, "D" : 41 }</span>
find("{}","{_id:0,A:1}")     // Aフィールドのみ取得
<span class=gray>{ "A" : 10 }
{ "A" : 11 }</span>
find({},{_id:0,A:0})         // _id,Aフィールド以外取得
<span class=gray>{ "B" : 20, "C" : 30, "D" : 40 }
{ "B" : 21, "C" : 31, "D" : 41 }</span>
find("{}","{_id:0,A:1,C:1}") // A,Cフィールドのみ取得
<span class=gray>{ "A" : 10, "C" : 30 }
{ "A" : 11, "C" : 31 }</span>
find("{}","{_id:0,A:0,C:0}") // _id,A,Cフィールド取得以外取得
<span class=gray>{ "B" : 20, "D" : 40 }
{ "B" : 21, "D" : 41 }</span>
find("{}","{_id:0,A:0,C:1}") // エラー
</pre>
<p>
取得しないと指定したフィールドでも検索条件、ソート条件に使用できます。
</p>

<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<!-- --------------------------------------- -->
<p class=B1_2 id="find_list">
&emsp;検索結果の全レコードをArrayListで取得
</p>
<p>
{@link hi.db.hiMongo.Collection#find(Object,Object) find}の戻りは{@link hi.db.hiMongo.Finder hiMongo.Finder}です。
{@link hi.db.hiMongo.Finder#sort(Object) sort()},{@link hi.db.hiMongo.Finder#limit(int) limit()}など幾つかの手順を経て最終的にはgetXXXList()メソッドで結果を{@link java.util.ArrayList ArrayList<T>}にして戻します。
</p>
<p>
得られるリストは次のものです。
</p>
<p>
<table class=t>
<tr>
<td>要素の型</td><td>取得メソッド</td><td>説明</td>
</tr>

<tr>
<td>{@link org.bson.Document}</td><td>{@link hi.db.hiMongo.Accessor#getDocumentList(long) getDocumentList}</td>
<td>
mongoDBのデータノードです。<br>
基本的にはJSONに対応するObjectのツリーです。{@link org.bson.Document}は{@link java.util.Map Map<String,Object>}型の辞書です。Stringや数値などの基本タイプの他{@link org.bson.types.ObjectId}クラス,{@link java.util.Date}クラスを持ちます。
</td>
</tr>

<tr>
<td>クラスインスタンス</td><td>{@link hi.db.hiMongo.Accessor#getClassList(Class,long) getClassList}</td>
<td>
利用者クラスにマップしたデータが得られます。<br>
最も間違いが起こりにくい方法です。
</td>
</tr>

<tr>
<td>{@link otsu.hiNote.hiJSON.Probe hiJSON.Probe}</td><td>{@link hi.db.hiMongo.Accessor#getProbeList getProbeList}</td>
<td>
データノードの探索機です。<br>
探索対象となるノード自体は{@link hi.db.hiMongo.Accessor#getDocumentList(long) getDocumentList()}で得られるものと同じ{@link org.bson.Document}とその構成要素（文字列やリスト、数値など）です。<br>
{@link org.bson.Document}はMap<String,Object>であり{@link otsu.hiNote.hiJSON.Probe hiJSON.Probe}では辞書として扱われます。
</td>
</tr>


<tr>
<td>String(mson)</td><td>{@link hi.db.hiMongo.Accessor#getMsonList(long) getMsonList}</td>
<td>
mongoの拡張JSON形の文字列。<br>
ObjectId("..."),ISODate("...")が用いられます。
</td>
</tr>


<tr>
<td>String(json)</td><td>{@link hi.db.hiMongo.Accessor#getJsonList(long) getJsonList}</td>
<td>
標準JSON形の文字列。<br>
{"$oid":"..."},{"$date":"..."}が用いられます。
</td>
</tr>

</table>
</p>
<p>
getXxxList()にhiU.REVERSEを付けると逆並びとなります。
{@link otsu.hiNote.hiU hiU}は汎用ライブラリ{@link otsu.hiNote}の代表クラスです。
</p>

<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<!-- - - - - - - - - - - - - - -->
<p class=c id="getDocument">
&emsp;{@link org.bson.Document Document}で取得
</p>
<p>
{@link hi.db.hiMongo.Collection#find(Object,Object) find}の結果を{@link hi.db.hiMongo.Accessor#getDocumentList(long) getDocumentList()}を用い検索結果を{@link org.bson.Document Document}のリストとして得ることが出来ます。<br>
引数としてhiU.REVERSEを付けると逆順のリストが得られます。
</p>
<pre class=quote10>
hiMongo.DB db=hiMongo.use("db01");
ArrayList<Document> _dlist
=db.in("coll_01")
   .find("{}","{_id:0}")
   .getDocumentList();
</pre>
<!--
<p>
{@link hi.db.hiMongo.Finder#forEachDocument(hiU.ConsumerEx) forEachDocument(Documentを引数とするラムダ式)}用いるとリストを構成することなく一要素毎に処理することが出来ます。
</p>
<pre class=quote10>
hiMongo.DB db=hiMongo.use("db01");
db.in("coll_01")
  .find("{}","{_id:0}")
  .forEachDocument(Rd->System.out.println(Rd.toJson()));
</pre>
-->

<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<!-- - - - - - - - - - - - - - -->
<p class=c id="getClass">
&emsp;利用者定義クラス・インスタンスとして取得
</p>
<p>
{@link hi.db.hiMongo.Collection#find(Object,Object) find}の結果を{@link hi.db.hiMongo.Accessor#getClassList(Class,long) getClassList()}を用い検索結果を利用者定義のクラス・インスタンスのリストとして得ることが出来ます。
</p>
<pre class=quote10>
<span class=red>class MyRecord</span> {   // レコード内容
   String type;
   double value;
   Date   date;
   }
-----
double _start_date=取得開始レコードのunixエポック
ArrayList<<span class=red>MyRecord</span>> _recs
=db.in("coll_01")
   .find("{$and:["+
            "{type:'A'},"+
            "{date:{$gte:{$date:"+_start_date+"}}}"+
            "]}",
         "{_id:0}")
   .<span class=blue>getClassList</span>(<span class=red>MyRecord.class</span>);
<span class=gray>for(MyRecord _rec:_recs){
   System.out.println("value="+_rec.value);
   }</span>
</pre>

<div id="divClass_1">
<p><input type="button" value="フルコードを表示する" style="WIDTH:12em"
   onClick="document.getElementById('divClass_2').style.display='block';
            document.getElementById('divClass_1').style.display='none'"></p>
</div>
<div id="divClass_2" style="display:none">
<p><input type="button" value="フルコードを隠す" style="WIDTH:12em"
   onClick="document.getElementById('divClass_2').style.display='none';
            document.getElementById('divClass_1').style.display='block'"></p>
<pre class=quote8>
// このプログラムでは先ず最後のレコードのdate情報を取得し、それから
// 30秒前からのレコードをRecordクラスの並びとして取得し、統計情報を
// 計算しています。
import hi.db.hiMongo;
import otsu.hiNote.*;
import java.util.*;
public class Test {
   static class WithDate { // dateだけを得るクラス
      Date date;
      }
   static class Record {   // レコード内容
      String type;
      double value;
      Date   date;
      }
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      // 最後の'A'レコードの時刻(unix-epoch)を得る
      long _last_date
      =db.in("coll_01")
         .find("{type:'A'}","{_id:0,date:1}")
         .sort("{_id:-1}").limit(1)
         .getClassList(WithDate.class)
         .get(0).date.getTime();

      // 最後のレコードの30秒前からの'A'レコード取得
      long _start_date= _last_date-30000; // 30秒前
      System.out.println("last="+_last_date+" start="+_start_date);
      ArrayList<Record> _recs
      =db.in("coll_01")
         .find("{$and:["+
                     "{type:'A'},"+
                     "{date:{$gte:{$date:"+_start_date+"}}}"+
                      "]}",
               "{_id:0}")
         .getClassList(Record.class);
      System.out.println("records="+hiU.str(_recs,hiU.WITH_INDENT));

      // 最大、最少、平均を求める
      double _min  = Double.MAX_VALUE;
      double _max  = Double.MIN_VALUE;
      double _total= 0;
      for(Record _rec:_recs){
         double _val=_rec.value;
         _min    = Math.min(_min,_val);
         _max    = Math.max(_max,_val);
         _total += _val;
         }
      double _avg= _total/_recs.size();
      System.out.printf("min=%.2f max=%.2f avg=%.2f\n",_min,_max,_avg);
      }
// {@link otsu.hiNote.hiU#str(Object,long) hiU.str()}は構造を表示するメソッドです.
// hiU.WITH_INDENTはインデントを付加するオプションです。{@link otsu.hiNote.hiFieldFormat#option hiFieldFormatオプション}参照
＊＊＊＊＊＊　dbデータ
> use db01
switched to db db01
> db.coll_01.find({},{_id:0})
{ "type" : "A", "value" : 12.3, "date" : ISODate("2020-08-17T07:07:00Z") }
{ "type" : "A", "value" : 4.56, "date" : ISODate("2020-08-17T07:07:10Z") }
{ "type" : "B", "value" : 2001, "date" : ISODate("2020-08-17T07:07:20Z") }
{ "type" : "A", "value" : 7.89, "date" : ISODate("2020-08-17T07:07:30Z") }
{ "type" : "A", "value" : 0.12, "date" : ISODate("2020-08-17T07:07:40Z") }
＊＊＊＊＊＊　ビルド実行結果
$ ./run.sh
----- 02classAssign -----
last=1597648060000 start=1597648030000
records=[
   {
      type="A",
      value=4.56,
      date=Mon Aug 17 16:07:10 JST 2020},
   {
      type="A",
      value=7.89,
      date=Mon Aug 17 16:07:30 JST 2020},
   {
      type="A",
      value=0.12,
      date=Mon Aug 17 16:07:40 JST 2020}]
min=0.12 max=7.89 avg=4.19
OK
</pre>
<p>
なお、ここの例の様な単純な計算をするだけであれば、<a class=A1 href="#aggre">「aggregate(集計）-単純集計($match,$group)」</a>を用いて実行できます。
</p>
<p><input type="button" value="フルコードを隠す△" style="WIDTH:12em"
   onClick="document.getElementById('divClass_2').style.display='none';
            document.getElementById('divClass_1').style.display='block';
            document.location='#divClass_1'"></p>
</div>


<p>
要素の過不足がチェックされますので、チェックをオフにする必要がある場合はgetClassList()の前にチェックをオフにする指定を入れます。<br>
{@link hi.db.hiMongo.Finder#without_option(long)}を用います。指定するフラグは<a class=A1 href="http://www.otsu.co.jp/OtsuLibrary/jdoc/otsu/hiNote/hiJSON.html#option">hiJSONのパーズオプション</a>を参照してください
</p>
<pre class=quote10>
ArrayList<MyClass> _recs=
db.in("coll_01")
  .find("{}","{_id:0}")
  .without_option(hiU.CHECK_UNKNOWN_FIELD // クラスにないフィールドを無視する
                 |hiU.CHECK_UNSET_FIELD)  // セットされないフィールドをエラーとしない
  .getClassList();
</pre>
<!--
<p>
{@link hi.db.hiMongo.Finder#forEachClass(Class,hiU.ConsumerEx) forEachClass(class,クラスインスタンスを引数とするラムダ式)}を用いるとリストを構成することなく一要素毎に処理することが出来ます。
</p>
<pre class=quote10>
hiMongo.DB db=hiMongo.use("db01");
db.in("coll_01")
  .find("{}","{_id:0}")
  .forEachClass(MyClass.class,Rc->System.out.println(Rc.value));
</pre>
-->

<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<!-- - - - - - - - - - - - - - -->
<p class=c id=getProbe>
&emsp;汎用データ探索機{@link otsu.hiNote.hiJSON.Probe hiJSON.Probe}で取得
</p>
<p>
{@link hi.db.hiMongo.Collection#find(Object,Object) find}の結果を{@link hi.db.hiMongo.Accessor#getProbeList(long) getProbeList()}を用い検索結果を{@link otsu.hiNote.hiJSON.Probe hiJSON.Probe}のリストで受け取ることが出来ます。<br>
{@link otsu.hiNote.hiJSON.Probe hiJSON.Probe}は汎用ノードの探索機で辞書(Map<String,Object>).リスト(List<Object>),文字列,数値などで構成されるツリーを探査するメソッドを持ちます。<br>
{@link org.bson.Document}は辞書/Map<String,Object>ですのでProbeの探索対象となります。<br>
要素へのアクセスは文字列による名前での指定となります。
</p>
<pre class=quote10>
double _start_date=取得開始レコードのunixエポック
ArrayList<hiJSON.Node> _recs
=db.in("coll_01")
   .find("{$and:["+
            "{type:'A'},"+
            "{date:{$gte:{$date:"+_start_date+"}}}"+
            "]}",
         "{_id:0}")
   .<span class=blue>getProbeList</span>();
<span class=gray>for(hiJSON.Probe _probe:_recs){
   System.out.println("value="+_probe.at("value").getDouble());
   }</span>
</pre>
<div id="divProbe_1">
<p><input type="button" value="フルコードを表示する" style="WIDTH:12em"
   onClick="document.getElementById('divProbe_2').style.display='block';
            document.getElementById('divProbe_1').style.display='none'"></p>
</div>
<div id="divProbe_2" style="display:none">
<p><input type="button" value="フルコードを隠す" style="WIDTH:12em"
   onClick="document.getElementById('divProbe_2').style.display='none';
            document.getElementById('divProbe_1').style.display='block'"></p>
<pre class=quote8>
import hi.db.hiMongo;
import otsu.hiNote.*;
import java.util.*;
public class Test {
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      // 最後の'A'レコードの時刻(unix-epoch)を得る
      long _last_date
      =db.in("coll_01")
         .find("{type:'A'}","{_id:0,date:1}")
         .sort("{_id:-1}").limit(1)
         .getProbeList()
         .get(0).at("date").get(D->((Date)D).getTime());

      // 最後のレコードの30秒前からの'A'レコード取得
      long _start_date= _last_date-30000; // 30秒前
      System.out.println("last="+_last_date+" start="+_start_date);
      ArrayList<hiJSON.Probe> _recs
              =db.in("coll_01")
                 .find("{$and:["+
                           "{type:'A'},"+
                           "{date:{$gte:{$date:"+_start_date+"}}}"+
                            "]}",
                        "{_id:0}")
                 .getProbeList();

      // 最大、最少、平均を求める
      double _min  = Double.MAX_VALUE;
      double _max  = Double.MIN_VALUE;
      double _total= 0;
      for(hiJSON.Probe _probe:_recs){
         double _val= _probe.at("value").getDouble();
         _min    = Math.min(_min,_val);
         _max    = Math.max(_max,_val);
         _total += _val;
         }
      double _avg= _total/_recs.size();
      System.out.printf("min=%.2f max=%.2f avg=%.2f\n",_min,_max,_avg);
      }
   }
＊＊＊＊＊＊　dbデータ
> use db01
switched to db db01
> db.coll_01.find({},{_id:0})
{ "type" : "A", "value" : 12.3, "date" : ISODate("2020-08-17T07:07:00Z") }
{ "type" : "A", "value" : 4.56, "date" : ISODate("2020-08-17T07:07:10Z") }
{ "type" : "B", "value" : 2001, "date" : ISODate("2020-08-17T07:07:20Z") }
{ "type" : "A", "value" : 7.89, "date" : ISODate("2020-08-17T07:07:30Z") }
{ "type" : "A", "value" : 0.12, "date" : ISODate("2020-08-17T07:07:40Z") }
＊＊＊＊＊＊　ビルド実行結果
$ ./run.sh
----- 03probeAccess -----
last=1597648060000 start=1597648030000
min=0.12 max=7.89 avg=4.19
OK
</pre>
<p><input type="button" value="フルコードを隠す△" style="WIDTH:12em"
   onClick="document.getElementById('divProbe_2').style.display='none';
            document.getElementById('divProbe_1').style.display='block';
            document.location='#divProbe_1'"></p>
</div>
<!--
<p>
{@link hi.db.hiMongo.Finder#forEachProbe(hiU.ConsumerEx) forEachProbe(Probeを引数とするラムダ式)}を用いるとリストを構成することなく一要素毎に処理することが出来ます。
</p>
<pre class=quote10>
hiMongo.DB db=hiMongo.use("db01");
db.in("coll_01")
  .find("{}","{_id:0}")
  .forEachProbe(Rp->System.out.println(Rp.getDouble("value")));
</pre>
-->

<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<!-- - - - - - - - - - - - - - - -->
<p class=B2_2 id="getJsonMson">
&emsp;JSON文字列、拡張JSON文字列で取得
</p>
<p>
{@link hi.db.hiMongo.Collection#find(Object,Object) find}の結果を{@link hi.db.hiMongo.Accessor#getJsonList(long) getJsonList()}を用いれば純粋なJSON文字列リスト、
{@link hi.db.hiMongo.Accessor#getMsonList(long) getMsonList()}を用いれば拡張JSON文字列のリストとして得ることが出来ます。
</p>
<pre class=prog10>
hiMongo.DB db=hiMongo.use("db01");
ArrayList<String> _jsons
=db.in("coll_01")
  .find("{}","{_id:0}")
  .getJsonList();
</pre>
<!--
<p>
{@link hi.db.hiMongo.Finder#forEachJson(hiU.ConsumerEx) forEachJson(Json文字列を引数とするラムダ式)},{@link hi.db.hiMongo.Finder#forEachMson(hiU.ConsumerEx) forEachMson(Mson文字列を引数とするラムダ式)}を用いるとリストを構成することなく一要素毎に処理することが出来ます。
</p>
<pre class=quote10>
hiMongo.DB db=hiMongo.use("db01");
db.in("coll_01")
  .find("{}","{_id:0}")
  .forEachJson(Rj->System.out.println(Rj));
</pre>
-->

<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<!-- - - - - - - - - - - - - - - - - - - - -->
<p class=B1_2 id="forEachType_lambda">
&emsp;検索結果１レコード毎に取得しラムダ式の引数とする
</p>
<p>
forEach型()メソッドを使うとfindで取得するレコード一個毎に変数としラムダ式に与えることができます。
</p>
<pre class=quote10>
import hi.db.hiMongo;
import otsu.hiNote.*;
import java.util.*;
public class Test {
   static class MyRecord {
      String type;
      double value;
      Date   date;
      }
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      db.in("coll_01")
        .find("{type:'A'}","{_id:0}")
        .forEachDocument(Rd->System.out.println("Rd "+Rd))
        .forEachMson(Rm->System.out.println("Rm "+Rm))
        .forEachJson(Rj->System.out.println("Rj "+Rj))
        .forEachClass(MyRecord.class,
                      Rc->System.out.println("Rc "+hiU.str(Rc)))
        .forEachProbe(Rp->System.out.println(
            "Rp "+Rp.get("type")+"/"+Rp.get("value")+"/"
                 +Rp.get("date")))
        ;
      }
   }
＊＊＊
----- 16forEachTypes -----
Rd Document{{type=A, value=12.3, date=Mon Aug 17 16:07:00 JST 2020}}
Rd Document{{type=A, value=4.56, date=Mon Aug 17 16:07:10 JST 2020}}
Rd Document{{type=A, value=7.89, date=Mon Aug 17 16:07:30 JST 2020}}
Rd Document{{type=A, value=0.12, date=Mon Aug 17 16:07:40 JST 2020}}
Rm {'type':'A', 'value':12.3, 'date':ISODate('2020-08-17T07:07:00.000Z')}
Rm {'type':'A', 'value':4.56, 'date':ISODate('2020-08-17T07:07:10.000Z')}
Rm {'type':'A', 'value':7.89, 'date':ISODate('2020-08-17T07:07:30.000Z')}
Rm {'type':'A', 'value':0.12, 'date':ISODate('2020-08-17T07:07:40.000Z')}
Rj {"type":"A", "value":12.3, "date":{"$date":1597648020000}}
Rj {"type":"A", "value":4.56, "date":{"$date":1597648030000}}
Rj {"type":"A", "value":7.89, "date":{"$date":1597648050000}}
Rj {"type":"A", "value":0.12, "date":{"$date":1597648060000}}
Rc {type="A", value=12.3, date=Mon Aug 17 16:07:00 JST 2020}
Rc {type="A", value=4.56, date=Mon Aug 17 16:07:10 JST 2020}
Rc {type="A", value=7.89, date=Mon Aug 17 16:07:30 JST 2020}
Rc {type="A", value=0.12, date=Mon Aug 17 16:07:40 JST 2020}
Rp A/12.3/Mon Aug 17 16:07:00 JST 2020
Rp A/4.56/Mon Aug 17 16:07:10 JST 2020
Rp A/7.89/Mon Aug 17 16:07:30 JST 2020
Rp A/0.12/Mon Aug 17 16:07:40 JST 2020
</pre>


<p>
現版ではまとめてレコードを取得し、一個ずつラムダ式に渡しています。<br>
「確実に」1個ずつDBから取得しながらラムダ式に与える形にすることを検討中です。forEachでなくforOneByOneとして追加する可能性もあります。
</p>

<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<!-- =================================================================== -->
<p class=B1 id="dict_and_lambda">
&emsp;辞書管理変数、パラメタ内変数展開、再帰探索
</p>
<p>
取得した情報をラムダ式の直接の引数とはせず、辞書管理する変数に置くことにより、柔軟なプログラム構造をとることができます。<br>
{@link hi.db.hiMongo.Finder#find(Object,Object) find()}などのパラメタ上に変数名を記述すると実行時の情報で展開されます。<br>
なお{@link hi.db.hiMongo.Finder#find(Object,Object) find()}は基本的には{@link hi.db.hiMongo.Collection}の{@link hi.db.hiMongo.Collection#find(Object,Object) メソッド}ですが、{@link hi.db.hiMongo.Finder}から呼ぶことも可能で、カレントの{@link hi.db.hiMongo.Collection}を使った呼び出しとなります。
</p>

<!-- - - - - - - - - - - - - - - - - - -->
<p class=B1_2 id="forEach_lambda">
&emsp;検索結果をラムダスコープ変数(#変数)に入れラムダ式実行
</p>
<p>
{@link hi.db.hiMongo.Finder#forEach(hiU.ConsumerEx) forEach()},{@link hi.db.hiMongo.Finder#forOne(hiU.ConsumerEx) forOne()},{@link hi.db.hiMongo.Finder#forEachRecursive(String,hiU.FunctionEx) forEachRecursive()},{@link hi.db.hiMongo.Finder#forOneRecursive(String,hiU.FunctionEx) forOneRecursive()}の各メソッドではforスコープの変数が定義され、{@link hi.db.hiMongo.Accessor#get(String) get()}などのメソッドで取得できるほか、{@link hi.db.hiMongo.Finder#find(Object,Object) find()}他のメソッドで引数内の変数記述が展開されます。
</p>
<table class=t>
<tr>
<td style="width:3.2em">項目</td>
<td>例</td>
<td>説明</td>
</tr>
<tr>
<td>{@link hi.db.hiMongo.Collection#find(Object,Object) find}など<br>dbアクセス</td>
<td>
<pre class=prog10>
db.setValue("#TARGET","A");
db.in("coll_01")
  .find("{name:#TARGET}}")
</pre>
</td>
<td>#TATGETに値を設定してあれば展開されfindが実行されます</td>
</tr>
<tr>
<td>{@link hi.db.hiMongo.Accessor#get(String) get}</td>
<td>
<pre class=prog10>
Object _target=get("#TARGET");
</pre></td>
<td>Javaプログラム上で変数内容を取得できます。得られるのオブジェクトです。</td>
</tr>
<tr>
<td>{@link hi.db.hiMongo.Accessor#disp(String) disp}</td>
<td>
<pre class=prog10>
String _text=disp("target=#TARGET name=#NAME"));
</pre>
</td>
<td>Javaプログラム上で、文字列上に置いた変数を文字列に展開します。展開後の文字列が得られます。</td>
</tr>
<tr>
<td>{@link hi.db.hiMongo.Accessor#eval(String) eval}</td>
<td>
<pre class=prog10>
Object _obj=eval("{$gt:{$calc:'#CUR.date+17000'}}"));
</pre>
</td>
<td>拡張JSON記述を解釈します。{$calc:'xxx'}は結合則付きの四則演算され展開されます。Dateインスタンスに対する加減算も実行されます</td>
</tr>
</table>

<p>
#CURがデフォルトでレコード全体を表しており、マッピング定義を省略しても#CURは設定されます。#CUR.フィールド名でフィールドのオブジェクトにアクセスできます。
</p>
<pre class=quote10>
import hi.db.hiMongo;
import java.io.*;
public class Test {
   static PrintStream ps=System.out;
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      db.in("coll_01")
        .find("{}","{_id:0}")
        .forOne("{#tp:type,#va:value}",Fi->{
            ps.println("--- get");
            ps.println("#CUR      "+Fi.get("#CUR").getClass().getName()+" "+Fi.get("#CUR"));
            ps.println("#tp       "+Fi.get("#tp").getClass().getName()+" "+Fi.get("#tp"));
            ps.println("#va       "+Fi.get("#va").getClass().getName()+" "+Fi.get("#va"));
            ps.println("#CUR.type "+Fi.get("#CUR.type").getClass().getName()+" "
                                   +Fi.get("#CUR.type"));
            ps.println("#CUR.date "+Fi.get("#CUR.date").getClass().getName()+" "
                                   +Fi.get("#CUR.date"));
            ps.println("--- disp");
            ps.println(Fi.disp("CUR=#CUR"));
            ps.println(Fi.disp("tp=#tp va=#va CUR.type=#CUR.type CUR.date=#CUR.date"));
            ps.println("--- eval");
            ps.println("{$gt:{$calc:'#CUR.date+17000}'}}->"
                      +Fi.mson(Fi.eval("{$gt:{$calc:'#CUR.date+17000'}}")));
            })
        ;
      }
   }
＊＊＊ 実行結果
---- 17forOneValue -----
-- get
CUR      org.bson.Document Document{{type=A, value=12.3, date=Mon Aug 17 16:07:00 JST 2020}}
tp       java.lang.String A
va       java.lang.Double 12.3
CUR.type java.lang.String A
CUR.date java.util.Date Mon Aug 17 16:07:00 JST 2020
-- disp
UR={'type':'A', 'value':12.3, 'date':ISODate('2020-08-17T07:07:00.000Z')}
p=A va=12.3 CUR.type=A CUR.date=ISODate('2020-08-17T07:07:00.000Z')
-- eval
$gt:{$calc:'#CUR.date+17000}'}}->{'$gt':ISODate('2020-08-17T07:07:17.000Z')}
</pre>
<p>
{@link hi.db.hiMongo.Accessor#eval(Object) elal()}の引数はJSON形式でなければなりません。{$calc:'数式'}は演算結果に置き換わります。数式は結合則付きの四則と括弧がサポートされます。
{@link java.util.Date Date}クラスへの数値の加算減算機能も持ちます。<br>
{@link hi.db.hiMongo.Accessor#eval(Object) eval()}は通常{@link hi.db.hiMongo.Finder#find(Object,Object) find()}などの引数に内部的に適用されており、通常利用者が使う場面は多く有りません。
</p>

<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<!-- - - - - - - - - - - - - - - - - - -->
<p class=B1_2 id="readOne_lambda">
&emsp;検索結果をDBスコープ変数(#変数)に取り込む
</p>
<p>
{@link hi.db.hiMongo.Finder#readOne(Object) readOne()}を用いればDBから読み込んだ値をDBスコープの変数に取り込むことができます。
</p>
<p>
指定は{#変数名:フィールド名}で{}内に複数並べることが出来ます。
</p>
<p>
{@link hi.db.hiMongo.Finder#limit(int) limit(1)}が補助的に発行されます。
</p>
<p>
次の例では最終レコードのdateを#last_dateとして取り込み、#last_dateより25秒前からのレコードを取得しています。<br>
{$calc:'#last_date-25000'}はDate演算を行った結果に置き換わります。
</p>
<pre class=quote10>
import hi.db.hiMongo;
public class Test {
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      db.in("coll_01")
        .find()
        .sort("{_id:-1}")
        .readOne("{#last_date:'date'}")
        .find("{date:{$gte:{$calc:'#last_date-25000'}}}","{_id:0}")
        .forEachMson(Rm->System.out.println(Rm))
        ;
      }
   }
＊＊＊　ＤＢ内容
   {type:'A',value:12.3,date:ISODate('2020-08-17T07:07:00.000Z')}
  ,{type:'A',value:4.56,date:ISODate('2020-08-17T07:07:10.000Z')}
  ,{type:'B',value:2001,date:ISODate('2020-08-17T07:07:20.000Z')}
  ,{type:'A',value:7.89,date:ISODate('2020-08-17T07:07:30.000Z')}
  ,{type:'A',value:0.12,date:ISODate('2020-08-17T07:07:40.000Z')}
＊＊＊　実行結果
----- 18readOneSimple -----
{'type':'B', 'value':2001, 'date':ISODate('2020-08-17T07:07:20.000Z')}
{'type':'A', 'value':7.89, 'date':ISODate('2020-08-17T07:07:30.000Z')}
{'type':'A', 'value':0.12, 'date':ISODate('2020-08-17T07:07:40.000Z')}
</pre>

<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<!-- - - - - - - - - - - - - - - - - - -->
<p class=B1_2 id="recursive_lambda">
&emsp;再帰探索と変数
</p>
<p>
{@link hi.db.hiMongo.Finder#forEachRecursive(String,hiU.FunctionEx) forEachRecursive()},{@link hi.db.hiMongo.Finder#forOneRecursive(String,hiU.FunctionEx) forOneRecursive()}で指定リンクをたどる再帰アクセスが出来ます。
</p>
<p>
リンクは
<pre class=prog10>
  {#CUR.リンク情報フィールド名:リンク先のidフィールド名}
</pre>
の形で指定します。{}内に複数並べることが出来ます。<br>
"#CUR."はリンク情報指定内のキーワードです。<br>
キーワードは他に"#FIELD"と"#FILTER"が有ります。
</p>
<p>
ラムダ式は３つです。それぞれ参照可能な変数があります。
</p>
<ol>
<li>取得したレコードに対する処理を行う。<br>
returnが必要<br>
nullを返せばその他のレコードに対して探索を続ける。<br>
null以外を返せば結果を#RESULT変数に入れて、探索打ち切り。<br>
変数は
<ul>
<li>#TOP:入り口となったレコード</li>
<li>#CUR:カレントレコード(入り口レコードも来ます）</li>
</ul>
が参照できます。
</li>
<li>探索打ち切り時の処理<br>
変数は
<ul>
<li>#TOP:入り口となったレコード</li>
<li>#RESULT:結果</li>
</ul>
が参照できます。
</li>

<li>探索完了の処理<br>
変数は
<ul>
<li>#TOP:入り口となったレコード
</ul>
が参照できます。
</li>
</ol>
<p>
２番目のラムダ式と３番目のラムダ式は省略可能です。
</p>


<p>
次の例では各レコードはnameで識別され、father、motherで参照されます。
<pre class=prog10>
   {'name':'P0009', 'status':'-', 'father':'P0001', 'mother':'P0002'}
</pre>

<p>
指定されたレコードからfather、motherを再帰的に遡りstatusがKINGであるレコードにたどり着くかを調べています。
</p>
</p>
<pre class=quote10>
import hi.db.hiMongo;
public class Test { 
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      db.in("famiryTree")
        .find("{name:{$in:['P0027','P0028','P0029','P0030','P0031']}}")
        .forEachRecursive(
            "<span class=green>{#CUR.father:name,#CUR.mother:name}</span>" // リンク
           ,Fr->{ // 主処理（再帰呼び出しされる)
               if( "KING".equals(Fr.get("#CUR.status")) ) {
                  return Fr.disp(" is a descendant of KING #CUR.name");
                  }
               return null;
               }
           ,Ff->{ // 完了 #RESULT に主処理の戻り値
               System.out.println(Ff.disp("#TOP.name")+Ff.disp("#RESULT")+".");
               }
           ,Fn->{ // 完了
               System.out.println(Fn.disp("#TOP.name is not."));
               }
            )
         ;
      }
   }
＊＊＊ 実行結果
----- 13recursive -----
P0027 is not.
P0028 is a descendant of KING P0003.
P0029 is not.
P0030 is a descendant of KING P0003.
P0031 is not.
</pre>
<p>
試験に使用したＤＢデータを示します。
</p>
<div id="divRecursive1">
<p><input type="button" value="データを表示する" style="WIDTH:10em"
   onClick="document.getElementById('divRecursive2').style.display='block';
            document.getElementById('divRecursive1').style.display='none'"></p>
</div>
<div id="divRecursive2" style="display:none">
<p><input type="button" value="データを隠す" style="WIDTH:10em"
   onClick="document.getElementById('divRecursive2').style.display='none';
            document.getElementById('divRecursive1').style.display='block'"></p>
<pre class=quote8>
   {'name':'P0001', 'status':'-', 'father':'-', 'mother':'-'}
  ,{'name':'P0002', 'status':'-', 'father':'-', 'mother':'-'}
  ,{'name':'P0003', 'status':'KING', 'father':'-', 'mother':'-'}
  ,{'name':'P0004', 'status':'QUEEN', 'father':'-', 'mother':'-'}
  ,{'name':'P0005', 'status':'-', 'father':'-', 'mother':'-'}
  ,{'name':'P0006', 'status':'-', 'father':'-', 'mother':'-'}
  ,{'name':'P0007', 'status':'-', 'father':'-', 'mother':'-'}
  ,{'name':'P0008', 'status':'-', 'father':'-', 'mother':'-'}
  ,{'name':'P0009', 'status':'-', 'father':'P0001', 'mother':'P0002'}
  ,{'name':'P0010', 'status':'-', 'father':'P0003', 'mother':'P0004'}
  ,{'name':'P0011', 'status':'-', 'father':'P0003', 'mother':'P0008'}
  ,{'name':'P0012', 'status':'-', 'father':'P0005', 'mother':'P0006'}
  ,{'name':'P0013', 'status':'-', 'father':'P0005', 'mother':'P0008'}
  ,{'name':'P0014', 'status':'-', 'father':'P0007', 'mother':'P0008'}
  ,{'name':'P0016', 'status':'-', 'father':'P0007', 'mother':'P0008'}
  ,{'name':'P0017', 'status':'-', 'father':'P0009', 'mother':'P0010'}
  ,{'name':'P0018', 'status':'-', 'father':'P0011', 'mother':'P0012'}
  ,{'name':'P0020', 'status':'-', 'father':'P0013', 'mother':'P0014'}
  ,{'name':'P0021', 'status':'-', 'father':'P0013', 'mother':'P0014'}
  ,{'name':'P0022', 'status':'-', 'father':'P0009', 'mother':'P0016'}
  ,{'name':'P0024', 'status':'-', 'father':'P0009', 'mother':'P0016'}
  ,{'name':'P0025', 'status':'-', 'father':'P0009', 'mother':'P0016'}
  ,{'name':'P0027', 'status':'-', 'father':'P0021', 'mother':'P0022'}
  ,{'name':'P0028', 'status':'-', 'father':'P0021', 'mother':'P0018'}
  ,{'name':'P0029', 'status':'-', 'father':'P0025', 'mother':'P0024'}
  ,{'name':'P0030', 'status':'-', 'father':'P0017', 'mother':'P0020'}
  ,{'name':'P0031', 'status':'-', 'father':'P0021', 'mother':'P0022'}
  ,{'name':'P0032', 'status':'-', 'father':'P0011', 'mother':'P0020'}
  ,{'name':'P0033', 'status':'-', 'father':'P0025', 'mother':'P0024'}
</pre>
<p><input type="button" value="データを隠す△" style="WIDTH:10em"
   onClick="document.getElementById('divRecursive2').style.display='none';
            document.getElementById('divRecursive1').style.display='block';
            document.location='#divRecursive1'"></p>
</div>
<p class=c>
&emsp;取得するフィールドの指定#FIELD
</p>
<p>
#FIELDを指定することにより、読み込むレコードのフィールド指定({@link hi.db.hiMongo.Collection#find(Object,Object) find()}の第二引数}ができます。
</p>
<pre class=quote10>
      db.in("famiryTree")
        .find("{name:{$in:['P0027','P0028','P0029','P0030','P0031']}}"<span class=green>,"{_id:0}"</span>)
        .forEachRecursive(
        .forEachRecursive(
            "{#CUR.father:name,#CUR.mother:name<span class=red>,#FIELD:{_id:0}</span>}"
           ,Fr->{...
</pre>
<p>
なお、入り口レコードのフィールド指定は再帰の外側での指定となります。
</p>

<p class=c>
&emsp;フィルターの指定#FILER
</p>
<p>
#FILTERを指定することにより、{@link hi.db.hiMongo.Collection#find(Object,Object) find()}のフィルターを直接指定できます。
</p>
<pre class=quote10>
      db.in("famiryTree")
        .find("{name:{$in:['P0027','P0028','P0029','P0030','P0031']}}")
        .forEachRecursive(
        .forEachRecursive(
            "<span class=red>{#FILTER:{name:{$in:[#CUR.father,#CUR.mother]}}</span>,#FIELD:{_id:0}}"
           ,Fr->{...
</pre>
<p>
この例で#FILTERの内容にある#CURはキーワードではなく変数の参照です。<br>
{@link hi.db.hiMongo.Collection#find(Object,Object) find()}発行時に変数が展開されます。<br>
その他の変数も同様に参照可能です。
</p>

<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<!-- ====================================================================== -->
<p class=B1_2 id="the_value">
&emsp;特別変数the_value
</p>
<p>
DBスコープの特別な変数が用意されています。<br>
これは#参照による展開はできません。<br>
{@link hi.db.hiMongo.Finder#set_the_value(Object) set_the_value(Object)}と{@link hi.db.hiMongo.Finder#get_the_value(Object) get_the_value()}でセット/ゲットできます。
</p>
<p>
次の例では{@link hi.db.hiMongo.Finder#forOne(hiU.ConsumerEx) forOne(...)}の中で{@link hi.db.hiMongo.Finder#getClassList(Class) getClassList()}により結果を得ていますが、forOneの戻り値は{@link hi.db.hiMongo.Finder Finder}なのでgetClassList()の戻り値を直接戻すことができません。
そのためforOne(...)の中でset_the_value()で特別変数the_valueにセットし、その後戻り値としてget_the_value()で値を取り出しています。<br>
なおget_the_valueの引数はget_the_valueの返すべき型を示すと同時にデフォルト値であり、'the_value'がnullまたはcast不能の場合引数値が戻ります。
</p>
<pre class=quote10>
import hi.db.hiMongo;
import java.util.*;
public class Test {
   static class Record {   // レコード内容
      String type;
      double value;
      Date   date;
      }
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      ArrayList<Record> _recs
      =db.in("coll_01")
         .find("{}","{_id:0}")
         .sort("{_id:-1}")
         .forOne("{#last_date:'date'}",Fi->{
             Fi.<span class=green>set_the_value</span>(
                Fi.find("{date:{$gte:{$calc:'#last_date-25000'}}}","{_id:0}")
                  .getClassList(Record.class));
             })
         .<span class=green>get_the_value(new ArrayList<Record>())</span>;
      for(Record _rec:_recs){
         System.out.println(_rec.type+"/"+_rec.value+"/"+_rec.date);
         }
      }
   }
＊＊＊　結果
----- 20the_value -----
B/2001.0/Mon Aug 17 16:07:20 JST 2020
A/7.89/Mon Aug 17 16:07:30 JST 2020
A/0.12/Mon Aug 17 16:07:40 JST 2020
</pre>

<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<!-- ====================================================================== -->
<p class=B1 id="insert_and_others">
&emsp;insert等のレコード処理
</p>
<p>
コレクション内のレコードの操作として追加（insert)、削除(delete)、更新(update)、置き換え(replace)、コレクションごと全削除(drop)が用意されています。

<div id="divInser_1">
<p><input type="button" value="フルコードを表示する" style="WIDTH:12em"
   onClick="document.getElementById('divInser_2').style.display='block';
            document.getElementById('divInser_1').style.display='none'"></p>
</div>
<div id="divInser_2" style="display:none">
<p><input type="button" value="フルコードを隠す" style="WIDTH:12em"
   onClick="document.getElementById('divInser_2').style.display='none';
            document.getElementById('divInser_1').style.display='block'"></p>
<pre class=quote8>
import hi.db.hiMongo;
import otsu.hiNote.*;
import java.util.*;
public class Test {
   public static void main(String[] args_){
      hiMongo.DB db =hiMongo.use("db01");
      hiMongo.Collection  _coll=db.in("coll_01").drop();
      System.out.println("--- insertOne/insertMany");
      _coll.insertOne(
           " {type:'A',value:12.3,date:"+hiMongo.date()+"}");
      _coll.insertMany("["+
           " {type:'A',value:4.56,date:"+hiMongo.date()+"}"+
           ",{type:'B',value:2001,date:"+hiMongo.date()+"}"+
           ",{type:'A',value:7.89,date:"+hiMongo.date()+"}"+
           ",{type:'A',value:0.12,date:"+hiMongo.date()+"}]");
      _coll.find("{}","{_id:0}").forEach(Rd->System.out.println(Rd));

      System.out.println("--- updateOne");
      _coll.updateOne("{$and:[{type:'B'},{value:{$gt:5}}]}",
                      "{$set:{value:4.32}}");
      _coll.find("{}","{_id:0,date:0}").forEach(Rd->System.out.println(Rd));

      System.out.println("--- updateMany");
      _coll.updateMany("{$and:[{type:'A'},{value:{$lt:5}}]}",
                      "{$set:{value:3.21}}");
      _coll.find("{}","{_id:0,date:0}").forEach(Rd->System.out.println(Rd));

      System.out.println("--- replaceOne");
      _coll.replaceOne("{$and:[{type:'A'},{value:{$lt:5}}]}",
                      "{type:'B',value:6543,date:"+hiMongo.date()+"}");
      _coll.find("{}","{_id:0,date:0}").forEach(Rd->System.out.println(Rd));

      System.out.println("--- deleteOne");
      _coll.deleteOne("{type:'B'}");
      _coll.find("{}","{_id:0,date:0}").forEach(Rd->System.out.println(Rd));

      System.out.println("--- deleteMany");
      _coll.deleteMany("{$and:[{type:'A'},{value:{$lt:8}}]}");
      _coll.find("{}","{_id:0,date:0}").forEach(Rd->System.out.println(Rd));
      }
   }
＊＊＊＊＊＊　ビルド実行結果
$ ./run.sh
----- 06insert -----
--- insertOne/insertMany
Document{{type=A, value=12.3, date=Tue Aug 25 03:08:53 JST 2020}}
Document{{type=A, value=4.56, date=Tue Aug 25 03:08:53 JST 2020}}
Document{{type=B, value=2001, date=Tue Aug 25 03:08:53 JST 2020}}
Document{{type=A, value=7.89, date=Tue Aug 25 03:08:53 JST 2020}}
Document{{type=A, value=0.12, date=Tue Aug 25 03:08:53 JST 2020}}
--- updateOne
Document{{type=A, value=12.3}}
Document{{type=A, value=4.56}}
Document{{type=B, value=4.32}}
Document{{type=A, value=7.89}}
Document{{type=A, value=0.12}}
--- updateMany
Document{{type=A, value=12.3}}
Document{{type=A, value=3.21}}
Document{{type=B, value=4.32}}
Document{{type=A, value=7.89}}
Document{{type=A, value=3.21}}
--- replaceOne
Document{{type=A, value=12.3}}
Document{{type=B, value=6543}}
Document{{type=B, value=4.32}}
Document{{type=A, value=7.89}}
Document{{type=A, value=3.21}}
--- deleteOne
Document{{type=A, value=12.3}}
Document{{type=B, value=4.32}}
Document{{type=A, value=7.89}}
Document{{type=A, value=3.21}}
--- deleteMany
Document{{type=A, value=12.3}}
Document{{type=B, value=4.32}}
OK
</pre>
<p><input type="button" value="フルコードを隠す△" style="WIDTH:12em"
   onClick="document.getElementById('divInser_2').style.display='none';
            document.getElementById('divInser_1').style.display='block';
            document.location='#divInser_1'"></p>
</div>

<p>
レコードインサートには利用者クラスを使う事もできます。
</p>

</p>
<p class=B1_2 id="col_insert">
&emsp;insert
</p>
<p>
{@link hi.db.hiMongo.Collection#insertOne(Object...) insertOne()}の引数は１レコードの拡張JSON形式で、{@link hi.db.hiMongo.Collection#insertMany(Object...) insertMany()}の引数は拡張JSONのList型です。<br>
それらを記述したテキストファイル、あるいはそれらの解析されたノードツリーも許されます。<br>
条件文は付きません。
</p>
<p>
次の様な使い方になります。{@link hi.db.hiMongo.DB#in(String) in("コレクション")}はその時点で存在しないコレクションに対して行っても構いません。なお{@link hi.db.hiMongo#date() hiMongo.date()}は現在時の標準JSON表現を得る関数です。
</p>
<pre class=quote10>
hiMongo.DB db =hiMongo.use("db01");
db.in("coll_01")
  .insertOne(
     " {type:'A',value:12.3,date:"+hiMongo.date()+"}");
</pre>
<p class=B1_2 id="col_drop">
&emsp;drop
</p>
<p>
{@link hi.db.hiMongo.Collection#drop() drop()}はレコードの削除のみならずコレクションそのものの削除ですが、{@link hi.db.hiMongo.Collection#insertOne(Object...) insertOne()},{@link hi.db.hiMongo.Collection#insertMany(Object...) insertMany()}を繋げると、元の名前のコレクションが再び作成され空の状態でのレコード追加となります。
</p>
<pre class=quote10>
hiMongo.DB db =hiMongo.use("db01");
db.in("coll_01")
  .drop()
  .insertMany("["+
     " {type:'A',value:4.56,date:"+hiMongo.date()+"}"+
     ",{type:'B',value:2001,date:"+hiMongo.date()+"}"+
     ",{type:'A',value:7.89,date:"+hiMongo.date()+"}"+
     ",{type:'A',value:0.12,date:"+hiMongo.date()+"}]");
</pre>

<p class=B1_2 id="col_delete">
&emsp;delete
</p>
<p>
{@link hi.db.hiMongo.Collection#deleteOne(Object) deleteOne()}では登録の古いレコードが検索され、合致した一個のみが削除されます。<br>
{@link hi.db.hiMongo.Collection#deleteMany(Object) deleteMany()}では合致するレコードが全て削除されます。
</p>
<pre class=quote10>
hiMongo.DB         db   =hiMongo.use("db01");
hiMongo.Collection _coll=db.in("coll_01");
// --- deleteOne
_coll.deleteOne("{type:'B'}");

// --- deleteMany
_coll.deleteMany("{$and:[{type:'A'},{value:{$lt:8}}]}");
</pre>

<p class=B1_2 id="col_update">
&emsp;update
</p>
<p>
{@link hi.db.hiMongo.Collection#updateOne(Object,Object) updateOne()},{@link hi.db.hiMongo.Collection#updateMany(Object,Object) updateMany()}は{$set:{フィールド名:値}}でフィールドの値の変更を指定します。<br>
複数フィールドの置き換えはフィールド名の値のセットをカンマで繋ぎます{$set:{フィールド1:値1,フィールド2:値2,...}<br>
存在しないフィールド名を指定するとフィールドの追加となります。<br>
単純な値の置き換えを行う$setの他に値の増減を行う$incなども用意されています。
(<a class=A1 href=
"https://docs.mongodb.com/manual/reference/operator/update/"
>更新の演算子</a>参照)
</p>
<p>
$setによる単純置き換え例(この例では3に置き換えている）
</p>
<pre class=quote10>
---- before $set
{'type':'C', 'name':'X', 'value':5}
{'type':'C', 'name':'Y', 'value':10}
{'type':'C', 'name':'X', 'value':13}
---- program
  .updateMany("{$and:[{type:'C'},{name:'X'}]}",
              "{$set:{value:3}}")
---- after $set
{'type':'C', 'name':'X', 'value':3}
{'type':'C', 'name':'Y', 'value':10}
{'type':'C', 'name':'X', 'value':3}
</pre>
<p>
$incによる増減例(この例では+3している)
</p>
<pre class=quote10>
---- before $inc
{'type':'C', 'name':'X', 'value':5}
{'type':'C', 'name':'Y', 'value':10}
{'type':'C', 'name':'X', 'value':13}
---- program
  .updateMany("{$and:[{type:'C'},{name:'X'}]}",
              "{$inc:{value:3}}")
---- after $inc
{'type':'C', 'name':'X', 'value':8}
{'type':'C', 'name':'Y', 'value':10}
{'type':'C', 'name':'X', 'value':16}
</pre>
<p>
条件に合致するレコードが無い場合は無処理です。
</p>
<p>
指定フィールドが存在ない場合、フィールドが追加されてしまうことに注意が必要です。<br>
指定フィールドが存在する場合のみupdateするには$existを用います。
</p>
<pre class=quote10>
   final String <span class=green>_field_name</span>="value";
   hiMongo.DB db=hiMongo.use("db01");
   db.in("coll_01")
     .updateMany("{$and:[{type:'C'},{name:'X'},{"+<span class=green>_field_name</span>+":{<span class=purple>$exists:true</span> }}]}",
                 "{$set:{"+<span class=green>_field_name</span>+":3}}");
</pre>
<p class=B1_2 id="col_replace">
&emsp;replace
</p>
<p>
{@link hi.db.hiMongo.Collection#replaceOne(Object,Object) replaceOne()}はレコード全体の置き換えになります。<br>
条件とレコード全体が引数となります。replaceMany()は有りません。
</p>
<pre class=quote10>
// --- replaceOne
_coll.replaceOne("{$and:[{type:'A'},{value:{$lt:5}}]}",
                 "{type:'B',value:6543,date:"+hiMongo.date()+"}");

</pre>



<p class=B1_2 id="class_insert">
&emsp;利用者定義クラス・インスタンスをinsertする
</p>
<p>
レコード情報を持つ利用者のクラス・インスタンスを{@link hi.db.hiMongo.Collection#insertOne(Object...) insertOne()}を用いてinsertすることが出来ます。
</p>
<pre class=quote10>
<span class=red>class MyRecord</span> {  // レコード内容
   String   type;
   double   value;
   Date     date;
   }
-----
hiMongo.DB db =  hiMongo.use("db01");
<span class=red>MyReord    _rec= new MyRecord();</span>
_rec.type = "D";
_rec.value= 12.3;
_rec.date = new Date();
db.in("coll_01")
  .insertOne(<span class=red>_rec</span>);
</pre>
<p>
Listにすれば{@link hi.db.hiMongo.Collection#insertMany(Object...) insertMeny()}も可能です。
</p>

<div id="divClassInsrt_1">
<p><input type="button" value="フルコードを表示する" style="WIDTH:12em"
   onClick="document.getElementById('divClassInsrt_2').style.display='block';
            document.getElementById('divClassInsrt_1').style.display='none'"></p>
</div>
<div id="divClassInsrt_2" style="display:none">
<p><input type="button" value="フルコードを表示する" style="WIDTH:12em"
   onClick="document.getElementById('divClassInsrt_2').style.display='none';
            document.getElementById('divClassInsrt_1').style.display='block'"></p>
<pre class=quote8>
import hi.db.hiMongo;
import otsu.hiNote.*;
import java.util.*;
public class Test {
   static class MyRecord {   // レコード内容
      String type;
      double value;
      Date   date;
      }
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      hiMongo.Collection coll
      =db.in("coll_01").drop();
      //
      ArrayList<MyRecord> _recs= new ArrayList<>();
      for(int _n=0;_n<4;++_n){
         MyRecord _rec= new MyRecord();
         _rec.type = "C";
         _rec.value= _n*10;
         _rec.date = new Date();
         _recs.add(_rec);
         }
      coll.insertMany(_recs);
      //
      MyRecord _rec = new MyRecord();
      _rec.type = "D";
      _rec.value= 12.3;
      _rec.date = new Date();
      coll.insertOne(_rec);
      //
      coll.find()
          .forEachMson(Rm->System.out.println(Rm));
      }
   }
＠＠＠＠　実行結果
$ ./run.sh
----- 10insertClass -----
{'_id':ObjectId('5f6b2000cbaae93df916f501'), 'type':'C', 'value':0, 'date':ISODate('2020-09-23T10:14:24.535Z')}
{'_id':ObjectId('5f6b2000cbaae93df916f502'), 'type':'C', 'value':10, 'date':ISODate('2020-09-23T10:14:24.535Z')}
{'_id':ObjectId('5f6b2000cbaae93df916f503'), 'type':'C', 'value':20, 'date':ISODate('2020-09-23T10:14:24.535Z')}
{'_id':ObjectId('5f6b2000cbaae93df916f504'), 'type':'C', 'value':30, 'date':ISODate('2020-09-23T10:14:24.535Z')}
{'_id':ObjectId('5f6b2000cbaae93df916f505'), 'type':'D', 'value':12.3, 'date':ISODate('2020-09-23T10:14:24.641Z')}
OK
</pre>
<p><input type="button" value="フルコードを表示する△" style="WIDTH:12em"
   onClick="document.getElementById('divClassInsrt_2').style.display='none';
            document.getElementById('divClassInsrt_1').style.display='block';
            document.location='#divClassInsrt_1'"></p>
</div>
<p>
利用者クラスはinsert以外でも拡張JSON文字列の代わりに与えることができます。
</p>


<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<p class=B1 id="cap_index">
&emsp;cap指定（最大容量)、index設定
</p>

<p class=B1_2 id="cap">
&emsp;キャップ（最大容量)指定
</p>
<p>
{@link hi.db.hiMongo.DB}の{@link hi.db.hiMongo.DB#createCappedCollection(String,String) createCappedCollection()}メソッドで、容量制限付きのコレクションを作成できます。<br>
既にあるコレクションの容量を変更することは出来ません。<br>
insertOne(),insertMany()を発行した場合、制限容量に達すると、古いレコードが削除されます。
</p>
<p>
createCappedCollection()にはコレクション名と、容量情報を指定します。<br>
容量情報はJSON形式の文字列で次の形となります。
</p>
<pre class=prog10>
{
   size   :<i>バイト数</i>,
   records:<i>レコード数</i>,
   force  :<i>trueまたはfalse</i>
   }
EX {size:1000000000,records:5000,force:true}
</pre>
<p>
forceがtrueだと必ず要素無しでcap指定のされたのコレクションを作成します。<br>
forceがfalseの場合すでにコレクションがあれば無処理となります。
</p>
<div id="divCap_1">
<p><input type="button" value="例を表示する" style="WIDTH:12em"
   onClick="document.getElementById('divCap_2').style.display='block';
            document.getElementById('divCap_1').style.display='none'"></p>
</div>
<div id="divCap_2" style="display:none">
<p><input type="button" value="例を隠す" style="WIDTH:10em"
   onClick="document.getElementById('divCap_2').style.display='none';
            document.getElementById('divCap_1').style.display='block'"></p>
<pre class=quote8>
import hi.db.hiMongo;
public class Test {
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      hiMongo.Collection col
      =db.createCappedCollection(
           "coll_cap",      // コレクション名
           "{size:10000,"+  // 最大容量（バイト)
           " records:5," +  // 最大レコード数
           " force:true}"   // 強制クリア
           );
      for(int _n=0;_n<20;++_n){
         col.insertOne("{type:'A',value:"+(_n+1)+"}");
         }
      col.find("{}","{_id:0}")
         .forEachMson(Rm->System.out.println(Rm));
      }
   }
＊＊＊＊＊＊　ビルド実行結果
$ ./run.sh
----- 08cap -----
{'type':'A', 'value':16}
{'type':'A', 'value':17}
{'type':'A', 'value':18}
{'type':'A', 'value':19}
{'type':'A', 'value':20}
OK
</pre>
<p><input type="button" value="例を隠す△" style="WIDTH:10em"
   onClick="document.getElementById('divCap_2').style.display='none';
            document.getElementById('divCap_1').style.display='block';
            document.location='#divCap_1'"></p>
</div>

<p class=B1_2 id="index">
&emsp;インデックス指定,ユニーク性保証,生存時間制限
</p>
<p>
{@link hi.db.hiMongo.Collection}の{@link hi.db.hiMongo.Collection#createIndex(Object,Object) createIndex()}を用いてインデックスを作成することにより検索の速度を上げることができます。
</p>
<p>
インデックスには値のユニーク性の保証(unique)、レコード生存時間の限定(expireAfter)などのオプションを付加することが出来ます。
</p>
<p>
次の例ではindexを作成する前と、'商品id'フィールドでindexを作成した後のインデックスのリストを出しています。
</p>
<pre class=quote10>
hiMongo.DB db=hiMongo.use("sampleDB");
<span class=gray>System.out.println("--- befor creteIndex");</span>
db.in("商品").getIndexList().forEach(Do->System.out.println(Do));
db.in("商品").createIndex("{商品id:1}","{unique:true}");
db.in("商品").createIndex("{商品id:1}","{unique:true,expireAfterDays:730}");
<span class=gray>System.out.println("--- after creteIndex");</span>
db.in("商品").getIndexList().forEach(Do->System.out.println(Do));
＠＠＠出力
--- befor creteIndex
Document{{v=2, key=Document{{_id=1}}, name=_id_, ns=sampleDB.商品}}
--- after creteIndex
Document{{v=2, key=Document{{_id=1}}, name=_id_, ns=sampleDB.商品}}
Document{{v=2, unique=true, key=Document{{商品id=1}}, name=商品id_1
          , ns=sampleDB.商品, expireAfterSeconds=63072000}}
</pre>
<p>
フルコードは<a class=A1 href="#lookup">「aggregate(集計）lookupによるフィールド結合」</a>にあります。
</p>

<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<p class=B1 id="AG">
&emsp;aggregate(集計）
</p>
<p>
{@link hi.db.hiMongo.Collection#aggregate(Object) aggregate()}を用いれば集計を得ることができます。<br>
結果の取得は{@link hi.db.hiMongo.Collection#find(Object,Object) find()}と同様で次のものが用意されています。
</p>
<table class=t0>
<tr>
<td>Documentで受け取る</td>
<td>{@link hi.db.hiMongo.Accessor#getDocumentList() getDocumentList()} リストで受ける<br>
    {@link hi.db.hiMongo.Aggregator#forEachDocument(hiU.ConsumerEx) forEachDocument(Documentを引数とするラムダ式)}</td>
</tr>
<tr>
<td>利用者クラスインタンスで受け取る</td>
<td>{@link hi.db.hiMongo.Accessor#getClassList(Class) getClassList(Class)} リストで受ける<br>
    {@link hi.db.hiMongo.Aggregator#forEachClass(Class,hiU.ConsumerEx) forEachClass(Class,利用者クラスインスタンスを引数とするラムダ式)}</td>
</tr>
<tr>
<td>ノード探査機で受け取る</td>
<td>{@link hi.db.hiMongo.Accessor#getProbeList() getProbeList()} リストで受ける<br>
    {@link hi.db.hiMongo.Aggregator#forEachProbe(hiU.ConsumerEx) forEachProbe(Probeを引数とするラムダ式)}</td>
</tr>
<tr>
<td>JSON文字列で受け取る</td>
<td>{@link hi.db.hiMongo.Accessor#getJsonList() getJsonList()} リストで受ける<br>
    {@link hi.db.hiMongo.Aggregator#forEachJson(hiU.ConsumerEx) forEachJson(Json文字列を引数とするラムダ式)}</td>
</tr>
<tr>
<td>拡張JSON文字列MSONで受け取る</td>
<td>{@link hi.db.hiMongo.Accessor#getMsonList() getMsonList()} リストで受ける<br>
    {@link hi.db.hiMongo.Aggregator#forEachMson(hiU.ConsumerEx) forEachJson(Mson文字列を引数とするラムダ式)}</td>
</tr>
</table>


<p class=B1_2 id="aggre">
&emsp;単純集計($match,$group)
</p>
<p>
$mactchと$groupで単純集計を得ることが出来ます。
</p>
<pre class=quote10>
<span class=red>class Arec</span> {
   String _id;
   double min;
   double max;
   double avg;
   }
----
double _start_date=取得開始レコードのunixエポック
<span class=red>Arec</span> _r
=db.in("coll_01")
   .aggregate("["+
      "{ $match:{$and:["+
         "{type:'A'},"+
         "{date:{$gte:{$date:"+_start_date+"}}}"+
         "]}},"+
      "{ $group:{"+
         "_id:'$type',"+
         "min:{$min:'$value'},"+
         "max:{$max:'$value'},"+
          "avg:{$avg:'$value'}}}"+
      "]")
   .getClassList(<span class=red>Arec.class</span>).get(0);
System.out.printf("min=%.2f max=%.2f avg=%.2f"
                  ,_r.min,_r.max,_r.avg));
</pre>
<p>
これは次のmongo-shell記述に相当します。
</p>
<pre class=quote10>
var _isodate=new Date(取得開始レコードのunixエポック)
var _r=
db.coll_01.
   aggregate([
    { $match:{$and:[
            {type:'A'},
            {date:{$gte:_isodate}}
            ]}},
    { $group:{_id:"$type",
          min:{$min:"$value"},
          max:{$max:"$value"},
          avg:{$avg:"$value"}}}
     ]).
   toArray()[0];
print("min="+_r.min+" max="+_r.max+" avg="+_r.avg.toFixed(2));
</pre>

<div id="divAggre_1">
<p><input type="button" value="フルコードを表示する" style="WIDTH:12em"
   onClick="document.getElementById('divAggre_2').style.display='block';
            document.getElementById('divAggre_1').style.display='none'"></p>
</div>
<div id="divAggre_2" style="display:none">
<p><input type="button" value="フルコードを隠す" style="WIDTH:12em"
   onClick="document.getElementById('divAggre_2').style.display='none';
            document.getElementById('divAggre_1').style.display='block'"></p>
<pre class=quote8>
// このプログラムでは先ず最後のレコードのdate情報を取得し、それから
// 30秒前からのレコードの統計情報をaggregateで取得しています。
import hi.db.hiMongo;
import otsu.hiNote.*;
import java.util.*;
import java.io.*;
public class Test {
   static class Arec {
      String _id;
      double min;
      double max;
      double avg;
      }
   static class WithDate {
      Date date;
      }
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      long _start_date
      =db.in("coll_01")
         .find("{type:'A'}","{_id:0,date:1}")
         .sort("{_id:-1}").limit(1).getClassList(WithDate.class).get(0)
         .date.getTime()-30000;
      Arec _r
      = db.in("coll_01")
          .aggregate("["+
              "{ $match:{$and:["+
                  "{type:'A'},"+
                  "{date:{$gte:{$date:"+_start_date+"}}}"+
                  "]}},"+
              "{ $group:{"+
                   "_id:'$type',"+
                   "min:{$min:'$value'},"+
                   "max:{$max:'$value'},"+
                   "avg:{$avg:'$value'}}}"+
              "]")
          .getClassList(Arec.class).get(0);
      System.out.println("start="+_start_date);
      System.out.println(String.format("min=%.2f max=%.2f avg=%.2f"
                                       ,_r.min,_r.max,_r.avg));
      }
   }
＊＊＊＊＊＊　dbデータ
> use db01
switched to db db01
> db.coll_01.find({},{_id:0})
{ "type" : "A", "value" : 12.3, "date" : ISODate("2020-08-17T07:07:00Z") }
{ "type" : "A", "value" : 4.56, "date" : ISODate("2020-08-17T07:07:10Z") }
{ "type" : "B", "value" : 2001, "date" : ISODate("2020-08-17T07:07:20Z") }
{ "type" : "A", "value" : 7.89, "date" : ISODate("2020-08-17T07:07:30Z") }
{ "type" : "A", "value" : 0.12, "date" : ISODate("2020-08-17T07:07:40Z") }
＊＊＊＊＊＊　ビルド実行結果
$ ./run.sh
----- 04aggregate -----
start=1597648030000
min=0.12 max=7.89 avg=4.19
OK
</pre>
<p><input type="button" value="フルコードを隠す△" style="WIDTH:12em"
   onClick="document.getElementById('divAggre_2').style.display='none';
            document.getElementById('divAggre_1').style.display='block';
            document.location='#divAggre_1'"></p>
</div>

<p class=c>
&emsp;#変数の参照
</p>
<p>
aggregateでも#変数参照ができます。
</p>
<p>
次の例ではreadOneで取り込んだ値を使ってaggregateを実行しています。
</p>
<div id="divAggreVal1">
<p><input type="button" value="例を表示する" style="WIDTH:10em"
   onClick="document.getElementById('divAggreVal2').style.display='block';
            document.getElementById('divAggreVal1').style.display='none'"></p>
</div>
<div id="divAggreVal2" style="display:none">
<p><input type="button" value="例を隠す" style="WIDTH:10em"
   onClick="document.getElementById('divAggreVal2').style.display='none';
            document.getElementById('divAggreVal1').style.display='block'"></p>
<p>
<pre class=quote10>
import hi.db.hiMongo;
public class Test {
   static class Arec {
      String _id;
      double min;
      double max;
      double avg;
      }
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      Arec _r
      = db.in("coll_01").find("{type:'A'}").sort("{_id:-1}")// last
          .readOne("{#last_date:'date'}")                   // one
          .aggregate("["+
              "{ $match:{$and:["+
                  "{type:'A'},"+
                  "{date:{$gte:{$calc:'#last_date-30000'}}}"+
                  "]}},"+
              "{ $group:{"+
                   "_id:'$type',"+
                   "min:{$min:'$value'},"+
                   "max:{$max:'$value'},"+
                   "avg:{$avg:'$value'}}}"+
              "]")
          .getClassList(Arec.class).get(0);
      System.out.println(String.format("min=%.2f max=%.2f avg=%.2f"
                                       ,_r.min,_r.max,_r.avg));
      }
   }
</pre>
</p>
<p><input type="button" value="例を隠す△" style="WIDTH:10em"
   onClick="document.getElementById('divAggreVal2').style.display='none';
            document.getElementById('divAggreVal1').style.display='block';
            document.location='#divAggreVal1'"></p>
</div>

<p class=B1_2 id="lookup">
&emsp;$lookupによるフィールド結合
</p>
<p>
$lookupを用いれば異なるコレクションのレコード・フィールドを結合することができます。<br>
結合されたデータは利用者定義の型のリストで受け取ることができます。
</p>
<pre class=quote10>
class A_Rec {
   String       店舗名;
   int          数量;
   from商品_Rec from商品;
   static class from商品_Rec{
      String 商品名;
      int    販売単価;
      }
   }
---
ArrayList<A_Rec> _recs
=db.in("店舗商品")
   .aggregate("["+
      "{$match:{$or:["+
           "{'店舗名':'東京'},"+
           "{'店舗名':'福岡'}"+
           "]}},"+
      "{$lookup:{"+
          "from:'商品',"+
          "localField:'商品id',"+
          "foreignField:'商品id',"+
          "as:'from商品'"+
          "}},"+
      "{$project:{"+
          "'_id':0,"+
          "'店舗名':1,"+
          "'from商品.商品名':1,"+
          "'from商品.販売単価':1,"+
          "'数量':1}},"+
       "{$unwind:'$from商品'}"+
      "]")
    .getClassList(A_Rec.class);
<span class=gray>for(A_rec _rec:_recs){
   System.out.println(_rec.店舗名+" "+_rec.from商品.商品名
                      +" 数量:"+_rec.数量);
   }</span>
</pre>
<p>
mongo-shellの次の記述に相当します。
</p>
<pre class=quote10>
use sampleDB
db.店舗商品.
    aggregate([
       {$match:{$or:[
          {'店舗名':'東京'},
          {'店舗名':'福岡'}
          ]}},
      {$lookup:{
         from:'商品',
         localField:'商品id',
         foreignField:'商品id',
         as:'from商品'
         }},
      {$project:{
         '_id':0,
         '店舗名':1,
         'from商品.商品名':1,
         'from商品.販売単価':1,
         '数量':1}},
     {$unwind:'$from商品'}
    ]);
</pre>

<div id="div_1">
<p><input type="button" value="フルコードを表示する" style="WIDTH:12em"
   onClick="document.getElementById('div_2').style.display='block';
            document.getElementById('div_1').style.display='none'"></p>
</div>
<div id="div_2" style="display:none">
<p><input type="button" value="フルコードを隠す" style="WIDTH:12em"
   onClick="document.getElementById('div_2').style.display='none';
            document.getElementById('div_1').style.display='block'"></p>
<pre class=quote8>
// （このサンプルはcreateIndexのサンプルを兼ねています）
// 東京あるいは福岡の店舗にある商品名と個数を得ています。
// 商品名は店舗商品コレクションの"商品id"で商品コレクションを連結し
// 商品名を得ています。
import hi.db.hiMongo;
import otsu.hiNote.*;
import java.util.*;
public class Test {
   static class A_Rec {
      String       店舗名;
      int          数量;
      from商品_Rec from商品;
      static class from商品_Rec{
         String 商品名;
         int    販売単価;
         }
      }
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("sampleDB");
      db.in("商品").getIndexList().forEach(Do->System.out.println(Do));
      db.in("商品").createIndex("{商品id:1}","{unique:true,expireAfterDays:730}");
      db.in("商品").getIndexList().forEach(Do->System.out.println(Do));
      ArrayList<A_Rec> _recs=
      db.in("店舗商品").aggregate("["+
            "{$match:{$or:["+
                 "{'店舗名':'東京'},"+
                 "{'店舗名':'福岡'}"+
                 "]}},"+
            "{$lookup:{"+
                "from:'商品',"+
                "localField:'商品id',"+
                "foreignField:'商品id',"+
                "as:'from商品'"+
                "}},"+
            "{$project:{"+
                "'_id':0,"+
                "'店舗名':1,"+
                "'from商品.商品名':1,"+
                "'from商品.販売単価':1,"+
                "'数量':1}},"+
             "{$unwind:'$from商品'}"+
            "]")
         .getClassList(A_Rec.class);
      for(A_Rec _rec:_recs){
         System.out.println(_rec.店舗名+" "+_rec.from商品.商品名+" 数量:"+_rec.数量);
         }
      }
   }
＊＊＊＊＊＊　dbデータ
> use sampleDB
switched to db sampleDB
> db.商品.find({},{_id:0,登録日:0})
{ "商品id" : "0001", "商品名" : "Tシャツ", "商品分類" : "衣服", "販売単価" : 1000, "仕入単価" : 500 }
{ "商品id" : "0002", "商品名" : "穴あけパンチ", "商品分類" : "事務用品", "販売単価" : 500, "仕入単価" : 320 }
{ "商品id" : "0003", "商品名" : "カッターシャツ", "商品分類" : "衣服", "販売単価" : 4000, "仕入単価" : 2800 }
{ "商品id" : "0004", "商品名" : "包丁", "商品分類" : "キッチン用品", "販売単価" : 3000, "仕入単価" : 2000 }
{ "商品id" : "0005", "商品名" : "フォーク", "商品分類" : "キッチン用品", "販売単価" : 500, "仕入単価" : 300 }
{ "商品id" : "0006", "商品名" : "ステイプラー", "商品分類" : "事務用品", "販売単価" : 880, "仕入単価" : 500 }
{ "商品id" : "0007", "商品名" : "ボールペン", "商品分類" : "事務用品", "販売単価" : 100, "仕入単価" : 20 }
> db.店舗商品.find({},{_id:0,更新日:0})
{ "店舗id" : "000A", "店舗名" : "東京", "商品id" : "0001", "数量" : 30 }
{ "店舗id" : "000A", "店舗名" : "東京", "商品id" : "0002", "数量" : 50 }
{ "店舗id" : "000A", "店舗名" : "東京", "商品id" : "0003", "数量" : 15 }
{ "店舗id" : "000B", "店舗名" : "名古屋", "商品id" : "0002", "数量" : 30 }
{ "店舗id" : "000B", "店舗名" : "名古屋", "商品id" : "0003", "数量" : 120 }
{ "店舗id" : "000B", "店舗名" : "名古屋", "商品id" : "0004", "数量" : 20 }
{ "店舗id" : "000B", "店舗名" : "名古屋", "商品id" : "0006", "数量" : 10 }
{ "店舗id" : "000B", "店舗名" : "名古屋", "商品id" : "0007", "数量" : 40 }
{ "店舗id" : "000C", "店舗名" : "大阪", "商品id" : "0003", "数量" : 20 }
{ "店舗id" : "000C", "店舗名" : "大阪", "商品id" : "0004", "数量" : 50 }
{ "店舗id" : "000C", "店舗名" : "大阪", "商品id" : "0006", "数量" : 90 }
{ "店舗id" : "000C", "店舗名" : "大阪", "商品id" : "0007", "数量" : 70 }
{ "店舗id" : "000D", "店舗名" : "福岡", "商品id" : "0001", "数量" : 100 }
＊＊＊＊＊＊　ビルド実行結果
$ ./run.sh
----- 05lookup -----
Document{{v=2, key=Document{{_id=1}}, name=_id_, ns=sampleDB.商品}}
Document{{v=2, key=Document{{_id=1}}, name=_id_, ns=sampleDB.商品}}
Document{{v=2, unique=true, key=Document{{商品id=1}}, name=商品id_1, ns=sampleDB.商品, expireAfterSeconds=63072000}}
東京 Tシャツ 数量:30
東京 穴あけパンチ 数量:50
東京 カッターシャツ 数量:15
福岡 Tシャツ 数量:100
OK
</pre>
<p><input type="button" value="フルコードを隠す△" style="WIDTH:12em"
   onClick="document.getElementById('div_2').style.display='none';
            document.getElementById('div_1').style.display='block';
            document.location='#div_1'"></p>
</div>

<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<!-- ====================================================================== -->
<p class=B1 id="mson">
&emsp;mongoDB拡張JSON(mson)記述
</p>
<p>
hiMongoはmongoDBの拡張JSON記述のパーザ、表記生成器の機能も持ちます。
</p>
<p>
パーズ結果は汎用オブジェクト（nodeツリー）または利用者クラスになります。
</p>
<p>
汎用オブジェクトレベルではmongoDBの{@link org.bson.Document}と内部形式のほぼ互換性を保っています。
</p>

<p class=B1_2 id="quote">
&emsp;引用符
</p>
<p>
英文字あるいは$で始まり英数字が続くフィールド名は引用符の省略が可能です。
</p>
<p>
引用符はダブルクオート、シングルクオート何れも可能です。
</p>
<pre class=quote10>
db.in("XX").find("{\"name\":\"A\"}")// 標準
db.in("XX").find("{name:\"A\"}")// 通常フィールド名引用符省略可
db.in("XX").find("{name:'A'}")// シングルクオート可
</pre>
<p>
出力はmsonではシングルクオート、jsonはダブルクオートとしています。
</p>

<p class=B1_2 id="date">
&emsp;ObjectId,Dateと数値
</p>
<p>
mongo拡張JSON(mson:bsonパーザ)で気を付ける必要があるのが、{@link org.bson.types.ObjectId ObjectId}と{@link java.util.Date Date}および数値です。
</p>
<p>
これらは次の表現形を持ちます。
</p>

<p>
<table class=t>
<tr>
<td>型</td>
<td>bson(内部値)</td>
<td>mson表現</td>
<td>json表現</td>
<td>stringify表現</td>
</tr>

<tr>
<td>{@link org.bson.types.ObjectId}</td>
<td>{@link org.bson.types.ObjectId}</td>
<td>ObjectId("...")</td>
<td>{$oid:"..."}</td>
<td>{$oid:"..."}</td>
</tr>

<tr>
<td>{@link java.util.Date}</td>
<td>{@link java.util.Date}</td>
<td>ISODate("...")<br>
shell入力:new Date(unixEpoch)</td>
<td>{$date:unixEpoch}
<td>"..."<br>入力不可</td>
</tr>


<tr>
<td>int</td>
<td>Integer</td>
<td>NumberInt("...")</td>
<td>{$numberInt:数値}</td>
<td>数値</td>
</tr>


<tr>
<td>long</td>
<td>Long</td>
<td>NumberLong("...")</td>
<td>{$numberLong:数値}</td>
<td>数値</td>
</tr>

<tr>
<td>double</td>
<td>Double</td>
<td>数値</td>
<td>数値</td>
<td>数値</td>
</tr>


<tr>
<td>{@link java.math.BigDecimal}</td>
<td>{@link org.bson.types.Decimal128}</td>
<td>NumberDecimal("...")<br>
数値入力不可</td>
<td>{$numberDecimal:"..."}<br>
数値入力不可</td>
<td>数値<br>
入力不可
</td>
</tr>

</table>
</p>
<p>
mongo-shellでは{...}系の入力はできません。指定名("$date"など)要素を持つ構造になってしまいます。
<br>
{@link org.bson.Document Document}パーザは19桁を超える数値を扱えません。NumberDecimal(..)または{$numberDecimal:..}を使う必要があります。<br>
</p>
<p>
hiMongoでは次の扱いとなります。
</p>

<table class=t>
<tr>
<td>型</td>
<td>Object(内部値)</td>
<td>mson表現</td>
<td>json表現</td>
</tr>

<tr>
<td>{@link org.bson.types.ObjectId}</td>
<td>{@link org.bson.types.ObjectId}</td>
<td>ObjectId("...")</td>
<td>{$oid:"..."}</td>
</tr>

<tr>
<td>{@link java.util.Date}</td>
<td>{@link java.util.Date}</td>
<td>ISODate("...")
</td>
<td>{$date:unixEpoch}</td>

</tr>

<tr>
<td>int</td>
<td>Long</td>
<td>数値<br>
NumberInt("...")可</td>
<td>数値<br>{$numberInt:数値}入力可</td>
</tr>

<tr>
<td>long</td>
<td>Long</td>
<td>数値<br>
NumberLong("...")可</td>
<td>数値<br>{$numberLong:数値}入力可</td>
</tr>

<tr>
<td>double</td>
<td>Double</td>
<td>数値</td>
<td>数値</td>
</tr>


<tr>
<td>{@link java.math.BigDecimal}</td>
<td>{@link java.math.BigDecimal}</td>
<td>NumberDecimal("...")<br>
数値入力可
</td>
<td>数値</td>
</td>
</tr>

</table>

</p>
<p>
hiMongoのパーザには桁数制限は有りません。hiMongoのパーズでは17桁までの整数はLong、仮数15桁まで指数15以下浮動小数はDouble、それ以外は{@link java.math.BigDecimal}となります。{@link java.math.BigDecimal}はbsonになる時点で{@link org.bson.types.Decimal128}となります。<br>
{@link org.bson.Document Document}内部データは{@link org.bson.Document Document}特有のクラスがありhiMongo内部データのJava標準のクラスとは異なりますが互いに変換可能です。
</p>
<p>
利用者クラスにマッピングする場合、数値はどの形でも利用者クラスの要素型になります。ただし、桁あふれは無視されます。
</p>


<div id="div_dateQuote1">
<p><input type="button" value="例を表示する" style="WIDTH:12em"
   onClick="document.getElementById('div_dateQuote2').style.display='block';
            document.getElementById('div_dateQuote1').style.display='none'"></p>
</div>
<div id="div_dateQuote2" style="display:none">
<p><input type="button" value="例を隠す" style="WIDTH:12em"
   onClick="document.getElementById('div_dateQuote2').style.display='none';
            document.getElementById('div_dateQuote1').style.display='block'"></p>
<pre class=quote8>
// ISODate(...),{$date:...}いずれもDate型になります。
// 要素名の引用符は省略可能です。（付けることも可）
// 値の引用符はシングルクオートでも構いません。（ダブルクオートも可）
import hi.db.hiMongo;
import otsu.hiNote.*;
import java.util.*;
import org.bson.Document;
public class Test {
   public static void main(String[] args_){
      String _filter
      ="{$and:["+
                "{type :'A'},"+
                "{date :{$gte:ISODate('2020-08-17T07:07:00.000Z')}},"+
                "{date2:{$gte:{$date:1597648021000}}}"+
              "]}";
      System.out.println("-- org.bson.Document");
      Document _doc=Document.parse(_filter);
      System.out.println("doc        ="+_doc);
      System.out.println("doc.toJson ="+_doc.toJson());
      System.out.println("-- hi.db.hiMongo");
      Object _node=hiMongo.parseText(_filter).asNode();
      System.out.println("node/mson  ="+hiMongo.mson(_node));
      System.out.println("node/json  ="+hiMongo.json(_node));
      System.out.println("---------");
      System.out.println("doc/Object ="+hiU.str(_doc,hiU.WITH_TYPE|hiU.WITH_INDENT));
      System.out.println("node/Object="+hiU.str(_node,hiU.WITH_TYPE|hiU.WITH_INDENT));
      }
   }
＊＊＊＊＊＊　ビルド実行結果
----- 07date -----
-- org.bson.Document
doc        =Document{{$and=[Document{{type=A}}, Document{{date=Document{{$gte=Mon Aug 17 16:07:00 JST 2020}}}}, Document{{date2=Document{{$gte=Mon Aug 17 16:07:01 JST 2020}}}}]}}
doc.toJson ={"$and": [{"type": "A"}, {"date": {"$gte": {"$date": 1597648020000}}}, {"date2": {"$gte": {"$date": 1597648021000}}}]}
-- hi.db.hiMongo
node/mson  ={"$and":[{"type":"A"}, {"date":{"$gte":ISODate("2020-08-17T07:07:00.000Z")}}, {"date2":{"$gte":ISODate("2020-08-17T07:07:01.000Z")}}]}
node/json  ={"$and":[{"type":"A"}, {"date":{"$gte":{"$date":1597648020000}}}, {"date2":{"$gte":{"$date":1597648021000}}}]}
---------
doc/Object =(Document){
   (String)"$and"=(ArrayList)[
      (Document){
         (String)"type"=(String)"A"},
      (Document){
         (String)"date"=(Document){
            (String)"$gte"=(Date)Mon Aug 17 16:07:00 JST 2020}},
      (Document){
         (String)"date2"=(Document){
            (String)"$gte"=(Date)Mon Aug 17 16:07:01 JST 2020}}]}
node/Object=(LinkedHashMap){
   (String)"$and"=(ArrayList)[
      (LinkedHashMap){
         (String)"type"=(String)"A"},
      (LinkedHashMap){
         (String)"date"=(LinkedHashMap){
            (String)"$gte"=(Date)Mon Aug 17 16:07:00 JST 2020}},
      (LinkedHashMap){
         (String)"date2"=(LinkedHashMap){
            (String)"$gte"=(Date)Mon Aug 17 16:07:01 JST 2020}}]}
OK
</pre>
<p><input type="button" value="例を隠す△" style="WIDTH:12em"
   onClick="document.getElementById('div_dateQuote2').style.display='none';
            document.getElementById('div_dateQuote1').style.display='block';
            document.location='#div_dateQuote1'"></p>
</div>

<!--
<p class=B1_2 id="regex">
&emsp;正規表現
</p>
<p>
スラッシュで囲めば正規表現と解釈されます。<br>
終端スラッシュの後ろには１文字のオプションが付けられます。
</p>
<pre class=quote10>
db.in("XX").find({name:/BC/i}).... 文字列BC(小文字可)を含む
db.in("XX").find({name:/^B.*n$/}).... Bで始まりnで終わる
db.in("XX").find({name:/(ra|re)/}).... 'ra'または're'を含む
</pre>
<p>
次の表現形を持ちます。
</p>
<p>
<table class=t>
<tr>
<td>型</td>
<td>bson(内部値)</td>
<td>mson表現</td>
<td>json表現</td>
</tr>

<tr>
<td>正規表現</td>
<td>BsonRegularExpression</td>
<td>{$regex:"..",$option:".."}<br>
/.../[.]入力のみ</td>
<td>{$regex:"..",$option:".."}</td>
</tr>

</table>
</p>
<p>
hiMongoでは次の扱いとなります。
</p>

<p>
<table class=t>

<tr>
<td>型</td>
<td>Object(内部値)</td>
<td>mson表現</td>
<td>json表現</td>
</tr>

<tr>
<td>正規表現</td>
<td>BsonRegularExpression</td>
<td>{$regex:"..",$option:".."}<br>
/.../[.]入力のみ</td>
<td>{$regex:"..",$option:".."}</td>
</tr>

</table>
</p>
-->

<p class=B1_2 id="comment">
&emsp;コメント等のhiMongo拡張(hson)
</p>
<p>
標準ではmson記述はコメントを受け付けません。<br>
hiMongo
hiMongo.parseXX(...).asNode()を用いて汎用Objectノードツリー化すればコメントの記述も可能です。<br>
また、hiMongo.parseXX(...).asNodeList()を用いれば複数レコードがただ並ぶだけでJSONの配列形式を採らない記述も受け付けinserManyなどの引数に出来ます。
</p>
<p>
例えば次の記述をinsertManyに与えることが出来ます。
</p>
<pre class=quote8>
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
</pre>
<p>
プログラムの断片を示します。<br>
with_hson()指定をすれば、hson拡張文字列を取り扱えます。
</p>
<pre class=quote10>
//-- with_hson()を発行した上で文字列を直接与える方法
String <span class=blue>_records_json</span>=hiFile.readTextAll("data.json");
hiMongo.DB db=hiMongo.use("db01");
db.in("composer").drop()
  <span class=red>.with_hson()</span>
  .insertMany(<span class=blue>_records_json</span>);
</pre>
<p>
ファイル入力の場合Stringに落とすことなくFileを直接指定することも可能です。
</p>
<pre class=quote10>
//-- with_hson()を発行した上でFile指定
hiMongo.DB db=hiMongo.use("db01");
db.in("composer").drop()
  <span class=red>.with_hson()</span>
  .insertMany(<span class=purple>new File("data.json")</span>);
</pre>
<p>
with_hson()指定を行わず、一旦hiMongo.parseText().adNodeList()でノードレベルまで解析した結果を与えることも出来ます。<br>
</p>
<pre class=quote10>
//-- パーズ結果を使う方法
String <span class=blue>_records_json</span>=hiFile.readTextAll("data.json");
Object <span class=green>_recodes_node</span>=hiMongo.parseText(<span class=blue>_records_json</span>)
                            .asNodeList();
hiMongo.DB db=hiMongo.use("db01");
db.in("composer").drop()
  .insertMany(<span class=green>_recodes_node</span>);
</pre>


<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<p class=B1 id="remote">
&emsp;remote接続
</p>
<p>
リモートホストに置かれたデータベースアクセスすることができます。
</p>
<p class=B1_2>
&emsp;リモート側（サーバ側）設定
</p>
<p class=c>
&emsp;外部からのアクセスを許すhost設定
</p>
<p>
リモート側ではmongodb設定でbind_ipにアクセスを許可するhostを指定する必要があります。
</p>
<pre class=quote10>
設定ファイル /etc/mongodb.conf
設定内容     #bind_ip = 127.0.0.1 # コメントアウト
             bind_ip = 0.0.0.0    # 追加
</pre>
<p>
0.0.0.0は全てのホストを許可する指定です。セキュリティを考慮すると、個別hostを記述したほうがよいでしょう。
なおmongoDBのバージョンにより記述法に差があるようです。
</p>
<p class=c>
&emsp;外部からのアクセスを許すユーザ設定
</p>
<p>
アクセスを許すユーザの設定をmongo-shellを使って設定します。<br>
例を示します。ここで指定するDBは利用者設定DBであり、利用者がデータ用に用いるDBとは別です。
</p>
<pre class=quote10>
$ mongo
> use <span class=red>testDB</span>;
> db.createUser({
     user:'<span class=blue>testUser</span>',
     pwd:'<span class=green>xxx</span>',
     roles:[
        {role:'dbOwner',db:'<span class=red>testDB</span>'}
        ]
     });
> exit;
</pre>

<p class=B1_2>
&emsp;クライアント側アクセス
</p>
<p class=c>
&emsp;mongo-shellでアクセス
</p>
<p>
次のような形でサーバとは別のPCからmongo-shellアクセスができます。
</p>
<pre class=quote10>
$ mongo -u <span class=blue>testUser</span> -p <span class=green>xxx</span> --host <i>192.168.1.139</i> <span class=red>testDB</span>
>
</pre>

<p class=c>
&emsp;hiMongoによるJavaプログラムアクセス
</p>
<p>
use()の前にconnect()メソッドにJSON文字列またはhiMongo.RemoteInfoで接続先の情報を指定します。
</p>
<pre class=quote10>
//---- JSON文字列で情報を与える
hiMongo.DB db=hiMongo.connect("{"+
                      "host:'192.168.1.139',"+
                      "port:27017,"+
                      "dbName:'<span class=red>testDB</span>',"+
                      "user:'<span class=blue>testUser</span>',"+
                      "password:'<span class=green>xxx</span>'"+
                      "}")
                    .use("<span class=purple>db01</span>");// 利用者が使うDBはdb01
db.in("coll_01").find()...
</pre>
<p>
または
</p>
<pre class=quote10>
//---- クラス(hiMongo.RemoteInfo)で情報を与える
hiMongo.RemoteInfo _info=new hiMongo.RemoteInfo();
_info.host    = "192.168.1.139";
_info.port    = 27017;
_info.dbName  = "<span class=red>testDB</span>";
_info.user    = "<span class=blue>testUser</span>";
_info.password= "<span class=green>xxx</span>";
hiMongo.DB db=hiMongo.connect(_info)
                     .use("<span class=purple>db01</span>");// 利用者が使うDBはdb01
db.in("coll_01").find()...
</pre>



<!-- ====================================================================== -->
<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<p class=B1 id="worker">
&emsp;Caller(API)とWorker(deiver呼び出し)
</p>
<p>
{@link hi.db.hiMongo.Client}～{@link hi.db.hiMongo.Finder}はinterfaceとなっています。
</p>
<p>
２種の実装が用意されています。
</p>
<ul>
<li>直接driverを呼ぶ</li>
<li>APIを受け持つ部分(Caller)とdriverを呼ぶ部分(Worker)を分離して通信で繋ぐ</li>
</ul>
<p>
直接driverを呼ぶ実装は{@link hi.db.hiMongoDirect}で{@link hi.db.hiMongoDirect.Client}～{@link hi.db.hiMongoDirect.Finder}の実装を持っています。
</p>
<p>
APIを受け持つ部分(Caller)実装は{@link hi.db.hiMongoCaller}で{@link hi.db.hiMongoCaller.Client}～{@link hi.db.hiMongoCaller.Finder}の実装を持っています。
</p>

<p>
通信機は{@link hi.db.hiStringCOM}の実装となります。
</p>
<p>
<p>
driverを呼ぶ部分(Worker)実装は{@link hi.db.hiMongoWorker}で通信機{@link hi.db.hiStringCOM}の実装ともなっています。
</p>
<p>
{@link hi.db.hiMongo.Client},{@link hi.db.hiMongo.DB}はhiMongo.connect(),hiMongo.use()で作成されます。<br>
この際引数に{@link hi.db.hiStringCOM}があると、{@link hi.db.hiMongoCaller}が生成され、なければ{@link hi.db.hiMongoDirect}が生成されます。
</p>
<p>
通信機を指定しない場合、mongo-java-driverを直接呼ぶ形となります。
</p>
<pre class=quote10>
hiMongo.DB db=hiMongo.use("db01"); // {@link hi.db.hiMongoDirect}となる
db.in("coll_01")
  .find("{}","{_id:0}")
  ...
</pre>
<p>
通信機を指定するとCallerが生成され、mongo-java-driverは通信機の先にあるworkerが呼び出します。<br>
次の例ではサンプルとして用意されている通信機{@link hi.db.hiMonWorkerSample.COM}を利用しています。<br>
Workerは{@link hi.db.hiMonWorkerSample}のWorkerSampleプログラムがサーバとして動いているものとしています。
</p>
<pre class=quote10>
hiStringCOM  COM=new hiMonWorkerSample.COM("192.168.1.14",8010); // 通信機
hiMongo.DB db=hiMongo.use("db01",COM); // {@link hi.db.hiMongoCaller}となる
db.in("coll_01")
  .find("{}","{_id:0}")
  ...
</pre>

<p>
なお{@link hi.db.hiMongoWorker}も通信機{@link hi.db.hiStringCOM}の実装なので次の様に動かすことも出来ます。
</p>
<pre class=quote10>
hiMongoWorker _worker=new hiMongoWorker();
hiMongo.DB db=hiMongo.use("db01",_walker); // {@link hi.db.hiMongoCaller}となる
db.in("coll_01")
  .find("{}","{_id:0}")
  ...
</pre>
<p>
最も単純な通信機はCallerとWorkerを繋ぐだけのものとして実装できます。
</p>
<pre class=quote10>
public class SimpleRepeater implements hiStringCOM{
   hiStringCOM to;
   public Repeater(hiStringCOM to_){
      to= to_;
      }
   @Overrride
   public String call(String msg_){
      return to.call(_msg);
      }
   }
//-----
hiMongoWorker  _worker  = new hiMongoWorker();
SimpleRepeater _repeater= new SimpleRepeater(_worker);
hiMongo.DB db=hiMongo.use("db01",_repeater);
db.in("coll_01")
  .find("{}","{_id:0}")
  ...
</pre>

<p class=c>
&emsp;Andoridで利用するサンプル
</p>
<p>
アンドロイドで利用するサンプルをgitHUBで公開しています。
</p>
<p>
&emsp;<a class=A1 target="_blank" rel="noopener noreferrer" href=
"https://github.com/hiuric/AndroMongo"
>https://github.com/hiuric/AndroMongo</a>
</p>

<!-- ====================================================================== -->
<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<p class=B1 id="build">
&emsp;build
</p>
<div id="divBuild_1">
<p><input type="button" value="説明を表示する" style="WIDTH:10em"
   onClick="document.getElementById('divBuild_2').style.display='block';
            document.getElementById('divBuild_1').style.display='none'"></p>
</div>
<div id="divBuild_2" style="display:none">
<p><input type="button" value="説明を隠す" style="WIDTH:10em"
   onClick="document.getElementById('divBuild_2').style.display='none';
            document.getElementById('divBuild_1').style.display='block'"></p>
<p>
<p>
hiMongoを動かすには次のjarをリンクする必要があります。(バージョン番号は変わるかも知れません）
</p>
<table class=t0>
<tr>
<td>hiMongo_0_11.jar</td>
<td>:  hiMongo本体</td>
</tr>
<tr>
<td>hiNote_3_10.jar</td>
<td>:  hiJSON他汎用機能</td>
</tr>
<tr>
<td>mongo-java-driver-3.12.5.jar</td>
<td>:  mongo-java-driver</td>
</tr>
</table>
<p>
これらのjarが../libにあり、プログラムがTest.javaだとすると、次のようにビルド実行出来ます。
</p>
<pre class=prog10>
#!/bin/sh
# 注意！ 改行=LF
MAIN=Test
LIB_DIR=../lib
hiMongoLIB=hiMongo_0_12.jar
hiNoteLIB=hiNote_3_10.jar
mongoLIB=mongo-java-driver-3.12.5.jar
LIBS=".:${LIB_DIR}/${hiNoteLIB}:${LIB_DIR}/${mongoLIB}:${LIB_DIR}/${hiMongoLIB}
# ビルド
javac -Xlint:unchecked -encoding utf-8 -cp ${LIBS} ${MAIN}.java
# 実行
java -cp ${LIBS} ${MAIN}
</pre>
<p><input type="button" value="説明を隠す△" style="WIDTH:10em"
   onClick="document.getElementById('divBuild_2').style.display='none';
            document.getElementById('divBuild_1').style.display='block';
            document.location='#divBuild_1'"></p>
</div>

<!-- ====================================================================== -->
<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<p class=B1 id="log">
&emsp;log他
</p>
<div id="divLog_1">
<p><input type="button" value="説明を表示する" style="WIDTH:10em"
   onClick="document.getElementById('divLog_2').style.display='block';
            document.getElementById('divLog_1').style.display='none'"></p>
</div>
<div id="divLog_2" style="display:none">
<p><input type="button" value="説明を隠す" style="WIDTH:10em"
   onClick="document.getElementById('divLog_2').style.display='none';
            document.getElementById('divLog_1').style.display='block'"></p>
<p class=c>
&emsp;mongo-java-driverが表示するlog
</p>
<p>
mongo-java-driver-3.12が標準エラーに出すlogの止め方は不明です。
</p>

<p class=c>
&emsp;検討事項(サブ・クエリー)
</p>

<p class=c>
&emsp;検討事項(トランザクション)
</p>
<p>
mongoDb4.0ではトランザクションが導入されるようです。<br>
hiMongoとしては次のようなAPIが可能か検討しています。<br>
JAVAのバージョンが9以上が要求されることも考慮中です。
</p>
<pre class=prog10>
hiMongo.DB db=hiMongo.use("db01")
db.in("coll_01")   // Collection指定
  <span class=red>.lock(Co->{  // 当該Collection対し一貫性保証</span>
     Co.find().sort("{_id:-1}").skip(2).limit(1) // 最後から3個め
       .forEachProbe(Rp-> // limit(1)なので１回だけ
           coll.deleteMany("{_id:{$lt:"+hiMongo.mson(Rp.get("_id"))+"}}")
           );
     <span class=red>})</span>
  .find()
  .forEachMson(Rm->System.out.println(Rm));
</pre>


<p><input type="button" value="説明を隠す△" style="WIDTH:10em"
   onClick="document.getElementById('divLog_2').style.display='none';
            document.getElementById('divLog_1').style.display='block';
            document.location='#divLog_1'"></p>
</div>



<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<p class=B1 id="version">
&emsp;更新履歴
</p>
<p class=c>
&emsp;0.05
</p>
<p>
公開初版
</p>

<p class=c>
&emsp;0.06
</p>
<ul>
<li>insertなどの引数として利用者クラスを受けれるようにした</li>
<li>CollenctionにcreateIndex追加</li>
<li>BigDecimal,Decimal128のjson出力を数値とした。入力は関数形,辞書形とも可</li>
<li>closeの方針を変更；Clientのcloseを呼ばない様にサンプルプログラムを変更(ライブラリ自体に変更はない)</li>
<li>javadocのmongo-java-driver参照を3.7から3.12に変更</li>
<li>一部Documentに代わりBsonを引数として受けるようにした（ただし積極的公開ではない）</li>
</ul>

<p class=c>
&emsp;0.07
</p>
<ul>
<li>混乱を防ぐためにメソッド名を型名準拠に変更.
   <ul>
   <li>getNodeListをgetDocumentListに変更</li>
   <li>getIteratorをgetIterableに変更</li>
   </ul>
</li>
<li>形式を揃えるため別名メソッド追加.
   <ul>
   <li>getList()/getDocumentList()の別名</li>
   <li>getList(Class)/getClassList(Class)の別名</li>
   <li>forEachDocument()/forEach()の別名</li>
   <li>forEachClass(Class)/forEach(Class)の別名</li>
   </ul>
</li>
<li>サンプルでのラムダ引数の命名法を統一</li>
<li>JAVADOC記述を修正/強化</li>
</ul>

<p class=c>
&emsp;0.08
</p>
<ul>
<li>出力フォーマット微調整設定をpublicにした<br>
msonで引用符を"にすることもできる</li> <!-- Output format change API enabled -->
<li>
hiMongo拡張JSON文字列(hson)からDocumet,Bson,List<Document>,List<Bson>を得るメソッドをpublicにした
</li>
</ul>
<p>
以下はhiMongo本体の変更ではありませんが、サンプルを含むリリースセットの変更としてここに載せます。
</p>
<ul>
<li>.batによるビルド/試験を追加</li>
<li>.shによる試験でmongo-shell呼び出しをhere_documentからjs参照に変更</li>
<li>javadocオプションに-Xdoclint:none追加(table-captionによるレイアウト乱れを避けるため）</li>
<li>$recordIdへの対処記述を追加</li>
<li>試験での結果ファイルをutf-8化。表示はシステム標準</li>
<li>サンプルで結果をkekka.txt(システム標準)に残すようにした</li>
<li>オツライブラリ/hiNoteを最新版3.10にした</li>
</ul>

<p class=c>
&emsp;0.10　Caller/Worker分離。Android対応
</p>
<p>
hiMongo-APIとdriver呼び出し部を分離し仮想通信機で結ぶことにより、Androidにアプリを置きWindows/LinuxにDBを置く形を可能とした。
</p>
<ul>
<li>Collection指定をget()からin()に変更</li>
<li>driverインスタンスの参照APIを変数から関数に変更</li>
<li>Client～Finderをinterfaceに変更</li>
<li>旧手続はdriver直呼のhiMongoDirectに移行</li>
<li>通信機interfaceとしてhiStringCOM新設</li>
<li>CallerとWorker新設</li>
<li>hiMongoのconnectとuseでdirect式とcaller式の実装を生成し分ける。<br>
(旧来の呼び出しではdirect型になるのでそのままdirect型で使える)
</li>
<li>サーバ/クライアントのsample(hiMonWorkerSample)新設。binフォルダに起動スクリプト</li>
</ul>


<p class=c>
&emsp;0.11 辞書変数と再帰探索導入
</p>
<ul>
<li>旧来の別名としてforEachを廃止</li>
<li>旧来の別名としてgetListを廃止</li>
<li>forEachRecursive,forOneRecursive導入</li>
<li>readOne():DBスコープの変数取り込み追加</li>
<li>forOneの場合limit(1)を内部発行する</li>
<li>forEachスコープの変数導入</li>
<li>#値の展開</li>
<li>$calc:'式'導入、結合則付き四則演算と括弧をサポート</li>
<li>Date演算導入</li>
<li>hson_mode指定廃止（常に有効)</li>
<li>正規表現のmson表示修正</li>
<li>bson構造体を説明から削除</li>
</ul>

<!-- ======================================================================================= -->
<!-- ======================================================================================= -->
<!-- ======================================================================================= -->
<!-- ======================================================================================= -->
<a class=A1 href="#top">top</a>、<a class=A1 href="#API">API</a>
<p class=B2 id="API">&emsp;ＡＰＩ</p>
*/
public class hiMongo {
   final static boolean MASTERD=false;   // デバグフラグ（開発時用）
   final static boolean D=MASTERD&&false;// デバグフラグ（開発時用）
   //===========================================
   // 定数
   //===========================================
   /** json文字列を使う */
   public final static long USE_JSON    = 0x0100000000L;
   /** mson文字列を使う */
   //public final static long USE_MSON    = 0x0200000000L;
   /** objectの表示にtoStringを使う */
   //public final static long USE_toString= 0x0400000000L;
   /** objectの表示にhiU.strを使う */
   public final static long USE_str     = 0x0800000000L;
   //
   final static long DISP_MASK=0x00ffffffffL&~hiU.KEEP_QUOTE&~hiU.AS_EMPTY_STRING;
   //===========================================
   // 解析/表示エンジン設定
   //===========================================
   private hiMongo(){} // hiMongoインスタンスは生成できない(staticメソッドのみ)
   static hiJSON.Engine mson_engine;
   static hiJSON.Engine json_engine;
   static {
      nolog();
      mson_engine= hiJSON.engine();
      mson_engine
        .with_functionStyle_parse("ISODate",      MongoConverter::funcStr_toDate)
        .with_functionStyle_parse("ObjectId",     MongoConverter::funcStr_toOid)
        .with_functionStyle_parse("NumberLong",   MongoConverter::funcStr_toLong)
        .with_functionStyle_parse("NumberInt",    MongoConverter::funcStr_toLong)
        .with_functionStyle_parse("NumberDecimal",MongoConverter::funcStr_toDecimal)
        .with_dict_parse("$date",                 MongoConverter::dictStr_toDate)
        .with_dict_parse("$oid",                  MongoConverter::dictStr_toOid)
        .with_dict_parse("$regex",                MongoConverter::dictStr_toRegex)
        .with_dict_parse("$numberInt",            MongoConverter::dictStr_toLong)
        .with_dict_parse("$numberLong",           MongoConverter::dictStr_toLong)
        .with_dict_parse("$numberDecimal",        MongoConverter::dictStr_toDecimal)
        .with_regex_node_parse(                   MongoConverter::regex_parse_func)
        .with_class_from_node(Date.class,         MongoConverter::node_toDate)
        .with_class_from_node(ObjectId.class,     MongoConverter::node_toOid)
        //.with_class_from_node(Integer.class,       MongoConverter::node_toLong)
        .with_class_from_node(BigDecimal.class,   MongoConverter::node_toBigDecimal)
        .with_option(hiU.CHECK_UNKNOWN_FIELD|hiU.CHECK_UNSET_FIELD)
        .str_format()
           .str_option(hiU.WITH_SINGLE_QUOTE)
           .str_class_disp(Date.class,      MongoConverter::toISODateStr)
           .str_class_disp(ObjectId.class,  MongoConverter::toOidStr)
           .str_class_disp(BigDecimal.class,MongoConverter::toDecStr)
           .str_class_disp(Decimal128.class,MongoConverter::toDecStr)
           .str_class_disp(BsonRegularExpression.class,MongoConverter::toRegexMapStr)
         ;
      json_engine= hiJSON.engine();
      json_engine
        .str_format()
           .str_class_disp(Date.class,      MongoConverter::toDateMapStr)
           .str_class_disp(ObjectId.class,  MongoConverter::toOidMapStr)
           //.str_class_disp(BigDecimal.class,MongoConverter::toStr)
           .str_class_disp(Decimal128.class,MongoConverter::toStr)
         ;
      //＊ hiU.IGNORE_toStringをdisableにすると、BsonRegularExpressionが
      // 非JSON形式で出力される。
      // 例 ) BsonRegularExpression{pattern='^Ba"'', options=''}}
      //＊ hiU.IGNORE_toStringがenableのままではDecimal128が内部構造表示
      // となる
      // 例){
      //  "high":(long)3476778912330024123,
      //  "low":(long)6671827972300883147}
      //＊そのためDecimal128は明示的にtoStringを呼ぶMongoConverter::toStr
      // を作用させる
      }
   /**
    *mongo-driver直呼びの接続を得る.
    *@return 接続
    *<!-- hiMongo -->
    */
   public static hiMongo.Client connect(){
      return new hiMongoDirect().connect(null);
      }
   /**
    *mongo-driver直呼びの接続を得る.
    *@param remote_ 接続情報
    *@return 接続
    *<!-- hiMongo -->
    */
   public static hiMongo.Client connect(Object remote_){
      return new hiMongoDirect().connect(remote_);
      }
   /**
    *mongo-driver直呼び,デフォルト接続のDBを得る.
    *@param dbName_ Database名
    *@return Database
    *<!-- hiMongo -->
    */
   public static hiMongo.DB use(String dbName_){
      return new hiMongoDirect().connect(null).use(dbName_);
      }
   /**
    *指定通信機を使うCallerによるClientを得る
    *@param comm_ 通信機
    *@return Database
    *<!-- hiMongo -->
    */
   public static hiMongo.Client connect(hiStringCOM comm_){
      return new hiMongoCaller(comm_).connect(null);
      }
   /**
    *指定通信機を使うCallerによるClientを得る
    *@param remote_ リモートDB情報
    *@param comm_ 通信機
    *@return Database
    *<!-- hiMongo -->
    */
   public static hiMongo.Client connect(Object remote_,hiStringCOM comm_){
      return new hiMongoCaller(comm_).connect(remote_);
      }
   /**
    *指定通信機を使うCallerによるDBを得る
    *@param dbName_ Database名
    *@param comm_ 通信機
    *@return Database
    *<!-- hiMongo -->
    */
   public static hiMongo.DB use(String dbName_,hiStringCOM comm_){
      return new hiMongoCaller(comm_).connect(null).use(dbName_);
      }
   /**
    *mongo-driver直呼び,デフォルト接続でDatabase名一覧を得る.
    *@param sort_ ソート指定
    *@return Database名一覧
    *<!-- hiMongo -->
    */
   public static ArrayList<String> show_dbs(boolean sort_){
      //return new hiMongoDirect.Client().connect().show_dbs(sort_);
      return new hiMongoDirect().connect(null).show_dbs(sort_);
      }
   @SuppressWarnings("unchecked")
   public static RemoteInfo getRemoteInfo(Object remote_){
      if( remote_==null ) return null;
      hiMongo.RemoteInfo _info= null;
      if( remote_ instanceof File ){
         remote_ = hiFile.readTextAll((File)remote_);
         }
      if( remote_ instanceof String ){
         Map<String,Object> _node=hiMongo.parseText((String)remote_)
                                         .asMap();
         if( _node.isEmpty() ) _info=null;
         else{
            _info=hiMongo.parseNode(_node)
                         .asClass(hiMongo.RemoteInfo.class);
            }
         }
      else if( remote_ instanceof hiMongo.RemoteInfo ){
         _info= (hiMongo.RemoteInfo)remote_;
         }
      else if( remote_ instanceof Map ){
         Map<String,Object> _map=(Map<String,Object>) remote_;
         if( _map.isEmpty() ) _info=null;
         else{
            _info=hiMongo.parseNode(_map)
                         .asClass(hiMongo.RemoteInfo.class);
            }
         }
      return _info;
      }
   /**
    * mongo-java-driverのログを止める（未）.
    *<!-- hiMongo -->
    */
   public final static void nolog(){}
   /**
    * mongo-java-driverのログを止める（未）.
    *@param class_ クラス（mainのクラス)
    *<!-- hiMongo -->
    */
   public static void nolog(Class<?> class_){}
   /**
    * パーズするテキストを指定.
    *<p>
    *本メソッドの後ろにパーズ形式を指定することでパーズが実行されます。
    *</p>
    *<pre class=quote10>
    *  String  _json_text="....";
    *  MyClass _data= hiMongo.parseText(_json_text).asClass(MyClass.class);
    *</pre>
    *<p>
    *{@link org.bson.Document Document}を得るには次のようにします。
    *</p>
    *<pre class=quote10>
    *  String    _json_text="....";
    *  Document _doc= new Document(hiMongo.parseText(_json_text).asMap());
    *</pre>
    *@param text_ テキスト
    *@return 解析エンジン
    *<!-- hiMongo -->
    */
   public static hiJSON.Engine parseText(String text_){
      return mson_engine.clone().parseText(text_);
      } 
   /**
    * パーズするテキストを指定.
    *<p>
    *本メソッドの後ろにパーズ形式を指定することでパーズが実行されます。
    *</p>
    *<p>
    *{@link org.bson.Document Document}を得るには次のようにします。
    *</p>
    *<pre class=quote10>
    *  String    _json_text="....";
    *  Document _doc= new Document(hiMongo.parse(_json_text).asMap());
    *</pre>
    *@param text_ テキスト
    *@return 解析エンジン
    *<!-- hiMongo -->
    */
   public static hiJSON.Engine parse(String text_){
      return mson_engine.clone().parseText(text_);
      }
   /**
    * パーズするFileを指定.
    *<p>
    *本メソッドの後ろにパーズ形式を指定することでパーズが実行されます。
    *</p>
    *<p>
    *{@link org.bson.Document Document}を得るには次のようにします。
    *</p>
    *<pre class=quote10>
    *  File     _file= new file("./data.json");
    *  Document _doc = new Document(hiMongo.parse(_file).asMap());
    *</pre>
    *@param textFile_ テキストファイル
    *@return 解析エンジン
    *<!-- hiMongo -->
    */
   public static hiJSON.Engine parse(File textFile_){
      return mson_engine.clone().parse(hiFile.readTextAll(textFile_));
      }
   /**
    * パーズするノードツリーObjectを指定.
    *<p>
    *本メソッドの後ろにパーズ形式を指定することでパーズが実行されます。<br>
    *他のツール(org.bson.Documentなど）で解析された結果のObjectもパーズできます。
    *</p>
    *<pre class=quote10>
    *  String   _json_text="....";
    *  Document _doc = Document.parse(_json_text);
    *  MyClass  _data= hiMongo.parseNode(_doc).asClass(MyClass.class);
    *</pre>
    *@param obj_ ノードツリーObject
    *@return 解析エンジン
    *<!-- hiMongo -->
    */
   public static hiJSON.Engine parseNode(Object obj_){
      return mson_engine.clone().parseNode(obj_);
      }
   /**
    * パーズするテキストファイルをファイル名で指定.
    *<p>
    *本メソッドの後ろにパーズ形式を指定することでパーズが実行されます。
    *</p>
    *<pre class=quote10>
    *  String  _json_file="./data.json";
    *  MyClass _data= hiMongo.parseFile(_json_file).asClass(MyClass.class);
    *</pre>
    *<p>
    *{@link org.bson.Document Document}を得るには次のようにします。
    *</p>
    *<pre class=quote10>
    *  String  _json_file="./data.json";
    *  Document _doc = new Document(hiMongo.parseFile(_file).asMap());
    *</pre>
    *@param fileName_ テキストファイル名
    *@return 解析エンジン
    *<!-- hiMongo -->
    */
   public static hiJSON.Engine parseFile(String fileName_){
      return mson_engine.clone().parseFile(fileName_);
      }
   /**
    * データ構造表示.
    *<p>
    *データ構造表示をします。JSON形式ではありません。
    *</p>
    *@param obj_ nodeオブジェクト
    *@return 拡張JSON表記
    *<!-- hiMongo -->
    */
   public static String str(Object obj_){
      return hiU.str(obj_);
      }
   /**
    * データ構造表示.
    *<p>
    *データ構造表示をします。JSON形式ではありません。
    *</p>
    *@param obj_ nodeオブジェクト
    *@param option_ 表示オプション (hiU.WITH_INDENTなど{@link otsu.hiNote.hiFieldFormat#option hiFieldFormatオプション}参照)
    *@return 拡張JSON表記
    *<!-- hiMongo -->
    */
   public static String str(Object obj_,long option_){
      return hiU.str(obj_,option_);
      }
   /**
    * mongo拡張JSON表記を得る.
    *<p>
    *nodeツリー上にObjectID,Dateクラスインスタンスが有る場合ObjectId(),ISODate()の形で表示します。
    *</p>
    *@param obj_ nodeオブジェクト
    *@return 拡張JSON表記
    *<!-- hiMongo -->
    */
   public static String mson(Object obj_){
      return mson_engine.str(obj_);
      }
   /**
    * mongo拡張JSON表記を得る.
    *<p>
    *nodeツリー上にObjectID,Dateクラスインスタンスが有る場合ObjectId(),ISODate()の形で表示します。
    *</p>
    *@param obj_ nodeオブジェクト
    *@param option_ 表示オプション (hiU.WITH_INDENTなど{@link otsu.hiNote.hiFieldFormat#option hiFieldFormatオプション}参照)
    *@return 拡張JSON表記
    *<!-- hiMongo -->
    */
   public static String mson(Object obj_,long option_){
      return mson_engine.str(obj_,option_);
      }
   /**
    * 標準JSON表記を得る.
    *<p>
    *nodeツリー上にObjectID,Dateクラスインスタンスが有る場合{"$oid":"..."},{"$date":unixEpoch}で表示されます。
    *</p>
    *@param obj_ nodeオブジェクト
    *@return 拡張JSON表記
    *<!-- hiMongo -->
    */
   public static String json(Object obj_){
      return json_engine.str(obj_);
      }
   /**
    * 標準JSON表記を得る.
    *<p>
    *nodeツリー上にObjectID,Dateクラスインスタンスが有る場合{"$oid":"..."},{"$date":unixEpoch}で表示されます。
    *</p>
    *@param obj_ nodeオブジェクト
    *@param option_ 表示オプション (hiU.WITH_INDENTなど{@link otsu.hiNote.hiFieldFormat#option hiFieldFormatオプション}参照)
    *@return 拡張JSON表記
    *<!-- hiMongo -->
    */
   public static String json(Object obj_,long option_){
      return json_engine.str(obj_,option_);
      }
   /**
    * 拡張JSON(MSON)解析/表示エンジン取得.
    *<p>
    *mongoDB用設定が追加されたJSON解析/MSON表示エンジン(clone)を取得します。
    *</p>
    *@return エンジン
    *<!-- hiMongo -->
    */
   public static hiJSON.Engine engine(){
      return mson_engine.clone();
      }
   /**
    * 標準JSON表示エンジン取得.
    *<p>
    *標準JSON表示エンジン(clone)を取得します。
    *</p>
    *@return エンジン
    *<!-- hiMongo -->
    */
   public static hiJSON.Engine engineJ(){
      return json_engine.clone();
      }
   /**
    * 現在時刻の拡張JSON表記を得る.
    *<p>
    *次の形式の文字列が得られます。
    *</p>
    *<pre class=prog10>
    * {$date:<i>Unix-Epoch数値</i>}
    * EX. {$date:1597648051506}
    *</pre>
    *@return 拡張JSON表記
    *<!-- hiMongo -->
    */
   public static String date(){
      return "{$date:"+new Date().getTime()+"}";
      }
   /**
    * 数値文字列から.0を削除する.
    *<p>
    *double値表示の最後の".0"を削除します。
    *</p>
    *<pre class=prog10>
    * 1234.0  -> 1234
    * 1234.01 -> 1234.01
    * a50.0   -> a50
    * a50.01  -> a50.01
    * a50.0x  -> a50x
    * a50.01x -> a50.01x
    * a.0     -> a.0
    *</pre>
    *@param str_ 数値文字列を含む文字列
    *@return .0を削除した文字列
    */
   public static String suprress_dot_0(String str_){
      return hiRegex.with(COMMA_ZERO_PATTERN,"${1}").replace(str_).result();
      }
   final static String COMMA_ZERO_PATTERN="(\\d+)\\.0(?!\\d)";
   /**
    * オブジェクトからBsonを得る.
    *@param obj_ 拡張JSON文字列、File、Document
    *@return Bson
    *<!-- hiMongo -->
    */
   @SuppressWarnings("unchecked")
   public  static Bson objToBson(Object obj_){
      if( obj_ instanceof Bson ) { // Documentも含む
         return (Bson)obj_;
         }
      return sObjToDoc(obj_);
      }
   /**
    * StringまたはオブジェクトからDocumentを得る.
    *@param obj_ 拡張JSON文字列、File、Document
    *@return Document
    *<!-- hiMongo -->
    */
   public static Document sObjToDoc(Object obj_){
      if( obj_ instanceof Document ) { // Documentも含む
         return (Document)obj_;
         }
      obj_=strToObj(obj_);
      return objToDoc(strToObj(obj_));
      }
   /**
    * StringまたはオブジェクトからStringを得る.
    *@param data_ 拡張JSON文字列、File、Document
    *@return String
    *<!-- hiMongo -->
    */
   static String sObjToStr(Object obj_){
      if( obj_ instanceof File ){
         obj_ = hiFile.readTextAll((File)obj_);
         }
      if( obj_ instanceof String ){
         return (String)obj_;
         }
      return hiMongo.mson(obj_);
      }
   /**
    * StringまたはオブジェクトからObjectを得る.
    *@param data_ 拡張JSON文字列、File、Document
    *@return Object
    *<!-- hiMongo -->
    */
   static Object strToObj(Object obj_){
     if( obj_ instanceof File ){
         obj_ = hiFile.readTextAll((File)obj_);
         }
      if( obj_ instanceof String ){
         obj_ = hiMongo.parse((String)obj_).asNode();
         }
      return obj_;
      }
   /**
    * オブジェクトからDocumentを得る.
    *<p>
    *オブジェクトが直接Document化できない場合は一旦文字列にしてDocument化する
    *</p>
    *@param Object
    *@return Document
    *<!-- hiMongo -->
    */
   @SuppressWarnings("unchecked")
   static Document objToDoc(Object obj_){
      if( obj_ instanceof Map ){// nodeと思われる
         try{
            return new Document((Map<String,Object>)obj_);
            }
         catch(Exception _ex){}// テキスト化して再トライ
         }
      String _text=hiMongo.mson(obj_);
      if(hiMongo.MASTERD)hiU.m("text="+_text);
      Map<String,Object> _node=hiMongo.parse(_text).asMap();
      return Document.parse(_text);
      }
   /**
    * StringまたはMapオブジェクトから作成されるDocumentを指定名の要素として持つDocumentを得る.
    *<!-- hiMongo -->
    */

   @SuppressWarnings("unchecked")
   public static Document namedObjToDoc(String name_,Object data_){//,hiJSON.Engine engine_){
      if( data_ instanceof Map ){
         return new Document(name_,new Document((Map<String,Object>)data_));
         }
      if( data_ instanceof File ){
         data_ = hiFile.readTextAll((File)data_);
         }
      if( data_ instanceof String ){
         //if( engine_!=null ){
            return new Document(name_
                        ,new Document(hiMongo.parseText((String)data_)
                                             .asMap()));
            //}
         //return new Document(name_,Document.parse((String)data_));
         }
      throw new hiException("illegal document class "+data_.getClass().getName());
      }
   /**
    * 動的connect/use実行機.
    */
   public static interface MoreMongo {
      /**
       *mongoDB接続を得る.
       *@param remoteInfo_ 接続先情報
       *@return 接続
       */
      public Client connect(Object remoteInfo_);
      /**
       *デフォルト接続によるDatabaseを得る.
       *@param dbName_ Database名
       *@return Database
       */
      public DB     use(String dbName_);
      /**
       *デフォルト接続によるDatabase一覧を得る.
       *@param sort_ ソート指定
       *@return Database一覧
       */
      public ArrayList<String> show_dbs(boolean sort_);
      }
   /**
    * レコードアクセス機(Finder,Aggregatorのベース).
    *<p>
    *{@link hi.db.hiMongo.Finder},{@link hi.db.hiMongo.Aggregator}のベースです。<br>
    *この階層を直接生成することは有りません。
    *</p>
    *<!-- Accessor -->
    */
   public static interface Accessor {
      //Accessor(Collection collection_);
      /**
       * Collectionに戻る.
       *@return コレクション
       *<!-- Accessor -->
       */
       public Collection back();
      /**
       * JSON用の表示エンジンを取得(clone).
       *@return json用表示エンジン
       *<!-- Accessor -->
       */
      public hiJSON.Engine engineJ();
      /**
       * この層の解析/mson表示エンジンを取得.
       *<p>
       *一時的にエンジンの設定を変更するために使用します。<br>
       *カスケードAPIを連続させるため、通常はforThisのラムダ式内で使います。
       *</p>
       *@return 基本の解析/表示エンジン
       *<!-- Accessor -->
       */
      public hiJSON.Engine engine();
      /**
       * 現状の解析/表示エンジンを得る(cloneではない).
       *<p>変更を加えてはなりません</p>
       *@return 解析/表示エンジン
       *<!-- Accessor -->
       */
      public hiJSON.Engine cur_engine();
      /**
       * 現状の解析/表示エンジンを得る(cloneではない).
       *<p>変更を加えてはなりません</p>
       *@return json用表示エンジン
       *<!-- Accessor -->
       */
      public hiJSON.Engine cur_engineJ();
      /**
       * パーズ用エンジンを得る.
       *<p>定義はCollectionにあります。</p>
       *@return nullの場合はDocument
       *<!-- Accessor -->
       */
      public hiJSON.Engine parse_engine();
      /**
       *JSON文字列のリストの形でレコード取得.
       *@param option_ hi.REVERSE:逆向き
       *@return リスト
       *<!-- Accessor -->
       */
      public ArrayList<String> getJsonList(long option_);
      /**
       *JSON文字列のリストの形でレコード取得
       *@return リスト
       *<!-- Accessor -->
       */
      public ArrayList<String> getJsonList();
      // Accessor
      /**
       *JSON文字列のリストの形でレコード取得.
       *@param option_ hiU.REVERSE:逆向き
       *@return リスト
       *<!-- Accessor -->
       */
      public ArrayList<String> getMsonList(long option_);
      /**
       *MSON文字列のリストの形でレコード取得
       *@return リスト
       *<!-- Accessor -->
       */
      public ArrayList<String> getMsonList();
      /**
       *hiJSON.Probeのリストの形でレコード取得.
       *@param option_ hiU.REVERSE:逆向き
       *@return リスト
       *<!-- Accessor -->
       */
      public ArrayList<hiJSON.Probe> getProbeList(long option_);
      /**
       *hiJSON.Probeのリストの形でレコード取得.
       *@return リスト
       *<!-- Accessor -->
       */
      public ArrayList<hiJSON.Probe> getProbeList();
      /**
       *指定クラスのリストの形でレコード取得.
       *@param class_ クラス
       *@param option_ hiU.REVERSE:逆向き
       *@return レコードリスト
       *<!-- Accessor -->
       */
      public <T> ArrayList<T>  getClassList(Class<T> class_
                                           ,long option_
                                           );
      /**
       *指定クラスのリストの形でレコード取得.
       *@param class_ クラス
       *@return レコードリスト
       *<!-- Accessor -->
       */
      public <T> ArrayList<T> getClassList(Class<T> class_);
      /**
       * Documentのリストの形でレコード取得.
       *@param option_ hiU.REVERSE:逆向き
       *@return リスト
       *<!-- Accessor -->
       */
      public ArrayList<Document> getDocumentList(long option_);
      /**
       * Documentのリストの形でレコード取得.
       *@return リスト
       *<!-- Accessor -->
       */
      public ArrayList<Document> getDocumentList();
      /**
       * オブジェクトをmson文字列にする.
       *@param obj_ オブジェクト
       *@return mson文字列
       *<!-- Accessor -->
       */
      public String mson(Object obj_);
      /**
       * オブジェクトをjson文字列にする.
       *@param obj_ オブジェクト
       *@return json文字列
       *<!-- Accessor -->
       */
      public String json(Object obj_);
      //======  GET/EVAL/DISP
      //--- Accessor.get × 3
      /**
       *  変数Objectを得る.
       *<p>
       *指定名の変数を登録された形のまま取得します。
       *</p>
       *@param value_name_ 値名
       *@return 登録されているObject
       *<!-- Accessor -->
       */
      public Object get(String value_name_);
      /**
       *  変数Objectを型を割り当てて得る.
       *<p>
       *戻り値の型は指定のデフォルト値と同じとなります。<br>
       *記憶されているものと型が異なる場合は再解釈します。<br>
       *記憶されていない場合、または再解釈に失敗した場合はデフォルト値が戻ります。
       *</p>
       *@param value_name_ 値名
       *@param default_value_ デフォルト値
       *@return 登録されているObject
       *<!-- Accessor -->
       */
      public <T> T get(String value_name_,T default_value_);
      /**
       *  変数Objectを型を割り当てて得る.
       *<p>
       *型が異なる場合は再解釈します。
       *</p>
       *@param value_name_ 値名
       *@param class_ 型
       *@return 登録されているObject
       *<!-- Accessor -->
       */
      public <T> T get(String value_name_,Class<T> class_);
      //--- Accessor.eval × 3
      /**
       *  変数を参照しオブジェクトを評価する.
       *<p>
       *変数の入ったオブジェクトを変数展開し$calc要素に関しては四則演算を実行します。
       *<br>
       *四則演算は結合則と括弧をサポートします。<br>
       *Dateオブジェクトに対する数値の加算減算をサポートします。数値はミリ秒となります。<br>
       *DateとDateの減算をサポートします。結果はミリ秒で得られます。
       *</p>
       *@param obj_ 評価したいオブジェクト
       *@return 評価された結果
       *<!-- Accessor -->
       */
      public Object eval(Object obj_);
      /**
       *  変数を参照しオブジェクトを評価する.
       *<p>
       *変数の入ったオブジェクトを変数展開し$calc要素に関しては四則演算を実行します。
       *<br>
       *四則演算は結合則と括弧をサポートします。<br>
       *Dateオブジェクトに対する数値の加算減算をサポートします。数値はミリ秒となります。<br>
       *DateとDateの減算をサポートします。結果はミリ秒で得られます。
       *</p>
       *@param obj_ 評価したいオブジェクト
       *@param default_value_ デフォルト値
       *@return 評価された結果(評価不能の場合はデフォルト値)
       *<!-- Accessor -->
       */
      public <T> T  eval(Object obj_,T default_value_);
      /**
       *  変数を参照しオブジェクトを評価する.
       *<p>
       *変数の入ったオブジェクトを変数展開し$calc要素に関しては四則演算を実行します。
       *<br>
       *四則演算は結合則と括弧をサポートします。<br>
       *Dateオブジェクトに対する数値の加算減算をサポートします。数値はミリ秒となります。<br>
       *DateとDateの減算をサポートします。結果はミリ秒で得られます。
       *</p>
       *@param obj_ 評価したいオブジェクト
       *@param default_value_ デフォルト値
       *@return 評価された結果(評価不能の場合は例外が投げられる)
       *<!-- Accessor -->
       */
      public <T> T  eval(Object obj_,Class<T> class_);
      //--- Accessor.disp × 2
      /**
       *  変数を参照し文字列を展開する.
       *<p>
       *第一層の変数には引用符を付加しません。<br>
       *nullが与えられると空文字列が返ります。
       *</p>
       *@param text_ 変数展開を行いたい文字列
       *@return 変数展開された文字列
       *<!-- Accessor -->
       */
      public String disp(String text_);
      /**
       *  変数を参照し文字列をoption付きで展開する.
       *<p>
       *次のオプションが有効です。
<table>
<tr>
<td>オプション</td>
<td>説明</td>
</tr>
<tr>
<td>hiU.KEEP_QUOTE</td>
<td>textの第一階層の変数が文字列の場合引用符を保持します。</td>
</tr>
<tr>
<td>hiMongo.USE_JSON</td>
<td>変数をJSON展開します。</td>
</tr>
<tr>
<td>hiMongo.USE_str</td>
<td>変数をhiU.str展開します。</td>
</tr>
<tr>
<td>その他hiUオプション</td>
<td>展開のオプションとなります。</td>
</tr>
</table>

       *</p>
       *@param text_ 変数展開を行いたい文字列
       *@return 変数展開された文字列
       *<!-- Accessor -->
       */
      public String disp(String text_,long option_);
      /**
       * 変数をDocumentとして得る.
       *<p>
       *指定名の変数をDocumentとして取得します。<br>
       *複製が返ることもあります。
       *</p>
       *@param value_name_ 値名
       *@return 登録されているObjectのDocument表現
       *<!-- Accessor -->
       */
      public Document getValueAsDocument(String value_name_);
      /**
       * 変数をhiJSON.Probeとして得る.
       */
      /**
       * 変数オブジェクトのProbeを得る.
       *<p>
       *指定名のオブジェクトのProbeを得ます。
       *</p>
       *@param value_name_ 値名
       *@return 登録されているObjectのProbe
       *<!-- Accessor -->
       */
      public hiJSON.Probe getValueAsProbe(String value_name_);
      /**
       * 変数を文字列として得る.
       *<p>
       *変数オブジェクトをmson文字列として得ます。
       *</p>
       *@param value_name_ 値名
       *@return 登録されているObjectのProbe
       *<!-- Accessor -->
       */
      //public String getValueAsString(String value_name_);
      /**
       * DBスコープの特別値(the_value)を取得する.
       *@return 特別値
       */
      public Object get_the_value();
      /**
       * DBスコープの特別値(the_value)をデフォルト値指定で得る.
       *<p>
       *戻り値の型は指定のデフォルト値と同じとなります。<br>
       *nullまたはcast不能の場合はdefault_value_が返ります。
       *</p>
       *@return 引数の型にキャストされた値
       *<!-- Accessor -->
       */
      public <T> T get_the_value(T default_value_);
      /**
       * DBスコープの特別値(the_value)を指定クラス値で得る
       *@return 引数の型にキャストされた値
       *<!-- Accessor -->
       */
      public <T> T get_the_value(Class<T> class_);
      }

   /**
    * DB内レコード範囲設定、リスト取得機構.
    *<p>
    *{@link hi.db.hiMongo.Collection#find(Object,Object)}で得られます。
    *</p>
    *<pre class=quote10>
    *hiMongo.finder _find=hiMongo.use("db01")
    *                            .in("coll_01")
    *                            .find("{}","_id:0");
    *</pre>
    *<p>
    *通常はこのインスタンスを明示することはなく{@link hi.db.hiMongo.Collection#find(Object,Object)}の後カスケード式にメソッドを呼び出します。
    *</p>
    *<pre class=quote10>
    *hiMongo.use("db01")                         // DBが得られる
    *       .in("coll_01")                      //   Collectionが得られる
    *       .find("{}","{_id:0}")                //     Finderが得られる
    *       .sort("{_id:-1}")                    //       Finderのメソッド
    *       .limit(100)                          //       Finderのメソッド
    *       .forEach(Rd->System.out.println(Rd));//       Finderのメソッド
    *</pre>
    *<!-- Finder -->
    */
   public static interface Finder extends Accessor{
      //Finder(Collection collection_);
      /**
       * イテラブル取得.
       *<p>
       *{@link com.mongodb.client.FindIterable FindIterable<Document>}を取得します。
       *</p>
       *<p>
       *注意！driver直叩きとなっていない場合nullが返ります
       *</p>
       *<p>
       *hiMongoが標準では用意していない機能を使うことができます。<br>
       *通常はforThisのラムダ式内で使います。
       *</p>
<pre class=prog10>
hiMongo.use("db01")
       .in("coll_01")
       .find()
       .forThis(Fi->Fi.getIterable().showRecordId(true))
       .forEach(Rd->System.out.println(Rd));
</pre>
       *@return 管理しているFindIterable<Document>またはnull
       *<!-- Finder -->
       */
      public FindIterable<Document> getIterable();
      /**
       * Mson表示オプションon設定.
       *<p>
       *{@link hi.db.hiMongo.Accessor#getMsonList(long) getMsonList()}で得る表示のオプションを変更します。
       *</p>
       *<p>
       *オプション値は{@link otsu.hiNote.hiFieldFormat#option hiFieldFormatオプション}を参照して下さい。
       *</p>
       *<p>
       *getJsonList()で得る表示の変更は{@link #forThis(hiU.ConsumerEx)}と{4link hi.db.hiMongo.Accessor#engineJ() engineJ()}を用い次の様に行います。
       *</p>
       *<pre class=quote10>
       * // Jsonの表示オプション変更
       *db.in("coll_01")
       *  .find("{}","{_id:0"})
       *  .forThis(Fi->Fi.engineJ().str_format().str_option(hiU.WITH_SINGLE_QUOTE))
       *  .forEachJson(Rj->System.out.println(Rj));
       *</pre>
       *@param option_ オプション
       *@return this
       *<!-- Finder -->
       */
      public Finder str_option(long option_);
      /**
       * Mson表示オプションoff設定.
       *<p>
       *getMsonListで得る表示のオプションを変更します。<br>
       *デフォルトのオプションはhiU.JSON_STYLEとhiU.WITH_SINGLE_QUOTEです。<br>
       *オプション値は{@link otsu.hiNote.hiFieldFormat#option hiFieldFormatオプション}を参照して下さい。
       *</p>
       *<pre class=prog10>
       * //例えば次の指定をすれば、引用符がダブルクオートになります
       * .str_disable_option(hiU.WITh_SINGLE_QUOTE)
       *</pre>
       *@param option_ オプション
       *@return this
       *<!-- Finder -->
       */
      public Finder str_disable_option(long option_);
      /**
       * パーズオプションon設定.
       *<p>
       *オプション値は<a class=A1 href="http://www.otsu.co.jp/OtsuLibrary/jdoc/otsu/hiNote/hiJSON.html#option">hiJSONのパーズオプション</a>を参照して下さい。
       *</p>
       *@param option_ オプション
       *@return this
       *<!-- Finder -->
       */
      public Finder with_option(long option_);
      /**
       * パーズオプションoff設定.
       *<p>
       *オプション値は<a class=A1 href="http://www.otsu.co.jp/OtsuLibrary/jdoc/otsu/hiNote/hiJSON.html#option">hiJSONのパーズオプション</a>を参照して下さい。
       *</p>
       *<pre class=prog10>
       * 例えば次の指定をすればクラス割り当て時に過不足フィールドのチェックをしません
       * .without_option(hiU.CHECK_UNKNOWN_FIELD
       *                |hiU.CHECK_UNSET_FIELD)
       *</pre>
       *@param option_ オプション
       *@return this
       *<!-- Finder -->
       */
      public Finder without_option(long option_);
      /**
       * ソート設定.
       *<p>
       *ソート法をJSON文字列で指定します。<br>
       *この段階でソートが行われるのではなく、最終的リスト取得またはforEachアクセス時に反映されます。
       *</p>
       *@param sort_condition_ ソート指定拡張JSON値
       *@return this
       *<!-- Finder -->
       */
      public Finder sort(Object sort_condition_);
      /**
       * 取得数設定.
       *<p>
       *取得レコード数を設定します。<br>
       *指定しない場合全レコードとなります。<br>
       *この段階で指定数のレコードを得る訳ではなく、最終的リスト取得またはforEachアクセス時に反映されます。
       *</p>
       *@param limit_ 取得数
       *@return this
       *<!-- Finder -->
       */
      public Finder limit(int limit_);
      /**
       * skip.
       *@param skip_ スキップ数
       *@return this
       */ // Finder
      public Finder skip(int skip_);
      /**
       * 取得Documentを引数とするラムダ式実行.
       *<p>
       *getDocumentList()で一旦リストを作成しリストのforEachを用いることも可能ですが
       *本メソッドを使用すると、リストを作成することなく、レコードに対し順にアクセスすることができます。
       *</p>
<pre class=prog10>
hiMongo.use("db01")
       .in("coll_01")
       .find("{type:'A'}","{_id:0}")
       .forEachDocument(Rd->System.out.println(Rd));// Document-nodeのtoString()表示
</pre>
       *@param func_ Documentを引数とするラムダ式
       *@return this
       *<!-- Finder -->
       */
      public Finder forEachDocument(hiU.ConsumerEx<Document,Exception> func_);
      /**
       *毎レコード分ラムダ式を実施する.
       *<p>
       *find()で得るレコードリストの一個毎にラムダ式を実施します。
       *</p>
       *<p>
       *forEachスコープの辞書に"#CUR"の名前で取得したレコードのオブジェクトが設定されます。
       *</p>
       *<!-- Finder -->
       */
      public Finder forEach(hiU.ConsumerEx<hiMongo.Finder,Exception> func_);
      /**
       *1レコードにラムダ式を実施する.
       *<p>
       *find()で得る１レコードにラムダ式を実施します。<br>
       *limit(1)が補助的に発行されます。
       *</p>
       *<p>
       *forEachスコープの辞書に"#CUR"の名前で取得したレコードのオブジェクトが設定されます。
       *</p>
       *<!-- Finder -->
       */
      public Finder forOne(hiU.ConsumerEx<hiMongo.Finder,Exception> func_);
      /**
       * Json文字列を引数とするラムダ式実行.
       *<p>
       *getJsonList()で一旦リストを作成しリストのforEachを用いることも可能ですが
       *本メソッドを使用すると、リストを作成することなく、レコードに対し順にアクセスすることができます。
       *</p>
<pre class=prog10>
hiMongo.use("db01")
       .in("coll_01")
       .find("{type:'A'}","{_id:0}")
       .forEachJson(Rj->System.out.println(Rj));// JSON表示
</pre>
       *@param func_ Json文字列を引数とするラムダ式
       *@return this
       *<!-- Finder -->
       */
      public Finder forEachJson(hiU.ConsumerEx<String,Exception> func_);
      /**
       * Msonを引数とするラムダ式実行.
       *<p>
       *getMsonList()で一旦リストを作成しリストのforEachを用いることも可能ですが
       *本メソッドを使用すると、リストを作成することなく、レコードに対し順にアクセスすることができます。<br>
       *(Caller/Workerシステムでは一旦全レコードを取り込みます)
       *</p>
<pre class=prog10>
hiMongo.use("db01")
       .in("coll_01")
       .find("{type:'A'}","{_id:0}")
       .forEachMson(Rm->System.out.println(Rm));// MSON表示
</pre>
       *@param func_ 拡張JSON文字列を引数とするラムダ式
       *@return this
       *<!-- Finder -->
       */
      public Finder forEachMson(hiU.ConsumerEx<String,Exception> func_);
      /**
       * {@link otsu.hiNote.hiJSON.Probe hiJSON.Probe}を引数とするラムダ式実行.
       *<p>
       *getProbeList()で一旦リストを作成しリストのforEachを用いることも可能ですが
       *本メソッドを使用すると、リストを作成することなく、レコードに対し順にアクセスすることができます。<br>
       *(Caller/Workerシステムでは一旦全レコードを取り込みます)
       *</p>
<pre class=prog10>
hiMongo.use("db01")
       .in("coll_01")
       .find("{type:'A'}","{_id:0}")
       .forEachProbe(Rp->System.out.println(Rp.to("type").get());// Probeを用いたノードアクセス
</pre>
       *@param func_ Probeを引数とするラムダ式
       *@return this
       *<!-- Finder -->
       */
      public Finder forEachProbe(hiU.ConsumerEx<hiJSON.Probe,Exception> func_);
      /**
       * クラスインスタンスを引数とするラムダ式実行.
       *<p>
       *getClassList()で一旦リストを作成しリストのforEachを用いることも可能ですが
       *本メソッドを使用すると、リストを作成することなく、レコードに対し順にアクセスすることができます。<br>
       *(Caller/Workerシステムでは一旦全レコードを取り込みます)
       *</p>
<pre class=prog10>
class MyClass{String type;double value;Date date}
----
hiMongo.use("db01")
       .in("coll_01")
       .find("{type:'A'}","{_id:0}")
       .forEachClass(MyClass.class,Rc->System.out.println(Rc.type));// クラスアクセス
</pre>
       *@param func_ クラスインスタンスを引数とするラムダ式
       *@return this
       *<!-- Finder -->
       */
      public <T> Finder forEachClass(Class<T> class_,hiU.ConsumerEx<T,Exception> func_);
      /**
       * レコード要素の値を変数として取り込み繰り返しラムダ式実行.
       *<p>
       *レコード取得を実行し、レコードリストの各要素の中の値を変素としてセットし、ラムダ式を実行します。
       *</p>
       *<p>
       *
       *</p>
       *@param param_ 要素変数定義
       *@param func_ ラムダ式
       *@return this_
       *<!-- Finder -->
       */
      public Finder forEach(String                           param_,
                            hiU.ConsumerEx<Finder,Exception> func_);
      /**
       * コード要素の値を変数として取り込みただ一度だけラムダ式実行.
       *<p>
       *レコード取得を実行し、レコードリストの各要素の中の値を変素としてセットし、ラムダ式を実行します。
       *</p>
       *<p>
       *
       *</p>
       *@param param_ 要素変数定義
       *@param func_ ラムダ式
       *@return this_
       *<!-- Finder -->
       */
      public Finder forOne(String                           param_,
                            hiU.ConsumerEx<Finder,Exception> func_);
      /**
       * レコードリストの各要素を入り口とし参照定義に従ってカーシブにアクセスする.
       *<p>
       *参照定義に従ってリカーシブにレコードにアクセスします。
       *</p>
       *@param refer_ 参照定義
       *@param func_ リカーシブに実行されるラムダ式
       *@return this;
       *<!-- Finder -->
       */
      public Finder forEachRecursive(String                                   refer_
                                ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_);
      /**
       * １レコードのみを入り口とし参照定義に従ってリカーシブにアクセスする.
       *<p>
       *参照定義に従ってリカーシブにレコードにアクセスします。
       *</p>
       *@param refer_ 参照定義
       *@param func_ リカーシブに実行されるラムダ式
       *@return this;
       *<!-- Finder -->
       */
      public Finder forOneRecursive(String                                   refer_
                                ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_);
      /**
       * レコードリストの各要素を入り口とし参照定義に従ってリカーシブにアクセスする.
       *<p>
       *参照定義に従ってリカーシブにレコードにアクセスします。
       *</p>
       *@param refer_ 参照定義
       *@param param_ 変数定義
       *@param func_ リカーシブに実行されるラムダ式
       *@return this;
       *<!-- Finder -->
       */
      public Finder forEachRecursive(String                                   refer_
                                ,String                                   param_
                                ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_);
      /**
       * １レコードのみを入り口とし参照定義に従ってリカーシブにアクセスする.
       *<p>
       *参照定義に従ってリカーシブにレコードにアクセスします。
       *</p>
       *@param refer_ 参照定義
       *@param param_ 変数定義
       *@param func_ リカーシブに実行されるラムダ式
       *@return this;
       *<!-- Finder -->
       */
      public Finder forOneRecursive(String                                   refer_
                                ,String                                   param_
                                ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_);
      /**
       * レコードリストの各要素を入り口とし参照定義に従ってリカーシブにアクセスする.
       *<p>
       *参照定義に従ってリカーシブにレコードにアクセスします。
       *</p>
       *@param refer_ 参照定義
       *@param func_ リカーシブに実行されるラムダ式
       *@param foundEnd_ 途中で情報を返した場合に最後に実行するラムダ式
       *@param notFoundEnd_ 途中で何も返さなかった場合に最後に実行するラムダ式
       *@return this;
       *<!-- Finder -->
       */
      public Finder forEachRecursive(String                                   refer_
                                ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_
                                ,hiU.ConsumerEx<hiMongo.Finder,Exception> foundEnd_
                                ,hiU.ConsumerEx<hiMongo.Finder,Exception> notFoundEnd_);
      /**
       * １レコードのみを入り口とし参照定義に従ってリカーシブにアクセスする.
       *<p>
       *参照定義に従ってリカーシブにレコードにアクセスします。
       *</p>
       *@param refer_ 参照定義
       *@param func_ リカーシブに実行されるラムダ式
       *@param foundEnd_ 途中で情報を返した場合に最後に実行するラムダ式
       *@param notFoundEnd_ 途中で何も返さなかった場合に最後に実行するラムダ式
       *@return this;
       *<!-- Finder -->
       */
      public Finder forOneRecursive(String                                   refer_
                                ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_
                                ,hiU.ConsumerEx<hiMongo.Finder,Exception> foundEnd_
                                ,hiU.ConsumerEx<hiMongo.Finder,Exception> notFoundEnd_);
      /**
       * レコードリストの各要素を入り口とし参照定義に従ってリカーシブにアクセスする.
       *<p>
       *参照定義に従ってリカーシブにレコードにアクセスします。
       *</p>
       *@param refer_ 参照定義
       *@param param_ 変数定義
       *@param func_ リカーシブに実行されるラムダ式
       *@param foundEnd_ 途中で情報を返した場合に最後に実行するラムダ式
       *@param notFoundEnd_ 途中で何も返さなかった場合に最後に実行するラムダ式
       *@return this;
       *<!-- Finder -->
       */
      public Finder forEachRecursive(String                                   refer_
                                ,String                                   param_
                                ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_
                                ,hiU.ConsumerEx<hiMongo.Finder,Exception> foundEnd_
                                ,hiU.ConsumerEx<hiMongo.Finder,Exception> notFoundEnd_);
      /**
       * １レコードのみを入り口とし参照定義に従ってリカーシブにアクセスする.
       *<p>
       *参照定義に従ってリカーシブにレコードにアクセスします。
       *</p>
       *@param refer_ 参照定義
       *@param param_ 変数定義
       *@param func_ リカーシブに実行されるラムダ式
       *@param foundEnd_ 途中で情報を返した場合に最後に実行するラムダ式
       *@param notFoundEnd_ 途中で何も返さなかった場合に最後に実行するラムダ式
       *@return this;
       *<!-- Finder -->
       */
      public Finder forOneRecursive(String                                   refer_
                                ,String                                   param_
                                ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_
                                ,hiU.ConsumerEx<hiMongo.Finder,Exception> foundEnd_
                                ,hiU.ConsumerEx<hiMongo.Finder,Exception> notFoundEnd_);
      /**
       * このFinderに対してラムダ式実行.
       *<p>
       *カスケード式の流れの中でFinderに対する操作を行います。
       *</p>
       *<p>
       *次の例ではprint文の実行、Iteratorの取得などを行いながらカスケード処理をしています。
       *</p>
<pre class=prog10>
hiMongo.DB db= hiMongo.use("db01");
db.in("coll_01")
  .find("{}","{_id:0}")
  .limit(2)
  .forThis(Fi->System.out.println("NO showRecordId"))
  .forEachMson(Rm->System.out.println(Rm))
  .forThis(Fi->System.out.println("WITH showRecordId"))
  .forThis(Fi->F.getIterable().showRecordId(true))
  .forEachMson(Rm->System.out.println(Rm));
＠＠＠ 結果
NO showRecordId
{'type':'A', 'value':12.3, 'date':ISODate('2020-08-17T07:07:00.000Z')}
{'type':'A', 'value':4.56, 'date':ISODate('2020-08-17T07:07:10.000Z')}
WITH showRecordId
{'type':'A', 'value':12.3, 'date':ISODate('2020-08-17T07:07:00.000Z'), '$recordId':1}
{'type':'A', 'value':4.56, 'date':ISODate('2020-08-17T07:07:10.000Z'), '$recordId':2}
</pre>
       *@param func_ Finderを引数とするラムダ式
       *@return this
       *<!-- Finder -->
       */
      public Finder forThis(hiU.ConsumerEx<Finder,Exception> func_);
      /**
       * カレントCollectionで{@link hi.db.hiMongo.Collection#find(Object,Object) find()}実行.
       *@param filterJ_ フィルター
       *@param memberJ_ 取得フィールド指定
       *@return Finder
       *<!-- Finder -->
       */
      public Finder find(Object filterJ_,Object memberJ_);

      /**
       * カレントCollectionで{@link hi.db.hiMongo.Collection#find(Object) find()}実行.
       *@param filterJ_ フィルター
       *@return Finder
       *<!-- Finder -->
       */
      public Finder find(Object filterJ_);
      /**
       * カレントCollectionで{@link hi.db.hiMongo.Collection#find() find()}実行.
       *<!-- Finder -->
       */
      public Finder find();
      /**
       * カレントCollectionで{@link hi.db.hiMongo.Collection#aggregate(Object) aggregate()}実行.
       *@param parm_ パラメタ―
       *@return Aggregator
       *<!-- Finder -->
       */
      public Aggregator aggregate(Object parm_);
      /**
       * カレントCollectionで{@link hi.db.hiMongo.Collection#aggregate() aggregate()}実行.
       *<!-- Finder -->
       */
      public Aggregator aggregate();
      /**
       * Collection切替,カレントDBで{@link hi.db.hiMongo.DB#in(String) in()}実行.
       *@param collectionName_ コレクション名
       *@return Collection
       *<!-- Finder -->
       */
      public Collection in(String collectionName_);
      /**
       * DBを読みリスト化された値を変数(DBスコープ)にセットする.
<pre class=quote10>
  構文:
     { 変数名:'ポジション' }
  ポジション
     '@'           リスト全体
     '数値'        指定番のレコード
     '数値.要素'   指定番のレコードの指定要素
  要素
     名前          指定名の要素 
     数値          配列の指定番の要素 '*'だと全て負の値は後ろからの位置-1:最後
     要素[.要素]   要素の階層
   .readOne("{lastdate:'0.date'}") -> {#lastdate:{$date:13000}} // Date型
</pre>
       *<!-- Finder -->
       */
      public Finder readValueList(Object target_);
      /**
       * DBを読み値1個を変数(DBスコープ)にセットする
       */
      public Finder readOne(Object target_);
      /**
       *オブジェクトを指定名でDBスコープにセットする.
       *@param name_ 変数名
       *@param obj_ オブジェクト
       *@return this
       *<!-- Finder -->
       */
      public Finder setValue(String name_,Object obj_);
      /**
       *指定変数をforEach解析対象とする.
       *<p>
       *指定変数をfind結果の代わりに解析対象とします。
       *</p>
<pre class=quote10>
hiMongo.DB db= hiMongo.use("db01")
db.find("{type:'A'")
  .setValue("#TEMP","[{type:'X',value:1},{type:'Y',value:13},{type:'Z',value:11}]");
  .withValue("#TEMP")
  .forEachMson(Rm->System.out.println(Rm)) //type'X'～'Z'のレコードが表示される
</pre>
       *@param value_name_ 変数名
       *@return this;
       *<!-- Finder -->
       */
      public Finder withValue(String value_name_);
      /**
       * DBスコープに特別値(the_value)をセットする.
       *@param obj_ セットするオブジェクト
       *@return this
       */
      public Finder set_the_value(Object obj_);
      /** カレントCollectionで{@link hi.db.hiMongo.Collection#insertOne(Object...) insertOne()}実行.*/
      public Collection insertOne(Object... records_);
      /** カレントCollectionで{@link hi.db.hiMongo.Collection#insertMany(Object...) insertMany()}実行.*/
      public Collection insertMany(Object... jsonTexts_);
      /** カレントCollectionで{@link hi.db.hiMongo.Collection#updateOne(Object,Object) updateOne()}実行.*/
      public Collection updateOne(Object filterJ_,Object updateJ_);
      /** カレントCollectionで{@link hi.db.hiMongo.Collection#updateMany(Object,Object) updateMany()}実行.*/
      public Collection updateMany(Object filterJ_,Object updateJ_);
      /** カレントCollectionで{@link hi.db.hiMongo.Collection#deleteOne(Object) deleteOne()}実行.*/
      public Collection deleteOne(Object filterJ_);
      /** カレントCollectionで{@link hi.db.hiMongo.Collection#deleteMany(Object) deleteMany()}実行.*/
      public Collection deleteMany(Object filterJ_);
      /** カレントCollectionで{@link hi.db.hiMongo.Collection#replaceOne(Object,Object) replaceOne()}実行.*/
      public Collection replaceOne(Object filterJ_,Object recordJ_);

      } // end Finder
   //======================================================================
   //

   /**
    * 集計器.
    *<p>
    *{@link hi.db.hiMongo.Collection#aggregate(Object)}で得られます。
    *</p>
    *<!-- Aggregator -->
    */
   public static interface Aggregator extends Accessor{
      /**
       * 指定のコレクション用の集計器.
       *<!-- Aggregator -->
       */
      //Aggregator(Collection collection_);
      /**
       * イテラブル取得
       *@return 管理しているAggregateIterable<Document> 
       *<!-- Aggregator -->
       */
      public AggregateIterable<Document> getIterable();
      /**
       * 表示オプションon設定.
       *<p>
       *オプション値は{@link otsu.hiNote.hiFieldFormat#option hiFieldFormatオプション}を参照して下さい。
       *</p>
       *@param option_ オプション
       *@return this
       *<!-- Aggregator -->
       */
      public Aggregator str_option(long option_);
      /**
       * 表示オプションoff設定.
       *<p>
       *オプション値は{@link otsu.hiNote.hiFieldFormat#option hiFieldFormatオプション}を参照して下さい。
       *</p>
       *<pre class=prog10>
       * //例えば次の指定をすれば、引用符がダブルクオートになります
       * .str_disable_option(hiU.WITh_SINGLE_QUOTE)
       *</pre>
       *@param option_ オプション
       *@return this
       *<!-- Aggregator -->
       */
      public Aggregator str_disable_option(long option_);
      /**
       * パーズオプションon設定.
       *<p>
       *オプション値は<a class=A1 href="http://www.otsu.co.jp/OtsuLibrary/jdoc/otsu/hiNote/hiJSON.html#option">hiJSONのパーズオプション</a>を参照してください。
       *</p>
       *@param option_ オプション
       *@return this
       *<!-- Aggregator -->
       */
      public Aggregator with_option(long option_);
      /**
       * パーズオプションoff設定.
       *<p>
       *オプション値は<a class=A1 href="http://www.otsu.co.jp/OtsuLibrary/jdoc/otsu/hiNote/hiJSON.html#option">hiJSONのパーズオプション</a>を参照してください。
       *</p>
       *<pre class=prog10>
       * 例えば次の指定をすればクラス割り当て時に過不足フィールドのチェックをしません
       * .without_option(hiU.CHECK_UNKNOWN_FIELD
       *                |hiU.CHECK_UNSET_FIELD)
       *</pre>
       *@param option_ オプション
       *@return this
       *<!-- Aggregator -->
       */
      public Aggregator without_option(long option_);
      /**
       * match設定.
       *@param arg_ match引数
       *@return this
       *<!-- Aggregator -->
       */
      public Aggregator match(Object arg_);
      /**
       * group設定.
       *@param arg_ group引数
       *@return this
       *<!-- Aggregator -->
       */
      public Aggregator group(Object arg_);
      /**
       * lookup設定.
       *@param arg_ lookup引数
       *@return this
       *<!-- Aggregator -->
       */
      public Aggregator lookup(Object arg_);
      /**
       * project設定.
       *@param arg_ project引数
       *@return this
       *<!-- Aggregator -->
       */
      public Aggregator project(Object arg_);
      /**
       * unwind設定.
       *@param arg_ unwind引数
       *@return this
       *<!-- Aggregator -->
       */
      public Aggregator unwind(Object arg_);
      /**
       * sort設定.
       *@param arg_ sort引数
       *@return this
       *<!-- Aggregator -->
       */
      public Aggregator sort(Object arg_);
      /**
       * limit設定.
       *@param limit_ limit数
       *@return this
       *<!-- Aggregator -->
       */
      public Aggregator limit(int limit_);
      /**
       * 機能unitを追加する.
       *<p>
       *機能unitを追加します。機能は'$'も含めて指定します。(EX "$sort")
       *</p>
       *@param proc_ 機能("$xxx")
       *@param arg_ 引数
       *@return this
       *<!-- Aggregator -->
       */
      public Aggregator add_proc(String proc_,Object arg_);
      /**
       * リストを介することなく、結果を１個ずつラムダ式で得る.
       *@param func_ Document-nodeを引数とするラムダ式
       *@return this
       *<!-- Aggregator -->
       */
      public Aggregator forEachDocument(hiU.ConsumerEx<Document,Exception> func_);
      /** {@link #forEachDocument(hiU.ConsumerEx) forEachDocument(func_)}の別名. */
      //public Aggregator forEach(hiU.ConsumerEx<Document,Exception> func_);
      //public Aggregator forOne(hiU.ConsumerEx<Document,Exception> func_);
      /**
       * リストを介することなく、Json結果を１個ずつラムダ式で得る.
       *@param func_ ラムダ式
       *@return this
       *<!-- Aggregator -->
       */
      public Aggregator forEachJson(hiU.ConsumerEx<String,Exception> func_);
      /**
       * リストを介することなく、Mson結果を１個ずつラムダ式で得る.
       *@param func_ ラムダ式
       *@return this
       *<!-- Aggregator -->
       */
      public Aggregator forEachMson(hiU.ConsumerEx<String,Exception> func_);
      /**
       * リストを介することなく、Probe結果を１個ずつラムダ式で得る.
       *@param func_ ラムダ式
       *@return this
       *<!-- Aggregator -->
       */
      public Aggregator forEachProbe(hiU.ConsumerEx<hiJSON.Probe,Exception> func_);
      /**
       * リストを介することなく、結果を１個ずつ利用者クラスを引数とするラムダ式で得る.
       *@param <T> 利用者クラス
       *@param class_ 利用者クラス
       *@return this
       *<!-- Aggregator -->
       */
      public <T> Aggregator forEachClass(Class<T>                    class_,
                                          hiU.ConsumerEx<T,Exception> func_);
      /** {@link #forEachClass(Class,hiU.ConsumerEx) forEachClass(func_)}の別名. */
//      public <T> Aggregator forEach(Class<T>                    class_,
//                                hiU.ConsumerEx<T,Exception> func_);
      /**
       * この集計器に対してラムダ式実行.
       *@param func_ Aggregatorを引数とするラムダ式
       *@return this
       *<!-- Aggregator -->
       */
      public Aggregator forThis(hiU.ConsumerEx<Aggregator,Exception> func_);
      }

   /**
    * collectionを表す.
    *<p>
    *コレクション(collection)に対応します。
    *</p>
    *<p>
    *{@link hi.db.hiMongo.DB#in(String)}によって得られます。
    *</p>
    *<pre class=quote10>
    *hiMongo.Collection coll=hiMongo.use("db01").in("coll_01");
    *</pre>
    *<p>
    *find,update,replase機能など持ちます。
    *</p>
    *<!-- Collection -->
    */
   public static interface Collection{
      /**
       * 指定DBを設定する.
       *<!-- Collection -->
       */
      /**
       * MongoCollectionを得る.
       *<p>
       *注意:driver直呼びでない場合はnullが返ります
       *</p>
       *@return driverのcollection構造体
       */
      public MongoCollection<Document> getMongoCollection();
      /**
       * with_hsonパーズ用エンジン取得.
       *@return use_hsonの場合現状のエンジン、!use_hsonの場合null
       *<!-- Collection -->
       */
      hiJSON.Engine parse_engine();
      /**
       * 現状の解析/表示エンジンを得る(cloneではない)
       *@return 解析/表示エンジン
       *<!-- Collection -->
       */
      public hiJSON.Engine cur_engine();
      /**
       * 現状のJSON表示エンジンを得る(cloneではない)
       *@return 解析/表示エンジン
       *<!-- Collection -->
       */
      public hiJSON.Engine cur_engineJ();

     /**
       * DBに戻る.
       *@return このコレクションが属するデータベース
       *<!-- Collection -->
       */
      DB back();
      /**
       * この集計器に対してラムダ式実行.
       *@param func_ Collectionを引数とするラムダ式
       *@return this
       *<!-- Collection -->
       */
      public Collection forThis(hiU.ConsumerEx<Collection,Exception> func_);
      /**
       * 検索条件とフィールド指定のfind.
       *<p>
       *このメソッドは条件設定を行います。実際の検索作業はforEachXXX(),getXXXList()時に行われます。
       *</p>
       *<p>
       *引数は文字列({@link java.lang.String}),テキストファイル({@link java.io.File})またはノードツリー(node-Object:{@link org.bson.Document}等)です。
       *</p>
       *@param filterJ_ jsonで条件指定
       *@param memberJ_ jsonで取得フィールド指定
       *@return Finder
       *<!-- Collection -->
       */
      public hiMongo.Finder find(Object filterJ_,Object memberJ_);
      /**
       * 検索条件指定のfind.
       *<p>
       *このメソッドは条件設定を行います。実際の検索作業はforEachXXX(),getXXXList()時に行われます。
       *</p>
       *<p>
       *引数は文字列({@link java.lang.String}),テキストファイル({@link java.io.File})またはノードツリー(node-Object:{@link org.bson.Document}等)です。
       *</p>
       *@param filterJ_ 条件指定
       *@return Finder
       *<!-- Collection -->
       */
      public hiMongo.Finder find(Object filterJ_);
      /**
       * 全件検索のfind.
       *<p>
       *このメソッドは条件設定を行います。実際の検索作業はforEachXXX(),getXXXList()時に行われます。
       *</p>
       *@return Finder
       *<!-- Collection -->
       */
      public hiMongo.Finder find();
      /**
       * 1レコード追加(mongoAPIのinsertOne使用).
       *<p>
       *レコードは拡張JSON、拡張JSONを内容とするテキストFile、拡張JSONの解析済みノードツリー、利用者クラスインスタンスが指定できます。
       *</p>
       *@param records_ レコード（通常mson文字列）。複数可
       *@return this
       *<!-- Collection -->
       */
      public Collection insertOne(Object... records_);
      /**
       * コレクションを削除します.
       *<p>
       * コレクションはDBから削除されますが連続してアクセスは可能です。<br>
       * insertが発行されれば現状の名前のコレクションが新たに生成されレコードが挿入されます。
       *</p>
<pre class=quote10>
hiMongo.use("db01")
       .in("coll_01")
       .drop()                             // コレクションが削除される
       .insertOne("{type:'A',value:1000}");// コレクションが生成され、レコードが追加される
</pre>
       *@return this
       *<!-- Collection -->
       */
      public Collection drop();
      /**
       * レコード数を得る.
       *@return レコード数
       *<!-- Collection -->
       */
      public long count();
      /**
       * 条件に合致するレコード数を得る.
       *<p>
       *findに与える条件と同等の条件を与え、該当レコードの個数を得ることが出来ます。
<pre class=prog10>
   hiMongo.DB db= hiMongo.use("testDB");
   System.out.println("count of type B|C = "+
        db.in("coll_01").count("{$or:[{type:'B'},{type:'C'}]}"));
</pre>
       *</p>
       *@param filterJ_ 条件
       *@return レコード数
       *<!-- Collection -->
       */
      public long count(Object filterJ_);
      /**
       * リスト形式文字列で複数レコードを追加する.
       *<p>
       *レコードの単純並び形式の文字列でレコードを追加します。<br>
       *記述はJSONの配列形式("[{...},{...}...]")の形です。一個だけの記述("{...}")も許されます<br>
       *nsertMany()に配列または複数引数を与えることも可能です。
       *</p>
       *<p>
       *レコードは拡張JSON、拡張JSONを内容とするテキストFile、拡張JSONの解析済みノードツリー、利用者クラスインスタンスのリストで指定します。<br>
       *一つのリストの中に複数の形式が混ざることは許されません。
       *</p>
<pre class=prog10>
   String _records=
        "[{type:'A',value:12.3,date:ISODate('2020-08-17T07:07:00.000Z')}"+
        ",{type:'A',value:4.56,date:ISODate('2020-08-17T07:07:10.000Z')}"+
        ",{type:'B',value:2001,date:ISODate('2020-08-17T07:07:20.000Z')}]";
   String _record2=
        "{type:'A',value:7.89,date:ISODate('2020-08-17T07:07:30.000Z')}";
   hiMongo.DB          db   =hiMongo.use("db02");
   hiMongo.Collection  _coll=db.in("coll_01");
   _coll.insertMany(_records,_records2); // 複数の文字列をセット可能
   db.close();
</pre>
       *<p>
       *コメントが入ったり配列形式でない形でレコードが並んでいる記述のファイルを読むことも出来ます。
<pre class=prog10>
＝＝＝ ファイル data.hson
  // 複数レコード記述が単純に並んでいる
  {type:'A',value:12.3,date:ISODate('2020-08-17T07:07:00.000Z')}
  {type:'A',value:/{@literal *}4.56{@literal *}/3.33,date:ISODate('2020-08-17T07:07:10.000Z')} // ブロックコメント
  {type:'B',value:2001,date:ISODate('2020-08-17T07:07:20.000Z')} // コメント
  {type:'A',value:7.89,date:ISODate('2020-08-17T07:07:30.000Z')}
＝＝＝ プログラム
   hiMongo.DB db= hiMongo.use("db01");
   db.in("coll_01").drop()
     .with_hson()
     .insertMany(new File("data.hson"));
</pre>
       *</p>
       *@param jsonTexts_ json記述
       *@return this
       *<!-- Collection -->
       */
      public Collection insertMany(Object... jsonTexts_);
      /**
       * aggregate(集計).
       *<p>
       *集計手続きを設定します。<br>
       *集計作業は集計器で行います。
       *</p>
       *@param proc_ 集計手続き [{...},{...}...]
       *@return 集計器
       *<!-- Collection -->
       */
      public Aggregator aggregate(Object proc_);

      /**
       * aggregate(集計).
       *<p>
       *集計手続き無で集計器を作成します。<br>
       *集計手続きは集計器の{@link hi.db.hiMongo.Aggregator#match(Object) match()メソッド}などで追加出来ます。
       *</p>
       *@return 集計器
       *<!-- Collection -->
       */
      public Aggregator aggregate();

      /**
       * 1レコードupdateする.
       *<pre class=quote10>
hiMongo.DB db=hiMongo.use("db01");
db.in("coll_01")
  .updateOne("{$and:[{type:'A'},{value:4.56}]}",
             "{$set:{value:0.15}}");
--- 対応するmongo-shell記述
use db01
db.coll_01.updateOne({$and:[{type:'A'},{value:4.56}]},
                    {$set:{value:0.15}});
       *</pre>
<p>
単純な値の置き換えを行う$setの他に値の増減を行う$incなども用意されています。
(<a class=A1 href=
"https://docs.mongodb.com/manual/reference/operator/update/"
>更新の演算子</a>参照)
</p>
       *@param filterJ_ 条件
       *@param updateJ_ 置き換えフィールド指定
       *@return this
       *<!-- Collection -->
       */
      public Collection updateOne(Object filterJ_,Object updateJ_);
      /**
       * 条件に合うレコードを全てupdateする.
       *<p>
       *引数は文字列({@link java.lang.String}),テキストファイル({@link java.io.File})またはノードツリー(node-Object:{@link org.bson.Document}等)です。
       *</p>
       *<pre class=quote10>
hiMongo.DB db=hiMongo.use("db01");
db.in("coll_01")
  .updateMany("{$and:[{type:'A'},{value:{$lt:1.00}}]}}",
              "{$set:{value:1.00}}")
--- 対応するmongo-shell記述
use db01
db.coll_01.updateOne({$and:[{type:'A'},{value:{$lt:1.00}}]},
                     {$set:{value:1.00}});
       *</pre>
       *@param filterJ_ 条件(String,File,node)
       *@param updateJ_ 置き換えフィールド指定(String,File,node)
       *@return this
       *<!-- Collection -->
       */
      public Collection updateMany(Object filterJ_,Object updateJ_);
      /**
       * １レコード置換する.
       *<p>
       *条件に合致するレコードを１個だけ置き換えます。
       *</p>
       *<p>
       *レコードは拡張JSON、拡張JSONを内容とするテキストFile、拡張JSONの解析済みノードツリー、利用者クラスインスタンスで指定します。
       *</p>
       *@param filterJ_ 条件
       *@param recordJ_ 新規レコード内容
       *@return this
       *<!-- Collection -->
       */
      public Collection replaceOne(Object filterJ_,Object recordJ_);
      /**
       * １レコード削除.
       *<p>
       *条件に合致するレコードを１個だけ削除。
       *</p>
       *@param filterJ_ 条件
       *@return this
       *<!-- Collection -->
       */
      public Collection deleteOne(Object filterJ_);
      /**
       * 複数レコード削除.
       *<p>
       *条件に合致するレコードを１個だけ削除。
       *</p>
       *@param filterJ_ 条件
       *@return this
       *<!-- Collection -->
       */
      public Collection deleteMany(Object filterJ_);
      /**
       * Index設定.
       *<p>
       *指定フィールドにindexを付加します。
       *</p>
       *<p>
       *keyset_にはフィールド名と昇順(-1)、降順(1)の指定を行います。昇順/降順の違いは気にする必要はほぼ有りません。
       *</p>
       *<p>
       *option_にはJSONデータで{}{unique:true}{expireAfter:秒数}を設定します。<br>
       *{unique:true}を設定すると、そのフィールドはユニーク値を持つものとなり、同値の別レコードのinsertはエラーとなります。<br>
       *{expireAfter:秒数}を設定すると、指定秒数を超えたレコードは削除されます。<br>
       *{expireAfterMinutes:分},{expireAfterHours:時},{expireAfterDays:日}の指定も可能です。
       *{@link com.mongodb.client.model.IndexOptions}を直接置くことも出来ます。
       *</p>
       *@param keyset_ フィールドと昇順降順指定のセット
       *@param option_ {}{unique:true}{expireAfter:秒数}
       *@return this
       *<!-- Collection -->
       */
      public Collection createIndex(Object keyset_,Object option_);
      /**
       * Index設定.
       *@param keyset_ キーと昇順降順指定のセット
       *@return this
       *<!-- Collection -->
       */
      public Collection createIndex(Object keyset_);
      /**
       * 全index消去.
       *@return this
       *<!-- Collection -->
       */
      public Collection dropIndexes();
      /**
       * index消去
       *@return this
       *<!-- Collection -->
       */
      public Collection dropIndex(String ... index_);
      /**
       * index情報のリストを得る.
       *@return リスト
       *<!-- Collection -->
       */
      public ArrayList<Document> getIndexList();
      /**
       * カレントDBでin実行(コレクションの切り替え).
       *@param collectionName_ コレクション名
       *@return コレクション
       *<!-- Collection -->
       */
      public Collection in(String collectionName_);
      /**
       * Collection名を得る
       *@return collection名
       */
      public String name();

      /**
       *指定変数をforEach解析対象とする.
       *<p>
       *指定変数をfind結果の代わりに解析対象とします。
       *</p>
<pre class=quote10>
hiMongo.DB db= hiMongo.use("db01")
db.setValue("#TEMP","[{type:'X',value:1},{type:'Y',value:13},{type:'Z',value:11}]");
  .withValue("#TEMP")
  .forEachMson(Rm->System.out.println(Rm)) //type'X'～'Z'のレコードが表示される
</pre>
       *@param value_name_ 変数名
       *@return this;
       *<!-- Collection -->
       */
      public Finder withValue(String value_name_);
      /**{@link hi.db.hiMongo.Finder#setValue(String,Object)}参照 */
      public Collection setValue(String name_,Object obj_);
      /**{@link hi.db.hiMongo.Accessor#disp(String)}参照 */

      //======  GET/EVAL/DISP
      // Collection.get × 3
      /**{@link hi.db.hiMongo.Accessor#get(String)参照 */
      public Object get(String value_name_);
      /**{@link hi.db.hiMongo.Accessor#get(String,Object) Accessor#eval(String,T)}参照 */
      public <T> T get(String value_name_,T default_value_);
      /**{@link hi.db.hiMongo.Accessor#get(String,Class)参照 */
      public <T> T get(String value_name_,Class<T> class_);
      // Collection.eval × 3
      /**{@link hi.db.hiMongo.Accessor#eval(Object)参照 */
      public Object eval(Object obj_);
      /**{@link hi.db.hiMongo.Accessor#eval(Object,Object) Accessor#eval(Object,T)}参照 */
      public <T> T eval(Object obj_,T default_value_);
      /**{@link hi.db.hiMongo.Accessor#eval(Object,Class)}参照 */
      public <T> T eval(Object obj_,Class<T> class_);
      // Collection.disp × 2
      /**{@link hi.db.hiMongo.Accessor#disp(String)}参照 */
      public String disp(String text_);
      /**{@link hi.db.hiMongo.Accessor#disp(String,long)}参照 */
      public String disp(String text_,long option_);
      //=========== Collection SET/GET THE VALUE
      /**{@link hi.db.hiMongo.Accessor#set_the_value(Object)}参照 */
      public Collection set_the_value(Object obj_);
      /**{@link hi.db.hiMongo.Accessor#get_the_value()}参照 */
      public Object get_the_value();
      /**{@link hi.db.hiMongo.Accessor#get_the_value(Object)}参照 */
      public <T> T get_the_value(T default_value_);
      /**{@link hi.db.hiMongo.Accessor#get_the_value(Class)}参照 */
      public <T> T get_the_value(Class<T> default_value_);
      //============
      /**{@link hi.db.hiMongo.Accessor#getValueAsDocument(String)}参照 */
      public Document getValueAsDocument(String text_);
      /**{@link hi.db.hiMongo.Accessor#getValueAsProbe(String)}参照 */
      public hiJSON.Probe getValueAsProbe(String text_);
      /**{@link hi.db.hiMongo.Accessor#set_the_value(String)}参照 */

      }

   /**
    * database.
    *<p>
    *dataBase(Collectionの集合）を表します。
    *</p>
    *<p>
    *{@link hi.db.hiMongo#use(String)}または{@link hi.db.hiMongo.Client#use(String)}で得られます。
    *</p>
    *<p>
    *close()は用意されていますが通常は呼ぶ必要はありません。
    *</p>
    *<!-- DB -->
    */
   public static interface DB extends Closeable{
      /**
       * client(DBサーバとの接続)とデータベース名で構築する.
       *@param client_ client_
       *@param dbName_ データベース名
       *<!-- DB -->
       */
      //DB(hiMongo.Client client_,String dbName_);
      /**
       * コレクションを指定する.
       *@param collectionName_ コレクション名
       *@return コレクション
       *<!-- DB -->
       */
      public hiMongo.Collection in(String collectionName_);
      /**
       * コレクションを指定する.
       *@param collectionName_ コレクション名
       *@return コレクション
       *<!-- DB -->
       */
      //public hiMongo.Collection get(String collectionName_);
      /**
       * client(サーバとの接続)を閉じる:通常は呼ばない.
       *<p>
       *特別な理由により、プロセスは生きたままDBの資源だけを完全開放したい場合にのみ使用します。<br>
       *例えば起動時に初期化用としてDBを読み込むだけといった場合です。<br>
       *通常は呼ぶ必要はありません。
       *</p>
       *<!-- DB -->
       */
      public void close();
      /**
       * コレクション名一覧を得る.
       *@param sort_ ソートを行うか
       *@return コレクション名一覧
       *<!-- DB -->
       */
      public ArrayList<String> show_collections(boolean sort_);
      /**
       * DB内容を消去する.
       *@return this
       *<!-- DB -->
       */
      public DB drop();
      /**
       * コレクションが存在するか調べる.
       *@return true:存在する、false:存在しない
       *<!-- DB -->
       */
       public boolean exists(String collectionName_);
      /**
       * Cap指定でCollectionを生成する.
       *<p>
       *Cap(最大容量)指定でCollectionを作成します。
       *</p>
       *<p>
       *最大バイト長(size)、最大レコード数(records)、強制(force)を第２引数にJSON文字列で指定します。<br>
       *すでにコレクションが存在している場合、forceがfalseなら何もしません。<br>
       *forceがtrueの場合空のコレクションを作成します。
       *</p>
       *<pre class=quote10>
       * db.createCappedCollection(
       *   "coll_01",
       *   "{size:5242880,records:5000,force:true}"
       *   );
       *</pre>
       *@param name_ コレクション名
       *@param capInfo_ キャップ指定
       *@return Collection
       *<!-- DB -->
       */
      public Collection createCappedCollection(String name_,String capInfo_);
      /**
       * DB名を得る
       *@return DB名
       *<!-- DB -->
       */
      public String name();
      /**{@link hi.db.hiMongo.Finder#setValue(String,Object)}参照 */
      public DB setValue(String name_,Object obj_);
      /**{@link hi.db.hiMongo.Accessor#disp(String)}参照 */
      //======  GET/EVAL/DISP
      //--- DB.get × 3
      public Object get(String value_name_);
      /**{@link hi.db.hiMongo.Accessor#get(String,Object)}参照 */
      public <T> T get(String value_name_,T default_value_);
      /**{@link hi.db.hiMongo.Accessor#get(String,Class)}参照 */
      public <T> T get(String value_name_,Class<T> class_);
      //--- DB.eval × 3
      /**{@link hi.db.hiMongo.Accessor#eval(Object)}参照 */
      public Object eval(Object obj_);
      /**{@link hi.db.hiMongo.Accessor#eval(Object,Object)}参照 */
      public <T> T eval(Object obj_,T default_value_);
      /**{@link hi.db.hiMongo.Accessor#eval(Object,Class)}参照 */
      public <T> T eval(Object obj_,Class<T> class_);
      //--- DB.disp × 2
      /**{@link hi.db.hiMongo.Accessor#disp(String)}参照 */
      public String disp(String text_);
      /**{@link hi.db.hiMongo.Accessor#disp(String,long)}参照 */
      public String disp(String text_,long option_);
      //=========== DBn SET/GET THE VALUE
      /**{@link hi.db.hiMongo.Accessor#set_the_value(Object)}参照 */
      public DB set_the_value(Object obj_);
      /**{@link hi.db.hiMongo.Accessor#get_the_value()}参照 */
      public Object get_the_value();
      /**{@link hi.db.hiMongo.Accessor#get_the_value(Object)}参照 */
      public <T> T get_the_value(T default_value_);
      /**{@link hi.db.hiMongo.Accessor#get_the_value(Class)}参照 */
      public <T> T get_the_value(Class<T> default_value_);
      //=====
      /**{@link hi.db.hiMongo.Accessor#getValueAsDocument(String)}参照 */
      public Document getValueAsDocument(String text_);
      /**{@link hi.db.hiMongo.Accessor#getValueAsProbe(String)}参照 */
      public hiJSON.Probe getValueAsProbe(String text_);
      /**{@link hi.db.hiMongo.Accessor#getValueAsString(String)}参照 */
      //public String getValueAsString(String text_);
      /**
       * このDBに対してラムダ式実行.
       *@param func_ DBを引数とするラムダ式
       *@return this
       *<!-- DB -->
       */
      public DB forThis(hiU.ConsumerEx<DB,Exception> func_);
      }
  static class CapInfo{
     long    size;
     long    records;
     boolean force;
     }

   /**
    * mongoDBサーバとの接続を表す.
    *<p>
    *{@link hi.db.hiMongo#connect(Object)}により生成されます。
    *</p>
    *<p>
    *close()は用意されていますが通常は呼ぶ必要はありません。。
    *</p>
    *<!-- Client -->
    */
   public static interface Client extends Closeable{
      /**
       * デフォルトの接続(localhost.27017).
       *<!-- Client -->
       */
      hiMongo.Client connect();
      /**
       * リモート接続指定.
       *<p>
       *サーバと接続します。<br>
       *通常は{@link hi.db.hiMongo#connect(Object remote_)}を使用します
       *</p>
       *@param info_ リモート情報
       *@return 接続されたhiMongo.Client
       *<!-- Client -->
       */
      hiMongo.Client connect(Object info_);
      /**
       * 切断する:通常は呼ばない.
       *<p>
       *特別な理由により、プロセスは生きたままDBの資源だけを完全開放したい場合にのみ使用します。<br>
       *例えば起動時に初期化用としてDBを読み込むだけといった場合です。<br>
       *通常は呼ぶ必要はありません。
       *</p>
       *<!-- Client -->
       */
      public void close();
      /**
       * データベース名一覧を得る.
       *@param sort_ ソートを行うか
       *@return データベース名一覧
       *<!-- Client -->
       */
      public ArrayList<String> show_dbs(boolean sort_);
      /**
       * databaseを得る.
       *@param dbName_ データベース名
       *@return データベース
       *<!-- Client -->
       */
      public DB use(String dbName_);
      }// end Client

   /** 
    * 接続先情報(connect引数).
    *<p>
    *リモートDBと接続するための
    *{@link #connect(Object) hiMongo.connect()}
    *に与える引数です。
    *</p>
<pre class=quote10>
hiMongo.RemoteInfo _info=new hiMongo.RemoteInfo();
_info.host    = "192.168.1.139";
_info.port    = 27017;
_info.dbName  = "testDB";
_info.user    = "testUser";
_info.password= "xxx";
DB db=hiMongo.connect(_info)
             .use("db01");
db.in("coll_01").find()...
</pre>
<p>
リモート側（サーバ側）での設定に関しては<a class=A1 href="hiMongo.html#remote">remote接続</a>を参照してください。
</p>
    *<!-- hiMongo -->
    */
   public static class RemoteInfo {
      /** ホスト名,ip-addr */
      public String host;
      /** ポート番号 */
      public int    port=27017;
      /** ユーザ名 */
      public String user;
      /** ユーザ定義のあるデータベース名 実際にデータを入れるデータベースとは別でよい */
      public String dbName;
      /** パスワード */
      public String password;
      /** 一致比較 */
      // @Override
      public boolean equals(RemoteInfo that_){
         try{
            if( !host.equals(that_.host) )         return false;
            if( port!=that_.port )                 return false;
            if( !user.equals(that_.user) )         return false;
            if( !dbName.equals(that_.dbName) )     return false;
            if( !password.equals(that_.password) ) return false;
            }
         catch(Exception _ex){
            return false;
            }
         return true;
         }
      }

   }
