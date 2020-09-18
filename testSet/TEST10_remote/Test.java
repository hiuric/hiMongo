import hi.hiMongo;
import otsu.hiNote.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bson.types.ObjectId;
public class Test {
   static PrintStream ps=System.out;
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
      if( "yes".equals(System.getenv("WITH_HSON")) ) hiMongo.with_hson(true);
      String _remote="{"+
                       "host:'192.168.1.139',"+
                       "port:27017,"+
                       "dbName:'testDB',"+
                       "user:'testUser',"+
                       "password:'xxx'"+
                      "}";
      try(hiMongo.DB db=hiMongo.connect(_remote).use("db01")){
         db.get("coll_01")                     // collection 'coll_01'選択
            .find("{type:'A'}","{_id:0}")        // typeが'A'のレコード,_idは不要
            .sort("{_id:-1}")                    // _idで逆向きにソート
            .limit(3)                            // 個数制限
            .getJsonList(hiU.REVERSE)            // 反転したリスト取得
            .forEach(S->System.out.println(S))   // レコード表示
            ;
         hiMongo.Finder _find=db.get("coll_01")
                                .find("{type:'A'}")
                                .sort("{_id:-1}")
                                .limit(3);
         _find.forThis(R->ps.println("#### MsonList"));
         _find.getMsonList(hiU.REVERSE)
              .forEach(S->ps.println(S));

         _find.forThis(R->ps.println("#### ClassList"));
         _find.getClassList(Record.class,hiU.REVERSE)
              .forEach(S->ps.println(hiU.str(S,hiU.WITH_TYPE)));

         _find.forThis(R->ps.println("#### MNodeList"));
         _find.getNodeList(hiU.REVERSE)
              .forEach(S->ps.println(S));

         _find.forThis(R->ps.println("#### MNodeList/hiU.str(WITH_TYPE)"));
         _find.getNodeList(hiU.REVERSE)
              .forEach(S->ps.println(hiU.str(hiMongo.parseNode(S).asNode(),hiU.WITH_TYPE)));

         _find.forThis(R->ps.println("#### MNodeList/mson"));
         _find.getNodeList(hiU.REVERSE)
              .forEach(S->ps.println(hiMongo.mson(S)));

         _find.forThis(R->ps.println("#### MNodeList/json"));
         _find.getNodeList(hiU.REVERSE)
              .forEach(S->ps.println(hiMongo.json(S)));


         _find.forThis(R->ps.println("#### MNodeList/mongo.str"));
         _find.getNodeList(hiU.REVERSE)
              .forEach(S->ps.println(hiMongo.str(S)));

         ps.println("========= USE hiMongo.parse()");

         Object _f_node=hiMongo.parseText("{type:'A'}").asNode();
         Object _s_node=hiMongo.parseText("{_id:-1}").asNode();
         hiMongo.Finder _find2=db.get("coll_01")
                                .find(_f_node)
                                .sort(_s_node)
                                .limit(3);
         _find2.forThis(R->ps.println("#### MsonList"));
         _find2.getMsonList(hiU.REVERSE)
              .forEach(S->ps.println(S));


         ps.println("========= local USE {}");
         hiMongo.DB db2=hiMongo.connect("{}").use("db01");
         db2.get("coll_01")
            .find("{type:'A'}")
            .sort("{_id:-1}").limit(3).getMsonList(hiU.REVERSE)
            .forEach(S->ps.println(S));
         ps.println("========= local USE {} node");
         Object _empty=hiMongo.parse("{}").asNode();
         hiMongo.DB db3=hiMongo.connect("{}").use("db01");
         db3.get("coll_01")
            .find("{type:'A'}")
            .sort("{_id:-1}").limit(3).getMsonList(hiU.REVERSE)
            .forEach(S->ps.println(S));
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }
