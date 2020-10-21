import hi.db.hiMongo;
import otsu.hiNote.*;
import java.util.*;
public class Test {
   static class MyRecord {   // レコード内容
      String type;
      double value;
      Date   date;
      }
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      hiMongo.Collection coll
      =db.in("coll_01").drop();
      //
      ArrayList<MyRecord> _recs= new ArrayList<>();
      for(int _n=0;_n<4;++_n){
         MyRecord _rec= new MyRecord();
         _rec.type = "C";
         _rec.value= _n*10;
         _rec.date = new Date();
         _recs.add(_rec);
         }
      coll.insertMany(_recs);
      //
      MyRecord _rec = new MyRecord();
      _rec.type = "D";
      _rec.value= 12.3;
      _rec.date = new Date();
      coll.insertOne(_rec);
      //
      coll.find()
          .forEachMson(Rm->System.out.println(Rm));
      }
   }
