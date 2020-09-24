import hi.hiMongo;
import otsu.hiNote.*;
import java.io.*;
import org.bson.Document;
public class Test {
   public static void main(String[] args_){
      if( "yes".equals(System.getenv("WITH_HSON")) ) hiMongo.with_hson(true);
      hiMongo.DB db=hiMongo.use("db02");
      db.get("composer")
        .find("{'famiryName': {'$regex': '^Ba', '$options': ''}}","{_id:0}")
        .forEachJson(R->System.out.println(R))
        .back()

        .forThis(C->System.out.println("--- start with Ba --"))
        .find("{famiryName:/^Ba/}","{_id:0}")
        .forEachJson(R->System.out.println(R))
        .back()

        .forThis(C->System.out.println("--- with ra or řá  --"))
        .find("{famiryName:/(ra|řá)/}","{_id:0}")
        .forEachJson(R->System.out.println(R))
        .back()

        .forThis(C->System.out.println("--- end with sky --"))
        .find("{famiryName:/sky$/}","{_id:0}")
        .forEachJson(R->System.out.println(R));

      Document _filt_01=Document.parse("{famiryName:/^Ba\"'/}");
      System.out.println(_filt_01.toString());
      System.out.println(_filt_01.toJson());
      System.out.println("hiMongo.str="+hiMongo.str(_filt_01,hiU.WITH_TYPE|hiU.WITH_INDENT));
      System.out.println("Mongo.mson="+hiMongo.mson(_filt_01,hiU.WITH_TYPE|hiU.WITH_INDENT|hiU.WITH_SINGLE_QUOTE));
      System.out.println("hiMongo.json="+hiMongo.json(_filt_01,hiU.WITH_TYPE|hiU.WITH_INDENT));
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
