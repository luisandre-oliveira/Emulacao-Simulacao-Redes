package com.emulacao.chat;

import java.nio.ByteBuffer;

public class LinkChat {

    public static final byte CONTROL_MESSAGE = 0;
    public static final byte CONTROL_FILE = 1;
    public static final byte CONTROL_ACK = 2;

    // fields for source and destination addresses
    private final byte sourceAddress;
    private final byte destinationAddress;

    // field for control information
    private final byte control;

    // field for sequence number
    private int sequenceNumber;

    // field for data payload
    private final byte[] data;

    // field for checksum
    private final byte checksum;

    // optional fields
    private byte[] optionalFields = new byte[255];

    public LinkChat(int sourceAddress, int destinationAddress, int control, int sequenceNumber, byte[] data, byte checksum) {
        this.sourceAddress = (byte) sourceAddress;
        this.destinationAddress = (byte) destinationAddress;
        this.control = (byte) control;
        this.sequenceNumber = sequenceNumber;
        this.data = data;
        this.checksum = checksum;
    }

    public LinkChat(byte sourceAddress, byte destinationAddress, byte control, int sequenceNumber, byte[] data, byte checksum) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.control = control;
        this.sequenceNumber = sequenceNumber;
        this.data = data;
        this.checksum = checksum;
    }

    public byte[] getPacket() {
        // calculate the total size of the packet
        int size = 1 + 1 + 1 + 4 + data.length + 1 + optionalFields.length;
        byte[] packet = new byte[size];

        // copy the fields into the packet
        packet[0] = sourceAddress;
        packet[1] = destinationAddress;
        packet[2] = control;
        byte[] seqNum = ByteBuffer.allocate(4).putInt(sequenceNumber).array();
        System.arraycopy(seqNum, 0, packet, 3, 4);


        if (data.length < 252) {
            // copy data into packet
            System.arraycopy(data, 0, packet, 7, data.length);
            // fill remaining bytes with garbage data
            for (int i = data.length; i < 252; i++) {
                packet[i + 7] = (byte) 0xFF;
            }
        } else {
            // copy data into packet (maximum of 252 bytes)
            System.arraycopy(data, 0, packet, 7, 252);
        }

        // add checksum to packet
        packet[259] = checksum;
        // add optional fields to packet
        //System.arraycopy(optionalFields, 0, packet, 260, optionalFields.length);

        return packet;
    }

    public byte getSourceAddress() {
        return sourceAddress;
    }

    public byte getDestinationAddress() {
        return destinationAddress;
    }

    public byte getControl() {
        return control;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public byte[] getData() {
        return data;
    }

    public byte getChecksum() {
        return checksum;
    }

    public byte[] getOptionalFields() {
        return optionalFields;
    }

    public void setSequenceNumber(int i) {
        this.sequenceNumber = i;
    }
}