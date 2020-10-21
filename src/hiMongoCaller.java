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
   final static String MSG_STA=
       "{format:'hiMongo',content_type:'request',status:'request',content:{";
   final static String MSG_END="}}";
   hiStringCOM jsonCOM;
   public hiMongoCaller(hiStringCOM jsonCOM_){
      jsonCOM    = jsonCOM_;
      }
   @SuppressWarnings("unchecked")
   private ArrayList<Object> callWorker(String request_){
      String                 _result = jsonCOM.call(MSG_STA+request_+MSG_END);
      hiMongoCOM.Message _msg    = hiMongo.parse(_result)
                                              .asClass(hiMongoCOM.Message.class);
      return (ArrayList<Object>)_msg.content;
      }
   @SuppressWarnings("unchecked")
   private Object callWorker(String request_,String result_key_){
      String                 _result = jsonCOM.call(MSG_STA+request_+MSG_END);
      hiMongoCOM.Message _msg    = hiMongo.parse(_result)
                                              .asClass(hiMongoCOM.Message.class);
      ArrayList<Map<String,Object>> _maps=hiMongo.parse(hiMongo.json(_msg.content))
                                                 .asMapList();
      for(Map<String,Object> _map:_maps){
         if( _map.containsKey(result_key_) ){
            return _map.get(result_key_);
            }
         }
      throw new hiException("result for "+result_key_+" not found");
      }
   /**
    *mongo-java-driver直叩き式アクセス機構生成機
    */
   //public hiMongoCaller(){}
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
   public class Accessor implements hiMongo.Accessor{
      hiJSON.Engine msonEngine;
      hiJSON.Engine jsonEngine;
      Collection    collection;
      StringBuilder message;    // ここにはconnect/in/find[],{..},..までの電文が入っている
                                //         connect/in/aggregate{},{..},..
      final String  END_MESSAGE="]";
      Accessor(Collection collection_){
         collection= collection_;
         }
      @SuppressWarnings("unchecked")
      protected ArrayList<Object> getNodeList(long option_){
         String  _getList= message.toString()+",{"+hiMongoCOM.GET_RECORD+":"+option_+"}"+END_MESSAGE;
         ArrayList<Object> _ret=(ArrayList<Object>)callWorker(_getList,hiMongoCOM.GET_RECORD);
         return _ret;
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
      protected <T> T toClass(Class<T> class_,Object node_){
         return parseNode(node_).asClass(class_);
         }
      /**
       * ObjectからProbeを得る.
       *Object node?
       *@param node_ ノード
       *@return Probe
       *<!-- Accessor -->
       */
      protected hiJSON.Probe toProbe(Object node_){
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
       * コマンドパラメタobjectをJSON文字列にする.
       *文字列の場合はそのまま、Fileの場合はFileから読み込み、Objectの場合はmson
       *@return 文字列
       */
      protected String objToJson(Object obj_){
         if( obj_ instanceof String ) return (String)obj_;
         if( obj_ instanceof File )   return hiFile.readTextAll((File)obj_);
         if( jsonEngine==null ) return hiMongo.mson_engine.str(obj_);
         return msonEngine.str(obj_);
         }
      //=========================================================================
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
      /** {@link hi.db.hiMongo.Accessor#getJsonList(long) hiMongo.Accessor.getJsonList()}参照. */
      @Override
      public ArrayList<String> getJsonList(long option_){
         ArrayList<String> _ret=new ArrayList<>();
         ArrayList<Object> _list=getNodeList(option_);
         for(Object _obj:_list)_ret.add(json(_obj));
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
         ArrayList<String> _ret =new ArrayList<>();
         ArrayList<Object> _list=getNodeList(option_);
         for(Object _obj:_list)_ret.add(mson(_obj));
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
         ArrayList<Object> _list=getNodeList(option_);
         for(Object _obj:_list)  _ret.add(toProbe(_obj));
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
         ArrayList<Object> _list=getNodeList(option_);
         ArrayList<T>      _ret=new ArrayList<>();
         for(Object _obj:_list)_ret.add(toClass(class_,_obj));
         return _ret;
         }
      /** {@link hi.db.hiMongo.Accessor#getClassList(Class) hiMongo.Accessor.getClassList()}参照. */
      @Override
      public <T> ArrayList<T> getClassList(Class<T> class_){
         return getClassList(class_,0);
         }
      /** {@link #getClassList(Class,long)}の別名. */
      @Override
      final public <T> ArrayList<T>  getList(Class<T> class_
                                           ,long option_
                                           ){
         return getClassList(class_,option_);
         }
      /** {@link #getClassList(Class)}の別名. */
      @Override
      public <T> ArrayList<T> getList(Class<T> class_){
         return getClassList(class_,0);
         }
      /** {@link hi.db.hiMongo.Accessor#getDocumentList(long) hiMongo.Accessor.getDocumentList()}参照. */
      @Override
      public ArrayList<Document> getDocumentList(long option_){
         ArrayList<Object> _list=getNodeList(option_);
         ArrayList<Document> _ret =new ArrayList<>();
         for(Object _obj:_list)_ret.add(hiMongo.objToDoc(_obj,cur_engine()));
         return _ret;
         }
      /** {@link hi.db.hiMongo.Accessor#getDocumentList() hiMongo.Accessor.getDocumentList()}参照. */
      @Override
      public ArrayList<Document> getDocumentList(){
         return getDocumentList(0);
         }
      /** {@link #getDocumentList(long)}の別名. */
      @Override
      public final ArrayList<Document> getList(long option_){
         return getDocumentList(option_);
         }
      /** {@link #getDocumentList()}の別名. */
      @Override
      public ArrayList<Document> getList(){
         return getDocumentList(0);
         }
      /**
       *forEachDocumentのベース実装.
       *カスケードAPIの為派生クラスにpublic APIを置いてある
       *<!-- Accessor -->
       */
      protected Accessor super_forEachDocument(hiU.ConsumerEx<Document,Exception> func_){
         ArrayList<Object> _list=getNodeList(0);
         for(Object _obj:_list) hiU.rap(func_,hiMongo.objToDoc(_obj,cur_engine()));
         return this;
         }
      /**
       *forEachJsonのベース実装.
       *カスケードAPIの為派生クラスにpublic APIを置いてある
       *<!-- Accessor -->
       */
      protected Accessor super_forEachJson(hiU.ConsumerEx<String,Exception> func_){
         ArrayList<Object> _list=getNodeList(0);
         for(Object _obj:_list) hiU.rap(func_,json(_obj));
         return this;
         }
      /**
       *forEachMsonのベース実装.
       *カスケードAPIの為派生クラスにpublic APIを置いてある
       *<!-- Accessor -->
       */
      protected Accessor super_forEachMson(hiU.ConsumerEx<String,Exception> func_){
         ArrayList<Object> _list=getNodeList(0);
         for(Object _obj:_list) hiU.rap(func_,mson(_obj));
         return this;
         }
      /**
       *forEachProbeのベース実装.
       *カスケードAPIの為派生クラスにpublic APIを置いてある
       *<!-- Accessor -->
       */
      protected Accessor super_forEachProbe(hiU.ConsumerEx<hiJSON.Probe,Exception> func_){
         ArrayList<Object> _list=getNodeList(0);
         for(Object _obj:_list)  hiU.rap(func_,toProbe(_obj));
         return this;
         }
      /**
       *forEachClassのベース実装.
       *カスケードAPIの為派生クラスにpublic APIを置いてある
       *<!-- Accessor -->
       */
      protected <T> Accessor super_forEachClass(Class<T>                    class_
                                               ,hiU.ConsumerEx<T,Exception> func_){
         ArrayList<Object> _list=getNodeList(0);
         for(Object _obj:_list) hiU.rap(func_,toClass(class_,_obj));
         return this;
         }
      }
   /**
    *Finder
    */
   public class Finder extends Accessor
                              implements hiMongo.Finder{
      Finder(Collection coll_,String message_){
         super(coll_);
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
      public Finder sort(Object param_){
         message.append(",{"+hiMongoCOM.SORT+":"+objToJson(param_)+"}");
         return this;
         }
      /** {@link hi.db.hiMongo.Finder#limit(int) hiMongo.Finder.limit()}参照. */
      @Override
      public Finder limit(int limit_){
         message.append(",{"+hiMongoCOM.LIMIT+":"+limit_+"}");
         return this;
         }
      /** {@link hi.db.hiMongo.Finder#skip(int) hiMongo.Finder.skip()}参照. */
      @Override
      public Finder skip(int skip_){
         message.append(",{"+hiMongoCOM.SKIP+":"+skip_+"}");
         return this;
         }
      /** {@link hi.db.hiMongo.Finder#forEachDocument(hiU.ConsumerEx) hiMongo.Finder.forEachDocument()}参照. */
      @Override
      public Finder forEachDocument(hiU.ConsumerEx<Document,Exception> func_){
         return (Finder)super_forEachDocument(func_);
         }
      /** {@link #forEachDocument(hiU.ConsumerEx) forEachDocument(func_)}の別名. */
      @Override
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
      @Override
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
   public class Aggregator extends Accessor
                                  implements hiMongo.Aggregator{
      String          base_message;
      List<Bson>      procs;
      final static String END_AGGREGATE="}";
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
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#group(Object) hiMongo.Aggregator.group()}参照. */
      @Override
      public Aggregator group(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$group",arg_,parse_engine()));
         constructMessage();
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#lookup(Object) hiMongo.Aggregator.lookup()}参照. */
      @Override
      public Aggregator lookup(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$lookup",arg_,parse_engine()));
         constructMessage();
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#project(Object) hiMongo.Aggregator.project()}参照. */
      @Override
      public Aggregator project(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$project",arg_,parse_engine()));
         constructMessage();
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#unwind(Object) hiMongo.Aggregator.unwind()}参照. */
      @Override
      public Aggregator unwind(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$unwind",arg_,parse_engine()));
         constructMessage();
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#sort(Object) hiMongo.Aggregator.sort()}参照. */
      @Override
      public Aggregator sort(Object arg_){
         procs.add(hiMongo.namedObjToDoc("$sort",arg_,parse_engine()));
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
         procs.add(hiMongo.namedObjToDoc(proc_,arg_,parse_engine()));
         constructMessage();
         return this;
         }
      /** {@link hi.db.hiMongo.Aggregator#forEachDocument(hiU.ConsumerEx) hiMongo.Aggregator.forEachDocument()}参照. */
      @Override
      public Aggregator forEachDocument(hiU.ConsumerEx<Document,Exception> func_){
         return (Aggregator)super_forEachDocument(func_);
         }
      /** {@link #forEachDocument(hiU.ConsumerEx) forEachDocument(func_)}の別名. */
      @Override
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
      @Override
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
    * Collecction.
    */
   public class Collection implements hiMongo.Collection{
      DB     db;
      hiJSON.Engine msonEngine;
      hiJSON.Engine jsonEngine;
      String base_message;
      Collection(DB db_,String collName_){// base_messageは get:'xxxx'まで
         db= db_;
         base_message= db.base_message+",in:'"+collName_+"'";;
         }
      boolean       use_hson;
      /**
       * コマンドパラメタobjectをJSON文字列にする.
       *文字列の場合はそのまま、Fileの場合はFileから読み込み、Objectの場合はmson
       *@return 文字列
       */
      protected String objToJson(Object obj_){
         if( obj_==null ) return "{}";
         if( obj_ instanceof String ) return (String)obj_;
         if( obj_ instanceof File )   return hiFile.readTextAll((File)obj_);
         if( jsonEngine==null ) return hiMongo.mson_engine.str(obj_);
         return msonEngine.str(obj_);
         }
      /** {@link hi.db.hiMongo.Collection#getMongoCollection() hiMongo.Collection.getMongoCollection()}参照.
       *MongoCaller.Collectionではこの関数は無効です。
       *@return null
       */
      @Override
      public MongoCollection<Document> getMongoCollection(){
         return null;
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
         return new Finder(this,base_message+",execute:[{find:["+filterJ_+","+memberJ_+"]}");
         }
      /** {@link hi.db.hiMongo.Collection#find(Object) hiMongo.Collection.find()}参照. */
      @Override
      public hiMongo.Finder find(Object filterJ_){
         return new Finder(this,base_message+",execute:[{find:["+filterJ_+",{}]}");
         }
      /** {@link hi.db.hiMongo.Collection#find() hiMongo.Collection.find()}参照. */
      @Override
      public hiMongo.Finder find(){
         return new Finder(this,base_message+",execute:[{find:[{},{}]}");
         }
      /** {@link hi.db.hiMongo.Collection#insertOne(Object...) hiMongo.Collection.insertOne()}参照. */
      @Override
      public Collection insertOne(Object... records_){
         StringBuilder _message= new StringBuilder(base_message+",execute:[{insert:[");
         String _dlmt="";
         hiJSON.Engine _engine=cur_engine();
         for(Object _obj:records_){
            _message.append(_dlmt+objToJson(_obj));
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
         String _message=base_message+",execute:[{"+hiMongoCOM.COUNT+":"+objToJson(filterJ_)+"}]";
         return hiJSON.Probe.asInt(callWorker(_message,hiMongoCOM.COUNT));
         }
      /** {@link hi.db.hiMongo.Collection#insertMany(Object...) hiMongo.Collection.insertMany()}参照. */
      @Override
      public Collection insertMany(Object... recordSets_){
         StringBuilder _message= new StringBuilder(base_message+",execute:[{insert:[");
         String _dlmt="";
         hiJSON.Engine _engine=cur_engine();
         for(Object _records:recordSets_){
            String _rec_list= objToJson(_records);
            ArrayList<Object> _rec_set=_engine.parse(_rec_list).asNodeList();
            for(Object _obj:_rec_set){
               _message.append(_dlmt+objToJson(_obj));
               _dlmt=",";
               }
            }
         _message.append("]}]");
         ArrayList<Object> _list= callWorker(_message.toString());
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#aggregate(Object) hiMongo.Collection.aggregate()}参照. */
      @Override
      public Aggregator aggregate(Object proc_){
         Aggregator _ret=new Aggregator(this,base_message+",execute:[{aggregate:");
         _ret.procs   = hiMongo.parseAsBsonList(proc_,parse_engine());// BSONのリスト
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
         _message.append(objToJson(filterJ_))
                 .append(",")
                 .append(objToJson(updateJ_));
         _message.append("]}]");
         callWorker(_message.toString());
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#updateMany(Object,Object) hiMongo.Collection.updateMany()}参照. */
      @Override
      public Collection updateMany(Object filterJ_,Object updateJ_){
         StringBuilder _message= new StringBuilder(base_message+",execute:[{"+hiMongoCOM.UPDATE_MANY+":[");
         _message.append(objToJson(filterJ_))
                 .append(",")
                 .append(objToJson(updateJ_));
         _message.append("]}]");
         callWorker(_message.toString());
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#replaceOne(Object,Object) hiMongo.Collection.replaceOne()}参照. */
      @Override
      public Collection replaceOne(Object filterJ_,Object recordJ_){
         StringBuilder _message= new StringBuilder(base_message+",execute:[{"+hiMongoCOM.REPLACE_ONE+":[");
         _message.append(objToJson(filterJ_))
                 .append(",")
                 .append(objToJson(recordJ_));
         _message.append("]}]");
         callWorker(_message.toString());
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#deleteOne(Object) hiMongo.Collection.deleteOne()}参照. */
      @Override
      public Collection deleteOne(Object filterJ_){
         StringBuilder _message= new StringBuilder(base_message+",execute:[{"+hiMongoCOM.DELETE_ONE+":");
         _message.append(objToJson(filterJ_));
         _message.append("}]");
         callWorker(_message.toString());
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#deleteMany(Object) hiMongo.Collection.deleteMany()}参照. */
      @Override
      public Collection deleteMany(Object filterJ_){
         StringBuilder _message= new StringBuilder(base_message+",execute:[{"+hiMongoCOM.DELETE_MANY+":");
         _message.append(objToJson(filterJ_));
         _message.append("}]");
         callWorker(_message.toString());
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#createIndex(Object,Object) hiMongo.Collection.createIndex()}参照. */
      @Override
      public Collection createIndex(Object keyset_,Object option_){
         String _message=base_message+",execute:[{"+hiMongoCOM.CREATE_INDEX+":["
                                                   +objToJson(keyset_)+","+objToJson(option_)+"]}]";
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
   public class DB implements hiMongo.DB,
                                     Closeable{
      Client client;
      String base_message;
      /**
       * client(DBサーバとの接続)とデータベース名で構築する.
       *@param client_ client_
       *@param dbName_ データベース名
       *<!-- DB -->
       */
      DB(Client client_,String dbName_){
         client      = client_;
         base_message= client.base_message+",use:'"+dbName_+"'";
         }
      /** {@link hi.db.hiMongo.DB#in(String) hiMongo.DB.in()}参照. */
      @Override
      public hiMongo.Collection in(String collName_){
         return new Collection(this,collName_);
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
         return new Collection(this,name_);
         }
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
   /**
    * driver直使用でmongoDBサーバとの接続を表す.
    */
   public class Client implements hiMongo.Client,Closeable{
      String base_message;
      /** {@link hi.db.hiMongo.Client#connect(Object) hiMongo.Client.connect()}参照. */
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
         return new DB(this,dbName_);
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
      Client(){}
      /** {@link hi.db.hiMongo.Client#close() hiMongo.Client.close()}参照. */
      public void close(){
         //hiU.close(mongoClient);
         }
      }// end Client
   }

