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
   //======  GET/DISP/EVAL
   //--- get × 3
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
   @SuppressWarnings("unchecked")
   public <T> T get(String value_name_,T default_value_){
      try{
         T _ret= (T)this.get(value_name_,default_value_.getClass());
         if( _ret!=null ) return _ret;
         }
      catch(Exception _ex){}
      return default_value_;
      }
   @SuppressWarnings("unchecked")
   public <T> T get(String value_name_,Class<T> class_){
      Object _obj=this.get(value_name_);
      if( _obj==null ) return null;
      if( _obj.getClass() == class_ ){
         return (T)_obj;
         }
      if(D)hiU.m("CONVERT "+_obj.getClass()+" to "+class_.getName());
      _obj= asNode(_obj);//文字列の場合もあるので
      return (T)hiMongo.parseNode(_obj).asClass(class_);
      }

   //--- disp × 2
   String disp(String text_){
      return disp(text_,0);
      }
   String disp(String text_,long option_){
      // KEEP_QOUTE      : KEEP_QUOTE, AUTO_FLUSH, BODY, EVEN_FINAL_STATIC
      // AS_EMPTY_STRING : AS_EMPTY_STRING, NO_ERR_PRINT
      try{
         text_=hiRegex.with("(#[\\w\\.]+)","${1}")
                      .withFunction(Rs->{
                          Object _obj=get(Rs);
                          if(_obj!=null) {
                             if((_obj instanceof String) &&
                                ((option_&hiU.KEEP_QUOTE)==0) ) return (String)_obj;
                             long _disp_option = option_&hiMongo.DISP_MASK;
                             if( (option_&hiMongo.USE_JSON)!=0 ){
                                return hiMongo.json(get(Rs),_disp_option);
                                }
                             else if( (option_&hiMongo.USE_str)!=0 ){
                                return hiU.str(get(Rs),_disp_option);
                                }
                             return hiMongo.mson(get(Rs),_disp_option);
                             }
                          return "";
                          })
                      .replace(text_)
                      .result();
         return text_;
         }
      catch(Exception _ex){
         //if( (option_&hiU.KEEP_ON_ERROR)!=0 ) throw hiU.rap(_ex);
         //if( (option_&WITH_EXCEPTION )!=0 ) _ex.printStackTrace(System.err);
         if( (option_&hiU.AS_EMPTY_STRING)!=0 ) return "";
         return "{$exception:'"+_ex.getMessage()+"'}";
         }
      }
   //--- eval × 3
   // eval nodeまたは文字列を評価する
   Object evalAsObj(Object obj_){
      if( obj_ instanceof File ){
         obj_ = hiFile.readTextAll((File)obj_);
         }
      if( obj_ instanceof String ){
         obj_ = hiMongo.parse((String)obj_).asNode();
         }
      return evalObjAsObj(obj_);
      }
   @SuppressWarnings("unchecked")
   public <T> T evalAsObj(Object obj_,T default_value_){
      try{
         Object _obj=this.evalAsObj(obj_);
         return (T)hiMongo.parseNode(_obj).asClass(default_value_.getClass());
         }
      catch(Exception _ex){}
      return default_value_;
      }
   @SuppressWarnings("unchecked")
   public <T> T evalAsObj(Object obj_,Class<T> class_){
      Object _obj=this.evalAsObj(obj_);
      return (T)hiMongo.parseNode(_obj).asClass(class_);
      }

   @SuppressWarnings("unchecked")
   public void set_the_value(Object obj_){
      the_value= obj_;
      }
   @SuppressWarnings("unchecked")
   public Object get_the_value(){
      return the_value;
      }
   @SuppressWarnings("unchecked")
   public <T> T get_the_value(T default_value_){
      if( the_value==null ) return default_value_;
      try{
         return (T)get_the_value(default_value_.getClass());
         }
      catch(Exception _ex){}
      return default_value_;
      }
   @SuppressWarnings("unchecked")
   public <T> T get_the_value(Class<T> class_){
      if( the_value==null ) return null;
      if( the_value.getClass() == class_ ){
         return (T)the_value;
         }
      Object _obj= asNode(the_value);//文字列の場合もあるので
      return (T)hiMongo.parseNode(_obj).asClass(class_);
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
   Object asNode(Object obj_){
      if( obj_ instanceof File ){
         obj_ = hiFile.readTextAll((File)obj_);
         }
      if( obj_ instanceof String ){
         obj_ = hiMongo.parse((String)obj_).asNode();
         }
      return obj_;
      }
   String evalAsStr(Object obj_){
      obj_=asNode(obj_);
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
      obj_=asNode(obj_);
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
