package com.master;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MasterMain {
    public static void main(String[] args){
        Registry r = null;
        MasterServiceInterface masterService;
        Integer port = Integer.parseInt(args[0]);
        try{
            r = LocateRegistry.createRegistry(port);
        }catch(RemoteException a){
            a.printStackTrace();
        }

        try{
            masterService = new MasterService();
            r.rebind("masterservice", masterService);

            System.out.println("Master ready");
        }catch(Exception e) {
            System.out.println("Master main " + e.getMessage());
        }
    }
}
