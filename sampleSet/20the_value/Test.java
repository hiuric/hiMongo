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
         .sort("{_id:-1}")
         .forOne("{#last_date:'date'}",Fi->{
             Fi.set_the_value(
                Fi.find("{date:{$gte:{$calc:'#last_date-25000'}}}","{_id:0}")
                  .getClassList(Record.class));
             })
         .get_the_value(new ArrayList<Record>());
      for(Record _rec:_recs){
         System.out.println(_rec.type+"/"+_rec.value+"/"+_rec.date);
         }
      }
   }
