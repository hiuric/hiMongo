import hi.db.*;
import otsu.hiNote.*;
import java.io.*;
import org.bson.Document;
public class Test {
   static void set_DB(hiMongo.DB db){
      hiU.out.println("---- famiryTree");
      db.in("famiryTree")
        .drop()
        .insertOne("{name:'P0001',status:'-'    ,father:'-',mother:'-'}"
                  ,"{name:'P0002',status:'-'    ,father:'-',mother:'-'}"
                  ,"{name:'P0003',status:'KING' ,father:'-',mother:'-'}"
                  ,"{name:'P0004',status:'QUEEN',father:'-',mother:'-'}"
                  ,"{name:'P0005',status:'-'    ,father:'-',mother:'-'}"
                  ,"{name:'P0006',status:'-'    ,father:'-',mother:'-'}"
                  ,"{name:'P0007',status:'-'    ,father:'-',mother:'-'}"
                  ,"{name:'P0008',status:'-'    ,father:'-',mother:'-'}"
                  //
                  ,"{name:'P0009',status:'-'    ,father:'P0001',mother:'P0002'}"
                  ,"{name:'P0010',status:'-'    ,father:'P0003',mother:'P0004'}" // M
                  ,"{name:'P0011',status:'-'    ,father:'P0003',mother:'P0008'}" // M
                  ,"{name:'P0012',status:'-'    ,father:'P0005',mother:'P0006'}"
                  ,"{name:'P0013',status:'-'    ,father:'P0005',mother:'P0008'}"
                  ,"{name:'P0014',status:'-'    ,father:'P0007',mother:'P0008'}"
                  ,"{name:'P0016',status:'-'    ,father:'P0007',mother:'P0008'}"
                  //
                  ,"{name:'P0017',status:'-'    ,father:'P0009',mother:'P0010'}" // F
                  ,"{name:'P0018',status:'-'    ,father:'P0011',mother:'P0012'}" // M
                  ,"{name:'P0020',status:'-'    ,father:'P0013',mother:'P0014'}"
                  ,"{name:'P0021',status:'-'    ,father:'P0013',mother:'P0014'}"
                  ,"{name:'P0022',status:'-'    ,father:'P0009',mother:'P0016'}"
                  ,"{name:'P0024',status:'-'    ,father:'P0009',mother:'P0016'}"
                  ,"{name:'P0025',status:'-'    ,father:'P0009',mother:'P0016'}"
                   //
                  ,"{name:'P0027',status:'-'    ,father:'P0017',mother:'P0020'}" // F
                  ,"{name:'P0028',status:'-'    ,father:'P0021',mother:'P0018'}"
                  ,"{name:'P0029',status:'-'    ,father:'P0025',mother:'P0024'}"
                  ,"{name:'P0030',status:'-'    ,father:'P0021',mother:'P0022'}"
                  ,"{name:'P0031',status:'-'    ,father:'P0021',mother:'P0022'}"
                  ,"{name:'P0032',status:'-'    ,father:'P0011',mother:'P0020'}" // M
                  ,"{name:'P0033',status:'-'    ,father:'P0025',mother:'P0024'}"
                  )
        .find("{}","{_id:0}")
        .forEachMson(Rm->hiU.out.println(Rm))
        ;
      hiU.out.println("---- friendLoop");
      db.in("friendLoop")
        .drop()
        .insertOne("{name:'P0028',friends:[P0027,P0030,P0032,P0033]}"
                  ,"{name:'P0029',friends:[P0028,P0031,P0033]}"
                  )
        .find("{}","{_id:0}")
        .forEachMson(Rm->hiU.out.println(Rm))
        ;
      }
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
      //String _target="P0027";
      hiMongo.DB db= mongo.use("db01");
      set_DB(db);
      hiU.out.println("-------- check for specified person");
      db.in("famiryTree")
        .setValue("#target","P0027")
        .find("{name:'P0027'}")
        .forEachRecursive(
            "{#CUR.father:name,#CUR.mother:name}"
           ,"{#status:status,#name:name}"
           ,Fi->{
               if( "KING".equals(Fi.get("#status")) ) {
                  hiU.out.println(Fi.disp("#target is a descendant of KING #name"));
                  return "FOUND";
                  }
               return null;// さらに検索
               }
            )
         ;
      hiU.out.println("-------- check for friendLoop");
      db.in("friendLoop")
        .find()
        .forEach(
            "{#friends:friends}"
           ,Fi->{
               Fi.in("famiryTree")
                 .find("{name:{$in:#friends}}")
                 .forEachRecursive(
                     "{#CUR.father:name,#CUR.mother:name}" 
                    ,Fr->{ // 主処理（再帰呼び出しされる)
                        if( "KING".equals(Fr.get("#CUR.status")) ) {
                           return Fi.disp(" is a descendant of KING #CUR.name");
                           }
                        return null;
                        }
                    ,Ff->{ // #TOPに入り口のレコード、#RESULTに主処理の戻り値
                        System.out.println(Ff.disp("#TOP.name")+Ff.get("#RESULT")+".");
                        }
                    ,Fn->{ // 主処理がnullのまま終了
                        System.out.println(Fn.disp("#TOP.name is not."));
                        }
                     )
                  ;
               }
            )
         ;
      //throw new hiException("IT'S OK");
      }
   }

