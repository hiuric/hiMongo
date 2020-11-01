import hi.db.*;
import otsu.hiNote.*;
import java.io.*;
import java.util.Date;
//import org.bson.Document;
public class Test {
   
   public static void main(String[] args_){
      //--------------------------------------------------------
      hiMongo.MoreMongo mongo;
      File _modeFile= new File("../test_workerMode.txt");
      if( _modeFile.exists() ) {
         String _host= hiFile.readTextAll(_modeFile).trim();
         if( _host.length()<5 ){
            mongo=new hiMongoCaller(new hiMongoWorker());
            hiU.out.println("// MODE: Caller/Worker");
            }
         else {
            mongo=new hiMongoCaller(new hiMonWorkerSample.COM(_host,8010,3));
            hiU.out.println("// MODE: call SERVER '"+_host+"'");
            }
         }
      else {
         mongo=new hiMongoDirect();
         hiU.out.println("// MODE: DIRECT");
         }
      //--------------------------------------------------------
      hiMongoCalc _calc=new hiMongoCalc();

      _calc.push(new Double(5));
      _calc.push('+');
      _calc.push(new Double(3));
      _calc.push('-');
      _calc.push(new Double(2));
      _calc.push('*');
      _calc.push(new Double(3));
      hiU.out.println("result="+_calc.finish());

      _calc.push('(');
      _calc.push(new Double(3));
      _calc.push('+');
      _calc.push(new Double(2));
      _calc.push(')');
      _calc.push('*');
      _calc.push('(');
      _calc.push(new Double(1.5));
      _calc.push('+');
      _calc.push(new Double(2));
      _calc.push(')');
      hiU.out.println("result="+_calc.finish());

      _calc.push(new Date(100000));
      _calc.push('+');
      _calc.push(new Long(3000));
      Date _result=(Date)_calc.finish();
      hiU.out.println("result="+_result.getTime());

      hiMongo.DB db= hiMongo.use("db01");
      db.in("coll_01")
        .find("{}","{_id:0}")
        .forEachJson(Rj->System.out.println(Rj))
        .forEach(Fi->{
            hiU.out.println("-----");
            hiU.out.println(Fi.json(Fi.get("#CUR.date")));
            hiU.out.println(Fi.json(Fi.eval("{$calc:'#CUR.date-8000000'}")));
            Fi.setValue("#last_date",new Date(1597648060000L));
            hiU.out.println(Fi.json(Fi.eval("{$calc:'#last_date-#CUR.date'}")));
            })
        ;

      //throw new hiException("IT'S OK");
      }
   }
/*
getListAs(名前[,hiU.REVERSE])

@名前             -> リスト
@名前.数値        -> 指定番のレコード
@名前.数値.要素名 -> 指定番のレコードの要素
@名前.要素名      -> 要素のリスト

{$sub:[A,B,C...]}
{$add:[A,B,C...]}
{$mul:[A,B,C...]}
{$div:[A,B,C...]}

A+B        -> {$add:[A,B]}
A*X+B/Y+D  -> {$add:[{$mul:[A,X]},{$div:[B,Y]},D]}

{$str_trm:{ text:A,start:開始,end:終了+1 }//
{$str_cat:[A,B,C...]}
{$str_regex{ text:A,pattern:B,replace:C }


      db.in("coll_01")
        .find("{}","{date:1}")
*/

