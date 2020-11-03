import hi.db.hiMongo;
public class Test { 
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      db.in("famiryTree")
        .find("{name:{$in:['P0027','P0028','P0029','P0030','P0031']}}")
        .forEachRecursive(
            "{#FILTER:{name:{$in:[#CUR.father,#CUR.mother]}},#FIELD:{_id:0}}"
           ,Fr->{ // 主処理（再帰呼び出しされる)
               if( "KING".equals(Fr.get("#CUR.status")) ) {
                  return Fr.disp(" is a descendant of KING #CUR.name");
                  }
               return null;
               }
           ,Ff->{ // 完了 #RESULT に主処理の戻り値
               System.out.println(Ff.disp("#TOP.name")+Ff.disp("#RESULT")+".");
               }
           ,Fn->{ // 完了
               System.out.println(Fn.disp("#TOP.name is not."));
               }
            )
         ;
      }
   }
