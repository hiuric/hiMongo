package hi.db;

import otsu.hiNote.*;
import java.util.*;
//
// hiCalculatorとしてotsuライブラリに移す可能性大
//
class hiMongoCalc {
   final static boolean D=hiMongo.MASTERD&&true;
   static class Element {
      char   type;// '#','+','-','*,'/','('
      Object value;
      Element(char type_){
         type= type_;
         }
      Element(Object value_){
         type = '#';
         value= value_;
         }
      }
   ArrayDeque<Element> stack=new ArrayDeque<>();
   public hiMongoCalc push(char type_){
      if(D)hiU.i(type_);
      switch(type_){
      case '+':case'-':
         reduce_MUL_DIV();
         reduce_ADD_SUB();
         stack.offerLast(new Element(type_));
         break;
      case '*':case'/':
         reduce_MUL_DIV();
         stack.offerLast(new Element(type_));
         break;
      case '(':
         stack.offerLast(new Element(type_));
         break;
      case ')':
         reduce_PARREN();
         break;
         }
      if(D)hiU.m(stack);
      return this;
      }
   public hiMongoCalc push(Object value_){
      if(D)hiU.i(value_);
      stack.offerLast(new Element(value_));
      reduce_MUL_DIV();
      if(D)hiU.m("STACK="+stack,hiU.WITH_TYPE);
      return this;
      }
   public Object finish(){
      if(D)hiU.i();
      if(D)hiU.m("$$$$ CALL reduce_MUL_DIV");
      reduce_MUL_DIV();
      if(D)hiU.m("$$$$ CALL reduce_ADD_SUB");
      reduce_ADD_SUB();
      if( stack.size()!=1 ){
         throw new hiException("Syntax error");
         }
      Object _ret=stack.pollLast().value;
      if(D)hiU.o(_ret);
      return _ret;
      }
   void reduce_ADD_SUB(){
      if(D)hiU.i();
      // 保留状態の+,-が有れば縮約する
      int _size=stack.size();
      if( _size>=3 ){
         if(D)hiU.m(hiU.str(stack,hiU.WITH_TYPE));
         Element _may_value2  = stack.pollLast();
         Element _may_OPERATOR= stack.pollLast();
         Element _may_value1  = stack.pollLast();
         if(  _may_value2.type=='#' &&
             (_may_OPERATOR.type=='+'||_may_OPERATOR.type=='-')&&
              _may_value1.type=='#'){
            if( _may_value1.value instanceof Date ){
               if( _may_value2.value instanceof Date ){
                  // (Date - Date) -> long
                  if( _may_OPERATOR.type=='+' ){
                     throw new hiException("Date + Date not supported");
                     }
                  Date _date1=(Date)(_may_value1.value);
                  Date _date2=(Date)(_may_value2.value);
                  long _val1= _date1.getTime();
                  long _val2= _date2.getTime();
                  stack.offerLast(new Element(new Long(_val2-_val1)));
                  }
               else{
                  // (Date +- long) -> Date
                  Date _date1=(Date)(_may_value1.value);
                  long _val1 = _date1.getTime();
                  long _val2 = hiJSON.Probe.asLong(_may_value2.value);
                  if( _may_OPERATOR.type=='+' ) _val1 += _val2;
                  else                          _val1 -= _val2;
                  stack.offerLast(new Element(new Date(_val1)));
                  } 
               }
            else{
               double _d1=hiJSON.Probe.asDouble(_may_value1.value);
               double _d2=hiJSON.Probe.asDouble(_may_value2.value);
               double _val=_may_OPERATOR.type=='+'?(_d1+_d2):(_d1-_d2);
               stack.offerLast(new Element((Double)(_val)));
               }
            if(D)hiU.m("reduced=>"+hiU.str(stack));
            }
         else{
            stack.offerLast(_may_value1);
            stack.offerLast(_may_OPERATOR);
            stack.offerLast(_may_value2);
            if(D)hiU.m("not-reduced=>"+hiU.str(stack));
            }
         }
      if(D)hiU.o(hiU.str(stack));
      }
   void reduce_MUL_DIV(){
      if(D)hiU.i();
      // 保留状態の*,/が有れば縮約する
      int _size=stack.size();
      if( _size>=3 ){
         Element _may_value2= stack.pollLast();
         Element _may_OPERATOR= stack.pollLast();
         Element _may_value1= stack.pollLast();
         if(  _may_value2.type=='#' &&
             (_may_OPERATOR.type=='*'||_may_OPERATOR.type=='/')&&
              _may_value1.type=='#'){
            double _d1=hiJSON.Probe.asDouble(_may_value1.value);
            double _d2=hiJSON.Probe.asDouble(_may_value2.value);
            double _val=_may_OPERATOR.type=='*'?(_d1*_d2):(_d1/_d2);
            stack.offerLast(new Element((Double)(_val)));
            if(D)hiU.m("reduced=>"+hiU.str(stack));
            }
         else{
            stack.offerLast(_may_value1);
            stack.offerLast(_may_OPERATOR);
            stack.offerLast(_may_value2);
            if(D)hiU.m("not-reduced=>"+hiU.str(stack));
            }
         }
      if(D)hiU.o(hiU.str(stack,hiU.WITH_TYPE));
      }
   void reduce_PARREN(){
      reduce_MUL_DIV();
      reduce_ADD_SUB();
      if( stack.size()<2 ){
         throw new hiException("Parentheses do not correspond<1>");
         }
      Element _may_value_or_parren= stack.pollLast();
      if( _may_value_or_parren.type=='#' ){
         // value:'('を削りvalueを入れる
         Element _must_parren= stack.pollLast();
         if( _must_parren.type!='(' ){
            throw new hiException("Parentheses do not correspond<2>");
            }
         stack.offerLast(_may_value_or_parren);
         }
      else if( _may_value_or_parren.type!='(' ){
         // 空の()でなければならないが
         throw new hiException("Parentheses do not correspond<3> type="+_may_value_or_parren);
         }
      }
   }


