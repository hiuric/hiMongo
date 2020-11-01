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
