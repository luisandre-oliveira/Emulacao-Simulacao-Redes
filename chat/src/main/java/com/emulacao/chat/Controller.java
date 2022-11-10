package com.emulacao.chat;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import com.fazecast.jSerialComm.*;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;


public class Controller {
                                                                TO DO LIST
    SerialPort porta;                                           -Refresh button
    String dataBuffer = "";                                     -Acabar a config das comboboxes (eventos)
    String dataBuffer2 = "";
    final String newLine = "\n";
    String result;
    ArrayList<Byte> textoConvertido = new ArrayList<>();

    @FXML
    public TextArea chat;
    public TextArea submit;
    public ComboBox Xporta;
    public ComboBox Xbaud;
    public ComboBox Xstop;
    public ComboBox Xdata;
    public ComboBox Xparity;

    @FXML
    public void initialize() {
        //baud rate
        Xbaud.getItems().removeAll(Xbaud.getItems());
        Xbaud.getItems().addAll(2400,9600,28800,56000,115200,256000);
        Xbaud.getSelectionModel().select("9600");
        //porta
        listPorts();
        //data bits
        Xdata.getItems().removeAll(Xdata.getItems());
        Xdata.getItems().addAll(2400,9600,28800,56000,115200,256000);
        Xdata.getSelectionModel().select(9600);
        //stop bits
        Xstop.getItems().removeAll(Xstop.getItems());
        Xstop.getItems().addAll(2400,9600,28800,56000,115200,256000);
        Xstop.getSelectionModel().select(9600);
        //parity
        Xparity.getItems().removeAll(Xparity.getItems());
        Xparity.getItems().addAll(2400,9600,28800,56000,115200,256000);
        Xparity.getSelectionModel().select(9600);


    }

    @FXML
    public void enviar(){
        Serial_EventBasedReading(porta);
        FileDialog fd = new FileDialog(new Frame());
        fd.setVisible(true);
        File[] f = fd.getFiles();
        if(f.length > 0){
            System.out.println(fd.getFiles()[0].getAbsolutePath());
        }
        //Caso nenhum ficheiro seja escolhido
        try{enviarTXT(fd.getFiles()[0].getAbsolutePath());}catch (Exception e){chat.appendText("\nErro ao escolher o ficheiro");}

        if (submit.getText() != null) {
            Serial_EventBasedReading(porta);
            chat.appendText("PC1: " + submit.getText());
            chat.appendText("\n");
            System.out.println(submit.getText());
            for(int i = 0; i < submit.getText().length(); i++)
            {
                textoConvertido.add((byte)(int)(submit.getText().charAt(i)));
            }
            byte[] textConverts = new byte[textoConvertido.size()];
            int u = 0;
            for(Byte b : textoConvertido)
            {
                textConverts[u] = b;
                u++;
            }
            porta.writeBytes(textConverts, textoConvertido.size());
            porta.writeBytes(newLine.getBytes(),newLine.length());
            submit.setText(null);
            textoConvertido.clear();
        }
    }

    public void anexar() throws IOException {
        if(this.porta != null){porta.closePort();}
        SerialPort[] ports = SerialPort.getCommPorts();
        System.out.println("Select a port:");
        int i = 1;
        for (SerialPort port : ports)
            System.out.println(i++ + ": " + port.getSystemPortName());
        Scanner s = new Scanner(System.in);
        int chosenPort = s.nextInt();
        porta = ports[chosenPort - 1];
        if (porta.openPort()) {
            System.out.println("Port opened successfully.");
        } else {
            System.out.println("Unable to open the port.");
        }
        porta.setComPortParameters(9600, 8, 1, porta.NO_PARITY);
        porta.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
        Serial_EventBasedReading(porta);
        /********************/
        /*enviarTXT();
        enviarIMG();*/
        /********************/
    }

    public void Serial_EventBasedReading(SerialPort porta) {
        porta.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return porta.LISTENING_EVENT_DATA_AVAILABLE | porta.LISTENING_EVENT_DATA_RECEIVED;
            }
            @Override
            public void serialEvent(SerialPortEvent event) {
                byte[] newData = event.getReceivedData();
                for (int i = 0; i < newData.length; i++) {
                    dataBuffer += (char)(int)(newData[i] & 0xFF);
                }
                try {
                    result = new String(newData, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                //result = dataBuffer.toString();
                System.out.println(result);
                //chat.appendText("PC2: " + result);
                chat.appendText("PC2: " + dataBuffer);
                chat.appendText(newLine);
                porta.flushIOBuffers();
                result = "";
                dataBuffer = "";
            }
        });
    }


    public int enviarTXT(String filePath) {
        if(filePath == "" || filePath == null){return 1;}
        File txt = new File(filePath);
        if (txt == null) {
            System.out.println(70);
        }
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while (line != null) {
                for (int y = 0; y < line.length(); y++) {
                    byte[] bytes = new byte[1];
                    bytes[0] = (byte) (int) (line.charAt(y));
                    porta.writeBytes(bytes, bytes.length);
                    //LIMPAR ARRAY
                }
                porta.writeBytes(newLine.getBytes(),newLine.length());
                System.out.println(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /*public void enviarTXT() throws IOException {
        String filePath = "N:/IntelliJ/chat/src/main/resources/com/emulacao/chat/Ola.txt"; //13 bytes
        try{
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            porta.writeBytes(bytes, bytes.length);
            System.out.println(bytes.length);
        }
        catch (IOException e){throw new IOException("INVALID FILE");}*/


    public void enviarIMG() throws IOException {
        BufferedImage imagem = ImageIO.read(new File("N:\\IntelliJ\\chat\\src\\main\\resources\\com\\emulacao\\chat\\vizel.png"));
        ByteArrayOutputStream imagemArray = new ByteArrayOutputStream();
        ImageIO.write(imagem, "jpg", imagemArray);
        byte[] bytes = imagemArray.toByteArray();
        porta.writeBytes(bytes, bytes.length);
    }

    public void listPorts()
    {
        Xporta.getItems().removeAll(Xporta.getItems());
        SerialPort[] ports = SerialPort.getCommPorts();
        for(SerialPort port : ports)
        {
            Xporta.getItems().add(port.getSystemPortName());
        }

        Xporta.getSelectionModel().select("Escolher");
    }
    public void close(){System.exit(0);}
}

