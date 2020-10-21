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
   /**
    *mongo-java-driver直叩き式アクセス機構生成機
    */
   public hiMongoDirect(){}
   /** {@link hi.db.hiMongo.MoreMongo#connect(Object) hiMongo.MoreMongo.connect()}参照. */
   @Override // hiMongo.MoreMongo
   public Client connect(Object remote_){
      return new Client().connect(hiMongo.getRemoteInfo(remote_));
      }
   /** {@link hi.db.hiMongo.MoreMongo#use(String) hiMongo.MoreMongo.use()}参照. */
   @Override // hiMongo.MoreMongo
   public DB     use(String dbName_){
      return new Client().connect(null).use(dbName_);
      }
   /** {@link hi.db.hiMongo.MoreMongo#show_dbs(boolean) hiMongo.MoreMongo.show_dbs()}参照. */
   @Override // hiMongo.MoreMongo
   public ArrayList<String> show_dbs(boolean sort_){
      return new Client().connect().show_dbs(sort_);
      }
   /**
    * レコードアクセス機(Finder,Aggregatorのベース).
    */
   public static class Accessor implements hiMongo.Accessor{
      MongoIterable<Document> records;
      hiJSON.Engine           msonEngine;
      hiJSON.Engine           jsonEngine;
      Collection              collection;
      Accessor(Collection collection_){
         collection= collection_;
         }
      /** {@link hi.db.hiMongo.Accessor#back() hiMongo.Accessor.back()}参照. */
      @Override
      public Collection back(){
         return collection;
         }
      /** {@link hi.db.hiMongo.Accessor#engineJ() hiMongo.Accessor.engineJ()}参照. */
      @Override
      final public hiJSON.Engine engineJ(){
         if( this.jsonEngine== null ){
            this.jsonEngine=hiMongo.json_engine.clone();
            }
         return this.jsonEngine;
         }
      /** {@link hi.db.hiMongo.Accessor#engine() hiMongo.Accessor.engine()}参照. */
      @Override
      public hiJSON.Engine engine(){
         if( this.msonEngine== null ){
            this.msonEngine=hiMongo.mson_engine.clone();
            }
         return this.msonEngine;
         }
      /** {@link hi.db.hiMongo.Accessor#cur_engine() hiMongo.Accessor.cur_engine()}参照. */
      @Override
      public hiJSON.Engine cur_engine(){
         if( this.msonEngine== null ){
            return collection.cur_engine();
            }
         return this.msonEngine;
         }
      /** {@link hi.db.hiMongo.Accessor#cur_engineJ() hiMongo.Accessor.cur_engineJ()}参照. */
      @Override
      public hiJSON.Engine cur_engineJ(){
         if( this.jsonEngine== null ){
            return collection.cur_engineJ();
            }
         return this.jsonEngine;
         }
      /** {@link hi.db.hiMongo.Accessor#parse_engine() hiMongo.Accessor.parse_engine()}参照. */
      @Override
      public hiJSON.Engine parse_engine(){
         return collection.parse_engine();
         }
      /**
       * 解析済みnodeを再解釈する.
       *@param node_ ノードツリー
       *@return 解析エンジン
       *<!-- Accessor -->
       */
      protected hiJSON.Engine parseNode(Object node_){
         if( msonEngine==null ) return hiMongo.mson_engine.parseNode(node_);
         return msonEngine.parseNode(node_);
         }
      /**
       * Documentからクラスを得る.
       *Object node?
       *@param class_ クラス
       *@param node_ ノード
       *@return クラス
       *<!-- Accessor -->
       */
      protected <T> T toClass(Class<T> class_,Document node_){
         return parseNode(node_).asClass(class_);
         }
      /**
       * DocumentからProbeを得る.
       *Object node?
       *@param node_ ノード
       *@return Probe
       *<!-- Accessor -->
       */
      protected hiJSON.Probe toProbe(Document node_){
         return hiJSON.probe(parseNode(node_).asNode());
         }
      /**
       * オブジェクトからMsonを得る.
       *@param node_ ノード
       *@return Mson
       *<!-- Accessor -->
       */
      protected String mson(Object obj_){
         if( msonEngine==null ) return hiMongo.mson_engine.str(obj_);
         return msonEngine.str(obj_);
         }
      /**
       * オブジェクトからJsonを得る.
       *@param node_ ノード
       *@return Json
       *<!-- Accessor -->
       */
      protected String json(Object obj_){
         if( jsonEngine==null ) return hiMongo.json_engine.str(obj_);
         return jsonEngine.str(obj_);
         }
      /**
       * オブジェクトから構造文字列を得る.
       *@param node_ ノード
       *@return 構造文字列
       *<!-- Accessor -->
       */
      protected String str(Object obj_){
         return hiU.str(obj_);
         }

      /** {@link hi.db.hiMongo.Accessor#getJsonList(long) hiMongo.Accessor.getJsonList()}参照. */
      @Override
      public ArrayList<String> getJsonList(long option_){
         ArrayList<String>     _ret   = new ArrayList<>();
         for(Document _record:records) _ret.add(json(_record));
         if( (option_&hiU.REVERSE)!=0 ) Collections.reverse(_ret);
         return _ret;
         }
      /** {@link hi.db.hiMongo.Accessor#getJsonList() hiMongo.Accessor.getJsonList()}参照. */
      @Override
      public ArrayList<String> getJsonList(){
         return getJsonList(0);
         }
      /** {@link hi.db.hiMongo.Accessor#getMsonList(long) hiMongo.Accessor.getMsonList()}参照. */
      @Override
      public ArrayList<String> getMsonList(long option_){
         ArrayList<String>         _ret   = new ArrayList<>();
         for(Document _record:records) _ret.add(mson(_record));
         if( (option_&hiU.REVERSE)!=0 ) Collections.reverse(_ret);
         return _ret;
         }
      /** {@link hi.db.hiMongo.Accessor#getMsonList() hiMongo.Accessor.getMsonList()}参照. */
      @Override
      public ArrayList<String> getMsonList(){
         return getMsonList(0);
         }
      /** {@link hi.db.hiMongo.Accessor#getProbeList(long) hiMongo.Accessor.getProbeList()}参照. */
      @Override
      public ArrayList<hiJSON.Probe> getProbeList(long option_){
         ArrayList<hiJSON.Probe>     _ret   = new ArrayList<>();
         for(Document _record:records) _ret.add(toProbe(_record));
         if( (option_&hiU.REVERSE)!=0 ) Collections.reverse(_ret);
         return _ret;
         }
      /** {@link hi.db.hiMongo.Accessor#getProbeList() hiMongo.Accessor.getProbeList()}参照. */
      @Override
      public ArrayList<hiJSON.Probe> getProbeList(){
         return getProbeList(0);
         }
      /** {@link hi.db.hiMongo.Accessor#getClassList(Class,long) hiMongo.Accessor.getClassList()}参照. */
      @Override
      public <T> ArrayList<T>  getClassList(Class<T> class_
                                           ,long option_
                                           ){
         ArrayList<T>    _ret   = new ArrayList<>();
         for(Document _record:records) _ret.add(toClass(class_,_record));
         if( (option_&hiU.REVERSE)!=0 ) Collections.reverse(_ret);
         return _ret;
         }
      /** {@link hi.db.hiMongo.Accessor#getClassList(Class) hiMongo.Accessor.getClassList()}参照. */
      @Override
      public <T> ArrayList<T> getClassList(Class<T> class_){
         return getClassList(class_,0);
         }
      /** {@link #getClassList(Class,long)}の別名. */
      final public <T> ArrayList<T>  getList(Class<T> class_
                                           ,long option_
                                           ){
         return getClassList(class_,option_);
         }
      /** {@link #getClassList(Class)}の別名. */
      public <T> ArrayList<T> getList(Class<T> class_){
         return getClassList(class_,0);
         }
      /** {@link hi.db.hiMongo.Accessor#getDocumentList(long) hiMongo.Accessor.getDocumentList()}参照. */
      @Override
      public ArrayList<Document> getDocumentList(long option_){
         ArrayList<Document> _ret = new ArrayList<>();
         records.into(_ret);
         if( (option_&hiU.REVERSE)!=0 ) Collections.reverse(_ret);
         return _ret;
         }
      /** {@link hi.db.hiMongo.Accessor#getDocumentList() hiMongo.Accessor.getDocumentList()}参照. */
      @Override
      public ArrayList<Document> getDocumentList(){
         return getDocumentList(0);
         }
      /** {@link #getDocumentList(long)}の別名. */
      public final ArrayList<Document> getList(long option_){
         return getDocumentList(option_);
         }
      /** {@link #getDocumentList()}の別名. */
      public ArrayList<Document> getList(){
         return getDocumentList(0);
         }
      /**
       *forEachDocumentのベース実装.
       *カスケードAPIの為派生クラスにpublic APIを置いてある
       *<!-- Accessor -->
       */
      protected Accessor super_forEachDocument(hiU.ConsumerEx<Document,Exception> func_){
         for(Document _record:records) hiU.rap(func_,_record);
         return this;
         }
      /**
       *forEachJsonのベース実装.
       *カスケードAPIの為派生クラスにpublic APIを置いてある
       *<!-- Accessor -->
       */
      protected Accessor super_forEachJson(hiU.ConsumerEx<String,Exception> func_){
         for(Document _record:records) hiU.rap(func_,json(_record));
         return this;
         }
      /**
       *forEachMsonのベース実装.
       *カスケードAPIの為派生クラスにpublic APIを置いてある
       *<!-- Accessor -->
       */
      protected Accessor super_forEachMson(hiU.ConsumerEx<String,Exception> func_){
         for(Document _record:records) hiU.rap(func_,mson(_record));
         return this;
         }
      /**
       *forEachProbeのベース実装.
       *カスケードAPIの為派生クラスにpublic APIを置いてある
       *<!-- Accessor -->
       */
      protected Accessor super_forEachProbe(hiU.ConsumerEx<hiJSON.Probe,Exception> func_){
         for(Document _record:records) hiU.rap(func_,toProbe(_record));
         return this;
         }
      /**
       *forEachClassのベース実装.
       *カスケードAPIの為派生クラスにpublic APIを置いてある
       *<!-- Accessor -->
       */
      protected <T> Accessor super_forEachClass(Class<T>                    class_
                                               ,hiU.ConsumerEx<T,Exception> func_){
         for(Document _record:records) hiU.rap(func_,toClass(class_,_record));
         return this;
         }
      }
   /**
    *driver直叩きFinder
    */
   public static class Finder extends Accessor
                              implements hiMongo.Finder{
      Finder(Collection collection_){
         super(collection_);
         }
      /**
       *FindIterableを得る.
       *@return このFinderのFindIterable
       */
      @Override
      public FindIterable<Document> getIterable(){
         return (FindIterable<Document>)records;
         }
      /** {@link hi.db.hiMongo.Finder#str_option(long) hiMongo.Finder.str_option()}参照. */
      @Override
      public Finder str_option(long option_){
         engine().str_format().str_option(option_);
         return this;
         }
      /** {@link hi.db.hiMongo.Finder#str_disable_option(long) hiMongo.Finder.str_disable_option()}参照. */
      @Override
      public Finder str_disable_option(long option_){
         engine().str_format().str_disable_option(option_);
         return this;
         }
      /** {@link hi.db.hiMongo.Finder#with_option(long) hiMongo.Finder.with_option()}参照. */
      @Override
      public Finder with_option(long option_){
         engine().with_option(option_);
         return this;
         }
      /** {@link hi.db.hiMongo.Finder#without_option(long) hiMongo.Finder.without_option()}参照. */
      @Override
      public Finder without_option(long option_){
         engine().without_option(option_);
         return this;
         }
      /** {@link hi.db.hiMongo.Finder#sort(Object) hiMongo.Finder.sort()}参照. */
      @Override
      public Finder sort(Object sort_condition_){
         ((FindIterable<Document>)records).sort(hiMongo.objToDoc(sort_condition_,parse_engine()));
         return this;
         }
      /** {@link hi.db.hiMongo.Finder#limit(int) hiMongo.Finder.limit()}参照. */
      @Override
      public Finder limit(int limit_){
         ((FindIterable<Document>)records).limit(limit_);
         return this;
         }
      /** {@link hi.db.hiMongo.Finder#skip(int) hiMongo.Finder.skip()}参照. */
      @Override
      public Finder skip(int skip_){
         ((FindIterable<Document>)records).skip(skip_);
         return this;
         }
      /** {@link hi.db.hiMongo.Finder#forEachDocument(hiU.ConsumerEx) hiMongo.Finder.forEachDocument()}参照. */
      @Override
      public Finder forEachDocument(hiU.ConsumerEx<Document,Exception> func_){
         return (Finder)super_forEachDocument(func_);
         }
      /** {@link #forEachDocument(hiU.ConsumerEx) forEachDocument(func_)}の別名. */
      public Finder forEach(hiU.ConsumerEx<Document,Exception> func_){
         return (Finder)super_forEachDocument(func_);
         }
      /** {@link hi.db.hiMongo.Finder#forEachJson(hiU.ConsumerEx) hiMongo.Finder.forEachJson()}参照. */
      @Override
      public Finder forEachJson(hiU.ConsumerEx<String,Exception> func_){
         return (Finder)super_forEachJson(func_);
         }
      /** {@link hi.db.hiMongo.Finder#forEachMson(hiU.ConsumerEx) hiMongo.Finder.forEachMson()}参照. */
      @Override
      public Finder forEachMson(hiU.ConsumerEx<String,Exception> func_){
         return (Finder)super_forEachMson(func_);
         }
      /** {@link hi.db.hiMongo.Finder#forEachProbe(hiU.ConsumerEx) hiMongo.Finder.forEachProbe()}参照. */
      @Override
      public Finder forEachProbe(hiU.ConsumerEx<hiJSON.Probe,Exception> func_){
         return (Finder)super_forEachProbe(func_);
         }
      /** {@link hi.db.hiMongo.Finder#forEachClass(Class,hiU.ConsumerEx) hiMongo.Finder.forEachClass()}参照. */
      @Override
      public <T> Finder forEachClass(Class<T> class_,hiU.ConsumerEx<T,Exception> func_){
         return (Finder)super_forEachClass(class_,func_);
         }
      /** {@link #forEachClass(Class,hiU.ConsumerEx) forEachClass(func_)}と同じ. */
      public <T> Finder forEach(Class<T>                    class_,
                            hiU.ConsumerEx<T,Exception> func_){
         return (Finder)super_forEachClass(class_,func_);
         }
      /** {@link hi.db.hiMongo.Finder#forThis(hiU.ConsumerEx) hiMongo.Finder.forThis()}参照. */
      @Override
      public Finder forThis(hiU.ConsumerEx<hiMongo.Finder,Exception> func_){
         hiU.rap(func_,this);
         return this;
         }
      } // end Finder
   /**
    * driver直呼び集計器.
    */
   public static class Aggregator extends Accessor
                                  implements hiMongo.Aggregator{
      MongoCollection<Document> mongoCollection;
      List<Bson>      procs;
      /**
       * 指定のコレクション用の集計器.
       *<!-- Aggregator -->
       */
      Aggregator(Collection collection_){
         super(collection_);
         mongoCollection= collection.mongoCollection;
         }
      /**
       * イテラブル取得
       *@return 管理しているAggregateIterable<Document> 
       *<!-- Aggregator -->
       */
      public AggregateIterable<Document> getIterable(){
         return (AggregateIterable<Document>)records;
         }
      /** {@link hi.db.hiMongo.Aggregator#str_option(long) hiMongo.Aggregator.str_option()}参照. */
      @Override
      public Aggregator str_option(long option_){
         engine().str_format().str_option(option_);
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#str_disable_option(long) hiMongo.Aggregator.str_disable_option()}参照. */
      @Override
      public Aggregator str_disable_option(long option_){
         engine().str_format().str_disable_option(option_);
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#with_option(long) hiMongo.Aggregator.with_option()}参照. */
      @Override
      public Aggregator with_option(long option_){
         engine().with_option(option_);
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#without_option(long) hiMongo.Aggregator.without_option()}参照. */
      @Override
      public Aggregator without_option(long option_){
         engine().without_option(option_);
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#match(Object) hiMongo.Aggregator.match()}参照. */
      @Override
      public Aggregator match(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$match",arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#group(Object) hiMongo.Aggregator.group()}参照. */
      @Override
      public Aggregator group(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$group",arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#lookup(Object) hiMongo.Aggregator.lookup()}参照. */
      @Override
      public Aggregator lookup(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$lookup",arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#project(Object) hiMongo.Aggregator.project()}参照. */
      @Override
      public Aggregator project(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$project",arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#unwind(Object) hiMongo.Aggregator.unwind()}参照. */
      @Override
      public Aggregator unwind(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$unwind",arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#sort(Object) hiMongo.Aggregator.sort()}参照. */
      @Override
      public Aggregator sort(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$sort",arg_,parse_engine()));
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
         procs.add(hiMongo.namedObjToDoc(proc_,arg_,parse_engine()));
         records= mongoCollection.aggregate(procs);
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#forEachDocument(hiU.ConsumerEx) hiMongo.Aggregator.forEachDocument()}参照. */
      @Override
      public Aggregator forEachDocument(hiU.ConsumerEx<Document,Exception> func_){
         return (Aggregator)super_forEachDocument(func_);
         }
      /** {@link #forEachDocument(hiU.ConsumerEx) forEachDocument(func_)}の別名. */
      public Aggregator forEach(hiU.ConsumerEx<Document,Exception> func_){
         return (Aggregator)super_forEachDocument(func_);
         }
      /** {@link hi.db.hiMongo.Aggregator#forEachJson(hiU.ConsumerEx) hiMongo.Aggregator.forEachJson()}参照. */
      @Override
      public Aggregator forEachJson(hiU.ConsumerEx<String,Exception> func_){
         return (Aggregator)super_forEachJson(func_);
         }
      /** {@link hi.db.hiMongo.Aggregator#forEachMson(hiU.ConsumerEx) hiMongo.Aggregator.forEachMson()}参照. */
      @Override
      public Aggregator forEachMson(hiU.ConsumerEx<String,Exception> func_){
         return (Aggregator)super_forEachMson(func_);
         }
      /** {@link hi.db.hiMongo.Aggregator#forEachProbe(hiU.ConsumerEx) hiMongo.Aggregator.forEachProbe()}参照. */
      @Override
      public Aggregator forEachProbe(hiU.ConsumerEx<hiJSON.Probe,Exception> func_){
         return (Aggregator)super_forEachProbe(func_);
         }
      /** {@link hi.db.hiMongo.Aggregator#forEachClass(Class,hiU.ConsumerEx) hiMongo.Aggregator.forEachClass()}参照. */
      @Override
      public <T> Aggregator forEachClass(Class<T>                    class_,
                                          hiU.ConsumerEx<T,Exception> func_){
         return (Aggregator)super_forEachClass(class_,func_);
         }
      /** {@link #forEachClass(Class,hiU.ConsumerEx) forEachClass(func_)}の別名. */
      public <T> Aggregator forEach(Class<T>                    class_,
                                hiU.ConsumerEx<T,Exception> func_){
         return (Aggregator)super_forEachClass(class_,func_);
         }
      /** {@link hi.db.hiMongo.Aggregator#forThis(hiU.ConsumerEx) hiMongo.Aggregator.forThis()}参照. */
      @Override
      public Aggregator forThis(hiU.ConsumerEx<hiMongo.Aggregator,Exception> func_){
         hiU.rap(func_,this);
         return this;
         }
      }
   /**
    * driver直呼び出しのCollecction.
    */
   public static class Collection implements hiMongo.Collection{
      DB            db;
      hiJSON.Engine msonEngine;
      hiJSON.Engine jsonEngine;
      boolean       use_hson;
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
      Collection(DB db_){
         db=db_;
         use_hson=hiMongo.use_hson;
         }
      /** {@link hi.db.hiMongo.Collection#with_hson() hiMongo.Collection.with_hson()}参照. */
      @Override
      public Collection with_hson(){
         use_hson=true;
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#with_hson(boolean) hiMongo.Collection.with_hson()}参照. */
      @Override
      public Collection with_hson(boolean use_hson_){
         use_hson=use_hson_;
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#parse_engine() hiMongo.Collection.parse_engine()}参照. */
      @Override
      public hiJSON.Engine parse_engine(){
         if( use_hson ) return cur_engine();
         return null;
         }
      /** {@link hi.db.hiMongo.Collection#cur_engine() hiMongo.Collection.cur_engine()}参照. */
      @Override
      public hiJSON.Engine cur_engine(){
         if( this.msonEngine== null ){
            return hiMongo.mson_engine;
            }
         return this.msonEngine;
         }
      /** {@link hi.db.hiMongo.Collection#cur_engineJ() hiMongo.Collection.cur_engineJ()}参照. */
      @Override
      public hiJSON.Engine cur_engineJ(){
         if( this.jsonEngine== null ){
            return hiMongo.json_engine;
            }
         return this.jsonEngine;
         }
      /** {@link hi.db.hiMongo.Collection#back() hiMongo.Collection.back()}参照. */
      @Override
      public DB back(){
         return db;
         }
      /** {@link hi.db.hiMongo.Collection#forThis(hiU.ConsumerEx) hiMongo.Collection.forThis()}参照. */
      @Override
      public Collection forThis(hiU.ConsumerEx<hiMongo.Collection,Exception> func_){
         hiU.rap(func_,this);
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#find(Object,Object) hiMongo.Collection.find()}参照. */
      @Override
      @SuppressWarnings("unchecked")
      public Finder find(Object filterJ_,Object memberJ_){
         if( filterJ_==null ) filterJ_="{}";
         Finder _ret= new hiMongoDirect.Finder(this);
         _ret.records = mongoCollection.find(hiMongo.objToBson(filterJ_,parse_engine()));
         if( memberJ_!=null ){
            _ret.records= ((FindIterable)_ret.records).projection(hiMongo.objToBson(memberJ_,parse_engine()));// BSON!
            }
         return _ret;
         }
      /** {@link hi.db.hiMongo.Collection#find(Object) hiMongo.Collection.find()}参照. */
      @Override
      public hiMongo.Finder find(Object filterJ_){
         return find(filterJ_,null);
         }
      /** {@link hi.db.hiMongo.Collection#find() hiMongo.Collection.find()}参照. */
      @Override
      public hiMongo.Finder find(){
         return find(null,null);
         }
      /** {@link hi.db.hiMongo.Collection#insertOne(Object...) hiMongo.Collection.insertOne()}参照. */
      @Override
      public Collection insertOne(Object... records_){
         for(Object _record:records_){
            mongoCollection.insertOne(hiMongo.objToDoc(_record,parse_engine()));// Bsonは不可
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
         return mongoCollection.countDocuments(hiMongo.objToBson(filterJ_,parse_engine()));// BSON!
         }
      /** {@link hi.db.hiMongo.Collection#insertMany(Object...) hiMongo.Collection.insertMany()}参照. */
      @Override
      public Collection insertMany(Object... jsonTexts_){
         List<Document> _doc_list= new ArrayList<Document>();
         for(Object _jsonText:jsonTexts_){
            _doc_list.addAll(hiMongo.parseAsDocumentList(_jsonText,parse_engine()));// Bsonではない
            }
         mongoCollection.insertMany(_doc_list);
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#aggregate(Object) hiMongo.Collection.aggregate()}参照. */
      @Override
      public Aggregator aggregate(Object proc_){
         Aggregator _ret=new Aggregator(this);
         _ret.procs   = hiMongo.parseAsBsonList(proc_,parse_engine());// BSONのリスト
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
         hiJSON.Engine _parse_engine=parse_engine();
         mongoCollection.updateOne(hiMongo.objToBson(filterJ_,_parse_engine)
                                  ,hiMongo.objToBson(updateJ_,_parse_engine));
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#updateMany(Object,Object) hiMongo.Collection.updateMany()}参照. */
      @Override
      public Collection updateMany(Object filterJ_,Object updateJ_){
         hiJSON.Engine _parse_engine=parse_engine();
         mongoCollection.updateMany(hiMongo.objToBson(filterJ_,_parse_engine)
                                   ,hiMongo.objToBson(updateJ_,_parse_engine));
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#replaceOne(Object,Object) hiMongo.Collection.replaceOne()}参照. */
      @Override
      public Collection replaceOne(Object filterJ_,Object recordJ_){
         hiJSON.Engine _parse_engine=parse_engine();
         mongoCollection.replaceOne(hiMongo.objToBson(filterJ_,_parse_engine) //フィルターはBSON可
                                   ,hiMongo.objToDoc(recordJ_,_parse_engine));
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#deleteOne(Object) hiMongo.Collection.deleteOne()}参照. */
      @Override
      public Collection deleteOne(Object filterJ_){
         mongoCollection.deleteOne(hiMongo.objToBson(filterJ_,parse_engine()));// BSON
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#deleteMany(Object) hiMongo.Collection.deleteMany()}参照. */
      @Override
      public Collection deleteMany(Object filterJ_){
         mongoCollection.deleteMany(hiMongo.objToBson(filterJ_,parse_engine()));//BSON
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#createIndex(Object,Object) hiMongo.Collection.createIndex()}参照. */
      @Override
      public Collection createIndex(Object keyset_,Object option_){
         try{
            Bson _keyset=hiMongo.objToBson(keyset_,parse_engine());
            IndexOptions _options=null;
            if( option_ instanceof IndexOptions ){
               _options=(IndexOptions)option_;
               }
            else if( option_!=null ){
               Document _option= hiMongo.objToDoc(option_,parse_engine());
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
   public static class DB implements hiMongo.DB,
                                     Closeable{
      Client                                          client;
      public MongoDatabase                            mongoDatabase;
      /**
       * client(DBサーバとの接続)とデータベース名で構築する.
       *@param client_ client_
       *@param dbName_ データベース名
       *<!-- DB -->
       */
      DB(hiMongo.Client client_,String dbName_){
         client=(hiMongoDirect.Client) client_;
         mongoDatabase = client.mongoClient.getDatabase(dbName_);
         }
      /** {@link hi.db.hiMongo.DB#in(String) hiMongo.DB.in()}参照. */
      @Override
      public hiMongo.Collection in(String collectionName_){
         Collection _ret = new Collection(this);
         _ret.mongoCollection= mongoDatabase.getCollection(collectionName_);
         return _ret;
         }
      /** {@link hi.db.hiMongo.DB#close() hiMongo.DB.close()}参照. */
      @Override
      public void close(){
         hiU.close(mongoDatabase);
         hiU.close(client);
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
         CapInfo _capInfo=hiJSON.parse(capInfo_).as(CapInfo.class);
         if( !_capInfo.force && exists(name_) ) return (Collection)in(name_);
         in(name_).drop();// 微妙
         mongoDatabase.createCollection(
            name_,
            new CreateCollectionOptions().capped(true)
                                         .sizeInBytes(_capInfo.size)
                                         .maxDocuments(_capInfo.records));
         return (Collection)in(name_);
         }
      /**
       * Cap指定
       *<!-- DB -->
       */
      static class CapInfo{
         long    size;
         long    records;
         boolean force;
         }
      }
   /**
    * driver直使用でmongoDBサーバとの接続を表す.
    */
   public static class Client implements hiMongo.Client,Closeable{
      public MongoClient   mongoClient;
      // 単独生成禁止
      Client(){}
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
         DB     _ret= new DB(this,dbName_);
         return _ret;
         }
      }// end Client
   }

