import hi.db.*;
import otsu.hiNote.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bson.types.ObjectId;
public class Test {
   static PrintStream ps=hiU.out;
   final static SimpleDateFormat dateFormat
         = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
   static class Record {
      org.bson.types.ObjectId _id;
      //Map<String,String> _id;
      String           type;
      double           value;
      //Map<String,Long> date;
      Date date;
      }
   static <T> String toISODate(Class<T> class_,Object obj_){
      long _unix_time =((Date)obj_).getTime();
      Date _dt       = new Date();
      _dt.setTime(_unix_time-TimeZone.getDefault().getRawOffset());
      return "ISODate(\""+dateFormat.format(_dt)+"\")";
      }
   public static void main(String[] args_){
      hiMongo.MoreMongo mongo;
      if( new File("../test_workerMode.txt").exists() ) {
         mongo=new hiMongoCaller(new hiMongoWorker());
         hiU.out.println("// caller-worker mode");
         }
      else {
         mongo=new hiMongoDirect();
         hiU.out.println("// direct mode");
         }
      String _remote="{"+
                       "host:'"+args_[0]+"',"+
                       "port:27017,"+
                       "dbName:'testDB',"+
                       "user:'testUser',"+
                       "password:'xxx'"+
                      "}";
      try{
         hiMongo.DB db=mongo.connect(_remote).use("db01");
         db.in("coll_01")                     // collection 'coll_01'選択
            .find("{type:'A'}","{_id:0}")        // typeが'A'のレコード,_idは不要
            .sort("{_id:-1}")                    // _idで逆向きにソート
            .limit(3)                            // 個数制限
            .getJsonList(hiU.REVERSE)            // 反転したリスト取得
            .forEach(Rj->hiU.out.println(Rj))   // レコード表示
            ;
         hiMongo.Finder _find=db.in("coll_01")
                                .find("{type:'A'}")
                                .sort("{_id:-1}")
                                .limit(3);
         _find.forThis(Fi->ps.println("#### MsonList"));
         _find.getMsonList(hiU.REVERSE)
              .forEach(Rm->ps.println(Rm));

         _find.forThis(Fi->ps.println("#### ClassList"));
         _find.getClassList(Record.class,hiU.REVERSE)
              .forEach(Rc->ps.println(hiU.str(Rc,hiU.WITH_TYPE)));

         _find.forThis(Fi->ps.println("#### MNodeList"));
         _find.getList(hiU.REVERSE)         // getDocumentListと同じ
              .forEach(Rd->ps.println(Rd));

         _find.forThis(Fi->ps.println("#### MNodeList/hiU.str(WITH_TYPE)"));
         _find.getDocumentList(hiU.REVERSE)
              .forEach(Rd->ps.println(hiU.str(hiMongo.parseNode(Rd).asNode(),hiU.WITH_TYPE)));

         _find.forThis(Fi->ps.println("#### MNodeList/mson"));
         _find.getList(hiU.REVERSE)
              .forEach(Rd->ps.println(hiMongo.mson(Rd)));

         _find.forThis(Fi->ps.println("#### MNodeList/json"));
         _find.getDocumentList(hiU.REVERSE)
              .forEach(Rd->ps.println(hiMongo.json(Rd)));


         _find.forThis(Fi->ps.println("#### MNodeList/mongo.str"));
         _find.getList(hiU.REVERSE)
              .forEach(Rd->ps.println(hiMongo.str(Rd)));

         ps.println("========= USE hiMongo.parse()");

         Object _f_node=hiMongo.parseText("{type:'A'}").asNode();
         Object _s_node=hiMongo.parseText("{_id:-1}").asNode();
         hiMongo.Finder _find2=db.in("coll_01")
                                .find(_f_node)
                                .sort(_s_node)
                                .limit(3);
         _find2.forThis(Fi->ps.println("#### MsonList"));
         _find2.getMsonList(hiU.REVERSE)
              .forEach(Rm->ps.println(Rm));


         ps.println("========= local USE {}");
         hiMongo.DB db2=hiMongo.connect("{}").use("db01");
         db2.in("coll_01")
            .find("{type:'A'}")
            .sort("{_id:-1}").limit(3)
            .getMsonList(hiU.REVERSE)
            .forEach(Rm->ps.println(Rm));
         ps.println("========= local USE {} node");
         Object _empty=hiMongo.parse("{}").asNode();
         hiMongo.DB db3=hiMongo.connect("{}").use("db01");
         db3.in("coll_01")
            .find("{type:'A'}")
            .sort("{_id:-1}").limit(3)
            .getMsonList(hiU.REVERSE)
            .forEach(Rm->ps.println(Rm));
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }
