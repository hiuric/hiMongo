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
/**
 * hiMongoCaller/hiMongoDirectの共通実装.
 */
class hiMongoBase {
   final static boolean D=hiMongo.MASTERD&&true;
   static abstract class Client implements hiMongo.Client,
                                           Closeable{
      hiMongoContext context;
      Client(hiMongoContext context_){
         context= context_;
         context.client=this;
         }
      }
   static abstract class DB implements hiMongo.DB,
                                       Closeable{
      hiMongoContext context;
      DB(hiMongoContext context_,hiMongoBase.Client client_,String name_){
         context         = context_.clone();
         context.db      = this;
         context.db_name = name_;
         }
      /** {@link hi.db.hiMongo.Finder#get(String) hiMongo.Finder.get()}参照. */
      @Override
      public String name(){
         return context.db_name;
         }
      /** {@link hi.db.hiMongo.Finder#get(String) hiMongo.Finder.get()}参照. */
      @Override
      public DB setValue(String name_,Object obj_){
         context.setValue(name_,obj_);
         return this;
         }

      /** {@link hi.db.hiMongo.Accessor#getValueAsDocument(String) hiMongo.Accessor.getValueAsDocument()}参照. */
      @Override
      @SuppressWarnings("unchecked")
      public Document getValueAsDocument(String text_){
         try{
            return (Document)context.get(text_);
            }
         catch(Exception _ex){}
         return new Document((Map)context.get(text_));
         }
      /** {@link hi.db.hiMongo.Accessor#getValueAsProbe(String) hiMongo.Accessor.getValueAsProbe()}参照. */
      @Override
      @SuppressWarnings("unchecked")
      public hiJSON.Probe getValueAsProbe(String text_){
         return hiJSON.probe(context.get(text_));
         }
      //======  GET/EVAL/DISP
      //--- DB.get × 3
      @Override
      public Object get(String name_){
         return context.get(name_);
         }
      @Override
      @SuppressWarnings("unchecked")
      public <T> T get(String value_name_,T default_value_){
         return context.get(value_name_,default_value_);
         }
      @Override
      @SuppressWarnings("unchecked")
      public <T> T get(String value_name_,Class<T> class_){
         return context.get(value_name_,class_);
         }
      //--- DB.eval × 3
      @Override
      public Object eval(Object obj_){
         return context.evalAsObj(obj_);
         }
      @Override
      public <T> T eval(Object obj_,T default_value_){
         return context.evalAsObj(obj_,default_value_);
         }
      @Override
      public <T> T eval(Object obj_,Class<T> class_){
         return context.evalAsObj(obj_,class_);
         }
      //--- DB.disp × 2
      @Override
      public String disp(String text_){
         return context.disp(text_);
         }
      @Override
      public String disp(String text_,long option_){
         return context.disp(text_,option_);
         }
      //----- SET/GET_THE_VALUE DB
      @Override
      public DB set_the_value(Object obj_){
         context.set_the_value(obj_);
         return this;
         }
      @Override
      @SuppressWarnings("unchecked")
      public Object get_the_value(){
         return context.get_the_value();
         }
      @Override
      @SuppressWarnings("unchecked")
      public <T> T get_the_value(T default_value_){
         return (T)context.get_the_value(default_value_);
         }
      @Override
      @SuppressWarnings("unchecked")
      public <T> T get_the_value(Class<T> class_){
         return (T)context.get_the_value(class_);
         }
      /** {@link hi.db.hiMongo.Collection#forThis(hiU.ConsumerEx) hiMongo.Collection.forThis()}参照. */
      @Override
      public hiMongo.DB forThis(hiU.ConsumerEx<hiMongo.DB,Exception> func_){
         hiU.rap(func_,this);
         return this;
         }
      }
   static abstract class Collection implements hiMongo.Collection { 
      hiMongoContext    context;
      hiJSON.Engine     msonEngine;
      hiJSON.Engine     jsonEngine;
      Collection(hiMongoContext context_,hiMongoBase.DB db_,String name_){
         context                 = context_;
         context.collection      = this;
         context.collection_name = name_;
         }
      @Override
      public String name(){
         return context.collection_name;
         }
      /** {@link hi.db.hiMongo.Collection#parse_engine() hiMongo.Collection.parse_engine()}参照. */
      @Override
      public hiJSON.Engine parse_engine(){
         return cur_engine();
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
      public hiMongo.DB back(){
         return (hiMongo.DB)context.db;
         }
      /** {@link hi.db.hiMongo.Collection#forThis(hiU.ConsumerEx) hiMongo.Collection.forThis()}参照. */
      @Override
      public hiMongo.Collection forThis(hiU.ConsumerEx<hiMongo.Collection,Exception> func_){
         hiU.rap(func_,this);
         return this;
         }
      /** {@link hi.db.hiMongo.Collection#in(String) hiMongo.Collection.in()}参照. */
      public hiMongo.Collection in(String collectionName_){
         return (hiMongo.Collection)(back().in(collectionName_));
         }
      /** {@link hi.db.hiMongo.Collection#find(Object) hiMongo.Collection.find()}参照. */
      @Override
      public hiMongo.Finder find(Object filterJ_){
         return find(filterJ_,"{}");
         }
      /** {@link hi.db.hiMongo.Collection#find() hiMongo.Collection.find()}参照. */
      @Override
      public hiMongo.Finder find(){
         return find("{}","{}");
         }
      /**
       * List<Doument>を得る.
       *@param data_ 拡張JSON文字列、File、List<Document>
       *@param engine_ 拡張JSON解析エンジン(nullの場合は用いない)
       *@return List<Document>
       *<!-- Collection -->
       */
      @SuppressWarnings("unchecked")
      List<Bson> parseAsBsonList(Object data_){//,hiJSON.Engine engine_){
         if( data_ instanceof List ){
            data_= context.evalAsObj(data_);
            List<Object>   _objs=(List<Object>)data_;
            if( _objs.isEmpty() ) return (List<Bson>)data_;
            if( _objs.get(0) instanceof Bson ) return (List<Bson>)data_;
            }
         return new ArrayList<Bson>(parseAsDocumentList(data_));//,engine_));
         }
      /**
       * List<Doument>を得る.
       *@param data_ 拡張JSON文字列、File、List<Document>
       *@param engine_ 拡張JSON解析エンジン(nullの場合は用いない)
       *@return List<Document>
       *<!-- Collection -->
       */
      @SuppressWarnings("unchecked")
      List<Document> parseAsDocumentList(Object data_){//,hiJSON.Engine engine_){
          ArrayList<Document> _ret=new ArrayList<>();
         if( data_ instanceof File ){
            data_ = hiFile.readTextAll((File)data_);
            }
         if( data_ instanceof List ){
            data_ = context.evalObjAsObj(data_);
            List<Object>   _objs=(List<Object>)data_;
            if( _objs.isEmpty() ) return (List<Document>)data_;
            if( _objs.get(0) instanceof Document ) return (List<Document>)data_;
            try{
               for(Object _obj:_objs){
                  _ret.add(new Document((Map<String,Object>)_obj));
                  }
               return _ret;
               }
            catch(Exception _ex){}
            data_=hiMongo.mson(data_);// 標準msonで文字列化
            }
         try{
            List<Object> _objs=hiMongo.parse((String)data_).asNodeList();
            _objs= (List<Object>)context.evalObjAsObj(_objs);
            for(Object _obj:_objs){
               _ret.add(new Document((Map<String,Object>)_obj));
               }
            return _ret;
            }
         catch(Exception _ex) {
            throw new hiException("illegal document class "+_ex.getMessage()+"  "+data_.getClass().getName());
            }
         }
      @Override
      public Collection setValue(String name_,Object obj_){
         context.setValue(name_,obj_);
         return this;
         }
      //======  GET/DISP/EVAL
      //--- Collection.get × 3
      @Override
      public Object get(String value_name_){
         return context.get(value_name_);
         }
      @Override
      @SuppressWarnings("unchecked")
      public <T> T get(String value_name_,T default_value_){
         return (T)context.get(value_name_,default_value_);
         }
      @Override
      @SuppressWarnings("unchecked")
      public <T> T get(String value_name_,Class<T> class_){
         return (T)context.get(value_name_,class_);
         }
      //--- Collection.disp
      @Override
      public String disp(String text_){
         return context.disp(text_);
         }
      @Override
      public String disp(String text_,long option_){
         return context.disp(text_,option_);
         }
      //--- Collection.eval
      @Override
      public Object eval(Object obj_){
         return context.evalAsObj(obj_);
         }
      @Override
      public <T> T  eval(Object obj_,T default_value_){
         return context.evalAsObj(obj_,default_value_);
         }
      @Override
      public <T> T  eval(Object obj_,Class<T> class_){
         return context.evalAsObj(obj_,class_);
         }
      //----- Collection SET/GET_THE_VALUE
      @Override
      public Collection set_the_value(Object obj_){
         context.set_the_value(obj_);
         return this;
         }
      @Override
      @SuppressWarnings("unchecked")
      public Object get_the_value(){
         return context.get_the_value();
         }
      @Override
      @SuppressWarnings("unchecked")
      public <T> T get_the_value(T default_value_){
         return (T) context.get_the_value(default_value_);
         }
      @Override
      @SuppressWarnings("unchecked")
      public <T> T get_the_value(Class<T> class_){
         return (T) context.get_the_value(class_);
         }
      //--------
      @Override
      @SuppressWarnings("unchecked")
      public Document getValueAsDocument(String text_){
         try{
            return (Document)context.get(text_);
            }
         catch(Exception _ex){}
         return new Document((Map)context.get(text_));
         }
     @Override
      @SuppressWarnings("unchecked")
      public hiJSON.Probe getValueAsProbe(String text_){
         return hiJSON.probe(context.get(text_));
         }
      }
   static abstract class Finder extends    hiMongoBase.Accessor
                                implements hiMongo.Finder {
      public Finder(hiMongoContext context_,hiMongoBase.Collection collection_){
         super(context_,collection_);
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
      /** {@link hi.db.hiMongo.Finder#forEachDocument(hiU.ConsumerEx) hiMongo.Finder.forEachDocument()}参照. */
      @Override
      public Finder forEachDocument(hiU.ConsumerEx<Document,Exception> func_){
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
      /** {@link hi.db.hiMongo.Finder#forThis(hiU.ConsumerEx) hiMongo.Finder.forThis()}参照. */
      @Override
      public Finder forThis(hiU.ConsumerEx<hiMongo.Finder,Exception> func_){
         hiU.rap(func_,this);
         return this;
         }
      /** {@link hi.db.hiMongo.Finder#readOne(Object) hiMongo.Finder.readOne()}参照. */
      @Override
      public Finder readValueList(Object param_){
         List<Document> _list=(List<Document>)getDocumentList(0);
         context.setEva(_list,param_);
         return this;
         }
      /** {@link hi.db.hiMongo.Finder#readOne(Object) hiMongo.Finder.readOne()}参照. */
      @Override
      public Finder readOne(Object param_){
         limit(1);
         List<Document> _list=(List<Document>)getDocumentList(0);
         limit(0);
         if( _list.isEmpty() ) return this;
         context.setEva(_list.get(0),param_);
         return this;
         }
      @Override
      public Finder setValue(String name_,Object obj_){
         context.setValue(name_,obj_);
         return this;
         }
      @Override
      public Finder set_the_value(Object obj_){
         context.set_the_value(obj_);
         return this;
         }
      @Override
      @SuppressWarnings("unchecked")
      public Finder withValue(String name_){
         Object _list=context.get(name_);
         if( _list==null ){
            _list= hiMongo.parse(name_).asNode();
            if( !(_list instanceof List) ) {
               throw new hiException("not list "+name_);
               }
            with_list=(List<Object>)_list;
            }
         else {
            if( _list instanceof String ){
               _list= hiMongo.parse((String)_list).asNode();
               }
            if( !(_list instanceof List) ) {
               throw new hiException("not list "+name_+" "+hiMongo.mson(_list));
               }
            with_list=(List<Object>)_list;
            }
         return this;
         }
      @Override
      public Finder forEach(String                                   param_,
                            hiU.ConsumerEx<hiMongo.Finder,Exception> func_){
         return (Finder)super_forEach(param_,func_);
         }
      @Override
      public Finder forOne(String                                   param_,
                           hiU.ConsumerEx<hiMongo.Finder,Exception> func_){
         limit(1);
         Finder _ret=(Finder)super_forEach(param_,func_);
         limit(0);
         return _ret;
         }
      /** {@link hi.db.hiMongo.Finder#find(Object,Object) hiMongo.Finder.find()}参照. */
      @Override
      public Finder find(Object filterJ_,Object memberJ_){
         return (Finder)back().find(filterJ_,memberJ_);
         }
      /** {@link hi.db.hiMongo.Finder#find(Object) hiMongo.Finder.find()}参照. */
      @Override
      public Finder find(Object filterJ_){
         return (Finder)back().find(filterJ_);
         }
      /** {@link hi.db.hiMongo.Finder#find() hiMongo.Finder.find()}参照. */
      @Override
      public Finder find(){
         return (Finder)back().find();
         }
      /** {@link hi.db.hiMongo.Finder#aggregate(Object) hiMongo.Finder.aggregate()}参照. */
      @Override
      public Aggregator aggregate(Object filterJ_){
         return (Aggregator)back().aggregate(filterJ_);
         }
      /** {@link hi.db.hiMongo.Finder#aggregate() hiMongo.Finder.aggregate()}参照. */
      @Override
      public Aggregator aggregate(){
         return (Aggregator)back().aggregate();
         }
      /** {@link hi.db.hiMongo.Finder#in(String) hiMongo.Finder.in()}参照. */
      @Override
      public Collection in(String name_){
         return (Collection)back().back().in(name_);
         }
      /** カレントCollectionのinsertOne()を呼ぶ. */
      @Override
      public Collection insertOne(Object... records_){
         return (Collection)back().insertOne(records_);
         }
      /** カレントCollectionのinsertMany()を呼ぶ.  */
      @Override
      public Collection insertMany(Object... jsonTexts_){
         return (Collection)back().insertMany(jsonTexts_);
         }
      /** カレントCollectionのupdateOne()を呼ぶ.  */
      @Override
      public Collection updateOne(Object filterJ_,Object updateJ_){
         return (Collection)back().updateOne(filterJ_,updateJ_);
         }
      /** カレントCollectionのupdateMany()を呼ぶ.  */
      @Override
      public Collection updateMany(Object filterJ_,Object updateJ_){
         return (Collection)back().updateMany(filterJ_,updateJ_);
         }
      /** カレントCollectionのupdateOne()を呼ぶ.  */
      @Override
      public Collection deleteOne(Object filterJ_){
         return (Collection)back().deleteOne(filterJ_);
         }
      /** カレントCollectionのupdateMany()を呼ぶ.  */
      @Override
      public Collection deleteMany(Object filterJ_){
         return (Collection)back().deleteMany(filterJ_);
         }
      /** カレントCollectionのreplaceOne()を呼ぶ.  */
      @Override
      public Collection replaceOne(Object filterJ_,Object recordJ_){
         return (Collection)back().replaceOne(filterJ_,recordJ_);
         }
      @Override 
      public  Finder forEach(hiU.ConsumerEx<hiMongo.Finder,Exception> func_){
         List<Object> _list=getNodeList(0);
         for(Object _obj:_list) {
            try(Closeable C=context.pushEva(_obj,"{'#CUR':'@'}");){
               hiU.rap(func_,this);
               }
            catch(Exception _ex){throw hiU.rap(_ex);}
            }
         return this;
         }
      @Override 
      public  Finder forOne(hiU.ConsumerEx<hiMongo.Finder,Exception> func_){
         limit(1);
         List<Object> _list=getNodeList(0);
         for(Object _obj:_list) {
            try(Closeable C=context.pushEva(_obj,"{'#CUR':'@'}");){
               hiU.rap(func_,this);
               }
            catch(Exception _ex){throw hiU.rap(_ex);}
            }
         limit(0);
         return this;
         }
      private Object recursiveCall(Object             record_
                                  ,Map<String,String> refer_
                                  ,String             param_
                                  ,String             fields_
                                  ,ArrayList<Object>  finds_
                                  ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_
                                  ){
         if(D)hiU.i(get("#TOP")+" -  "+get("#CUR"));
         try(Closeable C=context.pushEva(record_,"{'#CUR':'@'}",param_);){
            //-------------------------------------
            // 本レコードでのラムダ式呼び出し
            //-------------------------------------
            Object _ret=func_.apply((hiMongo.Finder)this);
            if( _ret!=null ) return _ret;
            //-------------------------------------
            // refer定義でのリカーシブコール
            //-------------------------------------
            hiJSON.Probe probe= hiJSON.probe(record_);
            for(Map.Entry<String,String> _kv:refer_.entrySet()){
               // keyは自レコードの要素名。この値をfindの値とする
               // valueはfindに記述する要素名
               hiJSON.Probe _probe   = hiJSON.probe(record_);
               Object       _this_val= _probe.get(_kv.getKey());
               if( _this_val==null ) continue;
               hiMongo.Finder _find=context.collection.find("{'"+_kv.getValue()+"':'"+_this_val+"'}"
                                                           ,fields_);
               List<Document> _recs=_find.getDocumentList();
               if( D ) hiU.m("RECS="+_recs);
               for(Document _doc:_recs){
                  _ret=recursiveCall(_doc,refer_,param_,fields_,finds_,func_);
                  if( _ret!=null ) return _ret;
                  }
               }
            //-------------------------------------
            // find定義でのリカーシブコール
            //-------------------------------------
            if( finds_!=null ){
               for(Object _filt:finds_){
                  // _filtの中身は{name:value}等のfind第一引数
                  if(D)hiU.m("_ff="+hiU.str(_filt,hiU.WITH_TYPE));
                  hiMongo.Finder _find = context.collection.find(_filt,fields_);
                  List<Document> _recs = _find.getDocumentList();
                  if(D) hiU.m("RECS<2>="+_recs);
                  for(Document _doc:_recs){
                     _ret=recursiveCall(_doc,refer_,param_,fields_,finds_,func_);
                     if( _ret!=null ) return _ret;
                     }
                  }
               }
            return null;
            }
         catch(Exception _ex){
            throw hiU.rap(_ex);
            }
         }
      @Override
      public  Finder forEachRecursive(String                                   refer_
                                 ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_){
         forEachRecursive(refer_,null,func_,null,null);
         return this;
         }
      @Override
      public  Finder forOneRecursive(String                                   refer_
                                 ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_){
         forOneRecursive(refer_,null,func_,null,null);
         return this;
         }
      @Override
      public  Finder forEachRecursive(String                                   refer_
                                 ,String                                   param_
                                 ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_){
         forEachRecursive(refer_,param_,func_,null,null);
         return this;
         }
      @Override
      public  Finder forOneRecursive(String                                   refer_
                                 ,String                                   param_
                                 ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_){
         forOneRecursive(refer_,param_,func_,null,null);
         return this;
         }
      @Override
      public  Finder forEachRecursive(String                                   refer_
                                 ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_
                                 ,hiU.ConsumerEx<hiMongo.Finder,Exception> foundEnd_
                                 ,hiU.ConsumerEx<hiMongo.Finder,Exception> notFoundEnd_
                                 ){
         forEachRecursive(refer_,null,func_,foundEnd_,notFoundEnd_);
         return this;
         }
      @Override
      public  Finder forOneRecursive(String                                   refer_
                                 ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_
                                 ,hiU.ConsumerEx<hiMongo.Finder,Exception> foundEnd_
                                 ,hiU.ConsumerEx<hiMongo.Finder,Exception> notFoundEnd_
                                 ){
         forEachRecursive(refer_,null,func_,foundEnd_,notFoundEnd_);
         return this;
         }
      @Override
      public  Finder forEachRecursive(String                                   refer_
                                 ,String                                   param_
                                 ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_
                                 ,hiU.ConsumerEx<hiMongo.Finder,Exception> foundEnd_
                                 ,hiU.ConsumerEx<hiMongo.Finder,Exception> notFoundEnd_
                                 ){
          return forEachRecursive(refer_,param_,func_,foundEnd_,notFoundEnd_,0);
          }
      Finder forEachRecursive(String                                   refer_
                                 ,String                                   param_
                                 ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_
                                 ,hiU.ConsumerEx<hiMongo.Finder,Exception> foundEnd_
                                 ,hiU.ConsumerEx<hiMongo.Finder,Exception> notFoundEnd_
                                 ,int limit_
                                 ){
         if(D)hiU.i();
         Map<String,Object> _map      = hiJSON.parse(refer_).asMap(); 
         Map<String,String> _referMap = new HashMap<String,String>();
         String             _fields   = "{}";
         ArrayList<Object>  _filts    = null; // Objectは[f,f]
         for(Map.Entry<String,Object> _kv:_map.entrySet()){
            String _key=_kv.getKey();
            String _val=_kv.getValue().toString();
            if( _key.startsWith("#CUR.") ){
               // 左辺が#CUR.
               if( _val.startsWith("#CUR.") ){
                  throw new hiException("'#CUR.' is only required on one side "+refer_);
                  }
               _referMap.put(_key.substring(5),_val);
               }
            else if( _key.equals("#FIELD") ){
               // 左辺が#FIELD
               if( _val.equals("#FIELD") ){
                  throw new hiException("'#FIELD' is only required on one side "+refer_);
                  }
               _fields=_val;
               }
            else if( _key.equals("#FILTER") ){
               // 左辺が#FIND
               if( _val.equals("#FILTER") ){
                  throw new hiException("'#FILTER' is only required on one side "+refer_);
                  }
               if( _filts==null ) _filts=new ArrayList<>();
               _filts.add(hiMongo.parse(_val).asNode());
               if(D)hiU.m("#FILTER="+hiU.str(_filts,hiU.WITH_TYPE));
               }
            else {
               // 左辺が値
               if( _val.startsWith("#CUR.") ){
                  _referMap.put(_val.substring(5),_key);
                  }
               else if( _val.startsWith("#FIELD") ){
                  _fields=_key;
                  }
               else if( _val.startsWith("#FILTER") ){
                  if( _filts==null ) _filts=new ArrayList<>();
                  _filts.add(hiMongo.parse(_key).asNode());
                  if(D)hiU.m("#FILTER="+hiU.str(_filts,hiU.WITH_TYPE));
                  }
               else{
                  throw new hiException("at least one '#CUR.','#FIELD'or'#FILTER' is required "+refer_);
                  }
               }
            }
         if(D)hiU.m("refMap="+hiU.str(_referMap));
         if(limit_!=0) limit(limit_);
         List<Object> _list=getNodeList(0);
         if(limit_!=0) limit(0);
         try{
            for(Object _obj:_list) {
               try(Closeable C=context.pushEva(_obj,"{'#TOP':'@'}");){
                  Object _ret=recursiveCall(_obj,_referMap,param_,_fields,_filts,func_);
                  if( _ret!=null ){
                     if( foundEnd_!=null ){
                        try(Closeable C2=context.pushEva(_ret,"{'#RESULT':'!'}");){// !は文字列をjson解釈させない
                           foundEnd_.accept(this);
                           }
                        catch(Exception _ex){throw _ex;}
                        }
                     }
                  else if( notFoundEnd_!=null ){
                     notFoundEnd_.accept(this);
                     }
                  }
               catch(Exception _ex){ throw _ex; }
               }
            }
         catch(Exception _ex){ throw hiU.rap(_ex); }
         return this;
         }
      @Override
      public  Finder forOneRecursive(String                                   refer_
                                 ,String                                   param_
                                 ,hiU.FunctionEx<hiMongo.Finder,Object,Exception> func_
                                 ,hiU.ConsumerEx<hiMongo.Finder,Exception> foundEnd_
                                 ,hiU.ConsumerEx<hiMongo.Finder,Exception> notFoundEnd_
                                 ){
          return forEachRecursive(refer_,param_,func_,foundEnd_,notFoundEnd_,1);
          }
      }
   static abstract class Aggregator extends    hiMongoBase.Accessor
                                    implements hiMongo.Aggregator {
      public Aggregator(hiMongoBase.Collection collection_){
         super(collection_.context,collection_);
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
      /** {@link hi.db.hiMongo.Aggregator#forEachDocument(hiU.ConsumerEx) hiMongo.Aggregator.forEachDocument()}参照. */
      @Override
      public Aggregator forEachDocument(hiU.ConsumerEx<Document,Exception> func_){
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
      //public <T> Aggregator forEach(Class<T>                    class_,
      //                          hiU.ConsumerEx<T,Exception> func_){
      //   return (Aggregator)super_forEachClass(class_,func_);
      //   }
      /** {@link hi.db.hiMongo.Aggregator#forThis(hiU.ConsumerEx) hiMongo.Aggregator.forThis()}参照. */
      @Override
      public Aggregator forThis(hiU.ConsumerEx<hiMongo.Aggregator,Exception> func_){
         hiU.rap(func_,this);
         return this;
         }
      }
   //=====================================================================
   // Accessor
   //=====================================================================
   static abstract class Accessor implements hiMongo.Accessor{
      hiMongoContext         context;
      hiJSON.Engine          msonEngine;
      hiJSON.Engine          jsonEngine;
      List<Object>           with_list;

      abstract protected ArrayList<Object> getNodeList(long option_);
      //
      Accessor(hiMongoContext context_,hiMongoBase.Collection collection_){
         context   = context_;
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
       * コマンドパラメタobjectをJSON文字列にする.
       *文字列の場合はそのまま、Fileの場合はFileから読み込み、Objectの場合はmson
       *@return 文字列
       *<!-- Accessor -->
       */
      protected String objToJson(Object obj_){
         if( obj_ instanceof String ) return (String)obj_;
         if( obj_ instanceof File )   return hiFile.readTextAll((File)obj_);
         if( jsonEngine==null ) return hiMongo.mson_engine.str(obj_);
         return msonEngine.str(obj_);
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
      //@Override
      //public <T> ArrayList<T>  getList(Class<T> class_
      //                                     ,long option_
      //                                     ){
      //   return getClassList(class_,option_);
      //   }
      /** {@link #getClassList(Class)}の別名. */
      //@Override
      //public <T> ArrayList<T> getList(Class<T> class_){
      //   return getClassList(class_,0);
      //   }
      /** {@link hi.db.hiMongo.Accessor#getDocumentList(long) hiMongo.Accessor.getDocumentList()}参照. */
      @Override
      public ArrayList<Document> getDocumentList(long option_){
         ArrayList<Object> _list=getNodeList(option_);
         ArrayList<Document> _ret =new ArrayList<>();
         for(Object _obj:_list)_ret.add(hiMongo.objToDoc(_obj));//,cur_engine()));
         return _ret;
         }
      /** {@link hi.db.hiMongo.Accessor#getDocumentList() hiMongo.Accessor.getDocumentList()}参照. */
      @Override
      public ArrayList<Document> getDocumentList(){
         return getDocumentList(0);
         }
      /**
       *forEachDocumentのベース実装.
       *カスケードAPIの為派生クラスにpublic APIを置いてある
       *<!-- Accessor -->
       */
      protected Accessor super_forEachDocument(hiU.ConsumerEx<Document,Exception> func_){
         ArrayList<Object> _list=getNodeList(0);
         for(Object _obj:_list) hiU.rap(func_,hiMongo.objToDoc(_obj));//,cur_engine()));
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
      protected  Accessor super_forEach(String                           param_
                                       ,hiU.ConsumerEx<hiMongo.Finder,Exception> func_){
         ArrayList<Object> _list=getNodeList(0);
         for(Object _obj:_list) {
            try(Closeable C=context.pushEva(_obj,"{'#CUR':'@'}",param_);){
               context.pushEva(_obj,"{'#CUR':'@'}",param_);
               //evaluator.getValueFromNode(_obj,param_);
               hiU.rap(func_,(hiMongo.Finder)this);
               }
            catch(Exception _ex){throw hiU.rap(_ex);}
            }
         return this;
         }

      /** {@link hi.db.hiMongo.Accessor#back() hiMongo.Accessor.back()}参照. */
      @Override
      public Collection back(){
         return context.collection;
         }
      /** {@link hi.db.hiMongo.Accessor#engineJ() hiMongo.Accessor.engineJ()}参照. */
      @Override
      public hiJSON.Engine engineJ(){
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
            return context.collection.cur_engine();
            }
         return this.msonEngine;
         }
      /** {@link hi.db.hiMongo.Accessor#cur_engineJ() hiMongo.Accessor.cur_engineJ()}参照. */
      @Override
      public hiJSON.Engine cur_engineJ(){
         if( this.jsonEngine== null ){
            return context.collection.cur_engineJ();
            }
         return this.jsonEngine;
         }
      /** {@link hi.db.hiMongo.Accessor#parse_engine() hiMongo.Accessor.parse_engine()}参照. */
      @Override
      public hiJSON.Engine parse_engine(){
         return context.collection.parse_engine();
         }
      /** {@link hi.db.hiMongo.Accessor#mson(Object) hiMongo.Accessor.mson()}参照. */
      @Override
      public String mson(Object obj_){
         return cur_engine().str(obj_);
         }
      /** {@link hi.db.hiMongo.Accessor#mson(Object) hiMongo.Accessor.json()}参照. */
      @Override
      public String json(Object obj_){
         return cur_engineJ().str(obj_);
         }
 
      /** {@link hi.db.hiMongo.Accessor#getValueAsDocument(String) hiMongo.Accessor.getValueAsDocument()}参照. */
      @Override
      @SuppressWarnings("unchecked")
      public Document getValueAsDocument(String text_){
         try{
            return (Document)context.get(text_);
            }
         catch(Exception _ex){}
         return new Document((Map)context.get(text_));
         }
      /** {@link hi.db.hiMongo.Accessor#getValueAsProbe(String) hiMongo.Accessor.getValueAsProbe()}参照. */
      @Override
      @SuppressWarnings("unchecked")
      public hiJSON.Probe getValueAsProbe(String text_){
         return hiJSON.probe(context.get(text_));
         }
      //======  GET/DISP/EVAL
      //--- Accessor.get × 3
     /** {@link hi.db.hiMongo.Accessor#get(String) hiMongo.Accessor.get()}参照. */
      @Override
      public Object get(String text_){
         return context.get(text_);
         }
      @Override
      @SuppressWarnings("unchecked")
      public <T> T get(String value_name_,T default_value_){
         return (T)context.get(value_name_,default_value_);
         }
      @Override
      @SuppressWarnings("unchecked")
      public <T> T get(String value_name_,Class<T> class_){
         return (T)context.get(value_name_,class_);
         }
      //--- Accessor.disp × 2
      /** {@link hi.db.hiMongo.Accessor#disp(String) hiMongo.Accessor.disp()}参照. */
      @Override
      public String disp(String text_){
         return context.disp(text_);
         }
      /** {@link hi.db.hiMongo.Accessor#disp(String,long) hiMongo.Accessor.disp()}参照. */
      @Override
      public String disp(String text_,long option_){
         return context.disp(text_,option_);
         }
      //--- Accessor.disp × 3
      /** {@link hi.db.hiMongo.Accessor#eval(Object) hiMongo.Accessor.eval()}参照. */
      @Override
      public Object eval(Object obj_){
         return context.evalAsObj(obj_);
         }
      @Override
      public <T> T eval(Object obj_,T default_value_){
         return (T)context.evalAsObj(obj_,default_value_);
         }
      @Override
      public <T> T eval(Object obj_,Class<T> class_){
         return (T)context.evalAsObj(obj_,class_);
         }
      //----- Accessor SET/GET_THE_VALUE Collection
      @Override
      @SuppressWarnings("unchecked")
      public Object get_the_value(){
         return context.the_value;
         }
      @Override
      @SuppressWarnings("unchecked")
      public <T> T get_the_value(T default_value_){
         return (T)context.get_the_value(default_value_);
         }
      @Override
      @SuppressWarnings("unchecked")
      public <T> T get_the_value(Class<T> class_){
         return (T)context.get_the_value(class_);
         }

      @SuppressWarnings("unchecked")
      List<Object> super_withValue(String name_){
         Object _list=context.get(name_);
         if( _list==null ){
            _list= hiMongo.parse(name_).asNode();
            if( !(_list instanceof List) ) {
               throw new hiException("not list "+name_);
               }
            }
         else {
            if( _list instanceof String ){
               _list= hiMongo.parse((String)_list).asNode();
               }
            if( !(_list instanceof List) ) {
               throw new hiException("not list "+name_+" "+hiMongo.mson(_list));
               }
            }
         return (List<Object>)_list;
         }
      }
   }
