package com.master;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MasterService extends UnicastRemoteObject implements MasterServiceInterface {

    public MasterService() throws RemoteException {
    }

}
