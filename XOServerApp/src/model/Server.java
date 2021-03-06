/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thoraya Hamdy
 */
public class Server {
    private static Server server;
    public Database databaseInstance ;
    private ServerSocket serverSocket ;
    private Socket clientSocket ;
    private Thread listenerThread;
    private ArrayList<Socket> allSocketsOfClients = new ArrayList();
    
    private Server(){
        
    }
    
    public static Server getServer(){
        if(server == null){
            server = new Server();
        }
        return server;
    }
    
    public void enableConnections() throws SQLException{
        databaseInstance = Database.getConnection();
        databaseInstance.resetStatus();
        //databaseInstance.disableConnection();
        databaseInstance.selectResultSet();
        initServer();
    }
    private void initServer(){
        try {
            serverSocket = new ServerSocket(9081);
            System.out.println(Inet4Address.getLocalHost().getHostAddress());
            listenerThread = new Thread(() -> {
                while(true){
                    try {
                        clientSocket = serverSocket.accept();
                        new ConnectedPlayer(clientSocket);
                        allSocketsOfClients.add(clientSocket);
                        System.out.println("socket is created");                        
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            listenerThread.start();
        }catch (IOException ex) {
            System.out.println("server exception");
            ex.printStackTrace();
        }
    }
    
    public void notifyAllClients() throws IOException{
        
        for(int i = 0; i< allSocketsOfClients.size(); ++i){
            allSocketsOfClients.get(i).close();
        }
    }
    public void disableConnections(){
        try {
            databaseInstance.disableConnection();
            listenerThread.stop();
            serverSocket.close();
        } catch (SQLException ex) {
            //alert connection issue
            System.out.print("Error while closing database connection in disableConnection");
        } catch (IOException ex) {
            System.out.print("error while closing server cocket in disableConnections");
        }
    }
    
    public void setActive(Boolean state, String mail){
        databaseInstance.changeActivation(state,mail);
    }
    public void setPlaying(boolean state,String email){
        databaseInstance.changePlaying(state,email);
    }

    public String checkSignIn(String email,String password){ //we can change it to return string
        //for validation messages to appear to the user
        return databaseInstance.validateLogin(email, password);  
    }
    public int getScore(String email){
        return databaseInstance.getScore(email);
    }
    public String getUserName(String email){
        return databaseInstance.getUsername(email);
    }
    public void login(String email,String password) throws SQLException{
        databaseInstance.login(email, password);
    }
    public String checkRegister(String username,String email){ //we can change it to return string
        //for validation messages to appear to the user
        return databaseInstance.validateRegister(username, email);
    }
    public void SignUp(String username,String email,String password) throws SQLException{
        Player p = new Player(username,email,password,false,false,0);
        databaseInstance.register(p);
    }
    public ResultSet getActivePlayers(){
        return databaseInstance.getActivePlayers();
    }
    public void updateScore(String email,int score){
        databaseInstance.updateScore(email, score);
    }
    public void makePlaying(String player1,String player2){
        databaseInstance.makePlaying(player1, player2);
    }
    public ResultSet getResultSet(){
        return databaseInstance.getResultSet();
    }
    
}