import hi.hiMongo;
import otsu.hiNote.*;
import java.io.*;
import org.bson.Document;
import java.util.*;
public class Test {
   public static void main(String[] args_){
      if( "yes".equals(System.getenv("WITH_HSON")) ) hiMongo.with_hson(true);
      String _filter
      ="{$and:["+
                "{type :'A'},"+
                "{date :{$gte:ISODate(\"2020-08-17T07:07:00.000Z\")}},"+
                "{date2:{$gte:{$date:1597648021000}}}"+
              "]}";
      hiU.out.println("-- org.bson.Document");
      Document _doc=Document.parse(_filter);
      hiU.out.println("doc        ="+_doc);
      hiU.out.println("doc.toJson ="+_doc.toJson());
      hiU.out.println("-- hi.hiMongo");
      Object _node=hiMongo.parseText(_filter).asNode();
      hiU.out.println("node/mson  ="+hiMongo.mson(_node));
      hiU.out.println("node/json  ="+hiMongo.json(_node));
      hiU.out.println("---------");
      hiU.out.println("doc/Object ="+hiU.str(_doc,hiU.WITH_TYPE|hiU.WITH_INDENT));
      hiU.out.println("node/Object="+hiU.str(_node,hiU.WITH_TYPE|hiU.WITH_INDENT));


      }
   }
