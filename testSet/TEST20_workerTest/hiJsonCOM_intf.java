//package hi;
public interface hiJsonCOM_intf {
   public static class Message {
      String format;
      String content_type;
      String status;
      Object content;
      }
   public String call(String msg_);
   }
