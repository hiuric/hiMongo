import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.BsonDocument;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import otsu.hiNote.*;
import java.util.*;
public class Test {
   public static void main(String[] args_){
      try(MongoClient client=new MongoClient("localhost",27017)){
         MongoDatabase             db    = client.getDatabase("db01");
         MongoCollection<Document> col   = db.getCollection("coll_01");
         col.drop();
         System.out.println("--- insertOne/insertMany");
         //HashMap<String,Object> doc=new HashMap<String,Object>(){{
         //   put("type","A");put("value",12.3);put("date",new Date());}};
         //col.insertOne(new Document(doc));
         Document in_doc= new Document()
               .append("type","A")
               .append("value",12.3)
               .append("date",new Date());
         col.insertOne(in_doc);
         List<Document> documents= Arrays.asList(
            new Document().append("type","A").append("value",4.56).append("date",new Date()),
            new Document().append("type","B").append("value",2001).append("date",new Date()),
            new Document().append("type","A").append("value",7.98).append("date",new Date()),
            new Document().append("type","A").append("value",0.12).append("date",new Date())
            );


         Document                  field = new Document();
         field.append("_id",0);
         field.append("date",0);


         col.insertMany(documents);
         FindIterable<Document> find = col.find().projection(field);
         for(Document out_doc:find){
            System.out.println(out_doc.toJson());
            }

        //-----------------------------------------------
         // 標準Objectを使用
         //  {$and:[{type:'A'},{value:{$lt:1}]},
         //  {$set:{value:0.01}
         //-----------------------------------------------
         System.out.println("\n--- with Object updateOne 0.12 -> 0.01");

         Document condition=new Document()
            .append("$and",
                 Arrays.asList(
                    new Document()
                        .append("type","A"),
                    new Document()
                        .append("value",
                            new Document()
                               .append("$lt",1))
                     )
                 );

         Object o_type_A  = new HashMap<String,Object>(){{put("type","A");}};
         Object o_lt_1    = new HashMap<String,Object>(){{put("$lt",1);}};
         Object o_val_lt_1= new HashMap<String,Object>(){{put("value",o_lt_1);}};
         List<Object> o_and_condition= Arrays.asList(
            o_type_A,o_val_lt_1
            );
         HashMap<String,Object> o_typeA_and_valLt1= new HashMap<String,Object>(){{put("$and",o_and_condition);}};
         Object o_val_001 = new HashMap<String,Object>(){{put("value",0.01);}};
         HashMap<String,Object> o_set_001 = new HashMap<String,Object>(){{put("$set",o_val_001);}};
         //col.updateOne(new Document(o_typeA_and_valLt1),new Document(o_set_001));
         col.updateOne(condition,new Document(o_set_001));
         find = col.find().projection(field);
         for(Document out_doc:find){
            System.out.println(out_doc.toJson());
            }


         System.out.println("condition="+hiU.str(condition,hiU.WITH_TYPE|hiU.WITH_INDENT));
         //System.out.println("filter(Obj)="+hiU.str(o_typeA_and_valLt1,hiU.WITH_TYPE|hiU.WITH_INDENT|hiU.IGNORE_toString));
         System.out.println("update(Obj)="+hiU.str(o_set_001,hiU.WITH_TYPE|hiU.WITH_INDENT|hiU.IGNORE_toString));

        //-----------------------------------------------
         // 標準Documentを使用
         //  {$and:[{type:'B'},{value:{$5gt:5}]},
         //  {$set:{value:4.32}
         //-----------------------------------------------
         System.out.println("\n--- with Document updateOne 2001->4.32");
         Document type_B  = new Document().append("type","B");
         Document gt_5    = new Document().append("$gt",5);
         Document val_gt_5= new Document().append("value",gt_5);
         List<Document> and_condition= Arrays.asList(
            type_B,val_gt_5
            );
         Document typeB_and_valGt5= new Document().append("$and",and_condition);
         Document val_432 = new Document().append("value",4.32);
         Document set_432 = new Document().append("$set",val_432);
         col.updateOne(typeB_and_valGt5,set_432);
         find = col.find().projection(field);
         for(Document out_doc:find){
            System.out.println(out_doc.toJson());
            }

         System.out.println("filter(Doc)="+hiU.str(typeB_and_valGt5,hiU.WITH_TYPE|hiU.WITH_INDENT|hiU.IGNORE_toString));
         System.out.println("update(Doc)="+hiU.str(set_432,hiU.WITH_TYPE|hiU.WITH_INDENT|hiU.IGNORE_toString));

         //-----------------------------------------------
         // Filters/Updatesを使用
         //  {$and:[{type:'A'},{value:{$lt:5}]},
         //  {$set:{value:23.4}
         //-----------------------------------------------
         System.out.println("\n--- with Bson (Filters/Updates) updateOne 2001->23.4");
         Bson filter   // Documentではない！
         =Filters.and(
            Filters.eq("type","A"),
            Filters.lt("value",5)
            );
         Bson update   // Documentではない！
         =Updates.set("value",23.4);
         col.updateOne(filter,update);
         find = col.find().projection(field);
         for(Document out_doc:find){
            System.out.println(out_doc.toJson());
            }

         System.out.println("filter="+hiU.str(filter,hiU.WITH_TYPE|hiU.WITH_INDENT|hiU.IGNORE_toString));
         System.out.println("update="+hiU.str(update,hiU.WITH_TYPE|hiU.WITH_INDENT|hiU.IGNORE_toString));
         //----------------
         //BsonDocument bfilter= filter.toBsonDocument(Document.class,codecRegistry);
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      /* Bsonは使える所と使えない所がある。Documentはどこでも使える。
         従ってBsonは使うべきではない。
         filterをBsonで受ける機能は実装しない
      try(hiMongo.DB db=hiMongo.use("db01")){
         System.out.println("\n======== use Bson to hiMongo");
         Bson filter   // Documentではない！
         =Filters.and(
            Filters.eq("type","A"),
            Filters.lt("value",5)
            );
         db.get("coll_01")
           .find(filter,"{_id:0}")
           .forEach(R->System.out.println(R));
         }
      */
      System.exit(0);
      }
   }

