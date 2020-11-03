import hi.db.*;
import otsu.hiNote.*;
import java.io.*;
import java.util.*;
import org.bson.Document;
public class Test {
   static class Record {
      String type;
      double value;
      Date   date;
      }
   public static void main(String[] args_){
      //--------------------------------------------------------
      hiMongo.MoreMongo mongo;
      File _modeFile= new File("../test_workerMode.txt");
      if( _modeFile.exists() ) {
         String _host= hiFile.readTextAll(_modeFile).trim();
         if( _host.length()<5 ){
            mongo=new hiMongoCaller(new hiMongoWorker());
            hiU.out.println("// MODE: Caller/Worker");
            }
         else {
            mongo=new hiMongoCaller(new hiMonWorkerSample.COM(_host,8010,3));
            hiU.out.println("// MODE: call SERVER '"+_host+"'");
            }
         }
      else {
         mongo=new hiMongoDirect();
         hiU.out.println("// MODE: DIRECT");
         }
      //--------------------------------------------------------
      // DB/Collection/Finder
      //   get(name)
      //   get(name,default)
      //   get(name,class)
      String _str_value="{type:'STRING',value:0.1,date:{$date:1597648120000}}";
      Record _default_value=new Record();
      _default_value.type="DEFAULT_VALUE";
      hiMongo.DB db= hiMongo.use("db01");
      db.setValue("#STR",_str_value);
      db.in("coll_01")
        .find("{}","{_id:0}")
        .forOne(Fi->{
           Record   _rec = null;
           Document _doc = null;

           hiU.out.println("------------ Finder");
           hiU.out.println("--- get(name)");
           hiU.out.println("DOC:"+hiU.str(Fi.get("#CUR"),hiU.WITH_TYPE));
           hiU.out.println("NUL:"+hiU.str(Fi.get("#CURx"),hiU.WITH_TYPE));
           hiU.out.println("(S):"+hiU.str(Fi.get("#STR"),hiU.WITH_TYPE));
           hiU.out.println("--- get(name,default)");
           hiU.out.println("CLS:"+hiU.str(Fi.get("#CUR",_default_value),hiU.WITH_TYPE));
           hiU.out.println("DEF:"+hiU.str(Fi.get("#CURx",_default_value),hiU.WITH_TYPE));
           hiU.out.println("STR:"+hiU.str(Fi.get("#STR",_default_value),hiU.WITH_TYPE));
           hiU.out.println("--- get(name,class)");
           hiU.out.println("CLS:"+hiU.str(Fi.get("#CUR",Record.class),hiU.WITH_TYPE));
           hiU.out.println("NUL:"+hiU.str(Fi.get("#CURx",Record.class),hiU.WITH_TYPE));
           hiU.out.println("STR:"+hiU.str(Fi.get("#STR",Record.class),hiU.WITH_TYPE));

           hiMongo.Collection Co=Fi.back();
           hiU.out.println("------------ Collection "+Co.getClass().getName());
           hiU.out.println("--- get(name)");
           hiU.out.println("DOC:"+hiU.str(Co.get("#CUR"),hiU.WITH_TYPE));
           hiU.out.println("NUL:"+hiU.str(Co.get("#CURx"),hiU.WITH_TYPE));
           hiU.out.println("(S):"+hiU.str(Co.get("#STR"),hiU.WITH_TYPE));
           hiU.out.println("--- get(name,default)");
           hiU.out.println("CLS:"+hiU.str(Co.get("#CUR",_default_value),hiU.WITH_TYPE));
           hiU.out.println("DEF:"+hiU.str(Co.get("#CURx",_default_value),hiU.WITH_TYPE));
           hiU.out.println("STR:"+hiU.str(Co.get("#STR",_default_value),hiU.WITH_TYPE));
           hiU.out.println("--- get(name,class)");
           hiU.out.println("CLS:"+hiU.str(Co.get("#CUR",Record.class),hiU.WITH_TYPE));
           hiU.out.println("NUL:"+hiU.str(Co.get("#CURx",Record.class),hiU.WITH_TYPE));
           hiU.out.println("STR:"+hiU.str(Co.get("#STR",Record.class),hiU.WITH_TYPE));

  
           hiMongo.DB Db=Co.back();
           hiU.out.println("------------ DB "+Db.getClass().getName());
           hiU.out.println("--- get(name)");
           hiU.out.println("DOC:"+hiU.str(Db.get("#CUR"),hiU.WITH_TYPE));
           hiU.out.println("NUL:"+hiU.str(Db.get("#CURx"),hiU.WITH_TYPE));
           hiU.out.println("(S):"+hiU.str(Db.get("#STR"),hiU.WITH_TYPE));
           hiU.out.println("--- get(name,default)");
           hiU.out.println("CLS:"+hiU.str(Db.get("#CUR",_default_value),hiU.WITH_TYPE));
           hiU.out.println("DEF:"+hiU.str(Db.get("#CURx",_default_value),hiU.WITH_TYPE));
           hiU.out.println("STR:"+hiU.str(Db.get("#STR",_default_value),hiU.WITH_TYPE));
           hiU.out.println("--- get(name,class)");
           hiU.out.println("CLS:"+hiU.str(Db.get("#CUR",Record.class),hiU.WITH_TYPE));
           hiU.out.println("NUL:"+hiU.str(Db.get("#CURx",Record.class),hiU.WITH_TYPE));
           hiU.out.println("STR:"+hiU.str(Db.get("#STR",Record.class),hiU.WITH_TYPE));
           })
        ;
      }
   }

