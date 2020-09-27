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
      //hiMongo.nolog(Test.class);
      if( true || "yes".equals(System.getenv("WITH_HSON")) ) hiMongo.with_hson(true);
      try{ // DBのcloseは呼ばない
         hiMongo.DB db=hiMongo.use("db01");    // database   'db01'選択
         db.get("coll_01")                     // collection 'coll_01'選択
            .find("{type:'A'}","{_id:0}")      // typeが'A'のレコード,_idは不要
            .sort("{_id:-1}")                  // _idで逆向きにソート
            .limit(3)                          // 個数制限
            // .forThis(T->T.getIterable().showRecordId(true))
            .getJsonList(hiU.REVERSE)          // 反転したリスト取得
            .forEach(Rj->System.out.println(Rj)) // レコード表示
            ;
         hiMongo.Finder _find=db.get("coll_01")
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
         _find.getDocumentList(hiU.REVERSE)
              .forEach(Rd->ps.println(Rd));

         _find.forThis(Fi->ps.println("#### MNodeList/hiU.str(WITH_TYPE)"));
         _find.getDocumentList(hiU.REVERSE)
              .forEach(Rd->ps.println(hiU.str(hiMongo.parseNode(Rd).asNode(),hiU.WITH_TYPE)));

         _find.forThis(Fi->ps.println("#### MNodeList/mson"));
         _find.getDocumentList(hiU.REVERSE)
              .forEach(Rd->ps.println(hiMongo.mson(Rd)));

         _find.forThis(Fi->ps.println("#### MNodeList/json"));
         _find.getDocumentList(hiU.REVERSE)
              .forEach(Rd->ps.println(hiMongo.json(Rd)));

         //if(true)throw new hiException("WAO!");

         _find.forThis(Fi->ps.println("#### MNodeList/mongo.str"));
         _find.getDocumentList(hiU.REVERSE)
              .forEach(Rd->ps.println(hiMongo.str(Rd)));

         ps.println("========= USE hiMongo.parse()");

         Object _f_node=hiMongo.parseText("{type:'A'}").asNode();
         Object _s_node=hiMongo.parseText("{_id:-1}").asNode();
         hiMongo.Finder _curs2=db.get("coll_01")
                                .find(_f_node)
                                .sort(_s_node)
                                .limit(3);
         _curs2.forThis(Fi->ps.println("#### MsonList"));
         _curs2.getMsonList(hiU.REVERSE)
              .forEach(Rm->ps.println(Rm));

         ps.println("========== multi forEach");
         db.get("coll_01")
           .find("{}","{_id:0}")
           .limit(2)
//           .forThis(Fi->System.out.println("json"))
//           .forEachJson(Rj->System.out.println(Rj))
           .forThis(Fi->System.out.println("mson"))
           .forEachMson(Rm->System.out.println(Rm))
/*
           .forThis(Fi->System.out.println("single-quote json"))
           .forThis(Fi->Fi.engineJ().str_format().str_option(hiU.WITH_SINGLE_QUOTE))
           .forEachJson(Rj->System.out.println(Rj))

           .forThis(Fi->System.out.println("double-quote mson"))
           .str_option(hiU.WITH_TYPE)
           .str_disable_option(hiU.WITH_SINGLE_QUOTE)
           .forEachMson(Rm->System.out.println(Rm))
*/
           .forThis(Fi->System.out.println("WITH showRecordId"))
           .forThis(Fi->Fi.getIterable().showRecordId(true))
           .forEachMson(Rm->System.out.println(Rm));
         //ps.println("WAO!");
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }
