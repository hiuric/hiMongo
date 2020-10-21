//package hi;
import hi.db.hiMongo;
import otsu.hiNote.*;
import java.util.*;

public class hiMongoWorker implements hiJsonCOM_intf {
   final static boolean D=true;// デバグフラグ（開発時用）
   static class Command {
      Object   connect;
      Object   show_dbs;
      Long     str_option;
      String   use;
      String   get;
      Object[] execute;
      }
   final static String SHOW_DBS           = "show_dbs";
   final static String FIND               = "find";
   final static String SORT               = "sort";
   final static String LIMIT              = "limit";
   final static String COMMAND            = "command";
   final static String GET_RECORD         = "getRecord";
   final static String COUNT              = "count";
   final static String INSERT        = "insert";

   Object namedObject(String name_,Object obj_){
       HashMap<String,Object> _ret=new HashMap<>();
       _ret.put(name_,obj_);
       return _ret;
       }
   @Override
   @SuppressWarnings("unchecked")
   public String call(String commandJson_){
      Message _result= new Message();
      _result.format      = "hiMongo";
      _result.content_type="result";
      _result.status      ="ok";
      //
hiU.m("Worker-command="+commandJson_);
      Message _message = hiMongo.parse(commandJson_)
                                .as(hiJsonCOM_intf.Message.class);
      Command _command   =hiMongo.parseNode(_message.content)
                                 .without_option(hiU.CHECK_UNSET_FIELD)
                                 .as(Command.class);
      long _disp_option=0;
      ArrayList<Object> _ret    = new ArrayList<>();
      _result.content=_ret;
      hiMongo.Client    _client = hiMongo.connect(_command.connect);
      if( _command.str_option!=null ){
         _disp_option= _command.str_option;
         }
      if( _command.show_dbs!=null ){
         Object _res=namedObject(hiMongoCOM.SHOW_DBS,
                                 _client.show_dbs(hiJSON.Probe.asBoolean(_command.show_dbs)));
         _ret.add(_res);
         }
      if( _command.use!=null ){
         hiMongo.DB         _db        = _client.use(_command.use);
         hiMongo.Collection _coll      = _db.get(_command.get);
         hiMongo.Finder     _finder    = null;
         hiMongo.Aggregator _aggregator= null;
         for(Object _obj:_command.execute){
            hiJSON.Probe       _probe= hiJSON.probe(_obj);
            if( _probe.has(FIND) ){
               hiJSON.Probe _find  = _probe.to(FIND);
               _finder=_coll.find(_find.get(0),_find.get(1));
               }
            else if(_finder!=null){
               if( _probe.has(SORT) ){
                  _finder.sort(_probe.get(SORT));// findeのnullチェック
                  }
               else if( _probe.has(LIMIT) ){
                  _finder.limit(_probe.getInt(LIMIT));// findeのnullチェック
                  }
               else if( _probe.has(COMMAND) ){
                  String _sub_command=_probe.getString(COMMAND);
//hiU.m("COMMAND:"+_sub_command);
                  if( hiMongoCOM.GET_RECORD.equals(_sub_command) ){
                     _ret.add(namedObject(hiMongoCOM.GET_RECORD,_finder.getDocumentList()));
                     }
                  else if( COUNT.equals(_sub_command) ){
                     _ret.add(_coll.count());
                     }
                  }
               }
            else{
               if( _probe.has(INSERT) ){
                  ArrayList<Object> _recs=(ArrayList<Object>)_probe.get(INSERT);
                  _coll.insertMany(_recs);
                  _ret.add(_coll.count());

                  }
               else if( _probe.has(COMMAND) ){
                  String _sub_command=_probe.getString(COMMAND);
//hiU.m("COMMAND:"+_sub_command);
                  if( COUNT.equals(_sub_command) ){
                     _ret.add(namedObject(COUNT,_coll.count()));
                     //_ret.add(_coll.count());
                     }
                  }
               }
            }
         }
hiU.m("result="+hiMongo.json(_result));
      return hiMongo.json(_result);
      }
   }
