import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import java.util.*;
public class Test { 
   public static void main(String[] args_){
      MongoClient client=new MongoClient("localhost",27017);
      MongoDatabase             db    = client.getDatabase("db01");
      MongoCollection<Document> col   = db.getCollection("coll_01");
      Document                  field = new Document();
      field.append("_id",0);
      Document                  filter= new Document();
      filter.append("type","A");
      Document                  srt   = new Document();
      srt.put("_id",-1);
      FindIterable<Document>    find  = col.find(filter)
                                           .projection(field)
                                           .sort(srt)
                                           .limit(3);
      // find.forEachはIterator/MongoIteratoの衝突のため使えません
      LinkedList<Document>      list  = new LinkedList<>();
      find.into(list);
      Collections.reverse(list);
      // ISODate()形式の出力法は不明です
      list.forEach(Rd->System.out.println(Rd.toJson()));
      }
   }
