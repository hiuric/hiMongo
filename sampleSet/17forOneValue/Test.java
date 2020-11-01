import hi.db.hiMongo;
import java.io.*;
public class Test {
   static PrintStream ps=System.out;
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      db.in("coll_01")
        .find("{}","{_id:0}")
        .forOne("{#tp:type,#va:value}",Fi->{
            ps.println("--- get");
            ps.println("#CUR      "+Fi.get("#CUR").getClass().getName()+" "+Fi.get("#CUR"));
            ps.println("#tp       "+Fi.get("#tp").getClass().getName()+" "+Fi.get("#tp"));
            ps.println("#va       "+Fi.get("#va").getClass().getName()+" "+Fi.get("#va"));
            ps.println("#CUR.type "+Fi.get("#CUR.type").getClass().getName()+" "+Fi.get("#CUR.type"));
            ps.println("#CUR.date "+Fi.get("#CUR.date").getClass().getName()+" "+Fi.get("#CUR.date"));
            ps.println("--- disp");
            ps.println(Fi.disp("CUR=#CUR"));
            ps.println(Fi.disp("tp=#tp va=#va CUR.type=#CUR.type CUR.date=#CUR.date"));
            ps.println("--- eval");
            ps.println("{$gt:{$calc:'#CUR.date+17000}'}}->"
                      +Fi.mson(Fi.eval("{$gt:{$calc:'#CUR.date+17000'}}")));
            })
        ;
      }
   }
