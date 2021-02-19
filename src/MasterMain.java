

import com.google.common.base.CharMatcher;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class MasterMain {
   private static MasterServiceInterface finalMasterService = null;
   private static ArrayList<String> arrayMapper;
   private static ArrayList<String> arrayReducer  = new ArrayList<>();
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
           try{
                arrayMapper = new ArrayList<>();
               String portMapper1 = "rmi://localhost:8022/mapperservice";
           //    MapperMain.main(new String[]{"8022"});
               arrayMapper.add(portMapper1);

               arrayReducer.add("rmi://localhost:9000/reducerservice"); // fail test reducer (manual launched)
               for (int portReducer = 9001 ; portReducer < 9009;portReducer++ ) {
                   String portReducer_string = "rmi://localhost:"+portReducer+"/reducerservice";
                   ReducerMain.main(new String[]{String.valueOf(portReducer)});
                   arrayReducer.add(portReducer_string);
               }
           }catch(Exception e){e.printStackTrace();}
       }).start();
        Thread.sleep(1000); // garante que todos os serviços estão disponíveis antes de executar o código do cliente

        System.out.println("MASTER DEBUG : Nª Reducers = " + arrayReducer.size());
        Registry r = null;

        Integer port = Integer.parseInt(args[0]);
        try{
            r = LocateRegistry.createRegistry(port);
        }catch(Exception a){
            a.printStackTrace();
        }
        try{
            finalMasterService = new MasterService(arrayReducer,arrayMapper);
            r.rebind("masterservice", finalMasterService);
            System.out.println("Master ready port:" +port);

            Thread.sleep(4000); // garante que todos os serviços estão disponíveis antes de executar o código do cliente
        }catch(Exception e) {
            System.out.println("Master main " + e.getMessage());
        }

      /* StorageServiceInterface storage_rmi = null;
        try{
            Thread.sleep(1000);
            String storage_rmi_address = "rmi://localhost:2022/storageservice";
            storage_rmi = (StorageServiceInterface) Naming.lookup(storage_rmi_address);
            System.out.println("Master: combstatisticSize = "+storage_rmi.getcombinationsStatisticsize());
            storage_rmi.clearCombStatistics();
        }catch(Exception e){ e.printStackTrace();} */
        MapperServiceInterface mapper_rmi = null;


        Thread threadHearthbeat = new Thread(()->{
            while(true){
                try {
                    Thread.sleep(13*1000);
                    ArrayList<String> revive_mapper = finalMasterService.heartbeat_revive("mapper");
                    System.out.println("Master: mappers to revive ="+revive_mapper.size());
                    for(String mapperToRevive : revive_mapper){
                        String mapperID = CharMatcher.inRange('0','9').retainFrom(mapperToRevive);
                        System.out.println("Master: Revived Mapper["+mapperID+"]");
                        MapperMain.main(new String[]{mapperID});
                    }
                    Thread.sleep(2*1000);
                    ArrayList<String> revive_reducer = finalMasterService.heartbeat_revive("reducer");
                    System.out.println("Master: Reducers to revive ="+revive_reducer.size());
                    for (String reducerToRevive : revive_reducer){
                        String reducerID = CharMatcher.inRange('0','9').retainFrom(reducerToRevive);
                        System.out.println("Master: Revived Reducer["+reducerID+"]");
                        ReducerMain.main(new String[]{reducerID});
                        finalMasterService.reducerTaskRevive(reducerToRevive);
                    }
                } catch (Exception e) {e.printStackTrace();}
            }
        });
        threadHearthbeat.start();
    }
}
