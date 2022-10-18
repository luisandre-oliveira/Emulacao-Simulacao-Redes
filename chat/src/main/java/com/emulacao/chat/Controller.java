package com.emulacao.chat;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import com.fazecast.jSerialComm.*;

import java.io.*;
import java.io.Serial;
import java.util.Scanner;


public class Controller {

    SerialPort porta;
    OutputStream outputStream1;
    InputStream inputStream1;
    String dataBuffer = "";
    final String newLine = "\n\r";


    @FXML
    public TextArea chat;
    public TextArea submit;

    @FXML
    public void enviar() throws IOException {
        Serial_EventBasedReading(porta);
        if (submit.getText() != null) {
            outputStream1 = porta.getOutputStream();
            String dataToSend = "";
            Serial_EventBasedReading(porta);
            dataToSend = submit.getText();
            chat.appendText("PC1: " + submit.getText());
            chat.appendText("\n");
            submit.setText(null);
            inputStream1 = porta.getInputStream();
            try {
                outputStream1.write(dataToSend.getBytes());
                outputStream1.write(newLine.getBytes());
            } catch (IOException e) {
                chat.appendText("ERRO\n");
            }
        }
    }

    public void anexar() {
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
        porta.setComPortParameters(115200, 8, 1, SerialPort.NO_PARITY);
        porta.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 9999, 9999);
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
                    dataBuffer += (char) newData[i];
                }
                chat.appendText("PC2: " + dataBuffer);
                chat.appendText(newLine);
                porta.flushIOBuffers();
                dataBuffer = "";

            }
        });
    }
    public void close(){System.exit(0);}
}

