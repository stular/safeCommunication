package Infrastructure;


import InternetSecurity.CAServer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author stula
 */
public class ServerCertificateAuthority {
    public static void main(String[] args) {
        CAServer caServer = new CAServer(10040,"CA1");
    }
}
