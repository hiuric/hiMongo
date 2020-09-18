import hi.hiMongo;
import otsu.hiNote.*;
import java.util.*;
import org.bson.Document;
public class Test {
   public static void main(String[] args_){
      String _filter
      ="{$and:["+
                "{type :'A'},"+
                "{date :{$gte:ISODate('2020-08-17T07:07:00.000Z')}},"+
                "{date2:{$gte:{$date:1597648021000}}}"+
              "]}";
      System.out.println("-- org.bson.Document");
      Document _doc=Document.parse(_filter);
      System.out.println("doc        ="+_doc);
      System.out.println("doc.toJson ="+_doc.toJson());
      System.out.println("-- hi.hiMongo");
      Object _node=hiMongo.parseText(_filter).asNode();
      System.out.println("node/mson  ="+hiMongo.mson(_node));
      System.out.println("node/json  ="+hiMongo.json(_node));
      System.out.println("---------");
      System.out.println("doc/Object ="+hiU.str(_doc,hiU.WITH_TYPE|hiU.WITH_INDENT));
      System.out.println("node/Object="+hiU.str(_node,hiU.WITH_TYPE|hiU.WITH_INDENT));
      }
   }
