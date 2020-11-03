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
      String _str1_value="{type:'STR-1',value:0.1,date:{$date:1597648120000}}";
      String _strE_value="{type:'STR-1',value:0.1}";
      Record _rec_value=new Record();
      _rec_value.type="REC_VALUE";
      Record _default_value=new Record();
      _default_value.type="DEFAULT_VALUE";
      hiMongo.DB db= hiMongo.use("db01");
      //==========================================================
      hiU.out.println("------------ DB->Collection");
      hiU.out.println("-- null");
      db.set_the_value(null);
      db.in("coll_01")
        .forThis(Ra->{
           hiU.out.println("-- "+Ra.getClass().getName());
           hiU.out.println("NUL:"+hiU.str(Ra.get_the_value(),hiU.WITH_TYPE));
           hiU.out.println("DEF:"+hiU.str(Ra.get_the_value(_default_value),hiU.WITH_TYPE));
           hiU.out.println("NUL:"+hiU.str(Ra.get_the_value(Record.class),hiU.WITH_TYPE));
           });
      hiU.out.println("-- REC");
      db.set_the_value(_rec_value);
      db.in("coll_01")
        .forThis(Ra->{
           hiU.out.println("-- "+Ra.getClass().getName());
           hiU.out.println("REC:"+hiU.str(Ra.get_the_value(),hiU.WITH_TYPE));
           hiU.out.println("REC:"+hiU.str(Ra.get_the_value(_default_value),hiU.WITH_TYPE));
           hiU.out.println("REC:"+hiU.str(Ra.get_the_value(Record.class),hiU.WITH_TYPE));
           });
      hiU.out.println("-- STR");
      db.set_the_value(_str1_value);
      db.in("coll_01")
        .forThis(Ra->{
           hiU.out.println("-- "+Ra.getClass().getName());
           hiU.out.println("(S):"+hiU.str(Ra.get_the_value(),hiU.WITH_TYPE));
           hiU.out.println("REC:"+hiU.str(Ra.get_the_value(_default_value),hiU.WITH_TYPE));
           hiU.out.println("REC:"+hiU.str(Ra.get_the_value(Record.class),hiU.WITH_TYPE));
           });
      hiU.out.println("-- STR ERROR");
      db.set_the_value(_strE_value);
      db.in("coll_01")
        .forThis(Ra->{
           hiU.out.println("-- "+Ra.getClass().getName());
           hiU.out.println("(S):"+hiU.str(Ra.get_the_value(),hiU.WITH_TYPE));
           hiU.out.println("DEF:"+hiU.str(Ra.get_the_value(_default_value),hiU.WITH_TYPE));
           try{
              hiU.out.println("---:"+hiU.str(Ra.get_the_value(Record.class),hiU.WITH_TYPE));
              }
           catch(Exception _ex){
              hiU.out.println("EXP:"+_ex.getMessage());
              }
           });
      //==========================================================
      hiU.out.println("------------ Collection->Finder");
      db.in("coll_01")
        .forThis(Ra->hiU.out.println("-- null"))
        .set_the_value(null)
        .find("{}","{_id:0}")
        .forThis(Ra->{
           hiU.out.println("-- "+Ra.getClass().getName());
           hiU.out.println("NUL:"+hiU.str(Ra.get_the_value(),hiU.WITH_TYPE));
           hiU.out.println("DEF:"+hiU.str(Ra.get_the_value(_default_value),hiU.WITH_TYPE));
           hiU.out.println("NUL:"+hiU.str(Ra.get_the_value(Record.class),hiU.WITH_TYPE));
           })
        .back()
        .forThis(Ra->hiU.out.println("-- REC"))
        .set_the_value(_rec_value)
        .find("{}","{_id:0}")
        .forThis(Ra->{
           hiU.out.println("-- "+Ra.getClass().getName());
           hiU.out.println("NUL:"+hiU.str(Ra.get_the_value(),hiU.WITH_TYPE));
           hiU.out.println("DEF:"+hiU.str(Ra.get_the_value(_default_value),hiU.WITH_TYPE));
           hiU.out.println("NUL:"+hiU.str(Ra.get_the_value(Record.class),hiU.WITH_TYPE));
           })
        .back()
        .forThis(Ra->hiU.out.println("-- STR"))
        .set_the_value(_str1_value)
        .find("{}","{_id:0}")
        .forThis(Ra->{
           hiU.out.println("-- "+Ra.getClass().getName());
           hiU.out.println("(S):"+hiU.str(Ra.get_the_value(),hiU.WITH_TYPE));
           hiU.out.println("REC:"+hiU.str(Ra.get_the_value(_default_value),hiU.WITH_TYPE));
           hiU.out.println("REC:"+hiU.str(Ra.get_the_value(Record.class),hiU.WITH_TYPE));
           })
        .back()
        .forThis(Ra->hiU.out.println("-- STR ERROR"))
        .set_the_value(_strE_value)
        .find("{}","{_id:0}")
        .forThis(Ra->{
           hiU.out.println("-- "+Ra.getClass().getName());
           hiU.out.println("(S):"+hiU.str(Ra.get_the_value(),hiU.WITH_TYPE));
           hiU.out.println("DEF:"+hiU.str(Ra.get_the_value(_default_value),hiU.WITH_TYPE));
           try{
              hiU.out.println("---:"+hiU.str(Ra.get_the_value(Record.class),hiU.WITH_TYPE));
              }
           catch(Exception _ex){
              hiU.out.println("EXP:"+_ex.getMessage());
              }
           })
        ;
      //==========================================================
      hiU.out.println("------------ DB:Collection<-Finder(for-scope)");
      db.in("coll_01")
        .find("{}","{_id:0}")
        .forThis(Ra->hiU.out.println("--- #CUR"))
        .forOne(Ra->{
           Ra.set_the_value(Ra.get("#CUR"));
           })
        .back()
        .forThis(Ra->{
           hiU.out.println("-- Collection "+Ra.getClass().getName());
           hiU.out.println("REC:"+hiU.str(Ra.get_the_value(),hiU.WITH_TYPE));
           hiU.out.println("REC:"+hiU.str(Ra.get_the_value(_default_value),hiU.WITH_TYPE));
           hiU.out.println("REC:"+hiU.str(Ra.get_the_value(Record.class),hiU.WITH_TYPE));
           })
        .back()
        .forThis(Ra->{
           hiU.out.println("-- DB "+Ra.getClass().getName());
           hiU.out.println("REC:"+hiU.str(Ra.get_the_value(),hiU.WITH_TYPE));
           hiU.out.println("REC:"+hiU.str(Ra.get_the_value(_default_value),hiU.WITH_TYPE));
           hiU.out.println("REC:"+hiU.str(Ra.get_the_value(Record.class),hiU.WITH_TYPE));
           })
        ;
      }
   }

