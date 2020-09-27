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
      System.out.println("################ Finder");
      db.get("coll_01")
        .find("{}","{_id:0}")
        .limit(3) // 先頭３個
        // --- Document ---
        .forThis(Fi->System.out.println("==== Document"))
        .forThis(Fi->System.out.println("-- forEach"))
        .forEach(Rd->System.out.println(Rd))
        .forThis(Fi->System.out.println("-- forEachDocument"))
        .forEachDocument(Rd->System.out.println(Rd))
        .forThis(Fi->{
            System.out.println("-- getList/forEach");
            Fi.getList()
              .forEach(Rd->System.out.println(Rd));
            System.out.println("-- getDocumentList/forEach");
            Fi.getDocumentList()
              .forEach(Rd->System.out.println(Rd));
            })
        // --- Document toJson ---
        .forThis(Fi->System.out.println("==== Document toJson"))
        .forThis(Fi->System.out.println("-- forEach toJson"))
        .forEach(Rd->System.out.println(Rd.toJson()))
        .forThis(Fi->System.out.println("-- forEachDocument toJson"))
        .forEachDocument(Rd->System.out.println(Rd.toJson()))
        .forThis(Fi->{
            System.out.println("-- getList/forEach toJson");
            Fi.getList()
              .forEach(Rd->System.out.println(Rd.toJson()));
            System.out.println("-- getDocumentList/forEach toJson");
            Fi.getDocumentList()
              .forEach(Rd->System.out.println(Rd.toJson()));
            })
        // --- Document hiMongo.json() ---
        .forThis(Fi->System.out.println("==== Document hiMongo.json()"))
        .forThis(Fi->System.out.println("-- forEach hiMongo.json()"))
        .forEach(Rd->System.out.println(hiMongo.json(Rd)))
        .forThis(Fi->System.out.println("-- forEachDocument hiMongo.json()"))
        .forEachDocument(Rd->System.out.println(hiMongo.json(Rd)))
        .forThis(Fi->{
            System.out.println("-- getList/forEach hiMongo.json()");
            Fi.getList()
              .forEach(Rd->System.out.println(hiMongo.json(Rd)));
            System.out.println("-- getDocumentList/forEach hiMongo.json()");
            Fi.getDocumentList()
              .forEach(Rd->System.out.println(hiMongo.json(Rd)));
            })
        // --- Document hiMongo.mson() ---
        .forThis(Fi->System.out.println("==== Document hiMongo.mson()"))
        .forThis(Fi->System.out.println("-- forEach hiMongo.mson()"))
        .forEach(Rd->System.out.println(hiMongo.mson(Rd)))
        .forThis(Fi->System.out.println("-- forEachDocument hiMongo.mson()"))
        .forEachDocument(Rd->System.out.println(hiMongo.mson(Rd)))
        .forThis(Fi->{
            System.out.println("-- getList/forEach hiMongo.mson()");
            Fi.getList()
              .forEach(Rd->System.out.println(hiMongo.mson(Rd)));
            System.out.println("-- getDocumentList/forEach hiMongo.mson()");
            Fi.getDocumentList()
              .forEach(Rd->System.out.println(hiMongo.mson(Rd)));
            })
        // --- MyClass ---
        .forThis(Fi->System.out.println("==== Class"))
        .forThis(Fi->System.out.println("-- forEach hiU.str"))
        .forEach(MyClass.class,Rc->System.out.println(hiU.str(Rc)))
        .forThis(Fi->System.out.println("-- forEachClass hiU.str"))
        .forEachClass(MyClass.class,Rc->System.out.println(hiU.str(Rc)))
        .forThis(Fi->{
            System.out.println("-- getList/forEach hiU.str");
            Fi.getList(MyClass.class)
              .forEach(Rc->System.out.println(hiU.str(Rc)));
            System.out.println("-- getClassList/forEach hiU.str");
            Fi.getClassList(MyClass.class)
              .forEach(Rc->System.out.println(hiU.str(Rc)));
            })
        // --- Probe ---
        .forThis(Fi->System.out.println("==== Probe"))
        .forThis(Fi->System.out.println("-- forEachProbe hiU.str"))
        .forEachProbe(Rp->System.out.println(hiU.str(Rp)))
        .forThis(Fi->{
            System.out.println("-- getProbeList/forEach hiU.str");
            Fi.getProbeList()
              .forEach(Rp->System.out.println(hiU.str(Rp)));
            })
        // --- Json ---
        .forThis(Fi->System.out.println("==== Json"))
        .forThis(Fi->System.out.println("-- forEachJson"))
        .forEachJson(Rj->System.out.println(Rj))
        .forThis(Fi->{
            System.out.println("-- getJsonList/forEach");
            Fi.getJsonList()
              .forEach(Rj->System.out.println(Rj));
            })
        // --- Mson ---
        .forThis(Fi->System.out.println("==== Mson"))
        .forThis(Fi->System.out.println("-- forEachJson"))
        .forEachMson(Rm->System.out.println(Rm))
        .forThis(Fi->{
            System.out.println("-- getMsonList/forEach");
            Fi.getMsonList()
              .forEach(Rm->System.out.println(Rm));
            })
         // --- index-list ---
        .forThis(Fi->System.out.println("==== back to Collection indexList(Record)"))
        .back() // Collectionに戻る
        .forThis(Co->{
            System.out.println("-- Co.getIndexList");
            Co.getIndexList().forEach(Do->System.out.println(Do));
            })
         ;
      System.out.println("################ Aggregator");
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
        .forThis(Ag->System.out.println("==== Document"))
        .forThis(Ag->System.out.println("-- forEach"))
        .forEach(Rd->System.out.println(Rd))
        .forThis(Ag->System.out.println("-- forEachDocument"))
        .forEachDocument(Rd->System.out.println(Rd))
        .forThis(Ag->{
            System.out.println("-- getList/forEach");
            Ag.getList()
              .forEach(Rd->System.out.println(Rd));
            System.out.println("-- getDocumentList/forEach");
            Ag.getDocumentList()
              .forEach(Rd->System.out.println(Rd));
            })
        // --- MyAgClass ---
        .forThis(Ag->System.out.println("==== Class"))
        .forThis(Ag->System.out.println("-- forEach hiU.str"))
        .forEach(MyAgClass.class,Rc->System.out.println(hiU.str(Rc)))
        .forThis(Ag->System.out.println("-- forEachClass hiU.str"))
        .forEachClass(MyAgClass.class,Rc->System.out.println(hiU.str(Rc)))
        .forThis(Ag->{
            System.out.println("-- getList/forEach hiU.str");
            Ag.getList(MyAgClass.class)
              .forEach(Rc->System.out.println(hiU.str(Rc)));
            System.out.println("-- getClassList/forEach hiU.str");
            Ag.getClassList(MyAgClass.class)
              .forEach(Rc->System.out.println(hiU.str(Rc)));
            })
        // --- Probe ---
        .forThis(Ag->System.out.println("==== Probe"))
        .forThis(Ag->System.out.println("-- forEachProbe hiU.str"))
        .forEachProbe(Rp->System.out.println(hiU.str(Rp)))
        .forThis(Ag->{
            System.out.println("-- getProbeList/forEach hiU.str");
            Ag.getProbeList()
              .forEach(Rp->System.out.println(hiU.str(Rp)));
            })
        // --- Json ---
        .forThis(Ag->System.out.println("==== Json"))
        .forThis(Ag->System.out.println("-- forEachJson"))
        .forEachJson(Rj->System.out.println(Rj))
        .forThis(Ag->{
            System.out.println("-- getJsonList/forEach");
            Ag.getJsonList()
              .forEach(Rj->System.out.println(Rj));
            })
        // --- Mson ---
        .forThis(Ag->System.out.println("==== Mson"))
        .forThis(Ag->System.out.println("-- forEachJson"))
        .forEachMson(Rm->System.out.println(Rm))
        .forThis(Ag->{
            System.out.println("-- getMsonList/forEach");
            Ag.getMsonList()
              .forEach(Rm->System.out.println(Rm));
            })
        ;
      }
   }
