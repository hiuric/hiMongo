package hi.db;
import otsu.hiNote.*;
import java.util.*;
/**
 * mongo-java-driver-API実行機.
 *<p>
 *拡張JSON形式のコマンドを受け、mongo-java-driverにアクセスし、結果を拡張JSON形式で返す機構です。
 *</p>
 *<p>
 *コマンドプロトコルの公開は検討中です。
 *</p>
 */
public class hiMongoWorker implements hiStringCOM {
   final static boolean D =hiMongo.MASTERD&&true;

   static boolean USE_POOL=true;
   static class Command {
      Object   connect;
      Boolean  show_dbs;
      Long     str_option;
      String   use;
      Boolean  show_collections;
      String   in;
      String[] createCapped;
      String   exists;
      Object   drop;
      // Collection以下
      Object[] execute;
      }
   static class ClientInfo {
      hiMongo.Client client;
      HashMap<String,hiMongo.DB> dbs=new HashMap<>();
      }
   static HashMap<String,ClientInfo> clients=new HashMap<>();
   static synchronized ClientInfo getClient(Object connect_){
      String     _clie_key = hiJSON.str(connect_);
      ClientInfo _clieInfo = clients.get(_clie_key);
      if( _clieInfo==null ){
         _clieInfo        = new ClientInfo();
         _clieInfo.client = hiMongo.connect(connect_);
         clients.put(_clie_key,_clieInfo);
         }
      return _clieInfo;
      }
   static synchronized hiMongo.DB getDB(ClientInfo clieInfo_,String use_){
      hiMongo.DB _db= clieInfo_.dbs.get(use_);
      if( _db==null ){
         _db = clieInfo_.client.use(use_);
         clieInfo_.dbs.put(use_,_db);
         }
      return _db;
      }
   Object namedObject(String name_,Object obj_){
       HashMap<String,Object> _ret=new HashMap<>();
       _ret.put(name_,obj_);
       return _ret;
       }
   @Override
   @SuppressWarnings("unchecked")
   public String call(String commandJson_){
      if(D)hiU.m("@@@@ WORKER COMMAND = "+commandJson_);
      hiMongoCOM.Message _result= new hiMongoCOM.Message();
      _result.format      = "hiMongo";
      _result.content_type= "result";
      _result.status      = "ok";
      //
      //if(D)hiU.m("Worker-command="+commandJson_);
      hiMongoCOM.Message _message = hiMongo.parse(commandJson_)
                                .as(hiMongoCOM.Message.class);
      Command _command   =hiMongo.parseNode(_message.content)
                                 .without_option(hiU.CHECK_UNSET_FIELD)
                                 .as(Command.class);
      long _disp_option=0;
      ArrayList<Object> _ret    = new ArrayList<>();
      _result.content=_ret;
      ClientInfo        _clieInfo= null;
      hiMongo.Client    _client; 
      if( USE_POOL ){
         _clieInfo= getClient(_command.connect);
         _client  = _clieInfo.client;
         }
      else{
         _client  = hiMongo.connect(_command.connect);
         }
      if( _command.str_option!=null ){
         _disp_option= _command.str_option;
         }
      if( _command.show_dbs!=null ){
         Object _res=namedObject(hiMongoCOM.SHOW_DBS,
                                 _client.show_dbs(_command.show_dbs));
         _ret.add(_res);
         }
      if( _command.use!=null ){
         hiMongo.DB     db;
         if( USE_POOL ) db= getDB(_clieInfo,_command.use);
         else           db= _client.use(_command.use);
         hiMongo.Collection _coll= null;
         if( _command.show_collections != null ){
            Object _res=namedObject(hiMongoCOM.SHOW_COLLECTIONS,
                                    db.show_collections(hiJSON.Probe.asBoolean(_command.show_collections)));
            _ret.add(_res);
            }
         if( _command.createCapped!=null ){
            _coll= db.createCappedCollection(_command.createCapped[0],_command.createCapped[1]);
            _ret.add(namedObject(hiMongoCOM.CREATE_CAPPED,0));
            }
         if( _command.exists!=null ){
            boolean _exists= db.exists(_command.exists);
            _ret.add(namedObject(hiMongoCOM.EXISTS,_exists));
            }
         if( _command.drop!=null ){
            db.drop();
            _ret.add(namedObject(hiMongoCOM.DROP,0));
            }
         if( _command.in != null || _coll!=null){
            if(_coll==null) _coll= db.in(_command.in);
            hiMongo.Finder     _finder = null;
            hiMongo.Aggregator _aggregator= null;
            if( _command.execute!=null )for(Object _obj:_command.execute){
               hiJSON.Probe       _probe= hiJSON.probe(_obj);
               if( _probe.has(hiMongoCOM.FIND) ){
                  hiJSON.Probe _param  = _probe.to(hiMongoCOM.FIND);
                  _finder=_coll.find(_param.get(0),_param.get(1));
                  }
               else if( _probe.has(hiMongoCOM.AGGREGATE) ){
                  hiJSON.Probe _param  = _probe.to(hiMongoCOM.AGGREGATE);
                  _aggregator=_coll.aggregate(_param.get());
                  }
               else if(_finder!=null){
                  //-------------------------------------------------
                  // Finder
                  //-------------------------------------------------
                  if( _probe.has(hiMongoCOM.SORT) ){
                     _finder.sort(_probe.get(hiMongoCOM.SORT));
                     }
                  else if( _probe.has(hiMongoCOM.LIMIT) ){
                     _finder.limit(_probe.getInt(hiMongoCOM.LIMIT));
                     }
                  else if( _probe.has(hiMongoCOM.SKIP) ){
                     _finder.skip(_probe.getInt(hiMongoCOM.SKIP));
                     }
                  else if( _probe.has(hiMongoCOM.GET_RECORD) ){
                     long _option=_probe.getLong(hiMongoCOM.GET_RECORD);
                     _ret.add(namedObject(hiMongoCOM.GET_RECORD,_finder.getDocumentList(_option)));
                     }
                  }
               else if(_aggregator!=null){
                  //-------------------------------------------------
                  // Aggregator
                  //-------------------------------------------------
                  if( _probe.has(hiMongoCOM.GET_RECORD) ){
                     long _option=_probe.getLong(hiMongoCOM.GET_RECORD);
                     _ret.add(namedObject(hiMongoCOM.GET_RECORD,_aggregator.getDocumentList(_option)));
                     }
                  }
               else if( _probe.has(hiMongoCOM.INSERT) ){
                  ArrayList<Object> _recs=(ArrayList<Object>)_probe.get(hiMongoCOM.INSERT);
                  _coll.insertMany(_recs);
                  _ret.add(_coll.count());
                  }
               else if( _probe.has(hiMongoCOM.UPDATE_ONE) ){
                  hiJSON.Probe _param  = _probe.to(hiMongoCOM.UPDATE_ONE);
                  _coll.updateOne(_param.get(0),_param.get(1));
                  _ret.add(namedObject(hiMongoCOM.UPDATE_ONE,0));
                  }
               else if( _probe.has(hiMongoCOM.UPDATE_MANY) ){
                  hiJSON.Probe _param  = _probe.to(hiMongoCOM.UPDATE_MANY);
                  _coll.updateMany(_param.get(0),_param.get(1));
                  _ret.add(namedObject(hiMongoCOM.UPDATE_MANY,0));
                  }
               else if( _probe.has(hiMongoCOM.REPLACE_ONE) ){
                  hiJSON.Probe _param  = _probe.to(hiMongoCOM.REPLACE_ONE);
                  _coll.replaceOne(_param.get(0),_param.get(1));
                  _ret.add(namedObject(hiMongoCOM.REPLACE_ONE,0));
                  }
               else if( _probe.has(hiMongoCOM.DELETE_ONE) ){
                  _coll.deleteOne(_probe.get(hiMongoCOM.DELETE_ONE));
                  _ret.add(namedObject(hiMongoCOM.REPLACE_ONE,0));
                  }
               else if( _probe.has(hiMongoCOM.DELETE_MANY) ){
                  _coll.deleteMany(_probe.get(hiMongoCOM.DELETE_MANY));
                  _ret.add(namedObject(hiMongoCOM.DELETE_MANY,0));
                  }
               else if( _probe.has(hiMongoCOM.COUNT) ){
                  _ret.add(namedObject(hiMongoCOM.COUNT,_coll.count(_probe.get(hiMongoCOM.COUNT))));
                  }
               else if( _probe.has(hiMongoCOM.DROP) ){
                  _coll.drop();
                  _ret.add(namedObject(hiMongoCOM.DROP,0));
                  }
               else if( _probe.has(hiMongoCOM.GET_INDEX_LIST) ){
                  _ret.add(namedObject(hiMongoCOM.GET_INDEX_LIST,_coll.getIndexList()));
                  }
               else if( _probe.has(hiMongoCOM.CREATE_INDEX) ){
                  hiJSON.Probe _param  = _probe.to(hiMongoCOM.CREATE_INDEX);
                  _coll.createIndex(_param.get(0),_param.get(1));
                  _ret.add(namedObject(hiMongoCOM.CREATE_INDEX,0));
                  }
               else if( _probe.has(hiMongoCOM.DROP_INDEX) ){
                  hiJSON.Probe _prb=_probe.to(hiMongoCOM.DROP_INDEX);
                  for(int _i=0;_i<_prb.size();++_i){
                     String _name= _prb.getString(_i);
                     _coll.dropIndex(_name);
                     }
                  _ret.add(namedObject(hiMongoCOM.DROP_INDEX,0));
                  }
               else if( _probe.has(hiMongoCOM.DROP_INDEXES) ){
                  _coll.dropIndexes();
                  _ret.add(namedObject(hiMongoCOM.DROP_INDEXES,0));
                  }
               }
            }
         }
      if(D)hiU.m("@@@@ WORKER RESULT = "+hiMongo.mson(_result));
      return hiMongo.mson(_result);// 
      }
   }
/*
--- !USE_POOL
Worker start
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Cluster created with settings {hosts=[localhost:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms', maxWaitQueueSize=500
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Cluster description not yet available. Waiting for 30000 ms before timing out
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Opened connection [connectionId{localValue:5, serverValue:13864}] to localhost:27017
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Monitor thread successfully connected to server with description ServerDescription{address=localhost:27017, type=STANDALONE, state=CONNECTED, ok=true, ve
]}, minWireVersion=0, maxWireVersion=6, maxDocumentSize=16777216, logicalSessionTimeoutMinutes=30, roundTripTimeNanos=887286}
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Opened connection [connectionId{localValue:6, serverValue:13865}] to localhost:27017
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Cluster created with settings {hosts=[localhost:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms', maxWaitQueueSize=500
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Cluster description not yet available. Waiting for 30000 ms before timing out
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Opened connection [connectionId{localValue:7, serverValue:13866}] to localhost:27017
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Monitor thread successfully connected to server with description ServerDescription{address=localhost:27017, type=STANDALONE, state=CONNECTED, ok=true, ve
]}, minWireVersion=0, maxWireVersion=6, maxDocumentSize=16777216, logicalSessionTimeoutMinutes=30, roundTripTimeNanos=951286}
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Opened connection [connectionId{localValue:8, serverValue:13867}] to localhost:27017
Worker end
Worker start
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Cluster created with settings {hosts=[localhost:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms', maxWaitQueueSize=500
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Cluster description not yet available. Waiting for 30000 ms before timing out
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Opened connection [connectionId{localValue:5, serverValue:13864}] to localhost:27017
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Monitor thread successfully connected to server with description ServerDescription{address=localhost:27017, type=STANDALONE, state=CONNECTED, ok=true, ve
]}, minWireVersion=0, maxWireVersion=6, maxDocumentSize=16777216, logicalSessionTimeoutMinutes=30, roundTripTimeNanos=887286}
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Opened connection [connectionId{localValue:6, serverValue:13865}] to localhost:27017
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Cluster created with settings {hosts=[localhost:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms', maxWaitQueueSize=500
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Cluster description not yet available. Waiting for 30000 ms before timing out
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Opened connection [connectionId{localValue:7, serverValue:13866}] to localhost:27017
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Monitor thread successfully connected to server with description ServerDescription{address=localhost:27017, type=STANDALONE, state=CONNECTED, ok=true, ve
]}, minWireVersion=0, maxWireVersion=6, maxDocumentSize=16777216, logicalSessionTimeoutMinutes=30, roundTripTimeNanos=951286}
10 22, 2020 11:20:51 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Opened connection [connectionId{localValue:8, serverValue:13867}] to localhost:27017
Worker end
--- USE_POOL
hiMongoSimpleServer waiting at port 8010
Worker start
10 22, 2020 11:41:28 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Cluster created with settings {hosts=[localhost:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms', maxWaitQueueSize=500}
10 22, 2020 11:41:28 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Cluster description not yet available. Waiting for 30000 ms before timing out
10 22, 2020 11:41:28 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Opened connection [connectionId{localValue:1, serverValue:13872}] to localhost:27017
10 22, 2020 11:41:28 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Monitor thread successfully connected to server with description ServerDescription{address=localhost:27017, type=STANDALONE, state=CONNECTED, ok=true, ver
]}, minWireVersion=0, maxWireVersion=6, maxDocumentSize=16777216, logicalSessionTimeoutMinutes=30, roundTripTimeNanos=3628169}
10 22, 2020 11:41:28 午前 com.mongodb.diagnostics.logging.JULLogger log
情報: Opened connection [connectionId{localValue:2, serverValue:13873}] to localhost:27017
Worker end
Worker start
Worker end
*/
