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
import org.bson.types.ObjectId;
import org.bson.types.Decimal128;
import org.bson.BsonRegularExpression;

/*
win8  java version "1.8.0_211"
linux openjdk version "1.8.0_265"
*/
/**
 直接mongoDBアクセス機.
<p>
driverを直接呼ぶ形での{@link hi.db.hiMongo}実装です。
</p>
 */
public class hiMongoDirect implements hiMongo.MoreMongo{
   final static boolean D=true;// デバグフラグ（開発時用）
   static class Context extends hiMongoContext {
      Context(hiMongoDirect this_){
         creator = this_;
         }
      @Override
      public Context clone(){
         return (Context)super.clone();
         }
      @Override
      ArrayList<Object> call(String request_){
         throw new hiException("hiMongoDirect.Contex.call() called");
         }
      @Override
      Object call(String request_,String result_key_){
         throw new hiException("hiMongoDirect.Contex.call() called");
         }
      }
   /**
    *mongo-java-driver直叩き式アクセス機構生成機
    */
   public hiMongoDirect(){}
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
    *driver直叩きFinder
    */
   static class Finder extends hiMongoBase.Finder{
      MongoIterable<Document> records;
      Finder(hiMongoContext context_,Collection collection_){
         super(context_,collection_);
         }
      @Override
      @SuppressWarnings("unchecked")
      protected ArrayList<Object> getNodeList(long option_){
         if( with_list!=null ){
             ArrayList<Object> _ret= (ArrayList<Object>)with_list;
             with_list=null;
             return _ret;
             }
         ArrayList<Object>   _ret   = new ArrayList<>();
         if( records==null ) return _ret;
         for(Document _record:records) _ret.add(_record);
         if( (option_&hiU.REVERSE)!=0 ) Collections.reverse(_ret);
         return _ret;
         }
      /**
       *FindIterableを得る.
       *@return このFinderのFindIterable
       */
      @Override
      public FindIterable<Document> getIterable(){
         return (FindIterable<Document>)records;
         }
      /** {@link hi.db.hiMongo.Finder#sort(Object) hiMongo.Finder.sort()}参照. */
      @Override
      public Finder sort(Object sort_condition_){
         if( records!=null ){
           ((FindIterable<Document>)records).sort(context.evalAsBson(sort_condition_));
           //((FindIterable<Document>)records).sort(hiMongo.objToDoc(sort_condition_,parse_engine()));
           }
         return this;
         }
      /** {@link hi.db.hiMongo.Finder#limit(int) hiMongo.Finder.limit()}参照. */
      @Override
      public Finder limit(int limit_){
         if( records!=null )((FindIterable<Document>)records).limit(limit_);
         return this;
         }
      /** {@link hi.db.hiMongo.Finder#skip(int) hiMongo.Finder.skip()}参照. */
      @Override
      public Finder skip(int skip_){
         if( records!=null )((FindIterable<Document>)records).skip(skip_);
         return this;
         }
      } // end Finder
   /**
    * driver直呼び集計器.
    */
   static class Aggregator extends hiMongoBase.Aggregator{
      MongoIterable<Document> records;
      @SuppressWarnings("unchecked")
      @Override
      protected ArrayList<Object> getNodeList(long option_){
         ArrayList<Object>   _ret   = new ArrayList<>();
         for(Document _record:records) _ret.add(_record);
         if( (option_&hiU.REVERSE)!=0 ) Collections.reverse(_ret);
         return _ret;
         }
      MongoCollection<Document> mongoCollection;
      List<Bson>      procs;
      /**
       * 指定のコレクション用の集計器.
       *<!-- Aggregator -->
       */
      Aggregator(Collection collection_){
         super(collection_);
         mongoCollection= collection_.mongoCollection;
         }
      /**
       * イテラブル取得
       *@return 管理しているAggregateIterable<Document> 
       *<!-- Aggregator -->
       */
      public AggregateIterable<Document> getIterable(){
         return (AggregateIterable<Document>)records;
         }

      /** {@link hi.db.hiMongo.Aggregator#match(Object) hiMongo.Aggregator.match()}参照. */
      @Override
      public Aggregator match(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$match",context.evalAsStr(arg_)));//arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#group(Object) hiMongo.Aggregator.group()}参照. */
      @Override
      public Aggregator group(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$group",context.evalAsStr(arg_)));//arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#lookup(Object) hiMongo.Aggregator.lookup()}参照. */
      @Override
      public Aggregator lookup(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$lookup",context.evalAsStr(arg_)));//arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#project(Object) hiMongo.Aggregator.project()}参照. */
      @Override
      public Aggregator project(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$project",context.evalAsStr(arg_)));//arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#unwind(Object) hiMongo.Aggregator.unwind()}参照. */
      @Override
      public Aggregator unwind(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$unwind",context.evalAsStr(arg_)));//arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#sort(Object) hiMongo.Aggregator.sort()}参照. */
      @Override
      public Aggregator sort(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$sort",context.evalAsStr(arg_)));//arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#limit(int) hiMongo.Aggregator.limit()}参照. */
      @Override
      public Aggregator limit(int limit_){
         procs.add(Document.parse("{$limit:"+limit_+"}"));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#add_proc(String,Object) hiMongo.Aggregator.add_proc()}参照. */
      @Override
      public Aggregator add_proc(String proc_,Object arg_){
         procs.add(hiMongo.namedObjToDoc(proc_,context.evalAsStr(arg_)));//arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }

      }
   /**
    * driver直呼び出しのCollecction.
    */
   static class Collection extends hiMongoBase.Collection{
      /**
       * driverレベルのcollection.
       * このデータを用いてmongo-java-driverレベルの細かな作業が可能です
       *<!-- Collection -->
       */
      public MongoCollection<Document> mongoCollection;
      /** {@link hi.db.hiMongo.Collection#getMongoCollection() hiMongo.Collection.getMongoCollection()}参照. */
      @Override
      public MongoCollection<Document> getMongoCollection(){
         return mongoCollection;
         }
      /**
       * 指定DBを設定する.
       *<!-- Collection -->
       */
      Collection(hiMongoContext context_,DB db_,String collectionName_){
         super(context_,db_,collectionName_);
         mongoCollection= db_.mongoDatabase.getCollection(collectionName_);
         }
      /** {@link hi.db.hiMongo.Collection#find(Object,Object) hiMongo.Collection.find()}参照. */
      @Override
      @SuppressWarnings("unchecked")
      public Finder find(Object filterJ_,Object memberJ_){
         if( filterJ_==null ) filterJ_="{}";
         Finder _ret= new hiMongoDirect.Finder(context,this);
         _ret.records = mongoCollection.find(context.evalAsBson(filterJ_));
         if( memberJ_!=null ){
            _ret.records= ((FindIterable)_ret.records).projection(context.evalAsBson(memberJ_));
            }
         return _ret;
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
         for(Object _record:records_){
            mongoCollection.insertOne(context.evalAsDoc(_record));
            }
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#drop() hiMongo.Collection.drop()}参照. */
      @Override
      public Collection drop(){
         mongoCollection.drop();
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#count() hiMongo.Collection.count()}参照. */
      @Override
      public long count(){
         return mongoCollection.countDocuments();
         }
      /** {@link hi.db.hiMongo.Collection#count(Object) hiMongo.Collection.count()}参照. */
      @Override
      public long count(Object filterJ_){
         return mongoCollection.countDocuments(context.evalAsBson(filterJ_));//hiMongo.objToBson(filterJ_,parse_engine()));// BSON!
         }
      /** {@link hi.db.hiMongo.Collection#insertMany(Object...) hiMongo.Collection.insertMany()}参照. */
      @Override
      public Collection insertMany(Object... jsonTexts_){
         List<Document> _doc_list= new ArrayList<Document>();
         for(Object _jsonText:jsonTexts_){
            _doc_list.addAll(parseAsDocumentList(_jsonText));//,parse_engine()));// Bsonではない
            }
         mongoCollection.insertMany(_doc_list);
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#aggregate(Object) hiMongo.Collection.aggregate()}参照. */
      @Override
      public Aggregator aggregate(Object proc_){
         Aggregator _ret=new Aggregator(this);
         _ret.procs   = parseAsBsonList(proc_);//,parse_engine());// BSONのリスト
         _ret.records = mongoCollection.aggregate(_ret.procs);
         return _ret;
         }
      /** {@link hi.db.hiMongo.Collection#aggregate() hiMongo.Collection.aggregate()}参照. */
      @Override
      public Aggregator aggregate(){
         Aggregator _ret=new Aggregator(this);
         _ret.procs = new ArrayList<Bson>();
         return _ret;
         }
      /** {@link hi.db.hiMongo.Collection#updateOne(Object,Object) hiMongo.Collection.updateOne()}参照. */
      @Override
      public Collection updateOne(Object filterJ_,Object updateJ_){

         mongoCollection.updateOne(context.evalAsBson(filterJ_)
                                  ,context.evalAsBson(updateJ_));
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#updateMany(Object,Object) hiMongo.Collection.updateMany()}参照. */
      @Override
      public Collection updateMany(Object filterJ_,Object updateJ_){
         hiJSON.Engine _parse_engine=parse_engine();
         mongoCollection.updateMany(context.evalAsBson(filterJ_)//hiMongo.objToBson(filterJ_,_parse_engine)
                                   ,context.evalAsBson(updateJ_));//hiMongo.objToBson(updateJ_,_parse_engine));
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#replaceOne(Object,Object) hiMongo.Collection.replaceOne()}参照. */
      @Override
      public Collection replaceOne(Object filterJ_,Object recordJ_){
         mongoCollection.replaceOne(context.evalAsBson(filterJ_) //フィルターはBSON可
                                   ,context.evalAsDoc(recordJ_));// レコードはDocument
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#deleteOne(Object) hiMongo.Collection.deleteOne()}参照. */
      @Override
      public Collection deleteOne(Object filterJ_){
         mongoCollection.deleteOne(context.evalAsBson(filterJ_));
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#deleteMany(Object) hiMongo.Collection.deleteMany()}参照. */
      @Override
      public Collection deleteMany(Object filterJ_){
         mongoCollection.deleteMany(context.evalAsBson(filterJ_));
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#createIndex(Object,Object) hiMongo.Collection.createIndex()}参照. */
      @Override
      public Collection createIndex(Object keyset_,Object option_){
         try{
            Bson _keyset= context.evalAsBson(keyset_);//hiMongo.objToBson(keyset_,parse_engine());
            IndexOptions _options=null;
            if( option_ instanceof IndexOptions ){
               _options=(IndexOptions)option_;
               }
            else if( option_!=null ){
               Document _option= context.evalAsDoc(option_);//hiMongo.objToDoc(option_,parse_engine());
               for(Map.Entry<String,Object> _kv:_option.entrySet()){
                  if( _kv.getKey().equals("unique") ){
                     if( _options==null ) _options=new IndexOptions();
                     boolean _unique=hiJSON.Probe.asBoolean(_kv.getValue());
                     _options.unique(_unique);
                     }
                  else if( _kv.getKey().startsWith("expireAfter") ){
                     if( _options==null ) _options=new IndexOptions();
                     long _time=hiJSON.Probe.asLong(_kv.getValue());
                     String _after=_kv.getKey();
                     if( _after.equals("expireAfter") ){
                        _options.expireAfter(_time,TimeUnit.SECONDS);
                        }
                     else if( _after.endsWith("Days") ){
                        _options.expireAfter(_time,TimeUnit.DAYS);
                        }
                     else if( _after.endsWith("Hours") ){
                        _options.expireAfter(_time,TimeUnit.HOURS);
                        }
                     else if( _after.endsWith("Minutes") ){
                        _options.expireAfter(_time,TimeUnit.MINUTES);
                        }
                     else{
                        throw new hiException("ellegal expireAfter option "+_after);
                        }
                     }
                  else{
                     throw new hiException("Unkown createIndex option "+_kv.getKey());
                     }
                  }
               }
            if( _options==null ) mongoCollection.createIndex(_keyset);
            else                 mongoCollection.createIndex(_keyset,_options);
            }
         catch(Exception _ex){
            System.err.println("hiMongo.Collection createIndex failure:"+_ex.getMessage());
            }
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
         mongoCollection.dropIndexes();
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#dropIndex(String...) hiMongo.Collection.dropIndex()}参照. */
      @Override
      public Collection dropIndex(String ... index_){
         for(String _index:index_){
            mongoCollection.dropIndex(_index);
            }
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#getIndexList() hiMongo.Collection.getIndexList()}参照. */
      @Override
      public ArrayList<Document> getIndexList(){
         ArrayList<Document> _ret = new ArrayList<>();
         mongoCollection.listIndexes().into(_ret);
         return _ret;
         }
      }
   /**
    * driver直呼びだしのDatabasr
    */
   static class DB extends hiMongoBase.DB{
      MongoDatabase     mongoDatabase;
      /**
       * client(DBサーバとの接続)とデータベース名で構築する.
       *@param client_ client_
       *@param dbName_ データベース名
       *<!-- DB -->
       */
      DB(hiMongoContext context_,Client client_,String dbName_){
         super(context_,client_,dbName_);
         mongoDatabase = client_.mongoClient.getDatabase(dbName_);
         }
      /** {@link hi.db.hiMongo.DB#in(String) hiMongo.DB.in()}参照. */
      @Override
      public hiMongo.Collection in(String collectionName_){
         return new Collection(this.context,this,collectionName_);
         //return collection;
         }
      /** {@link hi.db.hiMongo.DB#close() hiMongo.DB.close()}参照. */
      @Override
      public void close(){
         hiU.close(mongoDatabase);
         hiU.close(context.client);
         }
      /** {@link hi.db.hiMongo.DB#show_collections(boolean) hiMongo.DB.show_collections()}参照. */
      @Override
      public ArrayList<String> show_collections(boolean sort_){
         ArrayList<String> _ret= new ArrayList<>();
         MongoIterable<java.lang.String> _list  =mongoDatabase.listCollectionNames();
         for(String _collName:_list){
            _ret.add(_collName);
            }
         if( sort_ ) Collections.sort(_ret);
         return _ret;
         }
      /** {@link hi.db.hiMongo.DB#drop() hiMongo.DB.drop()}参照. */
      @Override
      public DB drop(){
         mongoDatabase.drop();
         return this;
         }
      /** {@link hi.db.hiMongo.DB#exists(String) hiMongo.DB.exists()}参照. */
      @Override
      public boolean exists(String collectionName_){
         MongoIterable<java.lang.String> _list  =mongoDatabase.listCollectionNames();
         for(String _name:_list){
            if( _name.equals(collectionName_) ) return true;
            }
         return false;
         }
      /** {@link hi.db.hiMongo.DB#createCappedCollection(String,String) hiMongo.DB.createCappedCollection()}参照. */
      @Override
      public Collection createCappedCollection(String name_,String capInfo_){
         hiMongo.CapInfo _capInfo=hiJSON.parse(capInfo_).as(hiMongo.CapInfo.class);
         if( !_capInfo.force && exists(name_) ) return (Collection)in(name_);
         in(name_).drop();// 微妙
         mongoDatabase.createCollection(
            name_,
            new CreateCollectionOptions().capped(true)
                                         .sizeInBytes(_capInfo.size)
                                         .maxDocuments(_capInfo.records));
         return (Collection)in(name_);
         }
      }
   /**
    * driver直使用でmongoDBサーバとの接続を表す.
    */
   static class Client extends hiMongoBase.Client {
      public MongoClient   mongoClient;
      // 単独生成禁止
      Client(hiMongoContext context_){
         super(context_);
         }
      /** {@link hi.db.hiMongo.Client#connect() hiMongo.Client.connect()}参照. */
      @Override
      public Client connect(){
         mongoClient   = new MongoClient("localhost", 27017);
         return this;
         }
      /** {@link hi.db.hiMongo.Client#connect(Object) hiMongo.Client.connect()}参照. */
      @Override
      public Client connect(Object info_){
         hiMongo.RemoteInfo _info=hiMongo.getRemoteInfo(info_);
         if( info_==null ){
            mongoClient   = new MongoClient("localhost", 27017);
            return this;
            }
         List<ServerAddress> _addrs         = new ArrayList<>();
         _addrs.add(new ServerAddress(_info.host,_info.port));
         MongoCredential _credential = MongoCredential.createScramSha1Credential(
                                              _info.user
                                             ,_info.dbName
                                             ,_info.password.toCharArray());
         MongoClientOptions.Builder _options     = MongoClientOptions.builder();
         mongoClient=new MongoClient(_addrs,_credential,_options.build());
         return this;
         }
      /** {@link hi.db.hiMongo.Client#close() hiMongo.Client.close()}参照. */
      @Override
      public void close(){
         hiU.close(mongoClient);
         }
      /** {@link hi.db.hiMongo.Client#show_dbs(boolean) hiMongo.Client.show_dbs()}参照. */
      @Override
      public ArrayList<String> show_dbs(boolean sort_){
         ArrayList<String> _ret= new ArrayList<>();
         // MongoIterable#forEachはjava8ではconflictを起こす
         MongoIterable<java.lang.String> _list  =mongoClient.listDatabaseNames();
         for(String _dbName:_list){
            _ret.add(_dbName);
            }
         if( sort_ ) Collections.sort(_ret);
         return _ret;
         }
      /** {@link hi.db.hiMongo.Client#use(String) hiMongo.Client.use()}参照. */
      @Override
      public DB use(String dbName_){
         DB     _ret= new DB(this.context.clone(),this,dbName_);
         return _ret;
         }
      }// end Client
   }

