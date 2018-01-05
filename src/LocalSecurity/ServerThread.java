/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LocalSecurity;

import java.net.Socket;

/**
 *
 * @author stula
 */
public class ServerThread extends Thread {
    private DataExchange dataExchange;

    public ServerThread(Socket socket) {
        this.dataExchange = new DataExchange(socket);
        System.out.println("socket connected");
    }

    public void run() {

        int command = this.dataExchange.readInt();
        System.out.println("Reading: " + command);

        command = 2;
        System.out.println("Writing: " + command);
        this.dataExchange.writeInt(command);
    }

}
