import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface MasterServiceInterface extends Remote {
    boolean task_combinations(int len) throws RemoteException;

}
