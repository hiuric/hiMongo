import hi.db.hiMongo;
public class Test {
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      db.in("coll_01")
        .find("{type:'B'}")
        .forOne("{#B_id:_id}",Fi->
            Fi.find("{_id:{$gte:#B_id}}","{_id:0}")
              .forEachMson(Rm->System.out.println(Rm)));
      }
   }
