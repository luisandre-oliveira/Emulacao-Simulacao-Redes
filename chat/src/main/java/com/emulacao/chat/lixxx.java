package com.emulacao.chat;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.IOException;
import java.util.Scanner;

abstract class lixxx {
/*
    private static final String palu = "Boas";
    String dataBuffer;

    public void Serial_EventBasedReading(SerialPort activePort)
    {
        activePort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {return SerialPort.LISTENING_EVENT_DATA_RECEIVED;}

            @Override
            public void serialEvent(SerialPortEvent event) {
                byte[] newData = event.getReceivedData();
                for(int i = 0; i < newData.length; i++)
                {
                    dataBuffer += (char)newData[i];
                    chat.appendText(dataBuffer);
                }
            }
        });
    }

    public void test(SerialPort porta) throws IOException {
        Scanner data = new Scanner(porta.getInputStream());

        int value;
        porta.flushIOBuffers();
        porta.writeBytes(palu.getBytes(), palu.length());
        porta.flushIOBuffers();
        /*while(data.hasNextLine()) {
            try {
                value = Integer.parseInt(data.nextLine());
                System.out.println(value);
            } catch (Exception e){}
            System.out.println("Done.");
        }
    }*/
    /*
    public SerialPort anexar()
    {
        SerialPort porta;
        SerialPort[] ports = SerialPort.getCommPorts();
        System.out.println("Select a port:");
        int i = 1;
        for (SerialPort port : ports)
            System.out.println(i++ + ": " + port.getSystemPortName());
        Scanner s = new Scanner(System.in);
        int chosenPort = s.nextInt();
        porta = ports[chosenPort - 1];
        if (porta.openPort()){
            System.out.println("Port opened successfully.");}
        else {
            System.out.println("Unable to open the port.");
        }
        porta.setComPortParameters(115200, 8, 1, SerialPort.NO_PARITY);
        porta.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 9999, 9999);
        Serial_EventBasedReading(porta);
        return porta;
    }

    public void close(){System.exit(0);}*/
}
