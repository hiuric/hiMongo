import hi.db.*;
import otsu.hiNote.*;
import java.io.*;
import org.bson.Document;
public class Test {
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
      hiMongo.DB db=mongo.use("db02");



      db.in("composer")
        .forThis(C->hiU.out.println("--- {'$regex': '^Ba', '$options': ''}} --"))
        .find("{'famiryName': {'$regex': '^Ba', '$options': ''}}","{_id:0}")
        .forEachJson(Rj->hiU.out.println(Rj))
//        .back()
        .forThis(C->hiU.out.println("--- start with Ba --"))
        .find("{famiryName:/^Ba/}","{_id:0}")
        .forEachJson(Rj->hiU.out.println(Rj))
//        .back()

        .forThis(C->hiU.out.println("--- with ra or řá  --"))
        .find("{famiryName:/(ra|řá)/}","{_id:0}")
        .forEachJson(Rj->hiU.out.println(Rj))
//        .back()

        .forThis(C->hiU.out.println("--- end with sky --"))
        .find("{famiryName:/sky$/}","{_id:0}")
        .forEachJson(Rj->hiU.out.println(Rj));

      Document _filt_01=Document.parse("{famiryName:/^Ba\"'/}");
      hiU.out.println(_filt_01.toString());
      hiU.out.println(_filt_01.toJson());
      hiU.out.println("hiMongo.str="+hiMongo.str(_filt_01,hiU.WITH_TYPE|hiU.WITH_INDENT));
      hiU.out.println("Mongo.mson="+hiMongo.mson(_filt_01,hiU.WITH_TYPE|hiU.WITH_INDENT|hiU.WITH_SINGLE_QUOTE));
      hiU.out.println("hiMongo.json="+hiMongo.json(_filt_01,hiU.WITH_TYPE|hiU.WITH_INDENT));
      }
   }

/*
--- start with Ba --
{"famiryName":"Bach", "givenName":["Johann", "Sebastian"], "nationality":["独"], "lifeTime":[1685, 1750]}
{"famiryName":"Bartók", "givenName":["Béla"], "nationality":["ハンガリー", "米"], "lifeTime":[1881, 1945]}
--- with ra or řá  --
{"famiryName":"Brahms", "givenName":["Johannes"], "nationality":["独"], "lifeTime":[1833, 1897]}
{"famiryName":"Dvořák", "givenName":["Antonín"], "nationality":["チェコ"], "lifeTime":[1841, 1904]}
{"famiryName":"Stravinsky", "givenName":["Igor", "Fydorovich"], "nationality":["露", "米"], "lifeTime":[1756, 1791]}
--- end with sky --
{"famiryName":"Stravinsky", "givenName":["Igor", "Fydorovich"], "nationality":["露", "米"], "lifeTime":[1756, 1791]}
{"famiryName":"Tchaikovsky", "givenName":["Peter", "Ilyich"], "nationality":["露"], "lifeTime":[1840, 1893]}
Document{{famiryName=BsonRegularExpression{pattern='^Ba', options=''}}}
{"famiryName": {"$regex": "^Ba", "$options": ""}}
(Document){
   (String)"famiryName"=(BsonRegularExpression)BsonRegularExpression{pattern='^Ba', options=''}}
(Document){
   (String)"famiryName":(BsonRegularExpression){
      "pattern":(String)"^Ba",
      "options":(String)""}}
(Document){
   (String)"famiryName":(BsonRegularExpression){
      "pattern":(String)"^Ba",
      "options":(String)""}}
*/
