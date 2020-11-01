package hi.db;
/**
 * JSON文字列（拡張JSON文字列）による通信機.
 *<p>
 *このインターフェースでは文字列でやり取りを行う以外のプロトコルは指定されません。
 *</p>
 *<p>
 *実装に当たっては単に文字列単位をそのまま流すことになります。
 *</p>
 *<p>
 *例えば、試験用には途中で電文を表示する中継器を次のように作成することが出来ます。
 *</p>
<pre class=quote10>
public class Repeater implements hiStringCOM{
   hiStringCOM to;
   public Repeater(hiStringCOM to_){
      to= to_;
      }
   @Override
   public String call(String msg_){
      System.err.println("call    :"+msg_);
      String _resp= to.call(msg_);
      System.err.println("response:"+_resp);
      return _resp;
      }
   }
</pre>
 *<p>
 *このインターフェースではセキュリティーは考慮されていません。<br>
 *実装部に任されます。
 *</p>
 *<p>
 *通信異常などもこのinterfaceとしては考慮しません。<br>
 *実装に当たっては、上位のプロトコル{@link hi.db.hiMongoCOM.Message}などを考慮し、異常時にメッセージを作成し戻り値とすべきです。
 *</p>
 */
public interface hiStringCOM {
   public String call(String msg_);
   }
