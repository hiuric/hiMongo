// package hi
import java.util.*;
public class hiMongoCOM {
   final static String GET_RECORD="get_record";
   final static String SHOW_DBS  ="show_dbs";
   final static String COUNT     ="count";
   static class Command {
      Object            connect;
      Object            show_dbs;
      Long              str_option;
      String            use;
      String            get;
      ArrayList<Object> execute;
      }
   static class Result {
      ArrayList<Object> get_record;
      ArrayList<String> show_dbs;
      Integer           count;
      }
   }
