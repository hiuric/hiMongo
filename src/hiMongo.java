package hi;

import otsu.hiNote.*;
import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.model.CreateCollectionOptions;
 
//import org.springframework.data.mongodb.core.CollectionOptions;
//import com.mongodb.core.CollectionOptions;
//import com.odi.coll.CollectionOptions;

import org.bson.types.Decimal128;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bson.BsonRegularExpression;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.lang.reflect.Constructor;

// 以下、ただただ、LOG退治用
//import java.util.logging.Logger;
//import java.util.logging.Level;
//import ch.qos.logback.classic.Level;
//import ch.qos.logback.classic.LoggerContext;
//import ch.qos.logback.core.ContextBase;

//import org.slf4j.helpers.NOPLogger;
//import org.slf4j.impl.SimpleLogger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;
//import org.slf4j.Level;
/**
mongoDBアクセス機.

<pre class=quote10 style="background-color:black;color:#eeffff">

 // JAVAプログラム
 hiMongo.DB db=hiMongo.use("db01");   // database   選択
 db.get("coll_01")                    // collection 選択
   .find("{type:'A'}","{_id:0}")      // typeが'A'のレコード
   .sort("{_id:-1}")                  // _idで逆向きにソート
   .limit(3)                          // 個数制限
   .getJsonList(hiU.REVERSE)          // 反転したリスト取得
   .forEach(S->System.out.println(S));// レコード表示

</pre>
<p>
hiMongoはドキュメント指向のDataBaseであるmongoDBにアクセスするjavaライブラリです。<br>
hiMongoはmongo-java-driverのラッパーです。
</p>
<ul>
<li><a class=A1 href="#api">JavaでmongoDB;hiMongo-APIの基本</a></li>
<li><a class=A1 href="#find">find(検索)の引数と結果</a>
   <ul>
   <li><a class=A1 href="#class">利用者定義クラス・インスタンスを得る</a>
   <li><a class=A1 href="#probe">汎用データ探索機hiJSON.Probeで受け取る</a>
   </ul>
</li>
<li><a class=A1 href="#insert">insert/update/replace/delete/drop</a>
   <ul>
   <li><a class=A1 href="#insertOne">insert,drop(条件無)</a>
   <li><a class=A1 href="#update">条件付き操作(update,replace,delete)</a>
   <li><a class=A1 href="#cap">キャップ（最大容量)指定</a>
   </ul>
</li>
<li><a class=A1 href="#aggre">aggregate(集計）の引数と結果</a></li>
<li><a class=A1 href="#lookup">aggregate(集計）lookupによるフィールド結合</a></li>
<li><a class=A1 href="#mson">mongoDB拡張JSON(mson)記述</a>
   <ul>
   <li><a class=A1 href="#quote">引用符</a>
   <li><a class=A1 href="#date">ObjectId,Dateと数値</a>
   <!--<li><a class=A1 href="#regex">正規表現</a>-->
   <li><a class=A1 href="#comment">コメント等のhiMongo拡張(hson)</a>
   </ul>
</li>
<li><a class=A1 href="#remote">リモート接続</a></li>
<li><a class=A1 href="#driver">driver-APIを使う</a>
   <ul>
   <li><a class=A1 href="#createIndex">例：createIndex</a>
   </ul>
</li>
<li><a class=A1 href="#node">node(Object,Document)の取り扱い</a></li>
<li><a class=A1 href="#build">ビルド</a></li>
<li><a class=A1 href="#log">ライブラリの出すログ</a></li>
<li><a class=A1 href="#interface">API</a></li>
</ul>
<p>


<table style="line-height:90%" class=t0>
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
<tr><td>
hiJSON
</td><td>:hiMongoが使用している
{@link otsu.hiNote.hiJSON JSONハンドリング機構}
；
<a class=A1 target="_blank" rel="noopener noreferrer" href=
"http://www.otsu.co.jp/OtsuLibrary"
><i>Otsu</i>ラリブラリ
</a>
/
<a class=A1 target="_blank" rel="noopener noreferrer" href=
"http://www.otsu.co.jp/OtsuLibrary/jdoc/index.html"
>hiNoteパッケージ
</a>
に属するクラスです。
<td></tr>
</table>
</p>
<p class=B1 id="api">
&emsp;JavaでmongoDB;hiMongo-APIの基本
</p>
<p>
次のプログラムはJava用のmongoDBライブラリhiMongoの使用例です。
</p>
<pre class=quote10>
 // JAVAプログラム
 hiMongo.DB db=hiMongo.use("db01");   // database   選択
 db.get("coll_01")                    // collection 選択
   .find("{type:'A'}","{_id:0}")      // typeが'A'のレコード
   .sort("{_id:-1}")                  // _idで逆向きにソート
   .limit(3)                          // 個数制限
   .getJsonList(hiU.REVERSE)          // 反転したリスト取得
   .forEach(S->System.out.println(S));// レコード表示
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
    forEach(S=>print(JSON.stringify(S)));// 要素表示
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
import hi.hiMongo;
import otsu.hiNote.*;
public class Test {
   public static void main(String[] args_){
      try(hiMongo.DB db=hiMongo.use("db01")){  // database   'db01'選択
         db.get("coll_01")                     // collection 選択
            .find("{type:'A'}","{_id:0}")      // typeが'A'のレコード,
            .sort("{_id:-1}")                  // _idで逆向きにソート
            .limit(3)                          // 個数制限
            .getMsonList(hiU.REVERSE)          // 反転したリスト取得
            .forEach(S->System.out.println(S)) // レコード表示
            ;
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
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
条件やレコード内容などの主要パラメタはmson(mongoDBの拡張JSON)で与えます。
</p>
<p>
レコードの挿入では次の様にレコード内容を拡張JSON(mson)で与えます。
</p>
<pre class=quote10>
 // JAVAプログラム
 hiMongo.DB db=hiMongo.use("db01"));  // database   選択
 db.get("coll_01")                    // collection 選択
   .insertOne("{type:'A',value:21,date:{$date:1597648050000}}");
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

<p>
次のようなAPIが用意されており、冒頭のようなカスケード式で呼び出すことも各型の変数で結果を受けて改めて呼び出すことも出来ます
</p>
<pre class=quote10>
hiMongo#<span class=red>use</span>                    => DB
   DB#<span class=red>get</span>                      => Collection
      Collection#<span class=red>find</span>          => Finder
         Finder#<span class=red>sort</span>           => Finder
         Finder#<span class=red>limit</span>          => Finder
         Finder#<span class=red>skip</span>           => Finder
         Finder#<span class=red>forEachXXX</span>     => Finder
         Finder#<span class=blue>getXXXList</span>     => ArrayList<XXX>
      Collection#<span class=red>aggregate</span>     => Aggregator
         Aggregator#<span class=red>forEachXXX</span> => Aggregator
         Aggregator#<span class=blue>getXXXList</span> => ArrayList<XXX>
      Collection#<span class=red>insertOne</span>     => Collection
      Collection#<span class=red>insertMany</span>    => Collection
      Collection#<span class=red>updateOne</span>     => Collection
      Collection#<span class=red>updateMany</span>    => Collection
      Collection#<span class=red>replaceOne</span>    => Collection
      Collection#<span class=red>deleteOne</span>     => Collection
      Collection#<span class=red>deleteMany</span>    => Collection
</pre>



<p class=B1 id=find>
&emsp;find(検索)の引数と結果
</p>
<p>
findは２つの引数を持ちます。検索条件と取得フィールドです。<br>
</p>
<p class=c>
&emsp;検索条件
</p>
<p>
検索条件には次の様な記述が出来ます。
</p>

<p>
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
<tr>
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
<tr>
<td>条件の組み合わせ</td>
<td>find("{$and:[{name:'A'},{value:{$gt:100}}]}")
<pre class=quot8>
形式:
{$and:[{条件},{条件},...]}
{$or:[{条件},{条件},...]}
</pre>

</td>
</tr>
<tr>
<td>部分一致、正規表現</td>
<td>
find("{name:/et/}") nameがetを含む<br> 
find("{name:/^T.*sky$/}") nameがTで始まりskyで終わる<br>
find("{name:/(ba|ab)}") nameがbaまたはabを含む
</td>
</tr>
</table>
</p>


<p class=c>
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
<p class=c>
&emsp;結果の取得
</p>
<p>
findの戻りは{@link hi.hiMongo.Finder hiMongo.Finder}で、hiMongo.Finderは{@link hi.hiMongo.Finder#sort(Object) sort()},{@link hi.hiMongo.Finder#limit(int) limit()}など幾つかの手順を経て最終的にはgetXXXList()メソッドで結果をArrayListにして戻します。
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
<td>{@link org.bson.Document}</td><td>getNodeList</td>
<td>
mongoDBの原始的なデータノードです。<br>
基本的にはJSONに対応するObjectのツリーですが、StringやMapなどの基本タイプの他{@link org.bson.types.ObjectId}クラス,{@link java.util.Date}クラスを持ちます。
toJson()メソッドでJSON文字列を得ることができますが、日本語はutfコードに文字化けすることがあります。<br>
ObjectIdは{"@oid":"....."}、Dateは{"@date":unixエポック}の形で得られます。
</td>
</tr>

<tr>
<td>クラスインスタンス</td><td>getClassList</td>
<td>
利用者クラスにマップしたデータが得られます。<br>
最も間違いが起こりにくい方法です。
</td>
</tr>

<tr>
<td>hiJSON.Probe</td><td>getProbeList</td>
<td>
データノードを探索機です。
</td>
</tr>


<tr>
<td>String(mson)</td><td>getMsonList</td>
<td>
mongoの拡張JSON形の文字列。<br>
ObjectId("..."),ISODate("...")が用いられます。
</td>
</tr>


<tr>
<td>String(json)</td><td>getJsonList</td>
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
<p>
hiMongo.Finderが持つforEachXXX()メソッドを用いればリストを構築することなく、各要素毎得ることが出来ます。
</p>


<p class=B2_2 id="class">
&emsp;利用者定義クラス・インスタンスを得る
</p>
<p>
findの結果を利用者定義のクラス・インスタンスのリストとして得ることが出来ます。
</p>
<pre class=quote10>
<span class=red>class Record</span> {   // レコード内容
   String type;
   double value;
   Date   date;
   }
-----
double _start_date=取得開始レコードのunixエポック
ArrayList&lt;<span class=red>Record</span>> _recs
=db.get("coll_01")
   .find("{$and:["+
            "{type:'A'},"+
            "{date:{$gte:{$date:"+_start_date+"}}}"+
            "]}",
         "{_id:0}")
   .<span class=blue>getClassList</span>(<span class=red>Record.class</span>);
<span class=gray>for(Record _rec:_recs){
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
import hi.hiMongo;
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
      try(hiMongo.DB db=hiMongo.use("db01")){
         //----------------------------------------------
         // 最後の'A'レコードの時刻(unix-epoch)を得る
         long _last_date
         =db.get("coll_01")
            .find("{type:'A'}","{_id:0,date:1}")
            .sort("{_id:-1}").limit(1)
            .getClassList(WithDate.class)
            .get(0).date.getTime();
         //----------------------------------------------
         // 最後のレコードの30秒前からの'A'レコード取得
         long _start_date= _last_date-30000; // 30秒前
         System.out.println("last="+_last_date+" start="+_start_date);
         ArrayList&lt;Record> _recs
         =db.get("coll_01")
            .find("{$and:["+
                        "{type:'A'},"+
                         "{date:{$gte:{$date:"+_start_date+"}}}"+
                          "]}",
                  "{_id:0}")
            .getClassList(Record.class);
         System.out.println("records="+hiU.str(_recs,hiU.WITH_INDENT));
         //----------------------------------------------
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
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
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
<p><input type="button" value="フルコードを隠す△" style="WIDTH:12em"
   onClick="document.getElementById('divClass_2').style.display='none';
            document.getElementById('divClass_1').style.display='block';
            document.location='#divClass_1'"></p>
</div>


<p class=B1_2 id=probe>
&emsp;汎用データ探索機{@link otsu.hiNote.hiJSON.Probe hiJSON.Probe}で受け取る
</p>
<p>
型を特定せず、{@link otsu.hiNote.hiJSON.Probe hiJSON.Probe}で受け取り、辞書アクセス、配列アクセスにより値を得ることができます。<br>
要素へのアクセスは文字列による名前での指定となります。
</p>
<pre class=quote10>
double _start_date=取得開始レコードのunixエポック
ArrayList&lt;hiJSON.Node> _recs
=db.get("coll_01")
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
import hi.hiMongo;
import otsu.hiNote.*;
import java.util.*;
public class Test {
   public static void main(String[] args_){
      try(hiMongo.DB db=hiMongo.use("db01")){
         //----------------------------------------------
         // 最後の'A'レコードの時刻(unix-epoch)を得る
         long _last_date
         =db.get("coll_01")
            .find("{type:'A'}","{_id:0,date:1}")
            .sort("{_id:-1}").limit(1)
            .getProbeList()
            .get(0).at("date").get(D->((Date)D).getTime());
         //----------------------------------------------
         // 最後のレコードの30秒前からの'A'レコード取得
         long _start_date= _last_date-30000; // 30秒前
         System.out.println("last="+_last_date+" start="+_start_date);
         ArrayList&lt;hiJSON.Probe> _recs
                 =db.get("coll_01")
                    .find("{$and:["+
                              "{type:'A'},"+
                              "{date:{$gte:{$date:"+_start_date+"}}}"+
                               "]}",
                           "{_id:0}")
                    .getProbeList();
         //----------------------------------------------
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
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
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

<p class=B1 id="insert">
&emsp;insert/update/replace/delete/drop
</p>
<p>
レコードのinsertOne,insertMany,updateOne,updateMany,replaceOne,deleteOne,deleteMany,dropなどの操作も用意されています。
</p>
<p class=B1_2 id="insertOne">
&emsp;insert,drop(条件無)
</p>
<p>
insertOneの引数は１レコードの拡張JSON形式で、insertManyの引数はJSONの配列型です。<br>
それらを記述したテキストファイル、あるいはそれらの解析されたノードツリーも許されます。<br>
条件文は付きません。
</p>
<p>
次の様な使い方になります。get("コレクション")はその時点で存在しないコレクションに対して行っても構いません。なお{@link hi.hiMongo#date() hiMongo.date()}は現在時の標準JSON表現を得る関数です。
</p>
<pre class=quote10>
hiMongo.DB db =hiMongo.use("db01");
=db.get("coll_01")
   .insertOne(
     " {type:'A',value:12.3,date:"+hiMongo.date()+"}");
</pre>
<p>
dropはレコードの削除のみならずコレクションそのものの削除ですが、insertを繋げることにより、元の名前のコレクションが再び作成され空の状態でのレコード追加となります。
</p>
<pre class=quote10>
hiMongo.DB db =hiMongo.use("db01");
db.get("coll_01")
  .drop()
  .insertMany("["+
     " {type:'A',value:4.56,date:"+hiMongo.date()+"}"+
     ",{type:'B',value:2001,date:"+hiMongo.date()+"}"+
     ",{type:'A',value:7.89,date:"+hiMongo.date()+"}"+
     ",{type:'A',value:0.12,date:"+hiMongo.date()+"}]");
</pre>

<p class=B1_2 id="update">
&emsp;条件付き操作(delete,update,replace)
</p>
<p>
delete,update,replaceには対象となるレコードを選択する条件が付きます。
</p>
<p>
deleteでは条件に合致したレコードが削除されます。<br>
deleteOneでは登録の古いレコードが検索され、合致した一個のみが削除されます。<br>
deleteMabyでは合致するレコードが全て削除されます。
</p>
<pre class=quote10>
hiMongo.DB         db =hiMongo.use("db01");
hiMongo.Collection col=db.get("coll_01");
// --- deleteOne
coll.deleteOne("{type:'B'}");

// --- deleteMany
coll.deleteMany("{$and:[{type:'A'},{value:{$lt:8}}]}");
</pre>
<p>
updateは{$set:{フィールド名:値}}でフィールドの値の変更を指定します。<br>
複数フィールドの置き換えはフィールド名の値のセットをカンマで繋ぎます{$set:{フィールド1:値1,フィールド2:値2,...}<br>
存在しないフィールド名を指定するとフィールドの追加となります。
</p>


<pre class=quote10>
// --- updateOne
coll.updateOne("{$and:[{type:'B'},{value:{$gt:5}}]}",
               "{$set:{value:4.32}}");

// --- updateMany
coll.updateMany("{$and:[{type:'A'},{value:{$lt:5}}]}",
                "{$set:{value:3.21}}");
</pre>
<p>
replaceはレコード全体の置き換えになります。<br>
レコード全体を引数とします。
</p>
<pre class=quote10>
// --- replaceOne
coll.replaceOne("{$and:[{type:'A'},{value:{$lt:5}}]}",
                "{type:'B',value:6543,date:"+hiMongo.date()+"}");

</pre>

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
import hi.hiMongo;
import otsu.hiNote.*;
import java.util.*;
public class Test {
   public static void main(String[] args_){
      try(hiMongo.DB db =hiMongo.use("db01")){
         hiMongo.Collection  _coll=db.get("coll_01").drop();
         System.out.println("--- insertOne/insertMany");
         _coll.insertOne(
              " {type:'A',value:12.3,date:"+hiMongo.date()+"}");
         _coll.insertMany("["+
              " {type:'A',value:4.56,date:"+hiMongo.date()+"}"+
              ",{type:'B',value:2001,date:"+hiMongo.date()+"}"+
              ",{type:'A',value:7.89,date:"+hiMongo.date()+"}"+
              ",{type:'A',value:0.12,date:"+hiMongo.date()+"}]");
         _coll.find("{}","{_id:0}").forEach(R->System.out.println(R));

         System.out.println("--- updateOne");
         _coll.updateOne("{$and:[{type:'B'},{value:{$gt:5}}]}",
                         "{$set:{value:4.32}}");
         _coll.find("{}","{_id:0,date:0}").forEach(R->System.out.println(R));

         System.out.println("--- updateMany");
         _coll.updateMany("{$and:[{type:'A'},{value:{$lt:5}}]}",
                         "{$set:{value:3.21}}");
         _coll.find("{}","{_id:0,date:0}").forEach(R->System.out.println(R));

         System.out.println("--- replaceOne");
         _coll.replaceOne("{$and:[{type:'A'},{value:{$lt:5}}]}",
                         "{type:'B',value:6543,date:"+hiMongo.date()+"}");
         _coll.find("{}","{_id:0,date:0}").forEach(R->System.out.println(R));

         System.out.println("--- deleteOne");
         _coll.deleteOne("{type:'B'}");
         _coll.find("{}","{_id:0,date:0}").forEach(R->System.out.println(R));

         System.out.println("--- deleteMany");
         _coll.deleteMany("{$and:[{type:'A'},{value:{$lt:8}}]}");
         _coll.find("{}","{_id:0,date:0}").forEach(R->System.out.println(R));
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
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

<p class=B1_2 id=cap>
&emsp;キャップ（最大容量)指定
</p>
<p>
hiMongol.DBの{@link hi.hiMongo.DB#createCappedCollection(String,String) createCappedCollection()}メソッドで、容量制限付きのコレクションを作成できます。<br>
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
<pre class=quote10>
import hi.hiMongo;
public class Test {
   public static void main(String[] args_){
      try(hiMongo.DB db=hiMongo.use("db01")){ 
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
            .forEachMson(M->System.out.println(M));
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
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

<p class=B1 id=aggre>
&emsp;aggregate(集計）の引数と結果
</p>
<p>
aggregateで集計を得ることができます。<br>
結果は利用者クラスで受け取ることができます。
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
=db.get("coll_01")
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
import hi.hiMongo;
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
      try(hiMongo.DB db=hiMongo.use("db01")){
         long _start_date
         = db.get("coll_01")
              .find("{type:'A'}","{_id:0,date:1}")
              .sort("{_id:-1}").limit(1).getClassList(WithDate.class)
              .get(0).date.getTime()-30000;
         Arec _r
         = db.get("coll_01")
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
         System.out.println(String.format("min=%.2f max=%.2f avg=%.2f"
                                          ,_r.min,_r.max,_r.avg));
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
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






<p class=B1 id="lookup">
&emsp;aggregate(集計）lookupによるフィールド結合
</p>
<p>
aggregateの$lookupを用いれば異なるコレクションのレコード・フィールドを結合することができます。<br>
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
ArrayList&lt;A_Rec> _recs
=db.get("店舗商品")
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
// 東京あるいは福岡の店舗にある商品名と個数を得ています。
// 商品名は店舗商品コレクションの"商品id"で商品コレクションを連結し
// 商品名を得ています。
import hi.hiMongo;
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
      try(hiMongo.DB db=hiMongo.use("sampleDB")){
         ArrayList&lt;A_Rec&gt; _recs=
         db.get("店舗商品").aggregate("["+
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
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
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
db.get("XX").find("{\"name\":\"A\"}")// 標準
db.get("XX").find("{name:\"A\"}")// 通常フィールド名引用符省略可
db.get("XX").find("{name:'A'}")// シングルクオート可
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
<table class=t>

<tr>
<td>型</td>
<td>bson(内部値)</td>
<td>mson表現</td>
<td>json表現</td>
<td>stringify表現</td>
</tr>

<tr>
<td>ObjectId</td>
<td>ObjectId</td>
<td>ObjectId("...")</td>
<td>{$oid:"..."}</td>
<td>{$oid:"..."}</td>
</tr>

<tr>
<td>Date</td>
<td>Date</td>
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
<td>BigDecimal</td>
<td>Decimal128</td>
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
<p>
<table class=t>

<tr>
<td>型</td>
<td>Object(内部値)</td>
<td>mson表現</td>
<td>json表現</td>
</tr>

<tr>
<td>ObjectId</td>
<td>ObjectId</td>
<td>ObjectId("...")</td>
<td>{$oid:"..."}</td>
</tr>

<tr>
<td>Date</td>
<td>Date</td>
<td>ISODate("...")
</td>
<td>{$date:unixEpoch}</td>

</tr>

<tr>
<td>int</td>
<td>Long</td>
<td>数値<br>
NumberInt("...")可</td>
<td>{$numberLong:数値}</td>
</tr>

<tr>
<td>long</td>
<td>Long</td>
<td>数値<br>
NumberLong("...")可</td>
<td>{$numberLong:数値}</td>
</tr>

<tr>
<td>double</td>
<td>Double</td>
<td>数値</td>
<td>数値</td>
</tr>


<tr>
<td>BigDecimal</td>
<td>BigDecimal</td>
<td>NumberDecimal("...")<br>
数値入力可
</td>
<td>{$numberDecimal:"..."}</td>
</td>
</tr>

</table>

</table>
</p>
<p>
hiMongoのパーザには桁数制限は有りません。hiMongoのパーズでは17桁までの整数はLong、仮数15桁まで指数15以下浮動小数はDouble、それ以外はBigDecimalとなります。BigDecimalはbsonになる時点でNumberDecimalとなります。<br>
hiMongoのBigDecimal表示をNumberDecimal形式にしてあるのは数値ではbsonパーザが取り扱えないからです。
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
import hi.hiMongo;
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
      System.out.println("-- hi.hiMongo");
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
-- hi.hiMongo
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
db.get("XX").find({name:/BC/i}).... 文字列BC(小文字可)を含む
db.get("XX").find({name:/^B.*n$/}).... Bで始まりnで終わる
db.get("XX").find({name:/(ra|re)/}).... 'ra'または're'を含む
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
db.get("composer").drop()
  <span class=red>.with_hson()</span>
  .insertMany(<span class=blue>_records_json</span>);
</pre>
<p>
ファイル入力の場合Stringに落とすことなくFileを直接指定することも可能です。
</p>
<pre class=quote10>
//-- with_hson()を発行した上でFile指定
hiMongo.DB db=hiMongo.use("db01");
db.get("composer").drop()
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
db.get("composer").drop()
  .insertMany(<span class=green>_recodes_node</span>);
</pre>



<p class=B1 id="remote">
&emsp;リモート接続
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
DB db=hiMongo.connect("{"+
                      "host:'192.168.1.139',"+
                      "port:27017,"+
                      "dbName:'<span class=red>testDB</span>',"+
                      "user:'<span class=blue>testUser</span>',"+
                      "password:'<span class=green>xxx</span>'"+
                      "}")
              .use("<span class=purple>db01</span>");// 利用者が使うDBはdb01
db.get("coll_01").find()...
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
DB db=hiMongo.connect(_info)
             .use("<span class=purple>db01</span>");// 利用者が使うDBはdb01
db.get("coll_01").find()...
</pre>

<p class=B1 id="driver">
&emsp;driver-APIを使う
</p>
<p>
hiMongoの各クラスが持つmongodb-java-driverの要素にアクセスすることが出来ます。
</p>
<table class=t>
<tr><td>driverのクラス/インターフェース</td><td>hiMongo要素</td></tr>
<tr>
<td>
{@link com.mongodb.MongoClient}
</td>
<td>
{@link hi.hiMongo.Client#mongoClient}
</td>
</tr>

<tr>
<td>
{@link com.mongodb.client.MongoDatabase}
</td>
<td>
{@link hi.hiMongo.DB#mongoDatabase}
</td>
</tr>

<tr>
<td>
{@link com.mongodb.client.MongoCollection MongoCollection&lt;TDocument&gt;}
</td>
<td>
{@link hiMongo.Collection#mongoCollection}
</td>
</tr>

<tr>
<td>
{@link com.mongodb.client.FindIterable FindIterable&lt;TResult&gt;}
</td>
<td>
{@link hi.hiMongo.Finder#getIterator()}
</td>
</tr>

<tr>
<td>
{@link com.mongodb.client.AggregateIterable AggregateIterable&lt;TResult&gt;}
</td>
<td>
{@link hi.hiMongo.Aggregrator#getIterator()}
</td>
</tr>

</table>



<p class=c id=createIndex>
&emsp;例：MongoCollection&lt;TDocument&gt;.createIndex
</p>
<p>
例えばcreateIndexはhiMongo(0.05)では用意していませんが、hiMongo.CollectionのmongoCollectionを参照し次のようにして実施することが出来ます。
</p>
<pre class=quote10>
import hi.hiMongo;
import org.bson.Document;
import com.mongodb.client.model.IndexOptions;
public class Test {
   public static void main(String[] args_){
      try(hiMongo.DB db=hiMongo.use("sampleDB")){
         <span class=gray>System.out.println("--- befor creteIndex");
         for(Document doc:db.get("商品").mongoCollection.listIndexes()){System.out.println(doc);}</span>
         <b>db.get("商品").mongoCollection.createIndex(Document.parse("{商品id:1}"),
                                                   new IndexOptions().unique(true));</b>
         <span class=gray>System.out.println("--- after creteIndex");
         for(Document doc:db.get("商品").mongoCollection.listIndexes()){System.out.println(doc);}</span>
         //...
         }
      }
   }
</pre>

<p class=c id=createIndex>
&emsp;例：FindIterable&lt;TResult&gt;.showRecordId
</p>
<p>
カスケードAPIの流れの中に組み込むにはforThisを用います。forThisはthisをラムダ式に与えて処理後、thisを返しますので、カスケードAPIを連続させることが出来ます。
</p>
<pre class=quote10>
<span class=gray>db.get("coll_01")
  .find("{type:'A'}")
  .sort("{_id:-1}")</span>
  <span class=gray>.limit(3)
  <b>.forThis(T->T.getIterator().showRecordId(true))</b>
  .forEachJson(J->System.out.println(J));</span>
</pre>

<p class=B1 id="node">
&emsp;node(Object,Document)の取り扱いx
</p>
<div id="divNode_1">
<p><input type="button" value="説明を表示する" style="WIDTH:10em"
   onClick="document.getElementById('divNode_2').style.display='block';
            document.getElementById('divNode_1').style.display='none'"></p>
</div>
<div id="divNode_2" style="display:none">
<p><input type="button" value="説明を隠す" style="WIDTH:10em"
   onClick="document.getElementById('divNode_2').style.display='none';
            document.getElementById('divNode_1').style.display='block'"></p>
<p>
hiMongoではクエリー情報やレコード情報は拡張JSON文字列で与えることを基本とします。
</p>
<p>
これらはプログラムで手続き的に作成されたノードオブジェクトの形で与えることも可能です。<br>
Document.parse(xxx)またはhiMongo.parse(xxx).asNode()で得ることも出来ます。
</p>
<p>
ノードオブジェクトは辞書(Map)、リスト（List)、文字列他単純値で構成されます。<br>
{@link org.bson.Document Document}はHashMap<String,Object>の派生でappendなどノード構成用のメソッドが追加されています。
</p>
<p>
例えば{type:'A'}をfindに与えるコードは次のように書くことができます。
</p>
<pre class=quote10>
HashMap<String,Object> filter=new HashMap<>();
filter.put("type","A");
hiMongo.use("db01").get("coll_01").find(filter)
       .forEach(D->System.out.prinln(D));
</pre>
<p>
少し複雑な条件だとHashMapで書くのは困難になります。{@link org.bson.Document Document}のappendを用いれば一応記述は可能です。<br>
例えば{$and[{type:'A'},{value:{lt:1}}]}をfindに与えるコードは次のように書くことができます。
</p>
<pre class=quote10>
import {@link org.bson.Document org.bson.Document};
-------
Document filter
=new Document()
     .append("$and",
         Arrays.asList(
            new Document()
               .append("type","A"),
            new Document()
               .append("value",
                   new Document()
                      .append("$lt",1))
            )
      );
hiMongo.use("db01").get("coll_01").find(filter)
       .forEach(D->System.out.prinln(D));
</pre>
<p>
これでも記述としては煩雑過ぎるのでmongo-java-driverには、JSONのノードの形からは完全に逸脱した
{@link org.bson.conversions.Bson Bson}というクラスが用意されています。<br>
次の様に使う事ができます。
</p>
<pre class=quote10>
import {@link org.bson.conversions.Bson org.bson.conversions.Bson};
import {@link com.mongodb.client.model.Filters com.mongodb.client.model.Filters};
-------
Bson filter   // Documentではない！
=Filters.and(
    Filters.eq("type","A"),
    Filters.lt("value",1)
    );
</pre>
<p>
Documentは受け付けるけどBsonは受け付けないなどmongo-json-driverのAPIに混乱が見られますので、hiMongoでは受け付けないようにしています。
</p>
<p>
Documentと(Filtersが返す)Bsonの内部形式は次の様になっています。
</p>
<pre class=quote10>
//---- Document (辞書構成となっています)
filter=(Document){
   (String)"$and"=(Arrays$ArrayList)[
      (Document){
         (String)"type"=(String)"A"},
      (Document){
         (String)"value"=(Document){
            (String)"$lt"=(Integer)1}}]}

//---- Bson (辞書ではなく専用クラスが割り当てられています）
filter=(Filters$AndFilter){
   filters=(Arrays$ArrayList)[
      (Filters$SimpleEncodingFilter){
         fieldName=(String)"type",
         value=(String)"A"},
      (Filters$OperatorFilter){
         operatorName=(String)"$lt",
         fieldName=(String)"value",
         value=(Integer)1}]}
</pre>
<p>
driverのAPIでBsonを受け付けるものはDocumentを受け付けますので,仮にdriver-APIを直に呼び出す場合でも,無理せず<br>
&emsp;Document.parse("{$and[{type:'A'},{value:{lt:1}}]}")<br>
とすることをお勧めします。
</p>


<!--
ttps://api.mongodb.com/java/3.0/?org/bson/conversions/Bson.html
-->
<p><input type="button" value="説明を隠す△" style="WIDTH:10em"
   onClick="document.getElementById('divNode_2').style.display='none';
            document.getElementById('divNode_1').style.display='block';
            document.location='#divNode_1'"></p>
</div>


<p class=B1 id="build">
&emsp;ビルド
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
hiMongoを動かすには次のjarをリンクする必要があります。(バージョン番号は変わるかも知れません）
</p>
<table class=t0>
<tr>
<td>hiMongo_0_05.jar</td>
<td>:  hiMongo本体</td>
</tr>
<tr>
<td>hiNote_3_09.jar</td>
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
hiMongoLIB=hiMongo_0_05.jar
hiNoteLIB=hiNote_3_09.jar
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




<p class=B1 id="log">
&emsp;ライブラリの出すログ
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
<p>
ライブラリが出すライブラリの為のログはライブラリの利用者にとって極めて邪魔なものです。
</p>
<p>
mongoDBはDBのopen/closeに関するログをコンソールに出します。
</p>
<p>
この停止法はまだ分かっていません。
</p>
<p><input type="button" value="説明を隠す△" style="WIDTH:10em"
   onClick="document.getElementById('divLog_2').style.display='none';
            document.getElementById('divLog_1').style.display='block';
            document.location='#divLog_1'"></p>
</div>
<p class=B2 id="interface"></p>
 */
public class hiMongo {
   final static boolean D=false;// デバグフラグ（開発時用）
   //===========================================
   // 解析/表示エンジン設定
   //===========================================
   private hiMongo(){} // hiMongoは生成できない
   static hiJSON.Engine mson_engine;
   static hiJSON.Engine json_engine;
   static boolean use_hson;
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
           .str_class_disp(Decimal128.class,MongoConverter::toDecStr);
      json_engine= hiJSON.engine();
      json_engine
        .str_format()
           .str_class_disp(Date.class,      MongoConverter::toDateMapStr)
           .str_class_disp(ObjectId.class,  MongoConverter::toOidMapStr)
           .str_class_disp(BigDecimal.class,MongoConverter::toDecMapStr)
           .str_class_disp(Decimal128.class,MongoConverter::toDecMapStr);
      }
   /**
    * Mongoのログを止める(未).
    * 残念ながらログを止めることはまだできていません。
    * logbak.xml記述も効果はありませんでした。
    */ // hiMongo
   public final static void nolog(){
      /*
      //---------------------------------------------------------------------------------
      //System.setProperty("org.slf4j.simpleLogger.log.defaultLogLevel","ERROR" );
      //->効果なし

      //---------------------------------------------------------------------------------
      //NOPLogger logger=(NOPLogger)LoggerFactory.getLogger("org.mongodb.driver.cluster");
      //->SimpleLogger cannot be cast to org.slf4j.helpers.NOPLogger

      //---------------------------------------------------------------------------------
      //SimpleLogger logger = (SimpleLogger) LoggerFactory.getLogger("org.mongodb.driver.cluster");
      //logger.setLevel(Level.OFF);
      //->コンパイルエラー symbol setLevel(Level)not found

      //---------------------------------------------------------------------------------
      //org.slf4j.Logger logger = (org.slf4j.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
      //logger.setLevel(Level.INFO);
      //->コンパイルエラー symbol setLevel(Level)not found

      //---------------------------------------------------------------------------------
      java.util.logging.Logger.getLogger("org.mongodb").setLevel(java.util.logging.Level.SEVERE);
      //->効果なし

      //---------------------------------------------------------------------------------
      java.util.logging.Logger.getLogger("JULLogger").setLevel(java.util.logging.Level.SEVERE);
      //->効果なし

      //---------------------------------------------------------------------------------
      //System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY,"ERROR" );
      //->コンパイルエラー symbol DEFAULT_LOG_LEVEL_KEY not found

      //---------------------------------------------------------------------------------
      java.util.logging.Logger.getLogger("org.mongodb.driver.cluster").setLevel(java.util.logging.Level.SEVERE);
      //->効果なし

      //---------------------------------------------------------------------------------
      java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(java.util.logging.Level.SEVERE);
      //->効果なし

      //---------------------------------------------------------------------------------
      java.util.logging.Logger.getLogger("org.mongodb").setLevel(java.util.logging.Level.SEVERE);
      //->効果なし

      //---------------------------------------------------------------------------------
      //LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
      //->SimpleLoggerFactory cannot be cast to LoggerContext
      //loggerContext.getLogger("org.mongodb.driver").setLevel(Level.ERROR);
      */
      }
   /**
    * mongoDBのログを止める（未）.
    */
   public static void nolog(Class<?> class_){
      /*
      org.slf4j.Logger logger=(Logger) LoggerFactory.getLogger(class_);
      ch.qos.logback.classic.Logger log = (ch.qos.logback.classic.Logger)logger;
      //-> SimpleLogger cannot be cast to ch.qos.logback.classic.Logger
      log.setLevel(Level.INFO);
      */
      }
   //static public SimpleLogger logger;
   /**
    * 文字列解析にbson/msonでなくhiMongoのhsonを使う.
    *<p>
    * 拡張JSON文字列解析には標準ではorg.bson.Documentが用いられます。<br>
    * 操作に先立ちstaticメソッドであるhiMongo.with_hson(true)を指定すると以降はhiMongoの解析機構hsonが用いられます。<br>
    * hsonではコメントが許されるほか[]で囲まない配列や18桁を超える10進数値をBigDecimalとして解釈することなどが出来ます。
    *</p>
    *<p>
    *このメソッドは全体に影響を与えます。
    *{@link hi.hiMongo.Collection#with_hson(boolean) db.get("xx").with_hson()...}
    *を用いれば、それぞれの場所での指定となります。
    *</p>
    *@param use_hson_ true:hsonを用いる、false:hsonを用いない
    */
   public static void with_hson(boolean use_hson_){
      use_hson=use_hson_;
      }
   /**
    * パーズするテキストを指定.
    *<p>
    *本メソッドの後ろにパーズ形式を指定することでパーズが実行されます。
    *</p>
    *<pre class=prog10>
    *  String  _json_text="....";
    *  MyClass _data= hiMongo.parseText(_json_text).asClass(MyClass.class);
    *</pre>
    *@param text_ テキスト
    *@return 解析エンジン
    */ // hiMongo
   public static hiJSON.Engine parseText(String text_){
      return mson_engine.clone().parseText(text_);
      } 
   /**
    * パーズするテキストを指定.
    *@param text_ テキスト
    *@return 解析エンジン
    */ // hiMongo
   public static hiJSON.Engine parse(String text_){
      return mson_engine.clone().parseText(text_);
      }
   /**
    * パーズするFileを指定.
    *@param textFile_ テキストファイル
    *@return 解析エンジン
    */ // hiMongo
   public static hiJSON.Engine parse(File textFile_){
      return mson_engine.clone().parse(hiFile.readTextAll(textFile_));
      }
   /**
    * パーズするノードツリーObjectを指定.
    *<p>
    *本メソッドの後ろにパーズ形式を指定することでパーズが実行されます。<br>
    *他のツール(org.bson.Documentなど）で解析された結果のObjectもパーズできます。
    *</p>
    *<pre class=prog10>
    *  String   _json_text="....";
    *  Document _doc = Document.parse(_json_text);
    *  MyClass  _data= hiMongo.parseNode(_doc).asClass(MyClass.class);
    *</pre>
    *@param obj_ ノードツリーObject
    *@return 解析エンジン
    */ // hiMongo
   public static hiJSON.Engine parseNode(Object obj_){
      return mson_engine.clone().parseNode(obj_);
      }
   /**
    * パーズするテキストファイルを指定.
    *<p>
    *本メソッドの後ろにパーズ形式を指定することでパーズが実行されます。
    *</p>
    *<pre class=prog10>
    *  String  _json_file="./json.txt";
    *  MyClass _data= hiMongo.parseFile(_json_file).asClass(MyClass.class);
    *</pre>
    *@param fileName_ テキストファイル名
    *@return 解析エンジン
    */ // hiMongo
   public static hiJSON.Engine parseFile(String fileName_){
      return mson_engine.clone().parseFile(fileName_);
      }
   /**
    * mongo拡張JSON表記を得る.
    *nodeツリー上にObjectID,Dateクラスインスタンスが有る場合ObjectId(),ISODate()の形で表示します。
    *@param obj_ nodeオブジェクト
    *@return 拡張JSON表記
    */ // hiMongo
   public static String str(Object obj_){
      return hiU.str(obj_);
      }
   /**
    * mongo拡張JSON表記を得る.
    *nodeツリー上にObjectID,Dateクラスインスタンスが有る場合ObjectId(),ISODate()の形で表示します。
    *@param obj_ nodeオブジェクト
    *@param option_ 表示オプション (hiU.WITH_INDENTなど)
    *@return 拡張JSON表記
    */ // hiMongo
   public static String str(Object obj_,long option_){
      return hiU.str(obj_,option_);
      }
   /**
    * mongo拡張JSON表記を得る.
    *nodeツリー上にObjectID,Dateクラスインスタンスが有る場合ObjectId(),ISODate()の形で表示します。
    *@param obj_ nodeオブジェクト
    *@return 拡張JSON表記
    */ // hiMongo
   public static String mson(Object obj_){
      return mson_engine.str(obj_);
      }
   /**
    * mongo拡張JSON表記を得る.
    *nodeツリー上にObjectID,Dateクラスインスタンスが有る場合ObjectId(),ISODate()の形で表示します。
    *@param obj_ nodeオブジェクト
    *@param option_ 表示オプション (hiU.WITH_INDENTなど)
    *@return 拡張JSON表記
    */ // hiMongo
   public static String mson(Object obj_,long option_){
      return mson_engine.str(obj_,option_);
      }
   /**
    * 標準JSON表記を得る.
    *nodeツリー上にObjectID,Dateクラスインスタンスが有る場合{"$oid":"..."},{"$date":unixEpoch}で表示されます
    *@param obj_ nodeオブジェクト
    *@return 拡張JSON表記
    */ // hiMongo
   public static String json(Object obj_){
      return json_engine.str(obj_);
      }
   /**
    * 標準JSON表記を得る.
    *nodeツリー上にObjectID,Dateクラスインスタンスが有る場合{"$oid":"..."},{"$date":unixEpoch}で表示されます
    *@param obj_ nodeオブジェクト
    *@return 拡張JSON表記
    */ // hiMongo
   public static String json(Object obj_,long option_){
      return json_engine.str(obj_,option_);
      }
   /**
    * JSON解析/表示エンジンのクローンを取得.
    *mongoDB用設定が追加されたJSON解析/表示エンジンを取得します。
    *@return エンジン
    */ // hiMongo
   public static hiJSON.Engine engine(){
      return mson_engine.clone();
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
    */ // hiMongo
   public static String date(){
      return "{$date:"+new Date().getTime()+"}";
      }
   //org.bson.Documentを用いて[...]形式をパーズする.
   @SuppressWarnings("unchecked")
   private static List<Document> parseAsDocumentList(Object data_,hiJSON.Engine engine_){
      if( data_ instanceof List ){
         ArrayList<Document> _ret=new ArrayList<>();
         List<Object>   _objs=(List<Object>)data_;
         for(Object _obj:_objs){
            _ret.add(new Document((Map<String,Object>)_obj));
            }
         return _ret;
         }
      if( data_ instanceof File ){
         data_ = hiFile.readTextAll((File)data_);
         }
      if( data_ instanceof String ){
         if( engine_!=null ){
            List<Object> _objs=engine_.parseText((String)data_)
                                      .asNodeList();
            ArrayList<Document> _ret=new ArrayList<>();
            for(Object _obj:_objs){
               _ret.add(new Document((Map<String,Object>)_obj));
               }
            return _ret;
            }
         return (List<Document>) Document.parse("{'L':"+((String)data_)+"}").get("L");// h済み
         }
      throw new hiException("illegal document class "+data_.getClass().getName());
      }
   // StringまたはMapオブジェクトからDocumentを得る
   // hiMongo
   @SuppressWarnings("unchecked")
   private static Document objToDoc(Object data_,hiJSON.Engine engine_){
      if( data_ instanceof Map ){
         return new Document((Map<String,Object>)data_);
         }
      if( data_ instanceof File ){
         data_ = hiFile.readTextAll((File)data_);
         }
      if( data_ instanceof String ){
         if( engine_!=null ){
            return new Document(engine_.parseText((String)data_).asMap());
            }
         return Document.parse((String)data_);// h済み
         }
      throw new hiException("illegal document class "+data_.getClass().getName());
      }
   // StringまたはMapオブジェクトから作成されるDocumentを
   // 指定名の要素として持つDocumentを得る
   // hiMongo
   @SuppressWarnings("unchecked")
   private static Document namedObjToDoc(String name_,Object data_,hiJSON.Engine engine_){
      if( data_ instanceof Map ){
         return new Document(name_,new Document((Map<String,Object>)data_));
         }
      if( data_ instanceof File ){
         data_ = hiFile.readTextAll((File)data_);
         }
      if( data_ instanceof String ){
         if( engine_!=null ){
            new Document(name_
                        ,new Document(engine_.parseText((String)data_)
                                             .asMap()));
            }
         return new Document(name_,Document.parse((String)data_));
         }
      throw new hiException("illegal document class "+data_.getClass().getName());
      }
   /**
    * レコードアクセス機(Finder,Aggregatorのベース)
    */
   public static class Accessor{
      MongoIterable<Document> records;
      //
      hiJSON.Engine                  msonEngine;
      hiJSON.Engine                  jsonEngine;
      Collection                     collection;
      Accessor(Collection collection_){
         collection= collection_;
         }
      /**
       * Collectionに戻る.
       *@return コレクション
       */ // Accessor
       public Collection back(){
         return collection;
         }
      /**
       * JSON用の表示エンジンを取得.
       *@return json用表示エンジン
       */ // Accessor
      final public hiJSON.Engine engineJ(){
         if( this.jsonEngine== null ){
            this.jsonEngine=hiMongo.json_engine.clone();
            }
         return this.jsonEngine;
         }

      /**
       * 現状の解析/表示エンジンを得る(cloneではない)
       *<p>変更を加えてはなりません</p>
       *@return 解析/表示エンジン
       */ // Accessor
      final public hiJSON.Engine cur_engine(){
         if( this.msonEngine== null ){
            return collection.cur_engine();
            }
         return this.msonEngine;
         }
      /**
       * 現状の解析/表示エンジンを得る(cloneではない)
       *<p>変更を加えてはなりません</p>
       *@return json用表示エンジン
       */ // Accessor
      final public hiJSON.Engine cur_engineJ(){
         if( this.jsonEngine== null ){
            return collection.cur_engineJ();
            }
         return this.jsonEngine;
         }
      /**
       * パーズ用エンジンを得る.
       *定義はCollectionにあります。
       *@return nullの場合はDocument
       */ // Accessor
      final public hiJSON.Engine parse_engine(){
         return collection.parse_engine();
         }

      /**
       * 解析済みnodeを再解釈する
       *@param node_ ノードツリー
       *@return 解析エンジン
       */     // Accessor
      protected hiJSON.Engine parseNode(Object node_){
         if( msonEngine==null ) return hiMongo.mson_engine.parseNode(node_);
         return msonEngine.parseNode(node_);
         }
      /**
       * 解析済みnodeを再解釈する
       *@param node_ ノードツリー
       *@return 解析エンジン
       */     // Accessor
      protected <T> T toClass(Class<T> class_,Document node_){
         return parseNode(node_).asClass(class_);
         }
      // Accessor
      protected hiJSON.Probe toProbe(Document node_){
         return hiJSON.probe(parseNode(node_).asNode());
         }
      // Accessor
      protected String mson(Object obj_){
         if( msonEngine==null ) return hiMongo.mson_engine.str(obj_);
         return msonEngine.str(obj_);
         }
      // Accessor
      protected String json(Object obj_){
         if( jsonEngine==null ) return hiMongo.json_engine.str(obj_);
         return jsonEngine.str(obj_);
         }
      // Accessor
      protected String str(Object obj_){
         return hiU.str(obj_);
         }

      //============= JSON API
      /**
       *JSON文字列のリストの形でレコード取得.
       *@param option_ hi.REVERSE:逆向き
       *@return リスト
       */ // Accessor
      public ArrayList<String> getJsonList(long option_){
         ArrayList<String>     _ret   = new ArrayList<>();
         for(Document _record:records){ 
            _ret.add(json(_record));
            }
         if( (option_&hiU.REVERSE)!=0 ) Collections.reverse(_ret);
         return _ret;
         }
      /**
       *JSON文字列のリストの形でレコード取得
       *@return リスト
       */   // Accessor
      public ArrayList<String> getJsonList(){
         return getJsonList(0);
         }
      // Accessor
      /**
       *JSON文字列のリストの形でレコード取得.
       *@param option_ hiU.REVERSE:逆向き
       *@return リスト
       */   // Accessor
      public ArrayList<String> getMsonList(long option_){
         ArrayList<String>         _ret   = new ArrayList<>();
         for(Document _record:records){ 
            _ret.add(mson(_record));
            }
         if( (option_&hiU.REVERSE)!=0 ) Collections.reverse(_ret);
         return _ret;
         }
      /**
       *MSON文字列のリストの形でレコード取得
       *@return リスト
       */  // Accessor
      public ArrayList<String> getMsonList(){
         return getMsonList(0);
         }
      /**
       *hiJSON.Probeのリストの形でレコード取得.
       *@param option_ hiU.REVERSE:逆向き
       *@return リスト
       */  // Accessor
      public ArrayList<hiJSON.Probe> getProbeList(long option_){
         ArrayList<hiJSON.Probe>     _ret   = new ArrayList<>();
         for(Document _record:records){ 
            _ret.add(toProbe(_record));
            }
         if( (option_&hiU.REVERSE)!=0 ) Collections.reverse(_ret);
         return _ret;
         }
      /**
       *hiJSON.Probeのリストの形でレコード取得.
       *@return リスト
       */  // Accessor
      public ArrayList<hiJSON.Probe> getProbeList(){
         return getProbeList(0);
         }
      // Accessor
      /**
       *指定クラスのリストの形でレコード取得.
       *@param class_ クラス
       *@param option_ hiU.REVERSE:逆向き
       *@return レコードリスト
       */  // Accessor
      public <T> ArrayList<T>  getClassList(Class<T> class_
                                           ,long option_
                                           ){
         ArrayList<T>    _ret   = new ArrayList<>();
         for(Document _record:records){ 
            _ret.add(toClass(class_,_record));
            }
         if( (option_&hiU.REVERSE)!=0 ) Collections.reverse(_ret);
         return _ret;
         }
      /**
       *指定クラスのリストの形でレコード取得.
       *@param class_ クラス
       *@return レコードリスト
       */  // Accessor
      public <T> ArrayList<T> getClassList(Class<T> class_){
         return getClassList(class_,0);
         }
      /**
       * nodeオブジェクト(bson-Document)のリストの形でレコード取得.
       *@param option_ hiU.REVERSE:逆向き
       *@return リスト
       */ 
      // Accessor
      public ArrayList<Document> getNodeList(long option_){
         ArrayList<Document> _ret = new ArrayList<>();
         records.into(_ret);
         if( (option_&hiU.REVERSE)!=0 ) Collections.reverse(_ret);
         return _ret;
         }
      /**
       * nodeオブジェクト(bson-Document)のリストの形でレコード取得.
       *@return リスト
       */ // Accessor
      public ArrayList<Document> getNodeList(){
         return getNodeList(0);
         }
      // Accessor
      protected Accessor super_forEach(hiU.ConsumerEx<Document,Exception> func_){
         for(Document _record:records) hiU.rap(func_,_record);
         return this;
         }
      // Accessor
      protected Accessor super_forEachJson(hiU.ConsumerEx<String,Exception> func_){
         for(Document _record:records){ 
            hiU.rap(func_,json(_record));
            }
         return this;
         }
      // Accessor
      protected Accessor super_forEachMson(hiU.ConsumerEx<String,Exception> func_){
         for(Document _record:records){ 
            hiU.rap(func_,mson(_record));
            }
         return this;
         }
      // Accessor
      protected Accessor super_forEachProbe(hiU.ConsumerEx<hiJSON.Probe,Exception> func_){
         for(Document _record:records){ 
            hiU.rap(func_,toProbe(_record));
            }
         return this;
         }
      // Accessor
      protected <T> Accessor super_forEach(Class<T> class_
                                ,hiU.ConsumerEx<T,Exception> func_){
         for(Document _record:records){ 
            hiU.rap(func_,toClass(class_,_record));
            }
         return this;
         }
      }
   //================================================================================
   //  mongoクラス群
   //     Finder
   //     Aggregrator
   //================================================================================
   /**
    * DB内レコード範囲設定、リスト取得機構.
    *<p>
    * find()で指定されるレコード範囲に対し、並べ替えや個数制限などを行います。
    *</p>
    */
   public static class Finder extends Accessor{
      Finder(Collection collection_){
         super(collection_);
         }
      /**
       * この層の解析/表示エンジンを取得.
       *<p>
       *一時的にエンジンの設定を変更するために使用します。<br>
       *カスケードAPIを連続させるため、通常はforThisのラムダ式内で使います。
       *</p>
       *<pre class=prog10>
       *    long _last_date_Y
       *    =db.get("coll_01")
       *       .find("{type:'A'}","{_id:0,date:1}")
       *       .sort("{_id:-1}")
       *       .forThis(T->T.engine().without_option(hiU.CHECK_UNKNOWN_FIELD))
       *       .limit(1).getClassList(WithDate_X.class).get(0)
       *       .date.getTime();
       *</pre>
       *@return 基本の解析/表示エンジン
       */ // Finder
      public hiJSON.Engine engine(){
         if( this.msonEngine== null ){
            this.msonEngine=hiMongo.mson_engine.clone();
            }
         return this.msonEngine;
         }
      /**
       * イテレータ取得.
       *<p>
       *{@link com.mongodb.client.FindIterable}を取得します。
       *</p>
       *<p>
       *hiMongoが標準では用意していない機能を使うことができます。<br>
       *通常はforThisのラムダ式内で使います。
       *</p>
<pre class=prog10>
hiMongo.find()
       .forThis(T->T.getIterator().showRecordId(true))
       .getMsonList();
</pre>
       *@return イテレータ
       */
      public FindIterable<Document> getIterator(){
         return (FindIterable<Document>)records;
         }
      /**
       * 表示オプションon設定.
       *<p>
       *オプション値は{@link otsu.hiNote.hiFieldFormat#option hiFieldFormatオプション}を参照して下さい。
       *</p>
       *@param option_ オプション
       *@return this
       */ // Finder  Accessorに入れないのは戻り値がFinderであるため
      public Finder str_option(long option_){
         engine().str_format().str_option(option_);
         return this;
         }
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
       */ // Finder  Accessorに入れないのは戻り値がFinderであるため
      public Finder str_disable_option(long option_){
         engine().str_format().str_disable_option(option_);
         return this;
         }
      /**
       * パーズオプションon設定.
       *<p>
       *オプション値は{@link otsu.hiNote.hiJSON hiJSON}のパーズオプション項目を参照して下さい。
       *</p>
       *@param option_ オプション
       *@return this
       */ // Finder  Accessorに入れないのは戻り値がFinderであるため
      public Finder with_option(long option_){
         engine().with_option(option_);
         return this;
         }
      /**
       * パーズオプションoff設定.
       *<p>
       *オプション値は{@link otsu.hiNote.hiJSON hiJSON}のパーズオプション項目を参照して下さい。
       *</p>
       *<pre class=prog10>
       * 例えば次の指定をすればクラス割り当て時に過不足フィールドのチェックをしません
       * .without_option(hiU.CHECK_UNKNOWN_FIELD
       *                |hiU.CHECK_UNSET_FIELD)
       *</pre>
       *@param option_ オプション
       *@return this
       */ // Finder  Accessorに入れないのは戻り値がFinderであるため
      public Finder without_option(long option_){
         engine().without_option(option_);
         return this;
         }
      /**
       * ソート設定.
       *<p>
       *ソート法をJSON文字列で指定します。ソートはgetXXX()でレコード情報を取得するときに行われます。
       *</p>
       *@param sort_condition_ ソート指定拡張JSON記述
       *@return this
       */ // Finder
      public Finder sort(Object sort_condition_){
         ((FindIterable<Document>)records).sort(objToDoc(sort_condition_,parse_engine()));
         return this;
         }
      /**
       * 取得数設定.
       *<p>
       *取得レコード数を設定します。getXXX()でレコード情報をするときに使用されます。<br>
       *指定しない場合全レコードとなります。
       *</p>
       */ // Finder
      public Finder limit(int limit_){
         ((FindIterable<Document>)records).limit(limit_);
         return this;
         }
      /**
       * skip.
       *@param skip_ スキップ数
       *@return this
       */ // Finder
      public Finder skip(int skip_){
         ((FindIterable<Document>)records).skip(skip_);
         return this;
         }
      /**
       * Documentを引数とするラムダ式実行.
       *<p>
       *getNodeList()で一旦リストを作成しリストのforEachを用いることも可能ですが
       *本メソッドを使用すると、リストを作成することなく、レコードに対し順にアクセスすることができます。
       *</p>
       *@param func_ Documentを引数とするラムダ式
       *@return this;
       */ // Finder
      public Finder forEach(hiU.ConsumerEx<Document,Exception> func_){
         return (Finder)super_forEach(func_);
         }
      /**
       * Jsonを引数とするラムダ式実行.
       *<p>
       *getNodeList()で一旦リストを作成しリストのforEachを用いることも可能ですが
       *本メソッドを使用すると、リストを作成することなく、レコードに対し順にアクセスすることができます。
       *</p>
       *@param func_ Documentを引数とするラムダ式
       *@return this;
       */ // Finder
      public Finder forEachJson(hiU.ConsumerEx<String,Exception> func_){
         return (Finder)super_forEachJson(func_);
         }
      /**
       * Msonを引数とするラムダ式実行.
       *<p>
       *getNodeList()で一旦リストを作成しリストのforEachを用いることも可能ですが
       *本メソッドを使用すると、リストを作成することなく、レコードに対し順にアクセスすることができます。
       *</p>
       *@param func_ Documentを引数とするラムダ式
       *@return this;
       */ // Finder
      public Finder forEachMson(hiU.ConsumerEx<String,Exception> func_){
         return (Finder)super_forEachMson(func_);
         }
      /**
       * Probeを引数とするラムダ式実行.
       *<p>
       *getNodeList()で一旦リストを作成しリストのforEachを用いることも可能ですが
       *本メソッドを使用すると、リストを作成することなく、レコードに対し順にアクセスすることができます。
       *</p>
       *@param func_ Documentを引数とするラムダ式
       *@return this;
       */ // Finder
      public Finder forEachProbe(hiU.ConsumerEx<hiJSON.Probe,Exception> func_){
         return (Finder)super_forEachProbe(func_);
         }
      /**
       * クラスインスタンスを引数とするラムダ式実行.
       *<p>
       *getClassList()で一旦リストを作成しリストのforEachを用いることも可能ですが
       *本メソッドを使用すると、リストを作成することなく、レコードに対し順にアクセスすることができます。
       *</p>
       *@param func_ クラスインスタンスを引数とするラムダ式
       *@return this
       */ // Finder
      public <T> Finder forEach(Class<T> class_,hiU.ConsumerEx<T,Exception> func_){
         return (Finder)super_forEach(class_,func_);
         }

      /**
       * このFinderに対してラムダ式実行.
       *<p>
       *カスケード式の流れの中でFinderに対する操作を行います。
       *</p>
       *@param func_ Finderを引数とするラムダ式
       *@return this;
       */ // Finder
      public Finder forThis(hiU.ConsumerEx<Finder,Exception> func_){
         hiU.rap(func_,this);
         return this;
         }
      } // end Finder
   /**
    * 集計器.
    */
   public static class Aggregrator extends Accessor{
      MongoCollection<Document> mongoCollection;
      List<Document>            procs;
      /**
       * 指定のコレクション用の集計器.
       */ // Aggregrator
      Aggregrator(Collection collection_){
         super(collection_);
         mongoCollection= collection.mongoCollection;
         }
      /**
       * イテレータ取得
       *@return イテレータ
       */
      public AggregateIterable<Document> getIterator(){
         return (AggregateIterable<Document>)records;
         }
      /**
       * 表示オプションon設定.
       *<p>
       *オプション値は{@link otsu.hiNote.hiFieldFormat#option hiFieldFormatオプション}を参照して下さい。
       *</p>
       *@param option_ オプション
       *@return this
       */ // Aggregrator  Accessorに入れないのは戻り値がAggregratorであるため
      public Aggregrator str_option(long option_){
         engine().str_format().str_option(option_);
         return this;
         }
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
       */ // Aggregrator  Accessorに入れないのは戻り値がAggregratorであるため
      public Aggregrator str_disable_option(long option_){
         engine().str_format().str_disable_option(option_);
         return this;
         }
      /**
       * パーズオプションon設定.
       *<p>
       *オプション値は{@link otsu.hiNote.hiJSON hiJSON}のパーズオプション項目を参照して下さい。
       *</p>
       *@param option_ オプション
       *@return this
       */ // Aggregrator  Accessorに入れないのは戻り値がAggregratorであるため
      public Aggregrator with_option(long option_){
         engine().with_option(option_);
         return this;
         }
      /**
       * パーズオプションoff設定.
       *<p>
       *オプション値は{@link otsu.hiNote.hiJSON hiJSON}のパーズオプション項目を参照して下さい。
       *</p>
       *<pre class=prog10>
       * 例えば次の指定をすればクラス割り当て時に過不足フィールドのチェックをしません
       * .without_option(hiU.CHECK_UNKNOWN_FIELD
       *                |hiU.CHECK_UNSET_FIELD)
       *</pre>
       *@param option_ オプション
       *@return this
       */ // Aggregrator  Accessorに入れないのは戻り値がAggregratorであるため
      public Aggregrator without_option(long option_){
         engine().without_option(option_);
         return this;
         }
      /**
       * match.
       *@param arg_ match引数
       */ // Aggregrator
      public Aggregrator match(Object arg_){
         procs.add(namedObjToDoc("$match",arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /**
       * group.
       *@param arg_ group引数
       */ // Aggregrator
      public Aggregrator group(Object arg_){
         procs.add(namedObjToDoc("$group",arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /**
       * lookup.
       *@param arg_ lookup引数
       */ // Aggregrator
      public Aggregrator lookup(Object arg_){
         procs.add(namedObjToDoc("$lookup",arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /**
       * project.
       *@param arg_ project引数
       */ // Aggregrator
      public Aggregrator project(Object arg_){
         procs.add(namedObjToDoc("$project",arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /**
       * unwind.
       *@param arg_ unwind引数
       */ // Aggregrator
      public Aggregrator unwind(Object arg_){
         procs.add(namedObjToDoc("$unwind",arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /**
       * sort.
       *@param arg_ sort引数
       */ // Aggregrator
      public Aggregrator sort(Object arg_){
         procs.add(namedObjToDoc("$sort",arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /**
       * limit.
       *@param limit_ limit数
       */ // Aggregrator
      public Aggregrator limit(int limit_){
         procs.add(Document.parse("{$limit:"+limit_+"}"));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /**
       * 機能を追加する.
       *@param proc_ 機能("$xxx")
       *@param arg_ 引数
       */ // Aggregrator
      public Aggregrator add_proc(String proc_,Object arg_){
         procs.add(namedObjToDoc(proc_,arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /**
       * リストを介することなく、結果を１個ずつラムダ式で得る.
       *@param func_ ラムダ式
       *@return this
       */ // Aggregrator
      public Aggregrator forEach(hiU.ConsumerEx<Document,Exception> func_){
         return (Aggregrator)super_forEach(func_);
         }
      /**
       * リストを介することなく、Json結果を１個ずつラムダ式で得る.
       *@param func_ ラムダ式
       *@return this
       */ // Aggregrator
      public Aggregrator forEachJson(hiU.ConsumerEx<String,Exception> func_){
         return (Aggregrator)super_forEachJson(func_);
         }
      /**
       * リストを介することなく、Probe結果を１個ずつラムダ式で得る.
       *@param func_ ラムダ式
       *@return this
       */ // Aggregrator
      public Aggregrator forEachProbe(hiU.ConsumerEx<hiJSON.Probe,Exception> func_){
         return (Aggregrator)super_forEachProbe(func_);
         }
      /**
       * リストを介することなく、結果を１個ずつ利用者クラスを引数とするラムダ式で得る.
       *@param <T> 利用者クラス
       *@param class_ 利用者クラス
       *@return this
       */ // Aggregrator
      public <T> Aggregrator forEach(Class<T>                    class_,
                                     hiU.ConsumerEx<T,Exception> func_){
         return (Aggregrator)super_forEach(class_,func_);
         }
      /**
       * この集計器に対してラムダ式実行.
       *@param func_ Aggregratorを引数とするラムダ式
       *@return this;
       */ // Aggregrator
      public Aggregrator forThis(hiU.ConsumerEx<Aggregrator,Exception> func_){
         hiU.rap(func_,this);
         return this;
         }
      }
   /**
    * collectionを表す.
    *<p>
    *コレクション(collection)に対応します。
    *</p>
    *<p>
    *find,update,replase機能など持ちます。
    *</p>
    */
   public static class Collection{
      DB            db;
      hiJSON.Engine msonEngine;
      hiJSON.Engine jsonEngine;
      boolean       use_hson;
      /**
       * コレクション(collection).
       * このデータを用いて細かな作業を行っても構いません(推奨はしない)
       */
      public MongoCollection<Document> mongoCollection;
      /**
       * 指定DBを設定する
       */ // Collection
      Collection(DB db_){
         db=db_;
         use_hson=hiMongo.use_hson;
         }
      /**
       * このコレクションに対する文字列解析にbson/msonでなくhiMongoのhsonを使う.
       *<p>
       * 拡張JSON文字列解析には標準ではorg.bson.Documentが用いられます。<br>
       * 操作に先立ちstaticメソッドであるhiMongo.with_hson(true)を指定すると以降はhiMongoの解析機構hsonが用いられます。<br>
       * hsonではコメントが許されるほか[]で囲まない配列や18桁を超える10進数値をBigDecimalとして解釈することなどが出来ます。
       *</p>
       *<pre class=quote10>
       *String _records_json=hiFile.readTextAll("records.json");
       *hiMongo.DB db=hiMongo.use("db01");
       *db.get("coll_01")
       *  .with_hson()
       *  .insertMany(json_text);
       *</pre>
       *@return this;
       */ // Collection
      public Collection with_hson(){
         use_hson=true;
         return this;
         }
      /**
       * このコレクションに対する文字列解析にbson/msonでなくhiMongoのhsonを使う.
       *<p>
       * 拡張JSON文字列解析には標準ではorg.bson.Documentが用いられます。<br>
       * 操作に先立ちstaticメソッドであるhiMongo.with_hson(true)を指定すると以降はhiMongoの解析機構hsonが用いられます。<br>
       * hsonではコメントが許されるほか[]で囲まない配列や18桁を超える10進数値をBigDecimalとして解釈することなどが出来ます。
       *</p>
       *@param use_hson_ true:hsonを用いる、false:hsonを用いない
       *@return this
       */ // Collection
      public Collection with_hson(boolean use_hson_){
         use_hson=use_hson_;
         return this;
         }
      /**
       * with_hsonパーズ用エンジン取得.
       *@return use_hsonの場合現状のエンジン、!use_hsonの場合null
       */ // Collection
      final hiJSON.Engine parse_engine(){
         if( use_hson ) return cur_engine();
         return null;
         }
      /**
       * 現状の解析/表示エンジンを得る(cloneではない)
       *@return 解析/表示エンジン
       */ // Accessor
      final public hiJSON.Engine cur_engine(){
         if( this.msonEngine== null ){
            return hiMongo.mson_engine;
            }
         return this.msonEngine;
         }
      /**
       * 現状のJSON表示エンジンを得る(cloneではない)
       *@return 解析/表示エンジン
       */ // Accessor
      final public hiJSON.Engine cur_engineJ(){
         if( this.jsonEngine== null ){
            return hiMongo.json_engine;
            }
         return this.jsonEngine;
         }

     /**
       * DBに戻る
       */// Collection
      DB back(){
         return db;
         }
      /**
       * この集計器に対してラムダ式実行.
       *@param func_ Collectionを引数とするラムダ式
       *@return this;
       */// Collection 
      public Collection forThis(hiU.ConsumerEx<Collection,Exception> func_){
         hiU.rap(func_,this);
         return this;
         }
      /**
       * 検索条件とフィールド指定のfind.
       *<p>
       *このメソッドは条件設定を行います。実際の検索作業はFinderが行います
       *</p>
       *<p>
       *引数は文字列({@link java.lang.String}),テキストファイル({@link java.io.File})またはノードツリー(node-Object:{@link org.bson.Document}等)です。
       *</p>
       *@param filterJ_ jsonで条件指定
       *@param memberJ_ jsonで取得フィールド指定
       *@return Finder
       */ // Collection
      @SuppressWarnings("unchecked")
      public hiMongo.Finder find(Object filterJ_,Object memberJ_){
         if( filterJ_==null ) {
            filterJ_="{}";
            }
         hiMongo.Finder _ret= new hiMongo.Finder(this);
         _ret.records = mongoCollection.find(objToDoc(filterJ_,parse_engine()));
         if( memberJ_!=null ){
            _ret.records= ((FindIterable)_ret.records).projection(objToDoc(memberJ_,parse_engine()));
            }
         return _ret;
         }
      /**
       * 検索条件指定のfind.
       *<p>
       *このメソッドは条件設定を行います。実際の検索作業はFinderが行います
       *</p>
       *<p>
       *引数は文字列({@link java.lang.String}),テキストファイル({@link java.io.File})またはノードツリー(node-Object:{@link org.bson.Document}等)です。
       *</p>
       *@param filterJ_ jsonで条件指定
       *@return Finder
       */ // Collection
      public hiMongo.Finder find(Object filterJ_){
         return find(filterJ_,null);
         }
      /**
       * 全件検索のfind.
       *<p>
       *このメソッドは条件設定を行います。実際の検索作業はFinderが行います
       *</p>
       *@return Finder
       */ // Collection
      public hiMongo.Finder find(){
         return find(null,null);
         }

      /**
       * 1レコード追加(mongoAPIのinsertOne使用).
       *<p>
       *引数は文字列({@link java.lang.String}),テキストファイル({@link java.io.File})またはノードツリー(node-Object:{@link org.bson.Document}等)です。
       *</p>
       *@param jsonTexts_ レコードを表すjson文字列。複数可
       *@return this
       */ // Collection
      public Collection insertOne(Object... jsonTexts_){
         for(Object _jsonText:jsonTexts_){
            mongoCollection.insertOne(objToDoc(_jsonText,parse_engine()));
            }
         return this;
         }
      /**
       * コレクション上の全レコードを削除します.
       *@return this
       */ // Collection
      public Collection drop(){
         mongoCollection.drop();
         //db.collections.remove(name);
         //   DBの辞書からは削除されるがアクセスは可能
         //   DBに再びget()が発行されればDBの辞書に追加される
         return this;
         }
      /**
       * レコード数を得る.
       *@return レコード数
       */ // Collection
      public long count(){
         return mongoCollection.countDocuments();
         }
      /**
       * 条件に合致するレコード数を得る.
       *<p>
       *引数は文字列({@link java.lang.String}),テキストファイル({@link java.io.File})またはノードツリー(node-Object:{@link org.bson.Document}等)です。
       *</p>
       *<p>
       *findに与える条件と同等の条件を与え、該当レコードの個数を得ることが出来ます。
<pre class=prog10>
   hiMongo.DB db= hiMongo.use("testDB");
   System.out.println("count of type B|C = "+
        db.get("coll_01").count("{$or:[{type:'B'},{type:'C'}]}"));
</pre>
       *</p>
       *@param filterJ_ 条件
       *@return レコード数
       */ // Collection
      public long count(Object filterJ_){
         return mongoCollection.countDocuments(objToDoc(filterJ_,parse_engine()));
         }
      /**
       * リスト形式文字列で複数レコードを追加する.
       *<p>
       *レコードの単純並び形式の文字列でレコードを追加します。<br>
       *記述はJSONの配列形式("[{...},{...}...]")と単純にレコードが並んだ形("{...}{...}...")の何れも受け付けます。<br>
       *これはリスト形式になってしまっている記述に対処するためのもので、単に複数レコードを一気にinsertしたいのであれば、insert()に配列または複数引数を与えることでも実現できます。
       *</p>
       *<p>
       *引数は文字列({@link java.lang.String}),テキストファイル({@link java.io.File})またはノードツリー(node-Object:{@link org.bson.Document}等)のリストです。
       *</p>
<pre class=prog10>
   String _records=
        "[{type:'A',value:12.3,date:ISODate('2020-08-17T07:07:00.000Z')}"+
        ",{type:'A',value:4.56,date:ISODate('2020-08-17T07:07:10.000Z')}"+
        ",{type:'B',value:2001,date:ISODate('2020-08-17T07:07:20.000Z')}]";
   String _record2=
        "[{type:'A',value:7.89,date:ISODate('2020-08-17T07:07:30.000Z')}"+
        ",{type:'A',value:0.12,date:ISODate('2020-08-17T07:07:40.000Z')}]";
   hiMongo.DB          db   =hiMongo.use("db02");
   hiMongo.Collection  _coll=db.get("coll_01");
   _coll.insertMany(_records,_records2); // 複数の文字列をセット可能
   db.close();
</pre>
       *@param jsonTexts_ json記述
       *@return this
       */ // Collection
      public Collection insertMany(Object... jsonTexts_){
         List<Document> _doc_list= new ArrayList<Document>();
         for(Object _jsonText:jsonTexts_){
            _doc_list.addAll(parseAsDocumentList(_jsonText,parse_engine()));
            }
         mongoCollection.insertMany(_doc_list);
         return this;
         }
      /**
       * aggregate(集計).
       *<p>
       *引数は文字列({@link java.lang.String}),テキストファイル({@link java.io.File})またはノードツリー(node-Object:{@link org.bson.Document}等)です。
       *</p>
       *集計手続きを設定します。<br>
       *集計作業は集計器で行います。
       *@param proc_ 集計手続き [{...},{...}...]
       *@return 集計器
       */ // Collection
      public Aggregrator aggregate(Object proc_){
         Aggregrator _ret=new Aggregrator(this);
         _ret.procs   = parseAsDocumentList(proc_,parse_engine());
         _ret.records = mongoCollection.aggregate(_ret.procs);
         return _ret;
         }

      /**
       * aggregate(集計).
       *集計手続き無で集計器を作成します。<br>
       *集計手続きは集計器のmatch()メソッドなどで追加できます。
       *@return 集計器
       */ // Collection
      public Aggregrator aggregate(){
         Aggregrator _ret=new Aggregrator(this);
         _ret.procs = new ArrayList<Document>();
         return _ret;
         }

      /**
       * 1レコードupdateする.
       *<p>
       *引数は文字列({@link java.lang.String}),テキストファイル({@link java.io.File})またはノードツリー(node-Object:{@link org.bson.Document}等)です。
       *</p>
       *<pre class=quote10>
hiMongo.DB db=hiMongo.use("db01");
db.get("coll_01")
  .updateOne("{$and:[{type:'A'},{value:4.56}]}",
             "{$set:{value:0.15}}");
--- 対応するmongo-shell記述
use db01
db.coll_01.updateOne({$and:[{type:'A'},{value:4.56}]},
                    {$set:{value:0.15}});
       *</pre>
       *@param filterJ_ 条件
       *@param updateJ_ 置き換えフィールド指定
       *@return this
       */ // Collection
      public Collection updateOne(Object filterJ_,Object updateJ_){
         hiJSON.Engine _parse_engine=parse_engine();
         mongoCollection.updateOne(objToDoc(filterJ_,_parse_engine)
                                  ,objToDoc(updateJ_,_parse_engine));
         return this;
         }
      /**
       * 条件にあうレコードを全てupdateする.
       *<p>
       *引数は文字列({@link java.lang.String}),テキストファイル({@link java.io.File})またはノードツリー(node-Object:{@link org.bson.Document}等)です。
       *</p>
       *<pre class=quote10>
hiMongo.DB db=hiMongo.use("db01");
db.get("coll_01")
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
       */ // Collection
      public Collection updateMany(Object filterJ_,Object updateJ_){
         hiJSON.Engine _parse_engine=parse_engine();
         mongoCollection.updateMany(objToDoc(filterJ_,_parse_engine)
                                   ,objToDoc(updateJ_,_parse_engine));
         return this;
         }
      /**
       * １レコード置換する.
       *<p>
       *条件に合致するレコードを１個だけ置き換えます。
       *</p>
       *@param filterJ_ 条件
       *@param recordJ_ 新規レコード内容
       *@return this
       */ // Collection
      public Collection replaceOne(Object filterJ_,Object recordJ_){
         hiJSON.Engine _parse_engine=parse_engine();
         mongoCollection.replaceOne(objToDoc(filterJ_,_parse_engine)
                                   ,objToDoc(recordJ_,_parse_engine));
         return this;
         }
      /**
       * １レコード削除.
       *<p>
       *条件に合致するレコードを１個だけ削除。
       *</p>
       *@param filterJ_ 条件
       *@return this
       */ // Collection
      public Collection deleteOne(Object filterJ_){
         mongoCollection.deleteOne(objToDoc(filterJ_,parse_engine()));
         return this;
         }
      /**
       * 複数レコード削除.
       *<p>
       *条件に合致するレコードを１個だけ削除。
       *</p>
       *@param filterJ_ 条件
       *@return this
       */ // Collection
      public Collection deleteMany(Object filterJ_){
         mongoCollection.deleteMany(objToDoc(filterJ_,parse_engine()));
         return this;
         }
      }
   /**
    * database.
    */
   public static class DB implements Closeable{
      Client                                          client;
      public MongoDatabase                            mongoDatabase;
      //public LinkedHashMap<String,hiMongo.Collection> collections=new LinkedHashMap<>();
      /**
       * client(DBサーバとの接続)とデータベース名で構築する.
       *@param client_ client_
       *@param dbName_ データベース名
       */ // DB
      DB(hiMongo.Client client_,String dbName_){
         client= client_;
         mongoDatabase = client.mongoClient.getDatabase(dbName_);
         }
      /**
       * コレクションを得る.
       *@param collectionName_ コレクション名
       *@return コレクション
       */ // DB
      public hiMongo.Collection get(String collectionName_){
         //hiMongo.Collection _ret= collections.get(collectionName_);
         //if( _ret!=null ) return _ret;
         hiMongo.Collection _ret = new hiMongo.Collection(this);
         //mongoDatabase.createCollection(collectionName_);
         _ret.mongoCollection= mongoDatabase.getCollection(collectionName_);
         //collections.put(collectionName_,_ret);
         return _ret;
         }
      /**
       * client(サーバとの接続)を閉じる
       */ // DB
      public void close(){
         hiU.close(mongoDatabase);
         hiU.close(client);
         }
      /**
       * コレクション名一覧を得る.
       *@return コレクション名一覧
       */ // DB
      public ArrayList<String> show_collections(){
         ArrayList<String> _ret= new ArrayList<>();
         MongoIterable<java.lang.String> _list  =mongoDatabase.listCollectionNames();
         MongoCursor<java.lang.String>   _cursor=_list.iterator();
         while (_cursor.hasNext()) { 
            String _collName=_cursor.next();
            _ret.add(_collName);
            }
         return _ret;
         }
      /**
       * DB内容を消去する.
       *@return this
       */ // DB
      public DB drop(){
         mongoDatabase.drop();
         return this;
         }
      /**
       * コレクションが存在するか調べる.
       *@return true:存在する、false:存在しない
       */ // DB
       public boolean exists(String collectionName_){
         MongoIterable<java.lang.String> _list  =mongoDatabase.listCollectionNames();
         MongoCursor<java.lang.String>   _cursor=_list.iterator();
         while (_cursor.hasNext()) { 
            if( collectionName_.equals(_cursor.next()) ) return true;
            }
         return false;
         }

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
       */ // DB
      public Collection createCappedCollection(String name_,String capInfo_){
         CapInfo _capInfo=hiJSON.parse(capInfo_).as(CapInfo.class);
         if( !_capInfo.force && exists(name_) ) return get(name_);
         get(name_).drop();// 微妙
         mongoDatabase.createCollection(
            name_,
            new CreateCollectionOptions().capped(true)
                                         .sizeInBytes(_capInfo.size)
                                         .maxDocuments(_capInfo.records));
         return get(name_);
         }
      // Cap指定
      static class CapInfo{
         long size;
         long records;
         boolean force;
         }
      }

   /**
    * mongoDBサーバとの接続を表す.
    */
   public static class Client implements Closeable{
      public MongoClient   mongoClient;
      // 単独生成禁止
      Client(){}
      /**
       * デフォルトの接続(localhost.27017)
       */ // Client
      hiMongo.Client connect(){
         mongoClient   = new MongoClient("localhost", 27017);
         return this;
         }
      /**
       * リモート接続指定.
       *<p>
       *サーバと接続します。<br>
       *通常は{@link hi.hiMongo#connect(Object remote_)}を使用します
       *</p>
       *@param info_ リモート情報
       *@return 接続されたhiMongo.Client
       */ // Client
      hiMongo.Client connect(hiMongo.RemoteInfo info_){
         if( info_==null ){
            mongoClient   = new MongoClient("localhost", 27017);
            return this;
            }
         List<ServerAddress> _addrs         = new ArrayList<>();
         _addrs.add(new ServerAddress(info_.host,info_.port));
         MongoCredential _credential = MongoCredential.createScramSha1Credential(
                                              info_.user
                                             ,info_.dbName
                                             ,info_.password.toCharArray());
         MongoClientOptions.Builder _options     = MongoClientOptions.builder();
         mongoClient=new MongoClient(_addrs,_credential,_options.build());
         return this;
         }
      /**
       * 切断する
       */ // Client
      public void close(){
         hiU.close(mongoClient);
         }
      /**
       * データベース名一覧を得る.
       *@return データベース名一覧
       */ // Client
      public ArrayList<String> show_dbs(){
         ArrayList<String> _ret= new ArrayList<>();
         // MongoIterable#forEachはjava8ではconflictを起こす
         MongoIterable<java.lang.String> _list  =mongoClient.listDatabaseNames();
         MongoCursor<java.lang.String>   _cursor=_list.iterator();
         while (_cursor.hasNext()) { 
            String _dbName=_cursor.next();
            _ret.add(_dbName);
            }
         return _ret;
         }
      /**
       * databaseを得る.
       */ // Client
      public DB use(String dbName_){
         DB     _ret= new DB(this,dbName_);
         return _ret;
         }
      }// end Client
   /**
    * デフォルト接続localhost.27017でデータベースを得る.
    *<p>
    *DBクラスはCloseableなので閉じたプログラム空間で使用する場合は次のような記述が推奨されます。
    *</p>
    *<pre class=prog10>
    *try(hiMongo.DB db=hiMongo.use("db01")){
    *   // db手続き
    *   }
    *</pre>
    *@return データベース
    */ // hiMongo
   public static hiMongo.DB use(String dbName_){
      Client _client=new Client().connect();
      return _client.use(dbName_);
      }
   /**
    * サーバ接続を得る.
    *<p>
    *ホスト名、ポート、ユーザ、ユーザ定義のあるdb、パスワードを指定し、mongoDBサーバに接続します。
    *</p>
    *<p>
    *引数はJSON文字列,{@link hi.hiMongo.RemoteInfo hiMongo.RemoteInfo},JSONのnode解析結果のいずれかです。
    *</p>
<p>
JSON文字列で情報を与える.
</p>
    *<pre class=prog10>
DB db=hiMongo.connect("{"+
                      "host:'192.168.1.139',"+
                      "port:27017,"+
                      "dbName:'testDB',"+
                      "user:'testUser',"+
                      "password:'xxx'"+
                      "}")
              .use("db01");
db.get("coll_01").find()...
</pre>

<p>
{@link hi.hiMongo.RemoteInfo hiMongo.RemoteInfo}で情報を与える
</p>
    *<pre class=prog10>
hiMongo.RemoteInfo _info=new hiMongo.RemoteInfo();
_info.host    = "192.168.1.139";
_info.port    = 27017;
_info.dbName  = "testDB";
_info.user    = "testUser";
_info.password= "xxx";
DB db=hiMongo.connect(_info)
             .use("db01");
db.get("coll_01").find()...
</pre>

<p>
node解析結果で情報を与える
</p>
    *<pre class=prog10>
Object _node={@link hi.hiMongo#parseText(String) hiMongo.parseText}("{"+
                      "host:'192.168.1.139',"+
                      "port:27017,"+
                      "dbName:'testDB',"+
                      "user:'testUser',"+
                      "password:'xxx'"+
                      "}")
                     .asNode();
DB db=hiMongo.connect(_node)
             .use("db01");
db.get("coll_01").find()...
    *</pre>
    *@param remote_ JSONまたはnodeまたはhiMongo.RemoteInfo
    *@return サーバとの接続
    */ // hiMongo
   @SuppressWarnings("unchecked")
   public static hiMongo.Client connect(Object remote_){
      Client _ret= new Client();
      hiMongo.RemoteInfo _info=null;
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
      return _ret.connect(_info);
      }
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
db.get("coll_01").find()...
</pre>
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
      }
   }
class MongoConverter {
   //=========== Date ハンドリング =======
   final static SimpleDateFormat dateFormat
         = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
   /** DateのISODate出力 */
   static <T> String toISODateStr(hiFieldFormat fmt_,
                                  Class<T>      class_,
                                  Object        obj_){
      long _unix_time =((Date)obj_).getTime();
      Date _dt       = new Date();
      _dt.setTime(_unix_time-TimeZone.getDefault().getRawOffset());
      return "ISODate("+fmt_.text_quote+dateFormat.format(_dt)+fmt_.text_quote+")";
      }
   /** Dateの辞書形式出力 */
   static <T> String toDateMapStr(hiFieldFormat fmt_,
                                  Class<T>      class_,
                                  Object        obj_){
      long _unix_time =((Date)obj_).getTime();
      Date _dt       = new Date();
      _dt.setTime(_unix_time-TimeZone.getDefault().getRawOffset());
      return "{"+fmt_.name_quote+"$date"+fmt_.name_quote+":"+_unix_time+"}";
      }
   /** ISODate関数形式解析 */
   static Object funcStr_toDate(String funcName_
                               ,ArrayList<Object> args_
                               )throws Exception{
      Date _dt=dateFormat.parse((String)(args_.get(0)));
      long _local=_dt.getTime()+TimeZone.getDefault().getRawOffset();
      return new Date(_local);
      }
   /** Dateの辞書形式解析 */
   static Object dictStr_toDate(String             key_
                               ,Object             data_
                               ,Map<String,Object> map_
                               )throws Exception{
      return new Date(hiJSON.Probe.asLong(data_));
      }
   /** Date型または辞書型ノードをDateにする */
   @SuppressWarnings("unchecked")
   static Object node_toDate(Class<?> class_
                            ,hiField  field_
                            ,Object   obj_){
      if( obj_.getClass()==Date.class ) return obj_;
      if( obj_ instanceof Map ){
         Map<String,Object> _map=(Map<String,Object>)obj_;
         Object _value=_map.get("$date");
         long _long_value=0;
         if( _value instanceof Double ){
            _long_value= (long)(double)(Double)_value;
            }
         else if( _value instanceof Long ){
            _long_value= (long)(Long)_value;
            }
         return new Date(_long_value);
         }
      return null;
      }
   //=========== ObjectId ハンドリング =======
   /** ObjectId出力 */
   static <T> String toOidStr(hiFieldFormat fmt_
                             ,Class<T>      class_
                             ,Object        obj_){
      ObjectId _oid=(ObjectId)obj_;
      return "ObjectId("+fmt_.text_quote+_oid.toHexString()+fmt_.text_quote+")";
      }
   /** ObjectIdの辞書形式出力 */
   static <T> String toOidMapStr(hiFieldFormat fmt_
                                ,Class<T>      class_
                                ,Object        obj_){
      ObjectId _oid=(ObjectId)obj_;
      return "{"+fmt_.name_quote+"$oid"+fmt_.name_quote+":"+_oid.toHexString()+"}";
      }
   /** ObjectIdの辞書形式解析 */
   static Object dictStr_toOid(String             key_
                              ,Object             data_
                              ,Map<String,Object> map_
                              )throws Exception{
      return new ObjectId((String)data_);
      }
   /** ObjectId関数形式解析 */
   static Object funcStr_toOid(String funcName_
                              ,ArrayList<Object> args_
                              )throws Exception{
      return new ObjectId((String)(args_.get(0)));
      }
   /** ObjectId型または辞書型ノードをObjectIdにする */
   @SuppressWarnings("unchecked")
   static Object node_toOid(Class<?> class_
                           ,hiField  field_
                           ,Object   obj_){
      if( obj_.getClass()==ObjectId.class ) return obj_;
      if( obj_ instanceof Map ){
         Map<String,String> _map=(Map<String,String>)obj_;
         String _value=_map.get("$oid");
         return new ObjectId(_value);
         }
      return null;
      }
   //=========== BsonRegularExpression ハンドリング =======
   /** /xxx/形式の解釈 */
   static Object regex_parse_func(String regex_,String option_){
      //System.out.println("Test.regex_func regex "+regex_+" option "+option_);
      return new BsonRegularExpression(regex_,option_);
      }
   /** 辞書形式出力 */
   static <T> String toRegexMapStr(hiFieldFormat fmt_
                                  ,Class<T>      class_
                                  ,Object        obj_){
      BsonRegularExpression _regex=(BsonRegularExpression)obj_;
      return "{"+fmt_.name_quote+"$regex"+fmt_.name_quote+":"
                                         +fmt_.quotedText(_regex.getPattern())+","
                +fmt_.name_quote+"$option"+fmt_.name_quote+":"
                                         +fmt_.quotedText(_regex.getPattern())
              +"}";
      }
   /** 辞書形式解析 */
   static Object dictStr_toRegex(String             key_
                                ,Object             data_
                                ,Map<String,Object> map_
                                )throws Exception{
      String _option= (String)map_.get("$option");
      if( _option != null ){
         return new BsonRegularExpression((String)data_,_option);
         }
      return new BsonRegularExpression((String)data_);
      }
   /** クラスまたは辞書型ノードをBsonRegularExpressionにする */
   @SuppressWarnings("unchecked")
   static Object node_toRegex(Class<?> class_
                             ,hiField  field_
                             ,Object   obj_){
      if( obj_.getClass()==BsonRegularExpression.class ) return obj_;
      if( obj_ instanceof Map ){
         Map<String,String> _map=(Map<String,String>)obj_;
         String _regex = (String)_map.get("$regex");
         String _option= (String)_map.get("$option");
         if( _option != null ){
            return new BsonRegularExpression((String)_regex,_option);
            }
         return new BsonRegularExpression((String)_regex);
         }
      return null;
      }
   /** nodeオブジェクトをLongに変える */
   @SuppressWarnings("unchecked")
   static Object node_toLong(Class<?> class_
                            ,hiField  field_
                            ,Object   obj_){
      return new Long((long)(Integer)obj_);
      }
   /** $numberLongの辞書形式解析 */
   static Object dictStr_toLong(String             key_
                               ,Object             data_
                               ,Map<String,Object> map_
                               )throws Exception{
      return new Long(Long.parseLong((String)data_));
      }
   /** NumberLong関数形式解析 */
   static Object funcStr_toLong(String            funcName_
                               ,ArrayList<Object> args_
                               )throws Exception{
      return new Long(Long.parseLong((String)args_.get(0)));
      }
   /** NumberDecimal関数解析 */
   static Object funcStr_toDecimal(String funcName_
                                  ,ArrayList<Object> args_
                                  )throws Exception{
      return new BigDecimal((String)args_.get(0));
      }
   /** $numberLongの辞書形式解析 */
   static Object dictStr_toDecimal(String                key_
                                  ,Object             data_
                                  ,Map<String,Object> map_
                                  )throws Exception{
      return new BigDecimal((String)data_);
      }
   /** BigDecimal,Decimal128型または辞書型ノードをBigDecimalにする */
   @SuppressWarnings("unchecked")
   static Object node_toBigDecimal(Class<?> class_
                                  ,hiField  field_
                                  ,Object   obj_){
      if( obj_.getClass()==BigDecimal.class ) return obj_;
      if( obj_.getClass()==Decimal128.class ) {
         return ((Decimal128)obj_).bigDecimalValue();
         } 
      if( obj_ instanceof Map ){
         Map<String,String> _map=(Map<String,String>)obj_;
         String _value=_map.get("$numberDecimal");
         return new BigDecimal(_value);
         }
      return null;
      }
   /** BigDecimal,Decimal128 辞書型(JSON) */
   static <T> String toDecMapStr(hiFieldFormat fmt_
                                ,Class<T>      class_
                                ,Object        obj_){
      return "{"+fmt_.name_quote+"$numberDecimal"+fmt_.name_quote+
             ":"+fmt_.text_quote+obj_.toString()+fmt_.text_quote+"}";
      }
   /** NumberDecimal関数型 */
   static <T> String toDecStr(hiFieldFormat fmt_
                             ,Class<T>      class_
                             ,Object        obj_){
      return "NumberDecimal("+fmt_.text_quote+obj_.toString()+fmt_.text_quote+")";
      }
/*
https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mapping-conversion
Collection has CodecRegistry getCodecRegistry​()
*/
   }
