import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MasterService extends UnicastRemoteObject implements MasterServiceInterface {
    private ArrayList<String> arrayReducer;
    private ArrayList<String> arrayMapper;

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
    public boolean task_combinations(int len) throws RemoteException {
        MapperServiceInterface mapper_rmi = null;
        boolean status = false;
        try{
            String mapper_rmi_address = "rmi://localhost:"+arrayMapper.get(0)+"/mapperservice";
            mapper_rmi = (MapperServiceInterface) Naming.lookup(mapper_rmi_address);
            status = mapper_rmi.process_data(len,arrayReducer);
        }catch(Exception e){e.printStackTrace();}
        return status;
    }

    //interface MasterServiceInterface functions



}
