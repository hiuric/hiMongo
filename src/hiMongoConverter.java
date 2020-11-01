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

class MongoConverter {
   //=========== Date ハンドリング =======
   final static SimpleDateFormat dateFormat
         = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
   /** DateのISODate出力 */
   static <T> String toISODateStr(hiFieldFormat fmt_,
                                  Class<T>      class_,
                                  Object        obj_){
      long _unix_time =((Date)obj_).getTime();
      Date _dt       = new Date();
      _dt.setTime(_unix_time-TimeZone.getDefault().getRawOffset());
      return "ISODate("+fmt_.text_quote+dateFormat.format(_dt)+fmt_.text_quote+")";
      }
   /** Dateの辞書形式出力 */
   static <T> String toDateMapStr(hiFieldFormat fmt_,
                                  Class<T>      class_,
                                  Object        obj_){
      long _unix_time =((Date)obj_).getTime();
      Date _dt       = new Date();
      _dt.setTime(_unix_time-TimeZone.getDefault().getRawOffset());
      return "{"+fmt_.name_quote+"$date"+fmt_.name_quote+":"+_unix_time+"}";
      }
   /** ISODate関数形式解析 */
   static Object funcStr_toDate(String funcName_
                               ,ArrayList<Object> args_
                               )throws Exception{
      Date _dt=dateFormat.parse((String)(args_.get(0)));
      long _local=_dt.getTime()+TimeZone.getDefault().getRawOffset();
      return new Date(_local);
      }
   /** Dateの辞書形式解析 */
   static Object dictStr_toDate(String             key_
                               ,Object             data_
                               ,Map<String,Object> map_
                               )throws Exception{
      return new Date(hiJSON.Probe.asLong(data_));
      }
   /** Date型または辞書型ノードをDateにする */
   @SuppressWarnings("unchecked")
   static Object node_toDate(Class<?> class_
                            ,hiField  field_
                            ,Object   obj_){
      if( obj_.getClass()==Date.class ) return obj_;
      if( obj_ instanceof Map ){
         Map<String,Object> _map=(Map<String,Object>)obj_;
         Object _value=_map.get("$date");
         long _long_value=0;
         if( _value instanceof Double ){
            _long_value= (long)(double)(Double)_value;
            }
         else if( _value instanceof Long ){
            _long_value= (long)(Long)_value;
            }
         return new Date(_long_value);
         }
      return null;
      }
   //=========== ObjectId ハンドリング =======
   /** ObjectId出力 */
   static <T> String toOidStr(hiFieldFormat fmt_
                             ,Class<T>      class_
                             ,Object        obj_){
      ObjectId _oid=(ObjectId)obj_;
      return "ObjectId("+fmt_.quotedText(_oid.toHexString())+")";
      }
   /** ObjectIdの辞書形式出力 */
   static <T> String toOidMapStr(hiFieldFormat fmt_
                                ,Class<T>      class_
                                ,Object        obj_){
      ObjectId _oid=(ObjectId)obj_;
      String   _nquot=fmt_.name_quote;
      String   _tquot=fmt_.text_quote;
      return "{"+_nquot+"$oid"+_nquot+":"+fmt_.quotedText(_oid.toHexString())+"}";
      }
   /** ObjectIdの辞書形式解析 */
   static Object dictStr_toOid(String             key_
                              ,Object             data_
                              ,Map<String,Object> map_
                              )throws Exception{
      return new ObjectId((String)data_);
      }
   /** ObjectId関数形式解析 */
   static Object funcStr_toOid(String funcName_
                              ,ArrayList<Object> args_
                              )throws Exception{
      return new ObjectId((String)(args_.get(0)));
      }
   /** ObjectId型または辞書型ノードをObjectIdにする */
   @SuppressWarnings("unchecked")
   static Object node_toOid(Class<?> class_
                           ,hiField  field_
                           ,Object   obj_){
      if( obj_.getClass()==ObjectId.class ) return obj_;
      if( obj_ instanceof Map ){
         Map<String,String> _map=(Map<String,String>)obj_;
         String _value=_map.get("$oid");
         return new ObjectId(_value);
         }
      return null;
      }
   //=========== BsonRegularExpression ハンドリング =======
   /** /xxx/形式の解釈 */
   static Object regex_parse_func(String regex_,String option_){
      //System.out.println("Test.regex_func regex "+regex_+" option "+option_);
      return new BsonRegularExpression(regex_,option_);
      }
   /** 辞書形式出力 */
   static <T> String toRegexMapStr(hiFieldFormat fmt_
                                  ,Class<T>      class_
                                  ,Object        obj_){
      BsonRegularExpression _regex=(BsonRegularExpression)obj_;
      String   _nquot=fmt_.name_quote;
      return "{"+_nquot+"$regex"+_nquot+":"
                                         +fmt_.quotedText(_regex.getPattern())+","
                +_nquot+"$options"+_nquot+":"
                                         +fmt_.quotedText(_regex.getOptions())
              +"}";
      }
   /** 辞書形式解析 */
   static Object dictStr_toRegex(String             key_
                                ,Object             data_
                                ,Map<String,Object> map_
                                )throws Exception{
      String _option= (String)map_.get("$option");
      if( _option != null ){
         return new BsonRegularExpression((String)data_,_option);
         }
      return new BsonRegularExpression((String)data_);
      }
   /** クラスまたは辞書型ノードをBsonRegularExpressionにする */
   @SuppressWarnings("unchecked")
   static Object node_toRegex(Class<?> class_
                             ,hiField  field_
                             ,Object   obj_){
      if( obj_.getClass()==BsonRegularExpression.class ) return obj_;
      if( obj_ instanceof Map ){
         Map<String,String> _map=(Map<String,String>)obj_;
         String _regex = (String)_map.get("$regex");
         String _option= (String)_map.get("$option");
         if( _option != null ){
            return new BsonRegularExpression((String)_regex,_option);
            }
         return new BsonRegularExpression((String)_regex);
         }
      return null;
      }
   /** nodeオブジェクトをLongに変える */
   @SuppressWarnings("unchecked")
   static Object node_toLong(Class<?> class_
                            ,hiField  field_
                            ,Object   obj_){
      return new Long((long)(Integer)obj_);
      }
   /** $numberLongの辞書形式解析 */
   static Object dictStr_toLong(String             key_
                               ,Object             data_
                               ,Map<String,Object> map_
                               )throws Exception{
      return new Long(Long.parseLong((String)data_));
      }
   /** NumberLong関数形式解析 */
   static Object funcStr_toLong(String            funcName_
                               ,ArrayList<Object> args_
                               )throws Exception{
      return new Long(Long.parseLong((String)args_.get(0)));
      }
   /** NumberDecimal関数解析 */
   static Object funcStr_toDecimal(String funcName_
                                  ,ArrayList<Object> args_
                                  )throws Exception{
      return new BigDecimal((String)args_.get(0));
      }
   /** $numberLongの辞書形式解析 */
   static Object dictStr_toDecimal(String                key_
                                  ,Object             data_
                                  ,Map<String,Object> map_
                                  )throws Exception{
      return new BigDecimal((String)data_);
      }
   /** BigDecimal,Decimal128型または辞書型ノードをBigDecimalにする */
   @SuppressWarnings("unchecked")
   static Object node_toBigDecimal(Class<?> class_
                                  ,hiField  field_
                                  ,Object   obj_){
      if( obj_.getClass()==BigDecimal.class ) return obj_;
      if( obj_.getClass()==Decimal128.class ) {
         return ((Decimal128)obj_).bigDecimalValue();
         } 
      if( obj_ instanceof Map ){
         Map<String,String> _map=(Map<String,String>)obj_;
         String _value=_map.get("$numberDecimal");
         return new BigDecimal(_value);
         }
      return null;
      }
   /** BigDecimal,Decimal128 辞書型(JSON) */
   static <T> String toDecMapStr(hiFieldFormat fmt_
                                ,Class<T>      class_
                                ,Object        obj_){
      return "{"+fmt_.name_quote+"$numberDecimal"+fmt_.name_quote+
             ":"+fmt_.text_quote+obj_.toString()+fmt_.text_quote+"}";
      }
   /** NumberDecimal関数型 */
   static <T> String toDecStr(hiFieldFormat fmt_
                             ,Class<T>      class_
                             ,Object        obj_){
      return "NumberDecimal("+fmt_.text_quote+obj_.toString()+fmt_.text_quote+")";
      }
   /** toString利用 */
   static <T> String toStr(hiFieldFormat fmt_
                             ,Class<T>      class_
                             ,Object        obj_){
      return obj_.toString();
      }
   }

