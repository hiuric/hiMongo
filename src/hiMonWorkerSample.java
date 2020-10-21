package hi.db;
import hi.db.hiMongoWorker;
import otsu.hiNote.*;
import java.net.*;
/**
 * お試しCaller/Worker クライアント/サーバ.
 *<p>
 *{@link hi.db.hiMongoCaller}と{@link hi.db.hiMongoWorker}を通信で繋ぐお試しキットです。
 *</p>
 *<p>
 *セキュリテイーも安定性も考慮してありません。
 *</p>
<p>
このサンプルはビルドされた形でもhiMongo_x_xx.jarに含まれています。
以下の様にして起動することができます。
</p>
<p>
Windowsでサーバを起動
</p>
<pre class=quote10>
:: simpleServ.bat
set LIB_DIR=<i>ライブラリフォルダ</i>
set hiMongoLIB=%LIB_DIR%hiMongo_0_10.jar
set hiNoteLIB=%LIB_DIR%\hiNote_3_10.jar
set mongoLIB=%LIB_DIR%\mongo-java-driver-3.12.5.jar
set LIBS=".;%hiNoteLIB%;%mongoLIB%;%hiMongoLIB%
java -cp %LIBS% hi.db.hiMonWorkerSample
</pre>
<p>
Windowsでクライアントを起動
</p>
<pre class=quote10>
:: simpleClie.bat
set LIB_DIR=<i>ライブラリフォルダ</i>
set hiMongoLIB=%LIB_DIR%hiMongo_0_10.jar
set hiNoteLIB=%LIB_DIR%\hiNote_3_10.jar
set mongoLIB=%LIB_DIR%\mongo-java-driver-3.12.5.jar
set LIBS=".;%hiNoteLIB%;%mongoLIB%;%hiMongoLIB%
java -cp %LIBS% hi.db.hiMonWorkerSample -call localhost
pause
</pre>
<p>
以上に対応するスクリプトをソースセットのbin下に置いてあります。
</p>
<p>
全コードを示します。
</p>
<pre class=quote10>
public class hiMonWorkerSample {
   public static class COM implements hiStringCOM,AutoCloseable {
      hiSocket.TCP.ForText sock;
      public COM(String host_,int port_,int timeout_){
         sock= new hiSocket.TCP.ForText(host_,port_,timeout_);
         }
      @Override
      public String call(String msg_){
         sock.println(msg_.replaceAll("\n"," "));// 1行にして送る
         return sock.readLine();                 // 1行受け取る
         }
      @Override
      public void close()throws Exception{
         hiU.close(sock);
         }
      }
   public static class SampleWorker extends Thread {
      hiSocket.TCP.ForText sock;
      hiMongoWorker        worker=new hiMongoWorker();
      public SampleWorker(hiSocket.TCP.ForText sock_){
         sock = sock_;
         start();
         }
      public void run(){
         System.err.println("Worker start");
         try{
            String _line;
            while( (_line=sock.readLine())!=null ){
               String _result=worker.call(_line);         // 1行受け取る
               sock.println(_result.replaceAll("\n"," "));// 1行にして送る
               }
            }
         catch(Exception _ex){hiU.close(sock);}
         System.err.println("Worker end");
         }
      }
   static void help(){
      System.out.println("PARAMETER: [-port #(8010)] [-timeout sec. (infinit)]");
      System.out.println("           [-call host] [-port #(8010)] [-timeout sec. (20)]");
      System.exit(0);
      }
   static public void main(String[] args_){
      hiArgs  _args   = new hiArgs(args_);
      if( _args.argBool("-help") ) help();
      String  _call   = _args.argStr("-call",null);
      int     _port   = _args.argInt("-port",8010);
      int     _timeout= _args.argInt("-timeout",0)*1000;
      if( _call!=null ){
         // クライアント実行
         System.out.println("SimpleClient STARTED "+_call+":"+_port+" tout="+_timeout);
         hiStringCOM  _com    = new COM(_call,_port,_timeout);
         hiMongo.DB db      = hiMongo.use("db01",_com);
         db.in("coll_01")
           .find("{}","{_id:0}")
           .sort("{_id:-1}")
           .limit(3)
           .forEachJson(Rj->System.out.println(Rj))
           ;
         System.out.println("SimpleClient FINISHED");
         }
      else{
         // サーバ
         System.err.println("hiMongoSimpleServer waiting at port "+_port);
         try{
            ServerSocket   _con = new ServerSocket(_port);
            while(true){
               hiSocket.TCP.ForText _sock= new hiSocket.TCP.ForText(_con,_timeout);
               new SampleWorker(_sock);
               }
            }
         catch(Exception e){
            e.printStackTrace(System.err);
            System.exit(1);
            }
         System.err.println("hiMongoSimpleServer finished.");
         }
      System.exit(0);
      }
   }
</pre>
 */
public class hiMonWorkerSample {
   public static class COM implements hiStringCOM,AutoCloseable {
      hiSocket.TCP.ForText sock;
      public COM(String host_,int port_,int timeout_){
         sock= new hiSocket.TCP.ForText(host_,port_,timeout_);
         }
      @Override
      public String call(String msg_){
         sock.println(msg_.replaceAll("\n"," "));// 1行にして送る
         return sock.readLine();                 // 1行受け取る
         }
      @Override
      public void close()throws Exception{
         hiU.close(sock);
         }
      }
   public static class SampleWorker extends Thread {
      hiSocket.TCP.ForText sock;
      hiMongoWorker        worker=new hiMongoWorker();
      public SampleWorker(hiSocket.TCP.ForText sock_){
         sock = sock_;
         start();
         }
      public void run(){
         System.err.println("Worker start");
         try{
            String _line;
            while( (_line=sock.readLine())!=null ){
               String _result=worker.call(_line);         // 1行受け取る
               sock.println(_result.replaceAll("\n"," "));// 1行にして送る
               }
            }
         catch(Exception _ex){hiU.close(sock);}
         System.err.println("Worker end");
         }
      }
   static void help(){
      System.out.println("PARAMETER: [-port #(8010)] [-timeout sec. (infinit)]");
      System.out.println("           [-call host] [-port #(8010)] [-timeout sec. (20)]");
      System.exit(0);
      }
   static public void main(String[] args_){
      hiArgs  _args   = new hiArgs(args_);
      if( _args.argBool("-help") ) help();
      String  _call   = _args.argStr("-call",null);
      int     _port   = _args.argInt("-port",8010);
      int     _timeout= _args.argInt("-timeout",0)*1000;
      if( _call!=null ){
         // クライアント実行
         System.out.println("SimpleClient STARTED "+_call+":"+_port+" tout="+_timeout);
         hiStringCOM  _com    = new COM(_call,_port,_timeout);
         hiMongo.DB db      = hiMongo.use("db01",_com);
         db.in("coll_01")
           .find("{}","{_id:0}")
           .sort("{_id:-1}")
           .limit(3)
           .forEachJson(Rj->System.out.println(Rj))
           ;
         System.out.println("SimpleClient FINISHED");
         }
      else{
         // サーバ
         System.err.println("hiMongoSimpleServer waiting at port "+_port);
         try{
            ServerSocket   _con = new ServerSocket(_port);
            while(true){
               hiSocket.TCP.ForText _sock= new hiSocket.TCP.ForText(_con,_timeout);
               new SampleWorker(_sock);
               }
            }
         catch(Exception e){
            e.printStackTrace(System.err);
            System.exit(1);
            }
         System.err.println("hiMongoSimpleServer finished.");
         }
      System.exit(0);
      }
   }
