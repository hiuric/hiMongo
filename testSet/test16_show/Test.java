import hi.hiMongo;
import otsu.hiNote.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bson.types.ObjectId;
public class Test {
   public static void main(String[] args_){
      ArrayList<String> _dbs= hiMongo.show_dbs(true);
      System.out.println("--- show dbs ---");
      for(String _db:_dbs){
         if( !_db.startsWith("db0") ) System.out.print("//");
         System.out.println(_db);
         }

      System.out.println("--- use(db01) show collections ---");
      ArrayList<String> _cols01=hiMongo.use("db01").show_collections(true);
      for(String _col:_cols01){
         System.out.println(_col);
         }
      System.out.println("--- use(db02) show collections ---");
      ArrayList<String> _cols02=hiMongo.use("db02").show_collections(true);
      for(String _col:_cols02){
         System.out.println(_col);
         }

      System.out.println("--- use(db01) coll_02 exists ---");
      System.out.println(hiMongo.use("db01").exists("coll_02"));
      System.out.println("--- use(db01) coll_03 exists ---");
      System.out.println(hiMongo.use("db01").exists("coll_03"));

      System.out.println("--- use(db01) coll_01 count({}) ---");
      System.out.println(hiMongo.use("db01").get("coll_01").count("{}"));
      System.out.println("--- use(db01) coll_01 count({value:{$gt:5}}) ---");
      System.out.println(hiMongo.use("db01").get("coll_01").count("{value:{$gt:5}}"));
      }
   }
