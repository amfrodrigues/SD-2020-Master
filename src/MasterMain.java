

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class MasterMain {

    public static void main(String[] args){

        ArrayList<String> arrayMapper = new ArrayList<>();
        String portMapper1 = "8022";
        MapperMain.main(new String[]{portMapper1});
        arrayMapper.add(portMapper1);

        ArrayList<String> arrayReducer = new ArrayList<>();
        for (int portReducer = 9000 ; portReducer < 9010;portReducer++ ) {
            String portReducer_string = String.valueOf(portReducer);
            ReducerMain.main(new String[]{portReducer_string});
            arrayReducer.add(portReducer_string);
        }
        System.out.println("MASTER DEBUG : Nª Reducers = " + arrayReducer.size());
        Registry r = null;
        MasterServiceInterface masterService;
        Integer port = Integer.parseInt(args[0]);
        try{
            r = LocateRegistry.createRegistry(port);
        }catch(RemoteException a){
            a.printStackTrace();
        }
        try{
            masterService = new MasterService(arrayReducer,arrayMapper);
            r.rebind("masterservice", masterService);
            masterService.task_combinations(2);
            System.out.println("Master ready port:" +port);
        }catch(Exception e) {
            System.out.println("Master main " + e.getMessage());
        }

       StorageServiceInterface storage_rmi = null;
        try{
            String storage_rmi_address = "rmi://localhost:2022/storageservice";
            storage_rmi = (StorageServiceInterface) Naming.lookup(storage_rmi_address);
            System.out.println("Master: combstatisticSize = "+storage_rmi.getcombinationsStatisticsize());
            storage_rmi.clearCombStatistics();
        }catch(Exception e){ e.printStackTrace();}
        MapperServiceInterface mapper_rmi = null;
        try{

        }catch(Exception e){e.printStackTrace();}
    }
}
