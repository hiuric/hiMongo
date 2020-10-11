import hi.hiMongo;
import otsu.hiNote.*;
import java.io.*;
import java.util.*;
import java.util.Date;
import org.bson.types.ObjectId;
//
// ラムダ引数命名法
//   レコード
//     Document Rd
//     Class    Rc
//     Probe    Rp
//     Json     Rj
//     Mso      Rm
//   Finder     Fi
//   Aggregator Ag
//   Collection Co
//   DB         Db
//   Client     Cl
//   index-list Do レコードとは異なるDocument
//
public class Test {
   final static boolean D=true;// デバグフラグ（開発時用）
   static class DB{
      }
   static class Collection{
      }
   static class Connect {
      }
   static class Command {
      hiMongo.RemoteInfo connect;
      String             use;
      String             get;
      Object[]           execute;
      }
   public static void main(String[] args_){
      try{
         String  _commandTxt=hiFile.readTextAll("test01_hs.json");
         Command _command   =hiMongo.parse(_commandTxt)
                                    .without_option(hiU.CHECK_UNSET_FIELD)
                                    .as(Command.class);
         hiU.out.println(hiU.str(_command.connect));
         //
         hiMongo.Client     _client    = hiMongo.connect(_command.connect);
         hiMongo.DB         _db        = _client.use(_command.use);
         hiMongo.Collection _coll      = _db.get(_command.get);
         hiMongo.Finder     _finder    = null;
         hiMongo.Aggregator _aggregator= null;
         hiMongo.Probe _p=new hiMongo.Probe(null);
         for(Object _obj:_command.execute){
            hiU.out.println("obj="+hiU.str(_obj));
            hiJSON.Probe _probe= hiJSON.probe(_obj);
            HashMap<String,Object> _param=_p.asMap(_obj); // TODO:asMapをfinal staticに
            for(MapEntry<String,Object> _kv:_param.enrtySet()){
               String       _key= _kv.getKey();
               hiJSON.Probe _elm= hiJSON.probe(_kv.getValue());
               if( _key.equals("find") ){
                  Map<String,Object> _filt= _p.asMap(_elm.get(0));// TODO ProbeにgetMap
                                                                      // <2> asMapをfinal staticに
                  hiU.out.println("filter="+hiU.str(_filt));
                  Map<String,Object> _fields=p.asMap(_elm.get(1));
                  hiU.out.println("_fields="+hiU.str(_fields));
                 _coll.find(hiMongo.mson(_filt),hiMongo.mson(_fields))

.forEachMson(Rm->hiU.out.println(Rm));

                  continue;
                  }
               if( _key.equals("sort") ){
                  }
               }

            }
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }
