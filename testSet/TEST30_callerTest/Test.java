import hi.db.hiMongo;
import hi.db.hiMongoWorker;
import otsu.hiNote.*;
import java.io.*;
import java.util.*;
import java.util.Date;
import org.bson.types.ObjectId;
//
//
public class Test {
   static class Record {
      String type;
      double value;
      Date   date;
      }
   public static void main(String[] args_){
      hiMongoWorker  _worker= new hiMongoWorker();
      hiU.out.println("--- Client");
      hiMongo.Client client = hiMongo.connect(_worker);
      ArrayList<String> _dbs=client.show_dbs(true);
      hiU.out.println("dbs="+hiU.str(_dbs));
      //
      hiU.out.println("--- DB");
      hiMongo.DB db= client.use("db01");
      ArrayList<String> _cols=db.show_collections(true);
      hiU.out.println("cols on db01="+hiU.str(_cols));
      //
      hiU.out.println("--- Collection");
      hiMongo.Collection _coll=db.in("coll_01");
      long _recs= _coll.count();
      hiU.out.println("recs in coll_01="+_recs);
      //
      hiU.out.println("--- finder");
      hiMongo.Finder _find=_coll.find();
      hiU.out.println("-- find()/Documnet");
      _find.forEachDocument(Rd->hiU.out.println(Rd));
      hiU.out.println("-- sort({_id,-1})/Json");
      _find.sort("{_id:-1}");
      _find.forEachJson(Rj->hiU.out.println(Rj));
      hiU.out.println("-- skip(1)/Mson");
      _find.skip(1);
      _find.forEachMson(Rm->hiU.out.println(Rm));
      hiU.out.println("-- limit(3)/Probe");
      _find.limit(3);
      _find.forEachProbe(Rp->hiU.out.println(Rp.get("value")));
      hiU.out.println("-- find({type:'A'},{_id:0})/Class");
      _coll.find("{type:'A'}","{_id:0}")
           .forEachClass(Record.class,
                         Rc->hiU.out.println(hiU.str(Rc)));
      //
      hiU.out.println("============== collection INSERT/last-3");
      _coll.insertOne("{type:'X',value:1001}");
      _coll.find("{}","{_id:0,date:0}")
           .sort("{_id:-1}")
           .limit(3)
           .getMsonList(hiU.REVERSE)
           .forEach(Rm->hiU.out.println(Rm));
      hiU.out.println("============== deleteOne(0.12)/last-3");
      _coll.deleteOne("{value:0.12}")
           .find("{}","{_id:0,date:0}")
           .sort("{_id:-1}")
           .limit(3)
           .getJsonList(hiU.REVERSE)
           .forEach(Rj->hiU.out.println(Rj));
      }
   }
