import java.rmi.Remote;
import java.rmi.RemoteException;


public interface MasterServiceInterface extends Remote {
    boolean task_combinations(int len) throws RemoteException;

}
