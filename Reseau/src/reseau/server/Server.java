/*
 * Copyright (c) 2004, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 * Neither the name of Oracle nor the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */
package reseau.server;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import reseau.protocol.ClientItf;
import reseau.protocol.ServerItf;

public class Server implements ServerItf
{
    List<ClientItf> listeClients = new ArrayList<ClientItf>();
    int nombreClients = 0;
    
    List<String> listePseudos = new ArrayList<String>();

    List<String> historiqueLocal = new ArrayList<String>();
    int nombreHistoriqueLocal = 0;

    String nomHistoriquePerm = "logfile.log";

    public Server() //Constructeur
    {
        try
        {
            LocateRegistry.createRegistry(1099);
            Registry registry = LocateRegistry.getRegistry();

            ServerItf addrServer = (ServerItf) UnicastRemoteObject.exportObject(this, 0);
            registry.bind("serv1", addrServer);
            System.out.println("Serveur cree");

            FileWriter log = new FileWriter(nomHistoriquePerm, true);
        }
        catch (Exception e)
        {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public boolean connexion(ClientItf refClient ) throws RemoteException
    {
        boolean pseudoPresent= listePseudos.contains(refClient.getId());
        if(!pseudoPresent)
        {
            listeClients.add(refClient);
            ClientItf clientAct = listeClients.get(nombreClients++);
            System.out.println(clientAct.getId() +" vient de se connecter.");
            if(!clientAct.getHistoriqueCharge())
            {
                clientAct.historiqueLocal(historiqueLocal, nombreHistoriqueLocal);
                //clientAct.historiqueGlobal(nomHistoriquePerm);
            }
            listePseudos.add(refClient.getId());
        }
        return !pseudoPresent;
    }

    public void distribuerMsg(String message, String id) throws RemoteException
    {
        char commande = '/';
        String messageFormate = id + " : " + message;
        if ( !(messageFormate.charAt(4) == commande) )
        {
            for (int index = 0; index < nombreClients; index++)
            {
                ClientItf clientAct = listeClients.get(index);
                clientAct.afficherTexte(messageFormate);
            }
        }

        historiqueLocal.add(messageFormate);
        //historiqueGlobal(messageFormate);
        nombreHistoriqueLocal++;
    }

    public void deconnexion( ClientItf refClient ) throws RemoteException
    {
        int indexClient = listeClients.indexOf(refClient);

        String clientDeco = refClient.getId();
        System.out.println("Client numero " + clientDeco + " deconnecte");
        listePseudos.remove(clientDeco);
        listeClients.remove(indexClient);
        nombreClients--;
    }

    public void historiqueGlobal(String message)
    {
        try
        {
            FileWriter log = new FileWriter(nomHistoriquePerm, true);
            log.write(message);
            log.write("\r\n");
            log.close();
        }

        catch (Exception e)
        {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
