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
import java.util.regex.*;
   /**
    * ノードツリー再解釈機(試験公開).
<p>
ノードツリーから要素を変数として取り出し、それに基づき別のノードツリーを再解釈します。
</p>
<pre class=preog10>
hiMongoEvaluator _eva=new hiMongoEvaluator();
_eva.getFromNodeTree(
   "[{type:'A',value:1020},{type:'A',value:600},{type:'A':value:2001}]"
  ,"{"{'#A_values':'*.value'}"
   );
または
hiMongoEvaluator _eva=new hiMongoEvaluator();
_eva.getFromNodeTree(
   "[{type:'A',value:1020},{type:'A',value:600},{type:'A':value:2001}]"
  ,"{"'*.value':'#A_values'}"
   );
// $values変数に[1020,600,2001]が記憶される
_eva.evaluate(
   "{value:{$in:#values}}"
   );
// {value:{$in:[1020,600,200]}}が得られる
</pre>
    */
class hiMongoEvaluator implements Cloneable {
   final static boolean D=hiMongo.MASTERD&&true;

   HashMap<String,Object> values=new HashMap<>();
   public hiMongoEvaluator(){}
   public hiMongoEvaluator(Object node_,Object... params_){
      getValueFromNode(node_,params_);
      }
   @Override
   @SuppressWarnings("unchecked")
   public hiMongoEvaluator clone(){
      try{
         hiMongoEvaluator _new_eva=(hiMongoEvaluator) super.clone();
         _new_eva.values = (HashMap<String,Object> ) this.values.clone();
         return _new_eva;
         }
      catch(Exception _ex){
         throw hiU.rap(_ex);
         }
      }
   public final boolean isEmpty(){
      return values.isEmpty();
      }
   public hiMongoEvaluator setValue(String name_,Object obj_){
      values.put(name_,obj_);
      return this;
      }
   @SuppressWarnings("unchecked")
   public final Object get(String name_){
      Object _ret= values.get(name_);
      if( _ret!=null ) return _ret;
      // nameのネストを調べる
      String[] _nest=hiText.split(name_,".");
      Object _obj= values.get(_nest[0]); // 先頭は文字列
      for(int _n=1;_n<_nest.length;++_n){
         if( _obj instanceof Map ){
            Map<String,Object> _map=(Map<String,Object>)_obj;
            _obj= _map.get(_nest[_n]);
            if( _obj==null ) return null;
            }
         else if( _obj instanceof List ){
            List<Object> _list=(List)_obj;
            int _pos= hiJSON.Probe.asInt(_nest[_n]);
            if( _pos<0 && _list.size()>= _pos ) return null;
            _obj= _list.get(_pos);
            }
         }
      return _obj;
      }


   /**
    * ノードツリーから指定の要素値を取り出し変数として記憶する.
    *@param node_ ノード(ノードツリーまたは文字列)
    *@param param_ 要素指定(ノードツリーまたは文字列)
    */
   @SuppressWarnings("unchecked")
   public void getValueFromNode(Object node_original_,Object... params_){
      Object _node= node_original_;
      if( _node instanceof String ){
         _node=hiMongo.mson_engine.parse((String)_node).asNode();
         }
      for(Object _param:params_){
         if(_param==null) continue;
         if( _param instanceof String ){
            _param=hiMongo.mson_engine.parse((String)_param).asNode();
            }
         Map<String,Object> _map= (Map<String,Object>)(_param);
         for(Map.Entry<String,Object> _kv:_map.entrySet()){
            // {#変数名:値}または{値:#変数名}
            String _key=_kv.getKey();
            String _val=(String)_kv.getValue();
            if( _key.startsWith("#")&&!_val.startsWith("#") ){}//OK
            else if( !_key.startsWith("#")&&_val.startsWith("#") ){
               String _sav= _key;
               _key       = _val;
               _val       = _sav;
               }
            else{
               throw new hiException("one and only one '#' is requied for '"+_key+"' and '"+_val+"'");
               }
            if( values.containsKey(_key) ){
               throw new hiException("the name "+_key+" is already defined.");
               }
            values.put(_key,findElement(_node,node_original_,_val));
            }
         }
      }
   private Object findElement(Object node_,Object node_original_,String position_){
      String[] _nest_and_default= hiText.split(position_,"?");
      String[] _nest=hiText.split(_nest_and_default[0],".");
      int _start=0;
      if( _nest[0].equals("@") ) return node_;
      if( _nest[0].equals("!") ) return node_original_;
      hiJSON.Probe _probe= hiJSON.probe(node_);
      Object       _ret  = findElementInner(_probe,_nest,_start);
      if( _ret!=null ) return _ret;
      if( _nest_and_default.length>=2 ){
         String _def=_nest_and_default[1];
         _ret= values.get(_def); // WAO!要チェック
         if( _ret!=null ) return _ret;
         return hiMongo.mson_engine.parse(_nest_and_default[1]).asNode();
         }
      return "";
      }
   private Object findElementInner(hiJSON.Probe probe_,String[] nest_,final int nest_idex_){
      if( nest_.length <= nest_idex_ ){
         return probe_.get();
         }
      String _pos=nest_[nest_idex_];
      if( probe_.isList() ){
         if( _pos.equals("*") ){
            // リスト取得
            ArrayList<Object> _ret=new ArrayList<>();
            probe_.forEach(Pr->{
               _ret.add(findElementInner(Pr,nest_,nest_idex_+1));
               });
            return _ret;
            }
         int _i_pos=hiU.atoi(_pos);
         if( _i_pos<0 ){
            _i_pos= probe_.size()+_i_pos;
            }
         if( _i_pos>= probe_.size() ) return new hiJSON.Probe(null);
         probe_.to(_i_pos);      // exceptionにはならずnull-probeのはず
         }
      else if( probe_.isMap() ){
         probe_.to(_pos);        // exceptionにはならずnull-probeのはず
         }
      else{
         throw new hiException("not List nor map index="+_pos);
         }
      return findElementInner(probe_,nest_,nest_idex_+1);
      }
   /**
    * 変数を解釈し、幾つかの演算を施す.
    *視野の取り扱いのためcontextを引数に入れ、少し汚くなった
    *@param
    */
  public Object evaluate(Object node_,hiMongoContext context_){
      if( node_ instanceof String ){
         node_ = hiMongo.parse((String)node_).asNode();
         }
      return evalObjAsObj(node_,context_);
      }
   /**
    * 変数を解釈し、幾つかの演算を施す.
    *視野の取り扱いのためcontextを引数に入れ、少し汚くなった
    *@param
    */
   @SuppressWarnings("unchecked")
   public Object evalObjAsObj(Object node_,hiMongoContext context_){
      //if( values.isEmpty() ) {
      //   return node_;
      //   }
      if( node_ instanceof List ){
         ArrayList<Object> _ret = new ArrayList<>();
         ArrayList<Object> _list= (ArrayList<Object>)node_;
         for(Object _obj:_list){
            _ret.add(evalObjAsObj(_obj,context_));
            }
         return _ret;
         }   
      if( node_ instanceof Map ){
         Map<String,Object> _ret= new HashMap<String,Object>();
         Map<String,Object> _map=(Map<String,Object>)node_;
         for(Map.Entry<String,Object> _kv:_map.entrySet()){
            String _key= _kv.getKey();
            if( _key.equals("$calc") ){
               hiMongoCalc _calc= new hiMongoCalc();
               String[] _tokens = hiText.tokens(_kv.getValue().toString()
                                               ,"+-*/()"
                                               ,hiU.KEEP_DELIMITER);
               for(String _tkn:_tokens){
                  _tkn=_tkn.trim();
                  if(D)hiU.m("TOKEN="+_tkn);
                  if(      _tkn.equals("+") ) _calc.push('+');
                  else if( _tkn.equals("-") ) _calc.push('-');
                  else if( _tkn.equals("*") ) _calc.push('*');
                  else if( _tkn.equals("/") ) _calc.push('/');
                  else if( _tkn.equals("(") ) _calc.push('(');
                  else if( _tkn.equals(")") ) _calc.push(')');
                  else {
                     Object _tkn_obj= context_.get(_tkn);
                     if(D)hiU.m("TOKEN_OBJ="+hiU.str(_tkn_obj,hiU.WITH_TYPE));
                     if( _tkn_obj== null ) _tkn_obj=_tkn;
                     Object _val=evaluate(_tkn_obj,context_);
                     _calc.push(_val);
                     }
                  }
               if(D)hiU.m(">>>>>>>> CALL FINISH");
               Object _retT=_calc.finish();
               if(D)hiU.o("<<<<<<<< FINISH FINISEHD :"+hiU.str(_retT,hiU.WITH_TYPE));
               return _retT;
               }
            _ret.put(_kv.getKey(),evaluate(_kv.getValue(),context_));
            }
         return _ret;
         }
      if( node_ instanceof String ){
         String _val=(String)node_;
         //Object _obj= values.get(_val);
         Object _obj= this.get(_val);
         if( _obj!=null ) {
            return _obj;
            }
         }
      return node_;
      }
   private long asLong(Object obj_){
      if( obj_ instanceof Date ){
         Date _date=(Date) obj_;
         return _date.getTime();
         }
      return hiJSON.Probe.asLong(obj_);
      }
   private double asDouble(Object obj_){
      if( obj_ instanceof Date ){
         Date _date=(Date) obj_;
         return (double)_date.getTime();
         }
      return hiJSON.Probe.asDouble(obj_);
      }
   }

