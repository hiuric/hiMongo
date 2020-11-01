package hi.db;

import otsu.hiNote.*;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.*;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
/*
import org.bson.types.ObjectId;
import org.bson.types.Decimal128;
import org.bson.BsonRegularExpression;
*/

/*
win8  java version "1.8.0_211"
linux openjdk version "1.8.0_265"
*/
/**
 直接mongoDBアクセス機.
 */
public class hiMongoCaller implements hiMongo.MoreMongo{
   final static boolean D=true;// デバグフラグ（開発時用）
   static class Context extends hiMongoContext{
      final static String MSG_STA=
       "{format:'hiMongo',content_type:'request',status:'request',content:{";
      final static String MSG_END="}}";
      hiStringCOM jsonCOM;
      Context(hiMongoCaller this_){
         jsonCOM = this_.jsonCOM;
         creator = this_;
         }
      @Override
      @SuppressWarnings("unchecked")
      public Context clone(){
         return (Context)super.clone();
         }
      @Override
      @SuppressWarnings("unchecked")
      ArrayList<Object> call(String request_){
         String             _result = jsonCOM.call(MSG_STA+request_+MSG_END);
         hiMongoCOM.Message _msg    = hiMongo.parse(_result)
                                              .asClass(hiMongoCOM.Message.class);
         return (ArrayList<Object>)_msg.content;
         }
      @Override
      @SuppressWarnings("unchecked")
      Object call(String request_,String result_key_){
         String             _result = jsonCOM.call(MSG_STA+request_+MSG_END);
         hiMongoCOM.Message _msg    = hiMongo.parse(_result)
                                             .asClass(hiMongoCOM.Message.class);
         ArrayList<Map<String,Object>> _maps=hiMongo.parse(hiMongo.mson(_msg.content))
                                                    .asMapList();
         for(Map<String,Object> _map:_maps){
            if( _map.containsKey(result_key_) ){
               return _map.get(result_key_);
               }
            }
         throw new hiException("result for "+result_key_+" not found");
         }
      }
   final static String MSG_STA=
       "{format:'hiMongo',content_type:'request',status:'request',content:{";
   final static String MSG_END="}}";
   hiStringCOM jsonCOM;
   public hiMongoCaller(hiStringCOM jsonCOM_){
      jsonCOM    = jsonCOM_;
      }
   @SuppressWarnings("unchecked")
   private ArrayList<Object> callWorker(String request_){
      String             _result = jsonCOM.call(MSG_STA+request_+MSG_END);
      hiMongoCOM.Message _msg    = hiMongo.parse(_result)
                                              .asClass(hiMongoCOM.Message.class);
      return (ArrayList<Object>)_msg.content;
      }
   @SuppressWarnings("unchecked")
   private Object callWorker(String request_,String result_key_){
      String                 _result = jsonCOM.call(MSG_STA+request_+MSG_END);
      hiMongoCOM.Message _msg    = hiMongo.parse(_result)
                                              .asClass(hiMongoCOM.Message.class);
      ArrayList<Map<String,Object>> _maps=hiMongo.parse(hiMongo.mson(_msg.content))
                                                 .asMapList();
      for(Map<String,Object> _map:_maps){
         if( _map.containsKey(result_key_) ){
            return _map.get(result_key_);
            }
         }
      throw new hiException("result for "+result_key_+" not found");
      }
   /**
    * Worker呼び出し式アクセス機構生成機
    */
   //public hiMongoCaller(){}
   /** {@link hi.db.hiMongo.MoreMongo#connect(Object) hiMongo.MoreMongo.connect()}参照. */
   @Override // hiMongo.MoreMongo
   public Client connect(Object remote_){
      Context _context=new Context(this);
      return new Client(_context).connect(hiMongo.getRemoteInfo(remote_));
      }
   /** {@link hi.db.hiMongo.MoreMongo#use(String) hiMongo.MoreMongo.use()}参照. */
   @Override // hiMongo.MoreMongo
   public DB     use(String dbName_){
      Context _context=new Context(this);
      return new Client(_context).connect(null).use(dbName_);
      }
   /** {@link hi.db.hiMongo.MoreMongo#show_dbs(boolean) hiMongo.MoreMongo.show_dbs()}参照. */
   @Override // hiMongo.MoreMongo
   public ArrayList<String> show_dbs(boolean sort_){
      Context _context=new Context(this);
      return new Client(_context).connect().show_dbs(sort_);
      }
   /**
    *Finder
    */
   public class Finder extends  hiMongoBase.Finder{
      StringBuilder message;    // ここにはconnect/in/find[],{..},..までの電文が入っている
                                //         connect/in/aggregate{},{..},..
      final String  END_MESSAGE="]";
      @SuppressWarnings("unchecked")
      @Override
      protected ArrayList<Object> getNodeList(long option_){
         if( with_list!=null ){
             ArrayList<Object> _ret= (ArrayList<Object>)with_list;
             with_list=null;
             return _ret;
             }
         if( message==null ) return new ArrayList<Object>();
         String  _getList= message.toString()+",{"+hiMongoCOM.GET_RECORD+":"+option_+"}"+END_MESSAGE;
         ArrayList<Object> _ret=(ArrayList<Object>)callWorker(_getList,hiMongoCOM.GET_RECORD);
         return _ret;
         }
      Finder(hiMongoContext context_,Collection coll_){
         super(context_,coll_);
         }
      Finder(hiMongoContext context_,Collection coll_,String message_){
         super(context_,coll_);
         message=new StringBuilder(message_);
         }
      /**
       *FindIterableを得る.
       *hiMongoCallerでは無効
       *@return null
       */
      @Override
      public FindIterable<Document> getIterable(){
         return null;
         }
       /** {@link hi.db.hiMongo.Finder#sort(Object) hiMongo.Finder.sort()}参照. */
      @Override
      public Finder sort(Object param_){
         if( message!=null ) message.append(",{"+hiMongoCOM.SORT+":"+objToJson(param_)+"}");
         return this;
         }
      /** {@link hi.db.hiMongo.Finder#limit(int) hiMongo.Finder.limit()}参照. */
      @Override
      public Finder limit(int limit_){
         if( message!=null )message.append(",{"+hiMongoCOM.LIMIT+":"+limit_+"}");
         return this;
         }
      /** {@link hi.db.hiMongo.Finder#skip(int) hiMongo.Finder.skip()}参照. */
      @Override
      public Finder skip(int skip_){
         if( message!=null )message.append(",{"+hiMongoCOM.SKIP+":"+skip_+"}");
         return this;
         }
      } // end Finder
   /**
    * driver直呼び集計器.
    */
   public class Aggregator extends hiMongoBase.Aggregator{
      String          base_message;
      List<Bson>      procs;
      StringBuilder message;    // ここにはconnect/in/find[],{..},..までの電文が入っている
                                //         connect/in/aggregate{},{..},..
      final static String  END_MESSAGE="]";
      final static String END_AGGREGATE="}";
      @SuppressWarnings("unchecked")
      @Override
      protected ArrayList<Object> getNodeList(long option_){
         String  _getList= message.toString()+",{"+hiMongoCOM.GET_RECORD+":"+option_+"}"+END_MESSAGE;
         ArrayList<Object> _ret=(ArrayList<Object>)callWorker(_getList,hiMongoCOM.GET_RECORD);
         return _ret;
         }
      /**
       * 指定のコレクション用の集計器.
       *message_には[{aggrete:までが入っている
       *この後ろに ,match:{..} などが続く
       *最後は]}で{aggreget:[...]} となる
       *<!-- Aggregator -->
       */
      Aggregator(Collection coll_,String message_){
         super(coll_);
         base_message=message_;
         }
      /**
       * 電文を組み立てる
       */
      void constructMessage(){
         message= new StringBuilder(base_message); //[{aggreget:["
         message.append(objToJson(procs));
         message.append(END_AGGREGATE);
         }
      /**
       * イテラブル取得
       *@return 管理しているAggregateIterable<Document> 
       *<!-- Aggregator -->
       */
      public AggregateIterable<Document> getIterable(){
         return null;
         }
      /** {@link hi.db.hiMongo.Aggregator#match(Object) hiMongo.Aggregator.match()}参照. */
      @Override
      public Aggregator match(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$match",context.evalAsStr(arg_)));//,parse_engine()));
         constructMessage();
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#group(Object) hiMongo.Aggregator.group()}参照. */
      @Override
      public Aggregator group(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$group",context.evalAsStr(arg_)));//,parse_engine()));
         constructMessage();
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#lookup(Object) hiMongo.Aggregator.lookup()}参照. */
      @Override
      public Aggregator lookup(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$lookup",context.evalAsStr(arg_)));//,parse_engine()));
         constructMessage();
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#project(Object) hiMongo.Aggregator.project()}参照. */
      @Override
      public Aggregator project(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$project",context.evalAsStr(arg_)));//,parse_engine()));
         constructMessage();
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#unwind(Object) hiMongo.Aggregator.unwind()}参照. */
      @Override
      public Aggregator unwind(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$unwind",context.evalAsStr(arg_)));//,parse_engine()));
         constructMessage();
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#sort(Object) hiMongo.Aggregator.sort()}参照. */
      @Override
      public Aggregator sort(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$sort",context.evalAsStr(arg_)));//,parse_engine()));
         constructMessage();
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#limit(int) hiMongo.Aggregator.limit()}参照. */
      @Override
      public Aggregator limit(int limit_){
         procs.add(Document.parse("{$limit:"+limit_+"}"));
         constructMessage();
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#add_proc(String,Object) hiMongo.Aggregator.add_proc()}参照. */
      @Override
      public Aggregator add_proc(String proc_,Object arg_){
         procs.add(hiMongo.namedObjToDoc(proc_,context.evalAsStr(arg_)));//,parse_engine()));
         constructMessage();
         return this;
         }
      }
   /**
    * Collecction.
    */
   public class Collection extends hiMongoBase.Collection{
      String base_message;
      Collection(hiMongoContext context_,DB db_,String collectionName_){// base_messageは get:'xxxx'まで
         super(context_,db_,collectionName_);
         base_message= db_.base_message+",in:'"+collectionName_+"'";;
         }
 
      /** {@link hi.db.hiMongo.Collection#getMongoCollection() hiMongo.Collection.getMongoCollection()}参照.
       *MongoCaller.Collectionではこの関数は無効です。
       *@return null
       */
      @Override
      public MongoCollection<Document> getMongoCollection(){
         return null;
         }
      /** {@link hi.db.hiMongo.Collection#find(Object,Object) hiMongo.Collection.find()}参照. */
      @Override
      @SuppressWarnings("unchecked")
      public Finder find(Object filterJ_,Object memberJ_){
         return new Finder(context,this,base_message+",execute:[{find:["
                          +context.evalAsStr(filterJ_)+","
                          +context.evalAsStr(memberJ_)+"]}");
         }
      @Override
      public Finder withValue(String name_){
         Finder _ret=new Finder(context,this);
         _ret.withValue(name_);
         return _ret;
         }
      /** {@link hi.db.hiMongo.Collection#insertOne(Object...) hiMongo.Collection.insertOne()}参照. */
      @Override
      public Collection insertOne(Object... records_){
         StringBuilder _message= new StringBuilder(base_message+",execute:[{insert:[");
         String _dlmt="";
         hiJSON.Engine _engine=cur_engine();
         for(Object _obj:records_){
            _message.append(_dlmt+context.evalAsStr(_obj));
            _dlmt=",";
            }
         _message.append("]}]");
         callWorker(_message.toString());
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#drop() hiMongo.Collection.drop()}参照. */
      @Override
      public Collection drop(){
         String _message=base_message+",execute:[{"+hiMongoCOM.DROP+":{}}]";
         callWorker(_message,hiMongoCOM.DROP);
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#count() hiMongo.Collection.count()}参照. */
      @Override
      public long count(){
         return count("{}");
         }
      /** {@link hi.db.hiMongo.Collection#count(Object) hiMongo.Collection.count()}参照. */
      @Override
      public long count(Object filterJ_){
         String _message=base_message+",execute:[{"+hiMongoCOM.COUNT+":"+context.evalAsStr(filterJ_)+"}]";
         return hiJSON.Probe.asInt(callWorker(_message,hiMongoCOM.COUNT));
         }
      /** {@link hi.db.hiMongo.Collection#insertMany(Object...) hiMongo.Collection.insertMany()}参照. */
      @Override
      public Collection insertMany(Object... recordSets_){
         StringBuilder _message= new StringBuilder(base_message+",execute:[{insert:[");
         String _dlmt="";
         List<Document> _doc_list= new ArrayList<Document>();
         for(Object _records:recordSets_){
            _doc_list.addAll(parseAsDocumentList(_records));//,null));
            }
         for(Document _doc:_doc_list){
            _message.append(_dlmt+hiMongo.mson(_doc));//context.evalAsStr(_doc));
            _dlmt=",";
            }
         _message.append("]}]");
         ArrayList<Object> _list= callWorker(_message.toString());
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#aggregate(Object) hiMongo.Collection.aggregate()}参照. */
      @Override
      public Aggregator aggregate(Object proc_){
         Aggregator _ret=new Aggregator(this,base_message+",execute:[{aggregate:");
         _ret.procs   = parseAsBsonList(context.evalAsStr(proc_));
         _ret.constructMessage();
         return _ret;
         }
      /** {@link hi.db.hiMongo.Collection#aggregate() hiMongo.Collection.aggregate()}参照. */
      @Override
      public Aggregator aggregate(){
         Aggregator _ret=new Aggregator(this,base_message+",execute:[{aggregate:");
         _ret.procs = new ArrayList<Bson>();
         _ret.constructMessage();
         return _ret;
         }
      /** {@link hi.db.hiMongo.Collection#updateOne(Object,Object) hiMongo.Collection.updateOne()}参照. */
      @Override
      public Collection updateOne(Object filterJ_,Object updateJ_){
         StringBuilder _message= new StringBuilder(base_message+",execute:[{"+hiMongoCOM.UPDATE_ONE+":[");
         _message.append(context.evalAsStr(filterJ_))
                 .append(",")
                 .append(context.evalAsStr(updateJ_));
         _message.append("]}]");
         callWorker(_message.toString());
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#updateMany(Object,Object) hiMongo.Collection.updateMany()}参照. */
      @Override
      public Collection updateMany(Object filterJ_,Object updateJ_){
         StringBuilder _message= new StringBuilder(base_message+",execute:[{"+hiMongoCOM.UPDATE_MANY+":[");
         _message.append(context.evalAsStr(filterJ_))
                 .append(",")
                 .append(context.evalAsStr(updateJ_));
         _message.append("]}]");
         callWorker(_message.toString());
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#replaceOne(Object,Object) hiMongo.Collection.replaceOne()}参照. */
      @Override
      public Collection replaceOne(Object filterJ_,Object recordJ_){
         StringBuilder _message= new StringBuilder(base_message+",execute:[{"+hiMongoCOM.REPLACE_ONE+":[");
         _message.append(context.evalAsStr(filterJ_))
                 .append(",")
                 .append(context.evalAsStr(recordJ_));
         _message.append("]}]");
         callWorker(_message.toString());
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#deleteOne(Object) hiMongo.Collection.deleteOne()}参照. */
      @Override
      public Collection deleteOne(Object filterJ_){
         StringBuilder _message= new StringBuilder(base_message+",execute:[{"+hiMongoCOM.DELETE_ONE+":");
         _message.append(context.evalAsStr(filterJ_));
         _message.append("}]");
         callWorker(_message.toString());
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#deleteMany(Object) hiMongo.Collection.deleteMany()}参照. */
      @Override
      public Collection deleteMany(Object filterJ_){
         StringBuilder _message= new StringBuilder(base_message+",execute:[{"+hiMongoCOM.DELETE_MANY+":");
         _message.append(context.evalAsStr(filterJ_));
         _message.append("}]");
         callWorker(_message.toString());
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#createIndex(Object,Object) hiMongo.Collection.createIndex()}参照. */
      @Override
      public Collection createIndex(Object keyset_,Object option_){
         String _message=base_message+",execute:[{"+hiMongoCOM.CREATE_INDEX+":["
                                                   +context.evalAsStr(keyset_)+","
                                                   +context.evalAsStr(option_)+"]}]";
         callWorker(_message,hiMongoCOM.CREATE_INDEX);
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#createIndex(Object) hiMongo.Collection.createIndex()}参照. */
      @Override
      public Collection createIndex(Object keyset_){
         return createIndex(keyset_,null);
         }
      /** {@link hi.db.hiMongo.Collection#dropIndexes() hiMongo.Collection.dropIndexes()}参照. */
      @Override
      public Collection dropIndexes(){
         String _message=base_message+",execute:[{"+hiMongoCOM.DROP_INDEXES+":{}}]";
         callWorker(_message,hiMongoCOM.DROP_INDEXES);
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#dropIndex(String...) hiMongo.Collection.dropIndex()}参照. */
      @Override
      public Collection dropIndex(String ... index_){
         StringBuilder _message=new StringBuilder(base_message+",execute:[{"+hiMongoCOM.DROP_INDEX+":[");
         String _dlmt="";
         for(String _str:index_){
            _message.append(_dlmt+"'"+_str+"'");
            _dlmt=",";
            }
         _message.append("]}]");
         callWorker(_message.toString(),hiMongoCOM.DROP_INDEX);
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#getIndexList() hiMongo.Collection.getIndexList()}参照. */
      @Override
      @SuppressWarnings("unchecked")
      public ArrayList<Document> getIndexList(){
         ArrayList<Document> _ret=new ArrayList<Document>();
         String _message=base_message+",execute:[{"+hiMongoCOM.GET_INDEX_LIST+":{}}]";
         ArrayList<Map<String,Object>> _list=(ArrayList<Map<String,Object>>)callWorker(_message,hiMongoCOM.GET_INDEX_LIST);
         for(Map<String,Object> _map:_list)_ret.add(new Document(_map));
         return _ret;
         }
      }
   /**
    * driver直呼びだしのDatabasr
    */
   class DB extends hiMongoBase.DB{
      String base_message;
      /**
       * client(DBサーバとの接続)とデータベース名で構築する.
       *@param client_ client_
       *@param dbName_ データベース名
       *<!-- DB -->
       */
      DB(hiMongoContext context_,Client client_,String dbName_){
         super(context_,client_,dbName_);
         base_message= client_.base_message+",use:'"+dbName_+"'";
         }
      /** {@link hi.db.hiMongo.DB#in(String) hiMongo.DB.in()}参照. */
      @Override
      public hiMongo.Collection in(String collName_){
         return new Collection(this.context,this,collName_);
         }
      /** {@link hi.db.hiMongo.DB#close() hiMongo.DB.close()}参照. */
      @Override
      public void close(){
         //hiU.close(client);
         }
      /** {@link hi.db.hiMongo.DB#show_collections(boolean) hiMongo.DB.show_collections()}参照. */
      @SuppressWarnings("unchecked")
      @Override
      public ArrayList<String> show_collections(boolean sort_){
         String _message = base_message+","+hiMongoCOM.SHOW_COLLECTIONS+":"+sort_;
         ArrayList<String> _ret=(ArrayList<String>)callWorker(_message,hiMongoCOM.SHOW_COLLECTIONS);// new ArrayList<>();
         return _ret;
         }
      /** {@link hi.db.hiMongo.DB#drop() hiMongo.DB.drop()}参照. */
      @Override
      public DB drop(){
         //mongoDatabase.drop();
         String _message=base_message+","+hiMongoCOM.DROP+":{}";
         callWorker(_message,hiMongoCOM.DROP);
         return this;
         }
      /** {@link hi.db.hiMongo.DB#exists(String) hiMongo.DB.exists()}参照. */
      @Override
      public boolean exists(String collectionName_){
         String _message=base_message+","+hiMongoCOM.EXISTS+":'"+collectionName_+"'";
         return hiJSON.Probe.asBoolean(callWorker(_message,hiMongoCOM.EXISTS));
         }
      /** {@link hi.db.hiMongo.DB#createCappedCollection(String,String) hiMongo.DB.createCappedCollection()}参照. */
      @Override
      public Collection createCappedCollection(String name_,String capInfo_){
         String _message=base_message+","+hiMongoCOM.CREATE_CAPPED+":['"+name_+"','"+capInfo_+"']";
         callWorker(_message,hiMongoCOM.CREATE_CAPPED);
         return new Collection(this.context,this,name_);
         }
      }
   /**
    * driver直使用でmongoDBサーバとの接続を表す.
    */
   class Client extends hiMongoBase.Client {
      String base_message;
      /** {@link hi.db.hiMongo.Client#connect(Object) hiMongo.Client.connect()}参照. */
      Client(hiMongoContext context_){
         super(context_);
         }
      @Override
      public Client connect(Object info_){
         hiMongo.RemoteInfo _info=hiMongo.getRemoteInfo(info_);
         if( _info==null ){
            base_message="connect:{}";
            }
         else{
            base_message="connect:"+hiMongo.mson(_info);
            }
         return this;
         }
      /** {@link hi.db.hiMongo.Client#connect() hiMongo.Client.connect()}参照. */
      @Override
      public Client connect(){
         base_message="connect:{}";
         return this;
         }
      /** {@link hi.db.hiMongo.Client#use(String) hiMongo.Client.use()}参照. */
      @Override
      public DB use(String dbName_){
         return new DB(this.context,this,dbName_);
         }
      /** {@link hi.db.hiMongo.Client#show_dbs(boolean) hiMongo.Client.show_dbs()}参照. */
      @Override
      @SuppressWarnings("unchecked")
      public ArrayList<String> show_dbs(boolean sort_){
         String _message = base_message+","+hiMongoCOM.SHOW_DBS+":"+sort_;
         ArrayList<String> _ret=(ArrayList<String>)callWorker(_message,hiMongoCOM.SHOW_DBS);// new ArrayList<>();
         return _ret;
         }
      // 単独生成禁止
      //Client(){}
      /** {@link hi.db.hiMongo.Client#close() hiMongo.Client.close()}参照. */
      public void close(){
         //hiU.close(mongoClient);
         }
      }// end Client
   }

