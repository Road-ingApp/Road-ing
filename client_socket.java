package com.example.roading;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class client_socket {
    // 傳送SQL關鍵字(資料表,SELECT ?,WHERE ? = ?)
    public ArrayList<ArrayList<String>> sql(ArrayList<String> user_input){
        return connect_server(user_input);
    }

    private ArrayList<ArrayList<String>> connect_server(ArrayList<String> user_inputs){
        String serverAddress = "140.127.220.77"; // 伺服器位址
        int serverPort = 8080; // 伺服器port
        ArrayList<ArrayList<String>> response = new ArrayList<>();

        try {
            Socket socket = new Socket(serverAddress, serverPort);

            // 傳送訊息給伺服器
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            String message = "jifAJFEI983Ifje";
            ArrayList<String> send_message = new ArrayList<>();
            send_message.add(message);
            for(String user_input : user_inputs){
                send_message.add(user_input);
            }
            objectOutputStream.writeObject(send_message);
            objectOutputStream.flush();

            // 接收伺服器回應
            InputStream inputStream = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            // 解析伺服器回應
            response = (ArrayList<ArrayList<String>>)objectInputStream.readObject();

            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return response;
    }
}