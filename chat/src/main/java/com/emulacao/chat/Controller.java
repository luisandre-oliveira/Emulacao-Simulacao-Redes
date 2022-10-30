package com.emulacao.chat;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import com.fazecast.jSerialComm.*;

import java.awt.event.KeyEvent;
import java.io.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static java.nio.charset.Charset.defaultCharset;
import static java.nio.charset.Charset.isSupported;

public class Controller {

    File txt = new File("N:\\IntelliJ\\chat\\src\\main\\resources\\com\\emulacao\\chat\\Ola.txt");//13 bytes
    FileInputStream binario;
    SerialPort porta;
    OutputStream outputStream1;
    InputStream inputStream1;
    String dataBuffer = "";
    final String newLine = "\n\r";
    byte[] textoConvertido;
    String result;
    String lo;


    @FXML
    public TextArea chat;
    public TextArea submit;

    @FXML
    public void enviar() throws IOException {
        Serial_EventBasedReading(porta);
        if (submit.getText() != null) {
            Serial_EventBasedReading(porta);
            chat.appendText("PC1: " + submit.getText());
            chat.appendText("\n");
            try {
                System.out.println(submit.getText());
                porta.writeBytes(convertutf(submit.getText()), submit.getText().length());
                submit.setText(null);
                porta.writeBytes(convertutf(newLine),newLine.length());
                System.out.println("result: " + new String(convertutf(submit.getText()), "UTF-8"));
            } catch (IOException e) {
                chat.appendText("ERRO\n");
            }
        }
    }

    /*public void enviar() throws IOException {
        Serial_EventBasedReading(porta);
        if (submit.getText() != null) {
            String dataToSend = "";
            Serial_EventBasedReading(porta);
            dataToSend = submit.getText();
            chat.appendText("PC1: " + submit.getText());
            chat.appendText("\n");
            submit.setText(null);
            try {
                System.out.println(dataToSend);
                //porta.writeBytes();
                //outputStream1.write(dataToSend.getBytes());
                porta.writeBytes(dataToSend.getBytes(), dataToSend.length());
                outputStream1.write(newLine.getBytes());
            } catch (IOException e) {
                chat.appendText("ERRO\n");
            }
        }
    }*/

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
                    dataBuffer += (char)(int)newData[i];
                }
                result = dataBuffer.toString();
                chat.appendText("PC2: " + result);
                chat.appendText(newLine);
                porta.flushIOBuffers();
                result = "";
                dataBuffer = "";
            }
        });
    }

    public byte[] converter(File file) throws IOException {
        this.binario = new FileInputStream(file);
        byte[] array = new byte[(int)file.length()];
        binario.read(array);
        binario.close();
        return array;
    }

    public byte[] convertutf(String s) throws UnsupportedEncodingException
    {
        byte[] arr = s.getBytes("UTF8");
        return arr;
    }
    public void close(){System.exit(0);}
}

