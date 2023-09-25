package mvc_02_dist_rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteModelObserver extends Remote {

	void notifyModelUpdated(int state) throws RemoteException;
}
