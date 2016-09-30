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
package reseau.client;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import reseau.MainClientFX;
import reseau.protocol.ClientItf;
import reseau.protocol.ServerItf;


public class Client implements ClientItf dd
{
    ServerItf serv;
    ClientItf addrClient;

    String deco = "/STOP";

    String identifiant;
    MainClientFX fenetre;
    
    boolean connecte;
    boolean historiqueCharge;

    public Client(MainClientFX fenetre) // Constructeur de Client
    {
        try {
            connecte=false;
            historiqueCharge=false;
            this.fenetre=fenetre;
            Registry registry = LocateRegistry.getRegistry("127.0.0.1");
            serv = (ServerItf) registry.lookup("serv1");
            addrClient = (ClientItf) UnicastRemoteObject.exportObject(this, 0);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void afficherTexte(String texte) throws RemoteException
    {
        fenetre.ajouterElement(texte);
    }

    public void envoyerTexte(String texte)
    {
        try
        {
            if (!texte.equals(""))
            {
                serv.distribuerMsg(texte, identifiant);
            }
        }
        catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void connexion()
    {

        System.out.println("Tentative de connexion");

        try {
            connecte=serv.connexion(addrClient);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void identifiant(String id)
    {
        identifiant = id;
        System.out.println("Connecte en tant que : " + id);
    }

    public String getId() throws RemoteException
    {
        return identifiant;
    }
    
     public boolean getConnecte()
    {
        return connecte;
    }
     
    public boolean getHistoriqueCharge() throws RemoteException
    {
        return historiqueCharge;
    }


    public void deconnexion() {
        System.out.println("Tentative de deco");

        try {
            serv.deconnexion(addrClient);
            connecte=false;
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
        System.out.println("Deconnecte");
    }

    public void historiqueLocal(List<String> historiqueLocal, int nombreHistoriqueLocal) throws RemoteException
    {
        for (int index=0; index<nombreHistoriqueLocal; index++)
        {
            fenetre.ajouterElement(historiqueLocal.get(index));
        }
        historiqueCharge=true;
    }

    public void historiqueGlobal(String nomFichier) throws RemoteException
    {
        try
        {
            FileInputStream fichier = new FileInputStream(new File(nomFichier));
            InputStreamReader read = new InputStreamReader(fichier);
            BufferedReader buffer = new BufferedReader(read);
            String ligne;
            while( (ligne = buffer.readLine()) != null)
            {
                fenetre.ajouterElement(ligne);
            }
            buffer.close();
            historiqueCharge=true;
        }
        catch (Exception e)
        {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

