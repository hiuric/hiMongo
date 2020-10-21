
import hi.db.hiMongo;
import otsu.hiNote.*;
import java.util.*;

import org.bson.Document;

public class Test {
   final static boolean D=true;// デバグフラグ（開発時用）
   static class MyRec{
      String type;
      double value;
      }
   public static void main(String[] args_){

    try{
         hiU.out.println("====================== use hiMongo test01_hs.json");
         hiMongo.DB hmdb= hiMongo.use("db01");
         hmdb.get("coll_01")
           .find("{type:'A'}","{_id:0}")
           .sort("{_id:-1}")
           .limit(3)
           .without_option(hiU.CHECK_UNKNOWN_FIELD)
           .forThis(Fi->hiU.out.println("--- forEachMson"))
           .forEachMson(Rm->hiU.out.println(Rm))

           .forThis(Fi->hiU.out.println("--- forEachJson"))
           .forEachJson(Rj->hiU.out.println(Rj))

           .forThis(Fi->hiU.out.println("--- forEachClass"))
           .forEachClass(MyRec.class,Rc->hiU.out.println(hiU.str(Rc)))

           .forThis(Fi->hiU.out.println("--- forEachDocument"))
           .forEachDocument(Rd->hiU.out.println(Rd))

           .forThis(Fi->hiU.out.println("--- getMsonList(REVERSE)"))
           .forThis(Fi->{
               ArrayList<String> _msonList=Fi.getMsonList(hiU.REVERSE);
               _msonList.forEach(Rm->hiU.out.println(Rm));
               })
           .forThis(Fi->hiU.out.println("--- getJsonList(REVERSE)"))
           .forThis(Fi->{
               ArrayList<String> _jsonList=Fi.getJsonList(hiU.REVERSE);
               _jsonList.forEach(Rj->hiU.out.println(Rj));
               })
           .forThis(Fi->hiU.out.println("--- getClassList(REVERSE)"))
           .forThis(Fi->{
               ArrayList<MyRec> _classList=Fi.getClassList(MyRec.class,hiU.REVERSE);
               _classList.forEach(Rc->hiU.out.println(hiU.str(Rc)));
               })
           .forThis(Fi->hiU.out.println("--- getDocumentList(REVERSE)"))
           .forThis(Fi->{
               ArrayList<Document> _docList=Fi.getDocumentList(hiU.REVERSE);
               _docList.forEach(Rj->hiU.out.println(Rj));
               })
           ;

         hiMongoWorker _worker=new hiMongoWorker();

         hiU.out.println("======================= test01_hs.json");
/*
         String _commandTxt=hiFile.readTextAll("test01_hs.json");
         String _result    =_worker.call(_commandTxt);
         hiU.out.println(_result);
*/
         hiU.out.println("======================== hiMongoCaller");
         hiMongoCaller    mongo = new hiMongoCaller(_worker);
/*
         hiMongoCaller.DB db    = mongo.connect("{"+
                                     "host:'192.168.1.139',"+
                                     "port:27017,"+
                                     "dbName:'testDB',"+
                                     "user:'testUser',"+
                                     "password:'xxx'"+
                                     "}"
                                     )
                                  .use("db01");
*/
         ArrayList<String> _dbs= mongo.show_dbs(true);
         hiU.out.println("show_dbs:"+hiU.str(_dbs));
         hiMongoCaller.DB db= mongo.use("db01");
         db.in("coll_01")
           .find("{type:'A'}","{_id:0}")
           .sort("{_id:-1}")
           .limit(3)
           .without_option(hiU.CHECK_UNKNOWN_FIELD)
           //--- forEach
           .forThis(Fi->hiU.out.println("--- forEachMson"))
           .forEachMson(Rm->hiU.out.println(Rm))

           .forThis(Fi->hiU.out.println("--- forEachJson"))
           .forEachJson(Rj->hiU.out.println(Rj))

           .forThis(Fi->hiU.out.println("--- forEachDocument"))
           .forEachDocument(Rd->hiU.out.println(Rd))

           .forThis(Fi->hiU.out.println("--- forEachClass"))
           .forEachClass(MyRec.class,Rc->hiU.out.println(hiU.str(Rc)))

           //--- getList
           .forThis(Fi->hiU.out.println("--- getMsonList(REVERSE)"))
           .forThis(Fi->{
               ArrayList<String> _msonList=Fi.getMsonList(hiU.REVERSE);
               _msonList.forEach(Rm->hiU.out.println(Rm));
               })

           .forThis(Fi->hiU.out.println("--- getJsonList(REVERSE)"))
           .forThis(Fi->{
               ArrayList<String> _jsonList=Fi.getJsonList(hiU.REVERSE);
               _jsonList.forEach(Rj->hiU.out.println(Rj));
               })

           .forThis(Fi->hiU.out.println("--- getDocumentList(REVERSE)"))
           .forThis(Fi->{
               ArrayList<Document> _docList=Fi.getDocumentList(hiU.REVERSE);
               _docList.forEach(Rd->hiU.out.println(Rd));
               })

           .forThis(Fi->hiU.out.println("--- getClassList(REVERSE)"))
           .forThis(Fi->{
               ArrayList<MyRec> _classList=Fi.getClassList(MyRec.class,hiU.REVERSE);
               _classList.forEach(Rc->hiU.out.println(hiU.str(Rc)));
               })

           // --- Collection.count
           .back()
           .forThis(Co->hiU.out.println("--- Collection.count"))
           .forThis(Co->hiU.out.println("count="+Co.count()))

           // --- insert
           .forThis(Co->hiU.out.println("--- insertOne type='X'"))
           .insertOne("{type:'X',value:21,date:{$date:1597648050000}}")
           .find("{}","{_id:0}")
           .forEachMson(Rm->hiU.out.println(Rm))
           .back()

           // --- insert
           .forThis(Co->hiU.out.println("--- insertMany type='Y','Z'"))
           .insertMany("[{type:'Y',value:22,date:{$date:1597648050000}},{type:'Z',value:23,date:{$date:1597648050000}}]")
           .find("{}","{_id:0}")
           .forEachMson(Rm->hiU.out.println(Rm))
           .back()
           ;


/*
         hiMongoWalker _walker=new hiMongoWalker();
         String _commandTxt=hiFile.readTextAll("test01_hs.json");
         String _result    =_walker.call(_commandTxt);
         hiU.out.println(_result);
*/
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }
