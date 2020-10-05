import hi.hiMongo;
import otsu.hiNote.*;
import java.io.*;
import java.util.*;
import java.util.Date;
import org.bson.types.ObjectId;
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
      hiMongo.DB db= hiMongo.use("db01");
      hiU.out.println("################ Finder");
      db.get("coll_01")
        .find("{}","{_id:0}")
        .limit(3) // 先頭３個
        // --- Document ---
        .forThis(Fi->hiU.out.println("==== Document"))
        .forThis(Fi->hiU.out.println("-- forEach"))
        .forEach(Rd->hiU.out.println(Rd))
        .forThis(Fi->hiU.out.println("-- forEachDocument"))
        .forEachDocument(Rd->hiU.out.println(Rd))
        .forThis(Fi->{
            hiU.out.println("-- getList/forEach");
            Fi.getList()
              .forEach(Rd->hiU.out.println(Rd));
            hiU.out.println("-- getDocumentList/forEach");
            Fi.getDocumentList()
              .forEach(Rd->hiU.out.println(Rd));
            })
        // --- Document toJson ---
        .forThis(Fi->hiU.out.println("==== Document toJson"))
        .forThis(Fi->hiU.out.println("-- forEach toJson"))
        .forEach(Rd->hiU.out.println(Rd.toJson()))
        .forThis(Fi->hiU.out.println("-- forEachDocument toJson"))
        .forEachDocument(Rd->hiU.out.println(Rd.toJson()))
        .forThis(Fi->{
            hiU.out.println("-- getList/forEach toJson");
            Fi.getList()
              .forEach(Rd->hiU.out.println(Rd.toJson()));
            hiU.out.println("-- getDocumentList/forEach toJson");
            Fi.getDocumentList()
              .forEach(Rd->hiU.out.println(Rd.toJson()));
            })
        // --- Document hiMongo.json() ---
        .forThis(Fi->hiU.out.println("==== Document hiMongo.json()"))
        .forThis(Fi->hiU.out.println("-- forEach hiMongo.json()"))
        .forEach(Rd->hiU.out.println(hiMongo.json(Rd)))
        .forThis(Fi->hiU.out.println("-- forEachDocument hiMongo.json()"))
        .forEachDocument(Rd->hiU.out.println(hiMongo.json(Rd)))
        .forThis(Fi->{
            hiU.out.println("-- getList/forEach hiMongo.json()");
            Fi.getList()
              .forEach(Rd->hiU.out.println(hiMongo.json(Rd)));
            hiU.out.println("-- getDocumentList/forEach hiMongo.json()");
            Fi.getDocumentList()
              .forEach(Rd->hiU.out.println(hiMongo.json(Rd)));
            })
        // --- Document hiMongo.mson() ---
        .forThis(Fi->hiU.out.println("==== Document hiMongo.mson()"))
        .forThis(Fi->hiU.out.println("-- forEach hiMongo.mson()"))
        .forEach(Rd->hiU.out.println(hiMongo.mson(Rd)))
        .forThis(Fi->hiU.out.println("-- forEachDocument hiMongo.mson()"))
        .forEachDocument(Rd->hiU.out.println(hiMongo.mson(Rd)))
        .forThis(Fi->{
            hiU.out.println("-- getList/forEach hiMongo.mson()");
            Fi.getList()
              .forEach(Rd->hiU.out.println(hiMongo.mson(Rd)));
            hiU.out.println("-- getDocumentList/forEach hiMongo.mson()");
            Fi.getDocumentList()
              .forEach(Rd->hiU.out.println(hiMongo.mson(Rd)));
            })
        // --- MyClass ---
        .forThis(Fi->hiU.out.println("==== Class"))
        .forThis(Fi->hiU.out.println("-- forEach hiU.str"))
        .forEach(MyClass.class,Rc->hiU.out.println(hiU.str(Rc)))
        .forThis(Fi->hiU.out.println("-- forEachClass hiU.str"))
        .forEachClass(MyClass.class,Rc->hiU.out.println(hiU.str(Rc)))
        .forThis(Fi->{
            hiU.out.println("-- getList/forEach hiU.str");
            Fi.getList(MyClass.class)
              .forEach(Rc->hiU.out.println(hiU.str(Rc)));
            hiU.out.println("-- getClassList/forEach hiU.str");
            Fi.getClassList(MyClass.class)
              .forEach(Rc->hiU.out.println(hiU.str(Rc)));
            })
        // --- Probe ---
        .forThis(Fi->hiU.out.println("==== Probe"))
        .forThis(Fi->hiU.out.println("-- forEachProbe hiU.str"))
        .forEachProbe(Rp->hiU.out.println(hiU.str(Rp)))
        .forThis(Fi->{
            hiU.out.println("-- getProbeList/forEach hiU.str");
            Fi.getProbeList()
              .forEach(Rp->hiU.out.println(hiU.str(Rp)));
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
            Co.getIndexList().forEach(Do->hiU.out.println(Do));
            })
         ;
      hiU.out.println("################ Aggregator");
      db.get("coll_01")
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
        .forEach(Rd->hiU.out.println(Rd))
        .forThis(Ag->hiU.out.println("-- forEachDocument"))
        .forEachDocument(Rd->hiU.out.println(Rd))
        .forThis(Ag->{
            hiU.out.println("-- getList/forEach");
            Ag.getList()
              .forEach(Rd->hiU.out.println(Rd));
            hiU.out.println("-- getDocumentList/forEach");
            Ag.getDocumentList()
              .forEach(Rd->hiU.out.println(Rd));
            })
        // --- MyAgClass ---
        .forThis(Ag->hiU.out.println("==== Class"))
        .forThis(Ag->hiU.out.println("-- forEach hiU.str"))
        .forEach(MyAgClass.class,Rc->hiU.out.println(hiU.str(Rc)))
        .forThis(Ag->hiU.out.println("-- forEachClass hiU.str"))
        .forEachClass(MyAgClass.class,Rc->hiU.out.println(hiU.str(Rc)))
        .forThis(Ag->{
            hiU.out.println("-- getList/forEach hiU.str");
            Ag.getList(MyAgClass.class)
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
