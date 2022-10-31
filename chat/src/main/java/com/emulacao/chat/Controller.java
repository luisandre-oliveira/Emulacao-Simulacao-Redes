package com.emulacao.chat;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import com.fazecast.jSerialComm.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.charset.Charset.defaultCharset;
import static java.nio.charset.Charset.isSupported;

public class Controller {

    SerialPort porta;
    String dataBuffer = "";
    final String newLine = "\n";
    String result;
    ArrayList<Byte> textoConvertido = new ArrayList<>();


    @FXML
    public TextArea chat;
    public TextArea submit;

    @FXML
    public void enviar(){
        Serial_EventBasedReading(porta);
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
        enviarTXT();
        enviarIMG();
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

    public void enviarTXT() throws IOException {
        String filePath = "N:\\IntelliJ\\chat\\src\\main\\resources\\com\\emulacao\\chat\\Ola.txt"; //13 bytes
        try{
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        porta.writeBytes(bytes, bytes.length);
        }
        catch (IOException e){throw new IOException("INVALID FILE");}
    }

    public void enviarIMG() throws IOException {
        BufferedImage imagem = ImageIO.read(new File("N:\\IntelliJ\\chat\\src\\main\\resources\\com\\emulacao\\chat\\vizel.png"));
        ByteArrayOutputStream imagemArray = new ByteArrayOutputStream();
        ImageIO.write(imagem, "jpg", imagemArray);
        byte[] bytes = imagemArray.toByteArray();
        porta.writeBytes(bytes, bytes.length);
    }

    public void close(){System.exit(0);}
}

