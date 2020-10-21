//package hi;
import hi.db.hiMongo;
import otsu.hiNote.*;
import java.util.*;
import java.io.*;
import org.bson.Document;

public class hiMongoCaller {
   private hiMongoCaller(){} // hiMongoは生成できない
   static hiJSON.Engine mson_engine;
   static hiJSON.Engine json_engine;
   final static String MSG_STA=
       "{format:'hiMongo',content_type:'request',status:'request',content:{";
   final static String MSG_END="}}";
   hiJsonCOM_intf jsonCOM;
   hiMongoCaller(hiJsonCOM_intf jsonCOM_){
      jsonCOM    = jsonCOM_;
      mson_engine= hiMongo.engine();
      json_engine= hiMongo.engineJ();
      }
   @SuppressWarnings("unchecked")
   private ArrayList<Object> callWorker(String request_){
      String                 _result = jsonCOM.call(MSG_STA+request_+MSG_END);
      hiJsonCOM_intf.Message _msg    = hiMongo.parse(_result)
                                              .asClass(hiJsonCOM_intf.Message.class);
      return (ArrayList<Object>)_msg.content;
      }
   @SuppressWarnings("unchecked")
   private Object callWorker(String request_,String result_key_){
      String                 _result = jsonCOM.call(MSG_STA+request_+MSG_END);
      hiJsonCOM_intf.Message _msg    = hiMongo.parse(_result)
                                              .asClass(hiJsonCOM_intf.Message.class);
      // TODO:parseNode(_msg.content)がno sourceエラーとなる
      ArrayList<Map<String,Object>> _maps=hiMongo.parse(hiMongo.json(_msg.content))
                                                 .asMapList();
      for(Map<String,Object> _map:_maps){
         if( _map.containsKey(result_key_) ){
            return _map.get(result_key_);
            }
         }
      throw new hiException("result for "+result_key_+" not found");
      }
   public static hiJSON.Engine engine(){
      return mson_engine.clone();
      }
   public static hiJSON.Engine engineJ(){
      return json_engine.clone();
      }
   public Client connect(String addr_){
      return new Client().connect(addr_);
      }
   public DB use(String dbName_){
      return new Client().connect().use(dbName_);
      }
   public ArrayList<String> show_dbs(boolean sort_){
      return new Client().connect().show_dbs(sort_);
      }
   private String objToJson(Object obj_,hiJSON.Engine engine_){
      if( obj_ instanceof String ) return (String)obj_;
      if( obj_ instanceof File )   return hiFile.readTextAll((File)obj_);
      return engine_.str(obj_);
      }
   public class Accessor {
      hiJSON.Engine           msonEngine;
      hiJSON.Engine           jsonEngine;
      Collection              collection;
      StringBuilder message;
      Accessor(Collection collection_){
         collection= collection_;
         }
      final public hiJSON.Engine engine(){
         if( this.msonEngine== null ){
            this.msonEngine=hiMongoCaller.mson_engine.clone();
            }
         return this.msonEngine;
         }
      final public hiJSON.Engine engineJ(){
         if( this.jsonEngine== null ){
            this.jsonEngine=hiMongoCaller.json_engine.clone();
            }
         return this.jsonEngine;
         }
      protected String mson(Object obj_){
         if( msonEngine==null ) return hiMongoCaller.mson_engine.str(obj_);
         return msonEngine.str(obj_);
         }
      protected String json(Object obj_){
         if( jsonEngine==null ) return hiMongoCaller.json_engine.str(obj_);
         return jsonEngine.str(obj_);
         }
      protected String str(Object obj_){
         return hiU.str(obj_);
         }
      @SuppressWarnings("unchecked")
      private ArrayList<Object> getNodeList(long option_){
         String  _getList= message.toString()+",{command:'"+hiMongoCOM.GET_RECORD+"'}]";
         ArrayList<Object> _ret=(ArrayList<Object>)callWorker(_getList,hiMongoCOM.GET_RECORD);
         if( (option_&hiU.REVERSE)!=0 ) Collections.reverse(_ret);
         return _ret;
         }
      // --- forEach
      public Accessor super_forEachMson(hiU.ConsumerEx<String,Exception> func_){
         ArrayList<Object> _list=getNodeList(0);
         for(Object _obj:_list) hiU.rap(func_,mson(_obj));
         return this;
         }
      public Accessor super_forEachJson(hiU.ConsumerEx<String,Exception> func_){
         ArrayList<Object> _list=getNodeList(0);
         for(Object _obj:_list) hiU.rap(func_,json(_obj));
         return this;
         }
      public Accessor super_forEachDocument(hiU.ConsumerEx<Document,Exception> func_){
         ArrayList<Object> _list=getNodeList(0);
         for(Object _obj:_list) hiU.rap(func_,hiMongo.objToDoc(_obj,cur_engine()));
         return this;
         }
      public <T> Accessor super_forEachClass(Class<T>                    class_,
                                             hiU.ConsumerEx<T,Exception> func_){
         ArrayList<Object> _content=getNodeList(0);
         hiJSON.Engine     _engine=cur_engine();
         for(Object _obj:_content) {
            hiU.rap(func_,_engine.parseNode(_obj).as(class_));
            }
         return this;
         }
      // ---- getList
      public ArrayList<String> getMsonList(long option_){
         ArrayList<Object> _list=getNodeList(option_);
         ArrayList<String> _ret =new ArrayList<>();
         for(Object _obj:_list)_ret.add(mson(_obj));
         return _ret;
         }
      public ArrayList<String> getMsonList(){
         return getMsonList(0L);
         }
      public ArrayList<String> getJsonList(long option_){
         ArrayList<Object> _list=getNodeList(option_);
         ArrayList<String> _ret=new ArrayList<>();
         for(Object _obj:_list)_ret.add(json(_obj));
         return _ret;
         }
      public ArrayList<String> getJsonList(){
         return getJsonList(0L);
         }
      public ArrayList<Document> getDocumentList(long option_){
         ArrayList<Object> _list=getNodeList(option_);
         ArrayList<Document> _ret =new ArrayList<>();
         for(Object _obj:_list)_ret.add(hiMongo.objToDoc(_obj,cur_engine()));
         return _ret;
         }
      public ArrayList<Document> getDocumentList(){
         return getDocumentList(0L);
         }
      public ArrayList<Document> getList(long option_){
         return getDocumentList(option_);
         }
      public ArrayList<Document> getList(){
         return getDocumentList(0L);
         }
      public <T> ArrayList<T> getClassList(Class<T> class_,long option_){
         ArrayList<Object> _list=getNodeList(option_);
         ArrayList<T>      _ret=new ArrayList<>();
         hiJSON.Engine     _engine=cur_engine();
         for(Object _obj:_list)_ret.add(_engine.parseNode(_obj).as(class_));
         return _ret;
         }
      public <T> ArrayList<T> getClassList(Class<T> class_){
         return getClassList(class_,0L);
         }
      public <T> ArrayList<T> getList(Class<T> class_,long option_){
         return getClassList(class_,option_);
         }
      public <T> ArrayList<T> getList(Class<T> class_){
         return getClassList(class_,0L);
         }
      // --- engine
      final public hiJSON.Engine cur_engine(){
         if( this.msonEngine== null ){
            return collection.cur_engine();
            }
         return this.msonEngine;
         }
      final public hiJSON.Engine cur_engineJ(){
         if( this.jsonEngine== null ){
            return collection.cur_engineJ();
            }
         return this.jsonEngine;
         }
      // ---
      public Collection back(){
         return collection;
         }
      }

   public class Aggregator{
      }


   public class Finder extends Accessor{
      Finder(Collection coll_,String message_){
         super(coll_);
         message=new StringBuilder(message_);
         }
      public Finder sort(String param_){
         message.append(",{sort:"+param_+"}");
         return this;
         }
      public Finder limit(int param_){
         message.append(",{limit:"+param_+"}");
         return this;
         }
      public Finder forThis(hiU.ConsumerEx<Finder,Exception> func_){
         hiU.rap(func_,this);
         return this;
         }
      // --- forEachr
      public <T> Finder forEach(Class<T> class_,hiU.ConsumerEx<T,Exception> func_){
         return forEachClass(class_,func_);
         }
      public Finder forEach(hiU.ConsumerEx<Document,Exception> func_){
         return forEachDocument(func_);
         }
      public Finder forEachMson(hiU.ConsumerEx<String,Exception> func_){
         return (Finder)(super_forEachMson(func_));
         }
      public Finder forEachJson(hiU.ConsumerEx<String,Exception> func_){
         return (Finder)(super_forEachJson(func_));
         }
      public Finder forEachDocument(hiU.ConsumerEx<Document,Exception> func_){
         return (Finder)(super_forEachDocument(func_));
         }
      public <T> Finder forEachClass(Class<T>                    class_,
                                     hiU.ConsumerEx<T,Exception> func_){
         return (Finder)(super_forEachClass(class_,func_));
         } 
      public Finder with_option(long option_){
         engine().with_option(option_);
         return this;
         }
      public Finder without_option(long option_){
         engine().without_option(option_);
         return this;
         }
      }
   public class Collection{
      DB     db;
      hiJSON.Engine msonEngine;
      hiJSON.Engine jsonEngine;
      String base_message;
      Collection(DB db_,String collName_){// base_messageは get:'xxxx'まで
         db= db_;
         base_message= db.base_message+",get:'"+collName_+"'";;
         }
      final public hiJSON.Engine cur_engine(){
         if( this.msonEngine== null ){
            return hiMongoCaller.mson_engine;
            }
         return this.msonEngine;
         }
      final public hiJSON.Engine cur_engineJ(){
         if( this.jsonEngine== null ){
            return hiMongoCaller.json_engine;
            }
         return this.jsonEngine;
         }
      public Finder find(Object filterJ_, Object memberJ_){
         return new Finder(this,base_message+",execute:[{find:["+filterJ_+","+memberJ_+"]}");
         }
      public Collection forThis(hiU.ConsumerEx<Collection,Exception> func_){
         hiU.rap(func_,this);
         return this;
         }
      public Collection insertOne(Object... records_){
         StringBuilder _message= new StringBuilder(base_message+",execute:[{insert:[");
         String _dlmt="";
         hiJSON.Engine _engine=cur_engine();
         for(Object _obj:records_){
            _message.append(_dlmt+objToJson(_obj,_engine));
            _dlmt=",";
            }
         _message.append("]}]");
         ArrayList<Object> _list= callWorker(_message.toString());
         return this;
         }
      public Collection insertMany(Object... recordSets_){
         StringBuilder _message= new StringBuilder(base_message+",execute:[{insert:[");
         String _dlmt="";
         hiJSON.Engine _engine=cur_engine();
         for(Object _records:recordSets_){
            String _rec_list= objToJson(_records,_engine);
            ArrayList<Object> _rec_set=_engine.parse(_rec_list).asNodeList();
            for(Object _obj:_rec_set){
               _message.append(_dlmt+objToJson(_obj,_engine));
               _dlmt=",";
               }
            }
         _message.append("]}]");
         ArrayList<Object> _list= callWorker(_message.toString());
         return this;
         }
      public int count(){
         String _message=base_message+",execute:[{command:'"+hiMongoCOM.COUNT+"'}]";
         int _count=hiJSON.Probe.asInt(callWorker(_message,hiMongoCOM.COUNT));
         hiU.m(_count);
         return _count;
         }
      }
   public class DB{
      Client client;
      String base_message;
      DB(Client client_,String dbName_){
         client      = client_;
         base_message= client.base_message+",use:'"+dbName_+"'";
         }
      public Collection get(String collName_){
         return new Collection(this,collName_);
         }
      }
   public class Client{
      String base_message;
      public Client connect(Object addr_){
         base_message="connect:"+addr_.toString();
         return this;
         }
      public Client connect(){
         base_message="connect:{}";
         return this;
         }
      public DB use(String dbName_){
         return new DB(this,dbName_);
         }
      @SuppressWarnings("unchecked")
      public ArrayList<String> show_dbs(boolean sort_){
         String _message = base_message+","+hiMongoCOM.SHOW_DBS+":"+sort_;
         //ArrayList<Object> _list=callWorker(_message,hiMongoCOM,SHOW_DBS);
         ArrayList<String> _ret=(ArrayList<String>)callWorker(_message,hiMongoCOM.SHOW_DBS);// new ArrayList<>();
         //for(Object _obj:_list) _ret.add((String)_obj);
         return _ret;
         }
      }
   public class RemoteInfo extends hiMongo.RemoteInfo{
      }
   }

