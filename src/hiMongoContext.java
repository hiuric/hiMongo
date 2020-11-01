package hi.db;
import otsu.hiNote.*;
import java.util.*;
import java.io.*;
import org.bson.conversions.Bson;
import org.bson.Document;
import java.util.regex.*;
/**
 * DB-コンテキスト.
 * Clientも持つがuse時にclone()される
 */
abstract class hiMongoContext implements Cloneable {
   final static boolean D=hiMongo.MASTERD&&true;

   hiMongo.MoreMongo            creator;
   hiMongoBase.Client           client;
   hiMongoBase.DB               db;
   String                       db_name;
   hiMongoBase.Collection       collection;
   String                       collection_name;
   ArrayDeque<hiMongoEvaluator> eva;       // nullの可能性あり
   hiMongoEvaluator             global_eva;// nullの可能性あり
   Object                       the_value;
   @Override
   public hiMongoContext clone(){
      try{
         hiMongoContext _new_context=(hiMongoContext) super.clone();
         // useによる生成時のcloneではevaもglobal_evaもnull
         // これはらdb.clone時
         if( eva!=null ){
            _new_context.eva=new ArrayDeque<>();
            for(hiMongoEvaluator _eva:eva){
               _new_context.eva.push(_eva.clone());
               }
            }
         if( global_eva!=null ){
            _new_context.global_eva=global_eva.clone();
            }
           return _new_context;
         }
      catch(Exception _ex){throw hiU.rap(_ex);}
      }
   
   public void set_the_value(Object obj_){
      the_value= obj_;
      }
   public Object get_the_value(){
      return the_value;
      }

   class AutoPop implements Closeable{
      public void close(){
         eva.pollFirst(); // 空の場合はnullが返る
         if( eva.isEmpty() ) eva=null;
         }
      }
   public AutoPop pushEva(Object node_,Object... params_){
      if( eva==null ) eva= new ArrayDeque<>();
      eva.addFirst(new hiMongoEvaluator(node_,params_));
      return new AutoPop();
      }
/*
   public hiMongoContext popEva(){
      if( eva==null ) return this;// あり得ない
      eva.pollFirst(); // 空の場合はnullが返る
      if( eva.isEmpty() ) eva=null;
      return this;
      }
*/
   public hiMongoContext setEva(Object node_,Object... params_){
      if( global_eva==null )global_eva=new hiMongoEvaluator();
      global_eva.getValueFromNode(node_,params_);
      return this;
      }
   public hiMongoContext setValue(String name_,Object obj_){
      if( global_eva==null )global_eva=new hiMongoEvaluator();
      global_eva.setValue(name_,obj_);
      return this;
      }
   //
   String disp(String text_){
      text_=hiRegex.with("(#[\\w\\.]+)","${1}")
                   .withFunction(Rs->{
                       Object _obj=get(Rs);
                       if(_obj!=null) return hiMongo.mson(get(Rs));
                       return "";
                       })
                   .replace(text_)
                   .result();
      return text_;
      }
   //
   public Object get(String name_){
      if( eva!=null ){
         for(hiMongoEvaluator _eva:eva){
            Object _ret=_eva.get(name_);
            if( _ret!=null ) return _ret;
            }
         }
      if( global_eva!=null ){
         return  global_eva.get(name_);
         }
      return null;
      }
   //--------------------------------------
   // 既にnode化されているものを評価する
   //--------------------------------------
   Object evalObjAsObj(Object obj_){
      if(D)hiU.m(hiMongo.mson(obj_));
      if( eva==null && global_eva== null){
         return obj_;
         }
      try{
         if( eva!=null ){
            for(hiMongoEvaluator _eva:eva){
               obj_ = _eva.evalObjAsObj(obj_,this);
               }
            }
         if( global_eva!=null ){
            obj_ = global_eva.evalObjAsObj(obj_,this);
            }
         return obj_;
         }
      catch(Exception _ex){
         return obj_.toString();
         }
      }
   //--------------------------------------
   // nodeまたは文字列を評価する
   //--------------------------------------
   Object evalAsObj(Object obj_){
      if( obj_ instanceof File ){
         obj_ = hiFile.readTextAll((File)obj_);
         }
      if( obj_ instanceof String ){
         obj_ = hiMongo.parse((String)obj_).asNode();
         }
      return evalObjAsObj(obj_);
      }
   String evalAsStr(Object obj_){
      if( obj_ instanceof File ){
         obj_ = hiFile.readTextAll((File)obj_);
         }
      if( obj_ instanceof String ){
         obj_ = hiMongo.parse((String)obj_).asNode();
         }
      obj_= evalObjAsObj(obj_);
      if( obj_ instanceof String ) return(String)obj_;
      try{
         return hiMongo.mson(obj_);
         }
      catch(Exception _ex){
         return obj_.toString();
         }
      }
   Bson evalAsBson(Object obj_){
      if( obj_ instanceof File ){
         obj_ = hiFile.readTextAll((File)obj_);
         }
      if( obj_ instanceof String ){
         obj_ = hiMongo.parse((String)obj_).asNode();
         }
      obj_= evalObjAsObj(obj_);
      return hiMongo.objToBson(obj_);
      }
   Document evalAsDoc(Object obj_){
      return (Document)evalAsBson(obj_);
      }
   // 以下の２つはCaller/Workerでのみ使用する
   abstract ArrayList<Object> call(String request_);
   abstract Object            call(String request_,String result_key_);
   }
