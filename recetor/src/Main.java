import com.fazecast.jSerialComm.SerialPort;

import java.util.Arrays;
import java.util.Scanner;

import static java.lang.System.exit;

// ERROR CODE LIBRARY
// CODE 101 -> NO AVAILABLE COM PORTS
// CODE 102 -> CAN'T CONNECT TO CHOSEN PORT

public class Main {
    public static void main(String[] args) /*throws IOException, InterruptedException */{
        SerialPort[] ports = SerialPort.getCommPorts();

        if (ports.length != 0) {
            System.out.println("Select a port:");
            int i = 1;
            for (SerialPort port : ports)
                System.out.println(i++ + ": " + port.getSystemPortName());
            Scanner s = new Scanner(System.in);
            int chosenPort = s.nextInt();

            SerialPort serialPort = ports[chosenPort - 1];
            if (serialPort.openPort()) {
                System.out.println("Port opened successfully.");
            } else {
                System.out.println("ERROR: Unable to open the port.");
                exit(102);
            }

            serialPort.setComPortParameters(115200, 8, 1, SerialPort.NO_PARITY);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 5000, 0);

            /*
            byte[] newData = serialPort.getInputStream().readNBytes(93000);
            System.out.println("Size of data: " + newData.length);
            for(int j = 0; j < newData.length; ++j)
            System.out.print((char)newData[j]);
            System.out.println("\n");
            */

            try {
                while (true)
                {
                    while (serialPort.bytesAvailable() == 0)
                        Thread.sleep(10);

                    byte[] readBuffer = new byte[10];
                    int bytesRead = serialPort.readBytes(readBuffer, readBuffer.length);
                    System.out.println("Bytes: " + bytesRead + " bytes");
                    System.out.println(Arrays.toString(readBuffer));

                    String temp = new String(readBuffer);
                    System.out.println(temp);
                    //System.out.println(Arrays.toString(readBuffer).getClass());

                    /* BufferedImage image = ImageIO.read(new File("C:/Users/luisa/Downloads/benfica.png"));
                    ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();
                    ImageIO.write(image,"png",outStreamObj);
                    byte[] byteArray = outStreamObj.toByteArray();

                    ByteArrayInputStream inStreambj = new ByteArrayInputStream(byteArray);
                    BufferedImage newImage = ImageIO.read(inStreambj);
                    ImageIO.write(newImage,"png",new File("output.png")); */
                }
            } catch (Exception e) { e.printStackTrace(); }
            serialPort.closePort();
        }
        else {
            System.out.println("ERROR: No COM ports available");
            exit(101);
        }
    }
}