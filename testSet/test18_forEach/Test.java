import hi.db.*;
import otsu.hiNote.*;
import java.io.*;
import java.util.*;
import java.util.Date;
import org.bson.types.ObjectId;
import org.bson.Document;
//
// ラムダ引数命名法
//   レコード
//     Document Rd
//     Class    Rc
//     Probe    Rp
//     Json     Rj
//     Mso      Rm
//   Finder     Fi
//   Aggregator Ag
//   Collection Co
//   DB         Db
//   Client     Cl
//   index-list Do レコードとは異なるDocument
//
public class Test {
   final static boolean USE_MSON=true;
   final static boolean DOT_ZERO=true;
   static String dot_zero(String value_){
      if( !DOT_ZERO ) return value_;
      return hiMongo.suprress_dot_0(value_);
      }
   static String str(Document doc_){
      if(!USE_MSON ) return dot_zero(doc_.toString());
      return hiMongo.mson(doc_);
      }
   static String json(Document doc_){
      if(!USE_MSON ) return dot_zero(doc_.toJson());
      return hiMongo.mson(doc_);
      }

   static class MyClass {
      String type;
      double value;
      Date   date;
      }
   static class MyAgClass {
      Object _id;
      double min;
      double max;
      double avg;
      }
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
      hiU.out.println("################ Finder");
      db.in("coll_01")
        .find("{}","{_id:0}")
        .limit(3) // 先頭３個
        // --- Document ---
        .forThis(Fi->hiU.out.println("==== Document"))
        .forThis(Fi->hiU.out.println("-- forEach #CRU/#CUR.value"))
        .forEach(Fi->hiU.out.println(Fi.disp("#CUR/#CUR.value")))
        .forThis(Fi->hiU.out.println("-- forEachDocument"))
        .forEachDocument(Rd->hiU.out.println(str(Rd)))
        .forThis(Fi->{
            hiU.out.println("-- getList/forEach");
            Fi.getDocumentList()
              .forEach(Rd->hiU.out.println(str(Rd)));
            hiU.out.println("-- getDocumentList/forEach");
            Fi.getDocumentList()
              .forEach(Rd->hiU.out.println(str(Rd)));
            })
        // --- Document toJson ---
        .forThis(Fi->hiU.out.println("==== Document toJson"))
        .forThis(Fi->hiU.out.println("-- forEach toJson"))
        .forEach(Fi->hiU.out.println("//"+Fi.getValueAsDocument("#CUR").toJson()))
        .forThis(Fi->hiU.out.println("-- forEachDocument toJson"))
        .forEachDocument(Rd->hiU.out.println(json(Rd)))
        .forThis(Fi->{
            hiU.out.println("-- getList/forEach toJson");
            Fi.getDocumentList()
              .forEach(Rd->hiU.out.println(json(Rd)));
            hiU.out.println("-- getDocumentList/forEach toJson");
            Fi.getDocumentList()
              .forEach(Rd->hiU.out.println(json(Rd)));
            })
        // --- Document hiMongo.json() ---
        .forThis(Fi->hiU.out.println("==== Document hiMongo.json()"))
        .forThis(Fi->hiU.out.println("-- forEach hiMongo.json()"))
        .forEach(Fi->hiU.out.println(hiMongo.json(Fi.getValueAsDocument("#CUR"))))
        .forThis(Fi->hiU.out.println("-- forEachDocument hiMongo.json()"))
        .forEachDocument(Rd->hiU.out.println(hiMongo.json(Rd)))
        .forThis(Fi->{
            hiU.out.println("-- getList/forEach hiMongo.json()");
            Fi.getDocumentList()
              .forEach(Rd->hiU.out.println(hiMongo.json(Rd)));
            hiU.out.println("-- getDocumentList/forEach hiMongo.json()");
            Fi.getDocumentList()
              .forEach(Rd->hiU.out.println(hiMongo.json(Rd)));
            })
        // --- Document hiMongo.mson() ---
        .forThis(Fi->hiU.out.println("==== Document hiMongo.mson()"))
        .forThis(Fi->hiU.out.println("-- forEach hiMongo.mson()"))
        .forEach(Fi->hiU.out.println(hiMongo.mson(Fi.getValueAsDocument("#CUR"))))
        .forThis(Fi->hiU.out.println("-- forEachDocument hiMongo.mson()"))
        .forEachDocument(Rd->hiU.out.println(hiMongo.mson(Rd)))
        .forThis(Fi->{
            hiU.out.println("-- getList/forEach hiMongo.mson()");
            Fi.getDocumentList()
              .forEach(Rd->hiU.out.println(hiMongo.mson(Rd)));
            hiU.out.println("-- getDocumentList/forEach hiMongo.mson()");
            Fi.getDocumentList()
              .forEach(Rd->hiU.out.println(hiMongo.mson(Rd)));
            })
        // --- MyClass ---
        .forThis(Fi->hiU.out.println("==== Class"))
        .forThis(Fi->hiU.out.println("-- forEach hiU.str"))
        .forEachClass(MyClass.class,Rc->hiU.out.println(hiU.str(Rc)))
        .forThis(Fi->hiU.out.println("-- forEachClass hiU.str"))
        .forEachClass(MyClass.class,Rc->hiU.out.println(hiU.str(Rc)))
        .forThis(Fi->{
            hiU.out.println("-- getList/forEach hiU.str");
            Fi.getClassList(MyClass.class)
              .forEach(Rc->hiU.out.println(hiU.str(Rc)));
            hiU.out.println("-- getClassList/forEach hiU.str");
            Fi.getClassList(MyClass.class)
              .forEach(Rc->hiU.out.println(hiU.str(Rc)));
            })
        // --- Probe ---
        .forThis(Fi->hiU.out.println("==== Probe"))
        .forThis(Fi->hiU.out.println("-- forEachProbe hiU.str"))
        .forEachProbe(Rp->hiU.out.println(hiMongo.suprress_dot_0(hiU.str(Rp))))
        .forThis(Fi->{
            hiU.out.println("-- getProbeList/forEach hiU.str");
            Fi.getProbeList()
              .forEach(Rp->hiU.out.println(hiMongo.suprress_dot_0(hiU.str(Rp))));
            })
        // --- Json ---
        .forThis(Fi->hiU.out.println("==== Json"))
        .forThis(Fi->hiU.out.println("-- forEachJson"))
        .forEachJson(Rj->hiU.out.println(Rj))
        .forThis(Fi->{
            hiU.out.println("-- getJsonList/forEach");
            Fi.getJsonList()
              .forEach(Rj->hiU.out.println(Rj));
            })
        // --- Mson ---
        .forThis(Fi->hiU.out.println("==== Mson"))
        .forThis(Fi->hiU.out.println("-- forEachJson"))
        .forEachMson(Rm->hiU.out.println(Rm))
        .forThis(Fi->{
            hiU.out.println("-- getMsonList/forEach");
            Fi.getMsonList()
              .forEach(Rm->hiU.out.println(Rm));
            })
         // --- index-list ---
        .forThis(Fi->hiU.out.println("==== back to Collection indexList(Record)"))
        .back() // Collectionに戻る
        .forThis(Co->{
            hiU.out.println("-- Co.getIndexList");
            Co.getIndexList().forEach(Do->hiU.out.println(str(Do)));
            })
         ;
      hiU.out.println("################ Aggregator");
      db.in("coll_01")
        .aggregate("["+
                "{ $match:{type:'A'}},"+
                "{ $group:{"+
                     "_id:'$type',"+
                     "min:{$min:'$value'},"+
                     "max:{$max:'$value'},"+
                     "avg:{$avg:'$value'}}}"+
                "]")
        // --- Document ---
        .forThis(Ag->hiU.out.println("==== Document"))
        .forThis(Ag->hiU.out.println("-- forEach"))
        .forEachDocument(Rd->hiU.out.println(Rd))
        .forThis(Ag->hiU.out.println("-- forEachDocument"))
        .forEachDocument(Rd->hiU.out.println(Rd))
        .forThis(Ag->{
            hiU.out.println("-- getList/forEach");
            Ag.getDocumentList()
              .forEach(Rd->hiU.out.println(Rd));
            hiU.out.println("-- getDocumentList/forEach");
            Ag.getDocumentList()
              .forEach(Rd->hiU.out.println(Rd));
            })
        // --- MyAgClass ---
        .forThis(Ag->hiU.out.println("==== Class"))
        .forThis(Ag->hiU.out.println("-- forEach hiU.str"))
        .forEachClass(MyAgClass.class,Rc->hiU.out.println(hiU.str(Rc)))
        .forThis(Ag->hiU.out.println("-- forEachClass hiU.str"))
        .forEachClass(MyAgClass.class,Rc->hiU.out.println(hiU.str(Rc)))
        .forThis(Ag->{
            hiU.out.println("-- getList/forEach hiU.str");
            Ag.getClassList(MyAgClass.class)
              .forEach(Rc->hiU.out.println(hiU.str(Rc)));
            hiU.out.println("-- getClassList/forEach hiU.str");
            Ag.getClassList(MyAgClass.class)
              .forEach(Rc->hiU.out.println(hiU.str(Rc)));
            })
        // --- Probe ---
        .forThis(Ag->hiU.out.println("==== Probe"))
        .forThis(Ag->hiU.out.println("-- forEachProbe hiU.str"))
        .forEachProbe(Rp->hiU.out.println(hiU.str(Rp)))
        .forThis(Ag->{
            hiU.out.println("-- getProbeList/forEach hiU.str");
            Ag.getProbeList()
              .forEach(Rp->hiU.out.println(hiU.str(Rp)));
            })
        // --- Json ---
        .forThis(Ag->hiU.out.println("==== Json"))
        .forThis(Ag->hiU.out.println("-- forEachJson"))
        .forEachJson(Rj->hiU.out.println(Rj))
        .forThis(Ag->{
            hiU.out.println("-- getJsonList/forEach");
            Ag.getJsonList()
              .forEach(Rj->hiU.out.println(Rj));
            })
        // --- Mson ---
        .forThis(Ag->hiU.out.println("==== Mson"))
        .forThis(Ag->hiU.out.println("-- forEachJson"))
        .forEachMson(Rm->hiU.out.println(Rm))
        .forThis(Ag->{
            hiU.out.println("-- getMsonList/forEach");
            Ag.getMsonList()
              .forEach(Rm->hiU.out.println(Rm));
            })
        ;
      }
   }
