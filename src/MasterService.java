import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;

public class MasterService extends UnicastRemoteObject implements MasterServiceInterface {
    private final String storage_rmi_address = "rmi://localhost:2022/storageservice";
    private ArrayList<String> arrayReducer;
    private ArrayList<String> arrayMapper;

    private ArrayList<String> arrayAliveMapper = new ArrayList<>();
    private ArrayList<String> arrayAliveReducer = new ArrayList<>();


    public MasterService(ArrayList<String> arrayReducer,ArrayList<String>arrayMapper) throws RemoteException {

       setArrayReducer(arrayReducer);
       setArrayMapper(arrayMapper);
    }

    private void setArrayReducer(ArrayList<String> arrayReducer) {
        this.arrayReducer = arrayReducer;
    }

    private void setArrayMapper(ArrayList<String> arrayMapper) {
        this.arrayMapper = arrayMapper;
    }



    @Override
    public LinkedList<ProcessCombinationModel> task_combinations(int len) throws RemoteException {
        StorageServiceInterface storageService = null;
        try{
            storageService = (StorageServiceInterface) Naming.lookup(storage_rmi_address);
            storageService.clearCombStatistics();
        }catch(Exception e){e.printStackTrace();}
        Thread t = new Thread(()->{
            MapperServiceInterface mapper_rmi ;
            try{
                String mapper_rmi_address = arrayMapper.get(0);
                mapper_rmi = (MapperServiceInterface) Naming.lookup(mapper_rmi_address);
                mapper_rmi.process_data(len,arrayReducer);
            }catch(Exception e){e.printStackTrace();}
        });
            t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return storageService.getcombinationsStatistic();
    }

    /*
    Method used for the other services to say their alive
        filtered by type of service
     */
    @Override
    public void heartbeat_check(String id,String type) throws RemoteException {
        if(type.equals("mapper")) this.arrayAliveMapper.add(id);
        else if(type.equals("reducer")) this.arrayAliveReducer.add(id);
    }

    /*
    Method that returns the list of services who need to be revived
        filtered by type of service
     */
    @Override
    public ArrayList<String> heartbeat_revive(String type) throws RemoteException {
        ArrayList<String> arrayToRevive = null;
        if(type.equals("mapper")) {
            arrayToRevive = new ArrayList<String>(this.arrayMapper);
            for (String s : arrayAliveMapper){
                arrayToRevive.remove(s);
            }
            this.arrayAliveMapper.clear();
        }

        else if(type.equals("reducer")){
            arrayToRevive = new ArrayList<String>(this.arrayReducer);
            for (String s : arrayAliveReducer){
                arrayToRevive.remove(s);
            }
            this.arrayAliveReducer.clear();
        }
        return arrayToRevive;
    }

    /*
        Method that send Mapper id of reducer to redo_task
     */
    @Override
    public void reducerTaskRevive(String reducerAddress) throws RemoteException {
        MapperServiceInterface mapper_rmi = null;
        String mapper_rmi_address = arrayMapper.get(0);
         try{
             mapper_rmi = (MapperServiceInterface) Naming.lookup(mapper_rmi_address);
             mapper_rmi.redo_taskReducer(reducerAddress);
         }catch(Exception e){e.printStackTrace();}
    }


}
