import hi.db.hiMongo;
import java.util.*;
public class Test {
   static class Record {   // レコード内容
      String type;
      double value;
      Date   date;
      }
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      ArrayList<Record> _recs
      =db.in("coll_01")
         .find("{}","{_id:0}")
         .getClassList(Record.class)
         ;
      for(Record _rec:_recs){
         System.out.println(_rec.type+"/"+_rec.value);
         }
      }
   }
