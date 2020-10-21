package hi.db;
import java.util.*;
/**
 * Caller-Worker間プロトコル.
 *<p>
 *詳細説明公開準備中
 *</p>
 */
public class hiMongoCOM {
   final static String EXCECUTE         = "excecute";
   final static String GET_RECORD       = "get_record";
   final static String SHOW_DBS         = "show_dbs";
   final static String SHOW_COLLECTIONS = "show_collections";
   final static String COUNT            = "count";
   final static String FIND             = "find";
   final static String SORT             = "sort";// F
   final static String LIMIT            = "limit";// F
   final static String COMMAND          = "command";
   final static String SKIP             = "skip";// F
   final static String DROP             = "drop";
   final static String INSERT           = "insert";
   final static String DELETE           = "delete";
   final static String AGGREGATE        = "aggregate";
   final static String GET_INDEX_LIST   = "getIndexList";
   final static String CREATE_INDEX     = "createIndex";
   final static String UPDATE_ONE       = "uptateOne";
   final static String UPDATE_MANY      = "uptateMany";
   final static String REPLACE_ONE      = "replaceOne";
   final static String DELETE_ONE       = "deleteOne";
   final static String DELETE_MANY      = "deleteMany";
   final static String CREATE_CAPPED    = "createCapped";
   final static String EXISTS           = "exists";
   final static String DROP_INDEX       = "dropIndex";
   final static String DROP_INDEXES     = "dropIndexes";
   /* 以下は通信には載らない
   final static String MATCH            = "match";
   final static String GROUP            = "group";
   final static String LOOKUP           = "lookup";
   final static String PROJECT          = "project";
   final static String UNWIND           = "unwind";
   */
   public static class Message {
      String format;
      String content_type;
      String status;
      Object content;
      }
   static class Command {
      Object            connect;
      Object            show_dbs;
      Long              str_option;
      String            use;
      String            in;
      ArrayList<Object> execute;
      }
   static class Result {
      ArrayList<Object> get_record;
      ArrayList<String> show_dbs;
      Integer           count;
      }
   }
