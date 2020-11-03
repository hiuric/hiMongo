import hi.db.*;
import otsu.hiNote.*;
import java.io.*;
import java.util.*;
import org.bson.Document;
public class Test {
   static class WithDate { // dateだけを得るクラス
      Date date;
      }
   static class Record {   // レコード内容
      String type;
      double value;
      Date   date;
      }
   static void set_coll_01(hiMongo.DB db){
      hiU.out.println("---- coll_01");
      db.in("coll_01")
        .drop()
        .insertOne("{name:'A',value:100}"
                  ,"{name:'B',value:200}"
                  ,"{name:'B',value:400}"
                  ,"{name:'B',value:600}"
                  ,"{name:'A',value:200}"
                  ,"{name:'A',value:300}")
        .find("{}","{_id:0}")
        .forEachMson(Rm->hiU.out.println(Rm))
        ;
      }
   static void set_coll_02(hiMongo.DB db){
      hiU.out.println("---- coll_02");
      db.in("coll_02")
        .drop()
        .insertOne("{name:'X',value:100}"
                  ,"{name:'Y',value:100}"
                  ,"{name:'Z',value:400}"
                  ,"{name:'K',value:600}"
                  ,"{name:'L',value:300}")
        .find("{}","{_id:0}")
        .forEachMson(Rm->hiU.out.println(Rm))
        ;
      }
   static void set_coll_03(hiMongo.DB db){
      hiU.out.println("---- coll_03");
      db.in("coll_03")
        .insertOne("{type:'A',name:'X',value:100}"
                  ,"{type:'A',name:'Y',value:200}"
                  ,"{type:'B',name:'X',value:300}"
                  ,"{type:'C',name:'X',value:400}"
                  ,"{type:'A',name:'Z',value:500}"
                  ,"{type:'D',name:'Z',value:600}"
                  ,"{type:'C',name:'X',value:700}")
        .find("{}","{_id:0}")
        .forEachMson(Rm->hiU.out.println(Rm))
        ;
     }

   static void set_coll_04(hiMongo.DB db){
      hiU.out.println("---- coll_04");
      db.in("coll_04")
        .insertOne("{type:'H',name:'X',values:[100,300,500]}"
                  ,"{type:'H',name:'Y',values:[300,500,600,700]}")
        .find("{}","{_id:0}")
        .forEachMson(Rm->hiU.out.println(Rm))
        ;
     }
   @SuppressWarnings("unchecked")
   public static void main(String[] args_){
      //--------------------------------------------------------
      hiMongo.MoreMongo mongo;
      File _modeFile= new File("../test_workerMode.txt");
      if( _modeFile.exists() ) {
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
         hiU.out.println("// MODE: DIRECT");
         }

      //--------------------------------------------------------
      hiMongo.DB db= mongo.use("db01");
 
      hiU.out.println("============ readOne and the_value");
      // このセットはdbにデータがあるとして行う
      // 次のセットでdb.dropがあるので注意
      hiU.out.println("---- readOne");
      ArrayList<Record> _recs
      =db.setValue("#TARGET","A")
         .in("coll_01")
         .find("{type:#TARGET}","{_id:0}")
         .sort("{_id:-1}")//.limit(1)
         .readOne("{#last_date:'date'}")
         .find("{$and:["+
                     "{type:'A'},"+
                     "{date:{$gte:{$calc:'#last_date-30000'}}}"+
                      "]}",
               "{_id:0}")
         .getClassList(Record.class);
      System.out.println("records="+hiU.str(_recs,hiU.WITH_INDENT));
      _recs=null;
      hiU.out.println("---- the_value");
      ArrayList<Record> _recs2
      =db.in("coll_01")
         .find("{type:'A'}","{_id:0}")
         .sort("{_id:-1}")
         .forOne("{#last_date:'date'}",Fi->{
             Fi.set_the_value(
                Fi.find("{$and:["+
                              "{type:'A'},"+
                              "{date:{$gte:{$calc:'#last_date-30000'}}}"+
                              "]}",
                          "{_id:0}")
                  .getClassList(Record.class));
             })
         .get_the_value(new ArrayList<Record>());
      System.out.println("records="+hiU.str(_recs2,hiU.WITH_INDENT));
      hiU.out.println("---- the_value (class)");
      Record _rec_value_set=new Record();
      _rec_value_set.type="SPECIAL";
      Record _rec_value_get
      =db.in("coll_01")
         .set_the_value(_rec_value_set)
         .get_the_value(Record.class);
      hiU.out.println(hiU.str(_rec_value_get));

      hiU.out.println("============ sub query ARRAY");
      db.drop();
      set_coll_01(db);
      set_coll_02(db);
      hiU.out.println("---- coll_02 value in [100,200,300]");
      db.in("coll_02")
        .find("{value:{$in:[100,200,300]}}","{_id:0}")
        .forEachMson(Rm->hiU.out.println(Rm))
        ;
      hiU.out.println("---- coll_02 value in coll_1 use *.value");
      db.in("coll_01")
        .find("{name:'A'}","{_id:0}")
        // 複数レコードから配列を作る
        .readValueList("{'#values':'*.value'}")
        .in("coll_02")
        .find("{value:{$in:#values}}","{_id:0}")
        .forEachMson(Rm->hiU.out.println(Rm))
        ;

      hiU.out.println("============ sub query _id remain last 4");
      set_coll_01(db);
      hiU.out.println("--- readValue");
      db.in("coll_01")
        .find().sort("{_id:-1}").skip(3)
        .readOne("{'#id':'_id?{}'}")
        .deleteMany("{_id:{$lt:#id}}")
        .find("{}","{_id:0}")
        .forEachMson(Rm->hiU.out.println(Rm))
        ;

      set_coll_01(db);
      hiU.out.println("--- forOne(\"{#id:_id}\",Fi->{deleteMany #id}");
      db.in("coll_01")
        .find().sort("{_id:-1}").skip(3)
        .forOne("{'#id':'_id'}",Fi->{
            Fi.deleteMany("{_id:{$lt:#id}}");
            })
        .find("{}","{_id:0}")
        .forEachMson(Rm->hiU.out.println(Rm))
        ;

      set_coll_01(db);
      hiU.out.println("--- forOne(Fi->{deleteMany #CUR._id}");
      db.in("coll_01")
        .find().sort("{_id:-1}").skip(3)
        .forOne(Fi->{
            Fi.deleteMany("{_id:{$lt:#CUR._id}}");
            })
        .find("{}","{_id:0}")
        .forEachMson(Rm->hiU.out.println(Rm))
        ;

     // db.in("coll_01")
      hiU.out.println("============ COL3 UPDATE type=A+1.5 B-2.5 C*1.5 d/2.5");
      set_coll_03(db);
      hiU.out.println("--- forEach(\"{}\",Fi->{}");
      hiMongo.Collection coll3= db.in("coll_03");
      db.in("coll_03")
        .find("{type:'A'}")
        .forEach("{'#id':'_id','#V':value}",Fi->
                  Fi.updateOne("{_id:'#id'}",
                               //"{$set:{value:{$add:[#V,'1.5']}}}"))
                               "{$set:{value:{$calc:'#V+1.5'}}}"))
        .find("{type:'B'}")
        .forEach("{'_id':'#id','value':'#V'}",Fi->
                  Fi.updateOne("{_id:'#id'}",
                               "{$set:{value:{$calc:'#V-2.5'}}}"))
        .find("{type:'C'}")
        .forEach("{'#id':'_id','#V':value}",Fi->
                  Fi.updateOne("{_id:'#id'}",
                               //"{$set:{value:{$mul:[#V,'1.5']}}}"))
                               "{$set:{value:{$calc:'#V*1.5'}}}"))
        .find("{type:'D'}")
        .forEach("{'#id':'_id','#V':value}",Fi->
                  Fi.updateOne("{_id:'#id'}",
                               //"{$set:{value:{$div:[#V,'2.5']}}}"))
                               "{$set:{value:{$calc:'#V/2.5'}}}"))
        .find("{}","{_id:0}")
        .forEachMson(Rm->hiU.out.println(Rm));


      hiU.out.println("============ BASIC TEST");
      hiU.out.println("-- find and setValue/withValue");
      set_coll_04(db);
      db.in("coll_04")
        .find("{type:'H'}","{_id:0}")
        .setValue("#test_list","[A,B,C]")
        .withValue("#test_list")
        .forEachMson(Rm->hiU.out.println(Rm))
        .setValue("#test_rec","[{type:'a',value:3},{type:'b',value:2},{type:'c',value:1}]")
        .withValue("#test_rec")
        .forEachMson(Rm->hiU.out.println(Rm))
        .withValue("#test_rec")
        .forEach("{'#value':'value','#type':'type','#this':'@'}",Fi->{ // #
            hiU.out.println(Fi.disp("#value -- #type -- #this"));
            hiU.out.println(hiU.str(Fi.get("#this"),hiU.WITH_TYPE));
            hiU.out.println("mson="+Fi.mson(Fi.get("#this")));
            hiU.out.println("json="+Fi.json(Fi.get("#this")));
            Document _doc=new Document((Map<String,Object>)Fi.get("#this"));
            hiU.out.println("doc.toString="+_doc.toString());
            hiU.out.println("doc.toJson  ="+_doc.toJson());
            })
        ;
      hiU.out.println("-- no find and setValue/withValue");
      db.in("coll_04")
        .setValue("#test_list","[A,B,C]")
        .withValue("#test_list")
        .forEachMson(Rm->hiU.out.println(Rm))
        .setValue("#test_rec","[{type:'a',value:3},{type:'b',value:2},{type:'c',value:1}]")
        .withValue("#test_rec")
        .forEachMson(Rm->hiU.out.println(Rm))
        .withValue("#test_rec")
        .forEach("{'#value':'value','#type':'type','#this':'@'}",Fi->{ // #
            hiU.out.println(Fi.disp("#value -- #type -- #this"));
            hiU.out.println(hiU.str(Fi.get("#this"),hiU.WITH_TYPE));
            hiU.out.println("mson="+Fi.mson(Fi.get("#this")));
            hiU.out.println("json="+Fi.json(Fi.get("#this")));
            Document _doc=new Document((Map<String,Object>)Fi.get("#this"));
            hiU.out.println("doc.toString="+_doc.toString());
            hiU.out.println("doc.toJson  ="+_doc.toJson());
            })
        ;


      hiU.out.println("============ COL4");
      set_coll_04(db);
      db.in("coll_04")
        .find("{type:'H'}","{_id:0}")
        .forEach("{'#values':'values','#name':'name'}",Fi->
            Fi.withValue("#values")// find結果の代わりに#valuesをレコードリストとしてつかう
              .forEach("{#value:@}",Fi2-> // "@"はレコード全体
                  Fi2.in("coll_02")
                     .forThis(Fi3->hiU.out.println(Fi3.disp("#value on #name")))
                     .find("{value:#value}","{_id:0}")
                     .forEachMson(Rm->hiU.out.println(Rm))
                 )
            )
        ;

      hiU.out.println("============ COL4/2 forEach-#CUR and withValue-#CUR");
      set_coll_04(db);
      db.in("coll_04")
        .find("{type:'H'}","{_id:0}")
        .forEach("{'#name':'name'}",Fi->
            Fi.withValue("#CUR.values")// find結果の代わりに#valuesをレコードリストとしてつかう
              .forEach(Fi2-> // "@"はレコード全体
                  Fi2.in("coll_02")
                     .forThis(Fi3->hiU.out.println(Fi3.disp("#CUR on #name")))
                     .find("{value:#CUR}","{_id:0}")
                     .forEachMson(Rm->hiU.out.println(Rm))
                 )
            )
        ;
      }    
   }
/*
getListAs(名前[,hiU.REVERSE])

@名前             -> リスト
@名前.数値        -> 指定番のレコード
@名前.数値.要素名 -> 指定番のレコードの要素
@名前.要素名      -> 要素のリスト

{$sub:[A,B,C...]}
{$add:[A,B,C...]}
{$mul:[A,B,C...]}
{$div:[A,B,C...]}

A+B        -> {$add:[A,B]}
A*X+B/Y+D  -> {$add:[{$mul:[A,X]},{$div:[B,Y]},D]}

{$str_trm:{ text:A,start:開始,end:終了+1 }//
{$str_cat:[A,B,C...]}
{$str_regex{ text:A,pattern:B,replace:C }


      db.in("coll_01")
        .find("{}","{date:1}")
*/

