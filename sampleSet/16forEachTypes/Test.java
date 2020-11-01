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
