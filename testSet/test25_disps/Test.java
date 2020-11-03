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
      String _str_value="ABC";
      String _rec_str_value="{type:'STRING',value:0.1,date:{$date:1597648120000}}";
      Record _default_value=new Record();
      _default_value.type="DEFAULT_VALUE";
      String _text="STR    =#STR\nREC_STR=#REC_STR\nREC    =#REC\nCUR    =#CUR";
      hiMongo.DB db= hiMongo.use("db01");
      //db.setValue("#STR"    ,_str_value);
      db.setValue("#REC_STR",_rec_str_value);
      db.in("coll_01")
        .find("{}","{_id:0}")
        .forOne(Ra->{
           hiU.out.println("------ Field in for scope "+Ra.getClass().getName());
           hiU.out.println("-- set #CUR -> #REC");
           Ra.setValue("#REC",Ra.get("#CUR"));
           hiU.out.println("-- set #CUR.type -> #STR");
           Ra.setValue("#STR",Ra.get("#CUR.type"));
           hiU.out.println(Ra.disp(_text));
           hiU.out.println(Ra.disp(_text,hiU.KEEP_QUOTE));
           hiU.out.println(Ra.disp(_text,hiMongo.USE_JSON));
           hiU.out.println(Ra.disp(_text,hiMongo.USE_str));
           hiU.out.println(Ra.disp(_text,hiMongo.USE_str|hiU.WITH_INDENT|hiU.WITH_TYPE));
           if( Ra.get("#REC")==Ra.get("#CUR") ){
              hiU.out.println("#REC and #CUR are the same instance");
              }
           else{
              hiU.out.println("#REC and #CUR are different instances");
              }
           })
        .forOne(Ra->{
           hiU.out.println("------ Field in for other scope "+Ra.getClass().getName());
           hiU.out.println(Ra.disp(_text));
           hiU.out.println(Ra.disp(_text,hiU.KEEP_QUOTE));
           if( Ra.get("#REC")==Ra.get("#CUR") ){
              hiU.out.println("#REC and #CUR are the same instance");
              }
           else{
              hiU.out.println("#REC and #CUR are different instances");
              }
           })
        .back()
        .forThis(Ra->{
           hiU.out.println("------ Collection "+Ra.getClass().getName());
           hiU.out.println(Ra.disp(_text));
           hiU.out.println(Ra.disp(_text,hiU.KEEP_QUOTE));
           })
        ;
      // back()でもよいがここではdb変数で試してみた
      hiU.out.println("------ DB "+db.getClass().getName());
      hiU.out.println(db.disp(_text));
      hiU.out.println(db.disp(_text,hiU.KEEP_QUOTE));
      hiU.out.println(db.disp(_text,hiU.AS_NULL));
      }
   }

