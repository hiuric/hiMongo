import hi.hiMongo;
import otsu.hiNote.*;
import java.util.*;
public class Test {
   static class MyRecord { 
      String type;
      double value;
      Date   date;
      @hiU.AltName("$recordId")
      long   record_id;
      }
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      db.get("coll_01")
        .find("{}","{_id:0}")
        .limit(3)
        .forThis(Fi->Fi.getIterable().showRecordId(true))
        .forEachDocument(Rd->hiU.out.println(Rd))
        .forEachClass(MyRecord.class,Rc->hiU.out.println(hiU.str(Rc)));
      }
   }
