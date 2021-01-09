import com.mapper.MapperMain;
import com.reducer.ReducerMain;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class MasterMain {

    public static void main(String[] args){

        ArrayList<String> arrayMapper = new ArrayList<>();
        String portMapper1 = "3022";
        MapperMain.main(new String[]{portMapper1});
        arrayMapper.add(portMapper1);

        ArrayList<String> arrayReducer = new ArrayList<>();
        for (int portReducer = 4000 ; portReducer < 4010;portReducer++ ) {
            String portReducer_string = String.valueOf(portReducer);
            ReducerMain.main(new String[]{portReducer_string});
            arrayMapper.add(portReducer_string);
        }

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

            System.out.println("Master ready");
        }catch(Exception e) {
            System.out.println("Master main " + e.getMessage());
        }

       StorageServiceInterface storage_rmi = null;
        try{
            String storage_rmi_address = "rmi://localhost:2022/storageservice";
        }catch(Exception e){ e.printStackTrace();}
        MapperServiceInterface mapper_rmi = null;
        try{

        }catch(Exception e){e.printStackTrace();}
    }
}
