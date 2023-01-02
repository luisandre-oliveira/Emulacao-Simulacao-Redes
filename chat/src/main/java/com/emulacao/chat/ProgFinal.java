package com.emulacao.chat;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import com.fazecast.jSerialComm.*;
import org.jetbrains.annotations.NotNull;

public class ProgFinal {
    int y = 0;
    // GUI elements
    @FXML
    private ComboBox<String> portList;
    @FXML
    private TextArea chat;
    @FXML
    private TextArea submit;
    @FXML
    private Button ligar;
    @FXML
    private Button enviar;
    @FXML
    private Button refresh;
    @FXML
    private ProgressBar progresso;

    private static final byte SOURCE_ADDRESS = 1;
    private static final byte DESTINATION_ADDRESS = 2;
    private boolean ackReceived = false;
    private boolean nackReceived = false;

    private SerialPort serialPort;
    private boolean connected;

    public ProgFinal() {
    }

    @FXML
    public void initialize() {
        // populate the port list ComboBox with available serial ports
       scan();
    }

    private void EventBasedReading(@NotNull SerialPort port)
    {
        serialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    return;
                }

                try {
                    // read the incoming data
                    byte[] data = event.getReceivedData();

                    // parse the LinkChat packet
                    LinkChat packet = parseLinkChatPacket(data);

                    // check if the packet is meant for this device
                    if (packet.getSourceAddress() == SOURCE_ADDRESS) {
                        // send an ACK to the sender
                        sendAck(packet.getSequenceNumber());

                        // handle the packet
                        handlePacket(packet);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void handlePacket(LinkChat packet) {
        // handle the packet based on the control field
        switch (packet.getControl()) {
            case LinkChat.CONTROL_MESSAGE:
                // display the message in the chat TextArea
                Platform.runLater(() -> chat.appendText("PC2: " + new String(packet.getData()) + "\n"));
                break;
            case LinkChat.CONTROL_FILE:
                // ask the user where to save the file
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save File");
                File file = fileChooser.showSaveDialog(chat.getScene().getWindow());
                if (file != null) {
                    // write the file data to the specified location
                    try {
                        Files.write(file.toPath(), packet.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @FXML
    private void sendAck(int sequenceNumber) {
        // create an ACK LinkChat packet
        LinkChat ackPacket = new LinkChat(SOURCE_ADDRESS, DESTINATION_ADDRESS,LinkChat.CONTROL_ACK, sequenceNumber, new byte[0],(byte)0);
        // send the ACK packet
        try {
            serialPort.writeBytes(ackPacket.getPacket(), ackPacket.getPacket().length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void connect() {
        // get the selected port name from the ComboBox
        int portIndex = portList.getSelectionModel().getSelectedIndex();
        SerialPort[] p = SerialPort.getCommPorts();
        serialPort = p[portIndex];
        try {
            serialPort.openPort();
            serialPort.setComPortParameters(9600, 8, 1, serialPort.NO_PARITY);
            connected = true;
            ligar.setText("Desligar");
            progresso.setStyle("-fx-accent: green");
            EventBasedReading(serialPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void disconnect() {
        // close the serial port
        try {
            serialPort.closePort();
            connected = false;
            ligar.setText("Ligar");
            progresso.setStyle("-fx-accent: red");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void sendFile() {
        // show a FileChooser to select the file to send
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        File file = fileChooser.showOpenDialog(chat.getScene().getWindow());
        if (file != null) {
            // read the file data into a byte array
            byte[] fileData = null;
            try {
                fileData = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            // send the file data as a LinkChat packet
            sendLinkChatPacket(LinkChat.CONTROL_FILE, fileData);
        }
    }


    private LinkChat[] sendLinkChatPacket(byte control, byte[] data) {
        // determine the number of packets needed
        int numPackets = (int) Math.ceil(data.length / 252.0);

        // create an array to store the packets
        LinkChat[] packets = new LinkChat[numPackets];

        // divide the data into packets
        for (int i = 0; i < numPackets; i++) {
            // determine the size of the packet data
            int size = Math.min(252, data.length - i * 252);

            // create the packet
            byte[] packetData = Arrays.copyOfRange(data, i * 252, i * 252 + size);
            packets[i] = new LinkChat(SOURCE_ADDRESS, DESTINATION_ADDRESS, control, i, packetData, (byte)0);
        }

        return packets;
    }


    private void sendPacket(LinkChat[] packets) {
// keep track of the next sequence number to use
        int nextSequenceNumber = 0;
        // send the packets and wait for an ACK or NACK
        for (LinkChat packet : packets) {
            // set the packet's sequence number
            packet.setSequenceNumber(nextSequenceNumber);
            // reset the received response flag
            boolean receivedResponse = false;
            while (!receivedResponse) {
                // send the packet
                try {
                    serialPort.writeBytes(packet.getPacket(), packet.getPacket().length);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                // wait for a response
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 25) {
                    // check if an ACK or NACK was received
                    if (ackReceived) {
                        // an ACK was received, so the packet was successfully sent
                        receivedResponse = true;
                        break;
                    } else if (nackReceived) {
                        // a NACK was received, so resend the packet
                        break;
                    }
                }
            }

            // increment the sequence number for the next packet
            nextSequenceNumber++;
        }
    }


    private LinkChat parseLinkChatPacket(byte[] data) {
        // extract the source address
        byte sourceAddress = data[0];

        // extract the destination address
        byte destinationAddress = data[1];

        // extract the control field
        byte control = data[2];

        // extract the sequence number
        int sequenceNumber = ByteBuffer.wrap(Arrays.copyOfRange(data, 3, 7)).getInt();

        // extract the data payload
        byte[] payload = Arrays.copyOfRange(data, 7, 259);

        // extract the checksum
        byte checksum = data[259];

        // extract the optional fields
        byte[] optionalFields = Arrays.copyOfRange(data, 260, data.length);

        // create and return a new LinkChat object
        return new LinkChat(sourceAddress, destinationAddress, control, sequenceNumber, payload, checksum);
    }

    @FXML
    private void sendMessage() {
    // get the message from the submit TextArea
        String message = submit.getText();
    // send the message as a LinkChat packet
        submit.clear();
        chat.appendText("Eu:" + message + "\n");
        sendLinkChatPacket(LinkChat.CONTROL_MESSAGE, message.getBytes());
    // clear the submit TextArea
        submit.clear();
    }

    @FXML
    private void close(){System.exit(0);}

    @FXML
    private void botaoLigar()
    {
        if(!connected)
        {
            connect();
            return;
        }
        disconnect();
    }

    @FXML
    public void scan()
    {
        portList.getItems().removeAll(portList.getItems());
        SerialPort[] ports = SerialPort.getCommPorts();
        for(SerialPort port : ports)
        {
            portList.getItems().add(port.getSystemPortName());
        }
        portList.getSelectionModel().select("Escolher");
    }


}


