import hi.db.*;
import java.io.*;
import otsu.hiNote.*;
import java.util.*;
import java.math.*;
import org.bson.Document;
public class Test {
   static class Values{
      int  int_1;
      int  inf_func;
      int  int_dict;
      long lng_1;
      long lng_func;
      long lng_dict;
      long bd_1;
      }
   static class Bytes{
      byte val1;
      byte val2;
      byte val3;
      byte val4;
      byte val5;
      byte val6;
      }
   static class Shorts{
      short val1;
      short val2;
      short val3;
      short val4;
      short val5;
      short val6;
      }
   static class Ints{
      int val1;
      int val2;
      int val3;
      int val4;
      int val5;
      int val6;
      }
   static class Longs{
      long val1;
      long val2;
      long val3;
      long val4;
      long val5;
      long val6;
      }
   static class Doubles{
      double val1;
      double val2;
      double val3;
      double val4;
      double val5;
      double val6;
      }
   static class BigDecimals{
      BigDecimal val1;
      BigDecimal val2;
      BigDecimal val3;
      BigDecimal val4;
      BigDecimal val5;
      BigDecimal val6;
      }
   static class byteArray{
      byte vals[];
      }
   static class intArray{
      int vals[];
      }
   static class shortArray{
      short vals[];
      }
   static class longArray{
      long vals[];
      }
   static class doubleArray{
      double vals[];
      }
   static class bigDecimalArray{
      BigDecimal vals[];
      }

   @SuppressWarnings("unchecked")
   public static void main(String[] args_){
      //--------------------------------------------------------
      hiMongo.MoreMongo mongo;
      File _modeFile= new File("../test_workerMode.txt");
      if( false && _modeFile.exists() ) {
         String _host= hiFile.readTextAll(_modeFile).trim();
         if( _host.length()<5 ){
            mongo=new hiMongoCaller(new hiMongoWorker());
            hiU.out.println("// MODE: Caller/Worker");
            }
         else {
            mongo=new hiMongoCaller(new hiMonWorkerSample.COM(_host,8010,3));
            hiU.out.println("// MODE: call SERVER '"+_host+"'");
            }
         }
      else {
         mongo=new hiMongoDirect();
         hiU.out.println("// 本試験はCaller/Warkerでは結果が異なるのでDIRECTのみ
         hiU.out.println("// MODE: DIRECT");
         }
      //--------------------------------------------------------
      try{

         //========== PART1
         String _mson="{"+
                          "int_1:100,"+
                          "int_func:NumberInt('201'),"+
                          "int_dict:{$numberInt:'301'},"+
                          "lng_1:1234567890123,"+
                          "lng_func:NumberLong('401'),"+
                          "lng_dict:{$numberLong:'501'},"+
                          "bd_1:'123456789012345678901231',"+
                          "bd_func:NumberDecimal('22345678901234567890123')"+
                          "bd_dict:{$numberDecimal:'32345678901234567890123'}"+
                         "}";
         String _msonX="{"+
                          "int_1:100,"+
                          "int_func:NumberInt('201'),"+
                          "int_dict:{$numberInt:'301'},"+
                          "lng_1:1234567890123,"+
                          "lng_func:NumberLong('401'),"+
                          "lng_dict:{$numberLong:'501'},"+
                          "bd_1:123456789012345678901231"+ //引用符無
                          "bd_func:NumberDecimal('22345678901234567890123')"+
                          "bd_dict:{$numberDecimal:'32345678901234567890123'}"+
                         "}";
         // Documentのパーズ機構

         hiU.out.println("====== Documentによるテキストパーズ");
         Document _doc=Document.parse(_mson);
         hiU.out.println("doc.toString="+_doc.toString());
         hiU.out.println("doc.toJson  ="+_doc.toJson());
         hiU.out.println("hiU.str(doc)="+hiU.str(_doc,hiU.WITH_TYPE|hiU.WITH_INDENT));

         // hiJSONによるDocument-nodeパーズ
         hiU.out.println("====== hiJSONによるノードパーズ");
         Object _node=hiJSON.parseNode(_doc).asNode();
         hiU.out.println("hiU.str(node)   ="+hiU.str(_node,hiU.WITH_INDENT|hiU.WITH_TYPE));
         hiU.out.println("hiJSON.str(node)="+hiJSON.str(_node,hiU.WITH_INDENT|hiU.WITH_TYPE));
         //
         hiU.out.println("- - クラス化");
         try{
             Values _vals=hiJSON.parseNode(_doc)
                                .without_option(hiU.CHECK_UNKNOWN_FIELD|hiU.CHECK_UNSET_FIELD)
                                .asClass(Values.class);
             hiU.out.println("cls     ="+hiU.str(_vals));
             }
         catch(Exception _ex){
             hiU.out.println("err="+_ex);
             }

         hiU.out.println("hiJSONによるテキストパーズ　例外発生のはず");
         try{
            Object _nodeh=hiJSON.parse(_mson).asNode();
            }
         catch(Exception _ex){
            hiU.out.println("err="+_ex);
            }


         hiU.out.println("====== hiMongoによるノードパーズ");
         Object _mnode=hiMongo.parseNode(_doc).asNode();
         hiU.out.println("hiU.str     ="+hiU.str(_mnode,hiU.WITH_TYPE|hiU.WITH_INDENT));
         hiU.out.println("hiJSON.str  ="+hiJSON.str(_mnode,hiU.WITH_TYPE|hiU.WITH_INDENT));
         hiU.out.println("hiMongo.mson="+hiMongo.mson(_mnode,hiU.WITH_TYPE|hiU.WITH_INDENT));
         hiU.out.println("- - クラス化　例外発生しない");
         Values _mvals=hiMongo.parseNode(_doc)
                              .without_option(hiU.CHECK_UNKNOWN_FIELD|hiU.CHECK_UNSET_FIELD)
                              .asClass(Values.class);
         hiU.out.println("cls     ="+hiU.str(_mvals,hiU.WITH_TYPE|hiU.WITH_INDENT));

         // hiMongoのパーズ機構
         hiU.out.println("====== hiMongoによるテキストパーズ");
         Object _mdoc=hiMongo.parse(_msonX).asNode();
         hiU.out.println("node.toString  ="+_mdoc.toString());
         hiU.out.println("hiMo.json(node)="+hiMongo.json(_mdoc,hiU.WITH_TYPE|hiU.WITH_INDENT));
         hiU.out.println("hiMo.mson(node)="+hiMongo.mson(_mdoc,hiU.WITH_TYPE|hiU.WITH_INDENT));
         hiU.out.println("hiU.str(node)  ="+hiU.str(_mdoc,hiU.WITH_TYPE|hiU.WITH_INDENT));
         hiU.out.println("- - クラス化　例外発生しない");
         Values _mmvals=hiMongo.parseNode(_mdoc)
                              .without_option(hiU.CHECK_UNKNOWN_FIELD|hiU.CHECK_UNSET_FIELD)
                              .asClass(Values.class);
         hiU.out.println("cls     ="+hiU.str(_mmvals,hiU.WITH_TYPE|hiU.WITH_INDENT));

         hiU.out.println("====== hiMongoによるテキスト->Dcoumentのparse");
         Document _docX=new Document((Map<String,Object>)_mdoc);
         hiU.out.println("doc.toString="+_docX.toString());
         hiU.out.println("doc.toJson  ="+_docX.toJson());
         hiU.out.println("hiU.str(doc)="+hiU.str(_docX,hiU.WITH_TYPE|hiU.WITH_INDENT));

         hiU.out.println("==== insert to db02.coll_04");
         try(hiMongo.DB db=mongo.use("db02")){
            db.in("coll_04").drop().insertOne(_docX);
            }
         hiU.out.println("==== find JSON");
         try(hiMongo.DB db=mongo.use("db02")){
            db.in("coll_04").find("{}")
              .forEachJson(Rj->hiU.out.println(Rj));
            }
         hiU.out.println("==== find MSON");
         try(hiMongo.DB db=mongo.use("db02")){
            db.in("coll_04").find("{}")
              .forEachMson(Rm->hiU.out.println(Rm));
            }
         //========== PART2
         hiU.out.println("\n\n============= PART2");
         String _vals
         ="{val1:1,val2:2.1,val3:3.4e100,val4:{$numberInt:'4'},val5:{$numberLong:'5'},val6:{$numberDecimal:'6'}}";
         Document _doc2  = Document.parse(_vals);
         Bytes    _bytes = hiMongo.parseNode(_doc2).as(Bytes.class);
         hiU.out.println("\nbytes="+hiU.str(_bytes));
         Shorts   _shorts = hiMongo.parseNode(_doc2).as(Shorts.class);
         hiU.out.println("\nshorts="+hiU.str(_shorts));
         Ints     _ints = hiMongo.parseNode(_doc2).as(Ints.class);
         hiU.out.println("\nints="+hiU.str(_ints));
         Longs      _longs = hiMongo.parseNode(_doc2).as(Longs.class);
         hiU.out.println("\nlongs="+hiU.str(_longs));
         Doubles     _doubles = hiMongo.parseNode(_doc2).as(Doubles.class);
         hiU.out.println("\ndoubles="+hiU.str(_doubles));
         BigDecimals _bigDecimals= hiMongo.parseNode(_doc2).as(BigDecimals.class);
         hiU.out.println("\nbigDecimals="+hiU.str(_bigDecimals));

         //========== PART3
         hiU.out.println("\n\n============= PART3");
         String _text="{vals:"+hiFile.readTextAll("nums.json")+"}";
         hiU.out.println("---- use hson");
         {
            hiMongo.DB db=mongo.use("db02");
            db.in("coll_05").drop()
              .with_hson(true)
              .insertOne(_text)
              .find("{}","{_id:0}")
              .forEach(Rd->hiU.out.println(hiU.str(Rd,hiU.WITH_INDENT|hiU.WITH_TYPE|hiU.NO_LIMIT)));
            }

         //========== PART4
         hiU.out.println("\n\n============= PART4");
         {
            hiMongo.DB db=mongo.use("db02");
            hiU.out.println("-- byte");
            byteArray _byteArray=db.in("coll_05")
              .with_hson(true)
              .find("{}","{_id:0}").limit(1)
              .getClassList(byteArray.class).get(0);
            hiU.out.println(hiU.str(_byteArray));

            hiU.out.println("-- short");
            shortArray _shortArray=db.in("coll_05")
              .with_hson(true)
              .find("{}","{_id:0}").limit(1)
              .getClassList(shortArray.class).get(0);
            hiU.out.println(hiU.str(_shortArray));

            hiU.out.println("-- int");
            intArray _intArray=db.in("coll_05")
              .with_hson(true)
              .find("{}","{_id:0}").limit(1)
              .getClassList(intArray.class).get(0);
            hiU.out.println(hiU.str(_intArray));

            hiU.out.println("-- long");
            longArray _longArray=db.in("coll_05")
              .with_hson(true)
              .find("{}","{_id:0}").limit(1)
              .getClassList(longArray.class).get(0);
            hiU.out.println(hiU.str(_longArray));

            hiU.out.println("-- double");
            doubleArray _doubleArray=db.in("coll_05")
              .with_hson(true)
              .find("{}","{_id:0}").limit(1)
              .getClassList(doubleArray.class).get(0);
            hiU.out.println(hiU.str(_doubleArray));

            hiU.out.println("-- bigDecimal");
            bigDecimalArray _bigDecimalArray=db.in("coll_05")
              .with_hson(true)
              .find("{}","{_id:0}").limit(1)
              .getClassList(bigDecimalArray.class).get(0);
            hiU.out.println(hiU.str(_bigDecimalArray));

            }
         //========== PART5
         hiU.out.println("\n\n============= PART5");
         hiU.out.println("\n---- use bson");
         try{
            hiMongo.DB db=mongo.use("db02");
            db.in("coll_05").drop()
              .with_hson(false)   // bson/mson解析　12345678901234567890解析不能
              .insertOne(_text)
              .find("{}","{_id:0}")
              .forEach(Rd->hiU.out.println(hiU.str(Rd,hiU.WITH_INDENT|hiU.WITH_TYPE|hiU.NO_LIMIT)));
            }
         catch(Exception _ex){
            hiU.out.println("excepted exception "+_ex);
            }

         String _text2=hiRegex.with("//.*","")
                       .replace("{vals:"+hiFile.readTextAll("nums2.json")+"}")
                       .result();
         try{
            hiMongo.DB db=mongo.use("db02");
            db.in("coll_06").drop()
              .with_hson(false)   // bson/mson解析　12345678901234567890解析不能
              .insertOne(_text2)
              .find("{}","{_id:0}")
              .forEach(Rd->hiU.out.println(hiU.str(Rd,hiU.WITH_INDENT|hiU.WITH_TYPE|hiU.NO_LIMIT)));

            hiU.out.println("-- byte");
            byteArray _byteArray=db.in("coll_06")
              .with_hson(true)
              .find("{}","{_id:0}").limit(1)
              .getClassList(byteArray.class).get(0);
            hiU.out.println(hiU.str(_byteArray));

            hiU.out.println("-- short");
            shortArray _shortArray=db.in("coll_06")
              .with_hson(true)
              .find("{}","{_id:0}").limit(1)
              .getClassList(shortArray.class).get(0);
            hiU.out.println(hiU.str(_shortArray));

            hiU.out.println("-- int");
            intArray _intArray=db.in("coll_06")
              .with_hson(true)
              .find("{}","{_id:0}").limit(1)
              .getClassList(intArray.class).get(0);
            hiU.out.println(hiU.str(_intArray));

            hiU.out.println("-- long");
            longArray _longArray=db.in("coll_06")
              .with_hson(true)
              .find("{}","{_id:0}").limit(1)
              .getClassList(longArray.class).get(0);
            hiU.out.println(hiU.str(_longArray));

            hiU.out.println("-- double");
            doubleArray _doubleArray=db.in("coll_06")
              .with_hson(true)
              .find("{}","{_id:0}").limit(1)
              .getClassList(doubleArray.class).get(0);
            hiU.out.println(hiU.str(_doubleArray));

            hiU.out.println("-- bigDecimal");
            bigDecimalArray _bigDecimalArray=db.in("coll_06")
              .with_hson(true)
              .find("{}","{_id:0}").limit(1)
              .getClassList(bigDecimalArray.class).get(0);
            hiU.out.println(hiU.str(_bigDecimalArray));
            }
         catch(Exception _ex){
            hiU.out.println("excepted exception "+_ex);
            }
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }
/*
use db01
var _last_date=db.coll_01.find({type:'A'},{_id:0,date:1}).
                    sort({_id:-1}).
                    limit(1).toArray()[0].date.getTime()
var _start_date=_last_date-30000
var _isodate=new Date(_start_date)
db.coll_01.
   find({date:{$gte:_isodate}})

use db01

db.coll_01.
   find({$and:[{type:'A'},{date:{$gte:_isodate}}]})

use db01
var _last_date=db.coll_01.find({type:'A'},{_id:0,date:1}).
                  sort({_id:-1}).
                  limit(1).toArray()[0].date.getTime()
var _start_date=_last_date-30000
var _isodate=new Date(_start_date)
var _result=db.coll_01.aggregate([
   { $match:{type:'A'}},
   { $match:{date:{$gte:_isodate}}},
   { $group:{_id:"$type",min:{$min:"$value"},max:{$max:"$value"},avg:{$avg:"$value"}}}
   ])

   //{ $match:{$and:[{type:'A'},{date:{$gte:_isodate}}]},

use db01
var _last_date=db.coll_01.find({type:'A'},{_id:0,date:1}).
                  sort({_id:-1}).
                  limit(1).toArray()[0].date.getTime()
var _start_date=_last_date-30000
var _isodate=new Date(_start_date)
db.coll_01.aggregate([
   { $match:{type:'A'}},
   { $group:{_id:"$type",min:{$min:"$value"},max:{$max:"$value"},avg:{$avg:"$value"}}}
   ])

db.coll_01.aggregate([
  // { $match:{$and:[{type:'A'},{date:{$gte:_isodate}}]},
   { $group:{_id:"$type",min:{$min:"$value"},max:{$max:"$value"},avg:{$avg:"$value"}}}
   ])

print(_last_date_B)
_last_date_B.date.getTime()

var _last=db.coll_01.find("{type:'A'}","{_id:0,date:1}")
                    .sort("{_id:-1}")
                    .limit(1)
db.coll_01.aggregate([
   { $match:{type:'A'} },
   { $group:{_id:"$type",min:{$min:"$value"},max:{$max:"$value"},avg:{$avg:"$value"}}}
   ])
*/
