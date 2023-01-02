package com.emulacao.chat;

public class SerialPortSource {
/**
package com.fazecast.jSerialComm;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

    public final class SerialPort {
        public static final int NO_PARITY = 0;
        public static final int ODD_PARITY = 1;
        public static final int EVEN_PARITY = 2;
        public static final int MARK_PARITY = 3;
        public static final int SPACE_PARITY = 4;
        public static final int ONE_STOP_BIT = 1;
        public static final int ONE_POINT_FIVE_STOP_BITS = 2;
        public static final int TWO_STOP_BITS = 3;
        public static final int FLOW_CONTROL_DISABLED = 0;
        public static final int FLOW_CONTROL_RTS_ENABLED = 1;
        public static final int FLOW_CONTROL_CTS_ENABLED = 16;
        public static final int FLOW_CONTROL_DSR_ENABLED = 256;
        public static final int FLOW_CONTROL_DTR_ENABLED = 4096;
        public static final int FLOW_CONTROL_XONXOFF_IN_ENABLED = 65536;
        public static final int FLOW_CONTROL_XONXOFF_OUT_ENABLED = 1048576;
        public static final int TIMEOUT_NONBLOCKING = 0;
        public static final int TIMEOUT_READ_SEMI_BLOCKING = 1;
        public static final int TIMEOUT_READ_BLOCKING = 16;
        public static final int TIMEOUT_WRITE_BLOCKING = 256;
        public static final int TIMEOUT_SCANNER = 4096;
        public static final int LISTENING_EVENT_TIMED_OUT = 0;
        public static final int LISTENING_EVENT_DATA_AVAILABLE = 1;
        public static final int LISTENING_EVENT_DATA_RECEIVED = 16;
        public static final int LISTENING_EVENT_DATA_WRITTEN = 256;
        public static final int LISTENING_EVENT_BREAK_INTERRUPT = 65536;
        public static final int LISTENING_EVENT_CARRIER_DETECT = 131072;
        public static final int LISTENING_EVENT_CTS = 262144;
        public static final int LISTENING_EVENT_DSR = 524288;
        public static final int LISTENING_EVENT_RING_INDICATOR = 1048576;
        public static final int LISTENING_EVENT_FRAMING_ERROR = 2097152;
        public static final int LISTENING_EVENT_FIRMWARE_OVERRUN_ERROR = 4194304;
        public static final int LISTENING_EVENT_SOFTWARE_OVERRUN_ERROR = 8388608;
        public static final int LISTENING_EVENT_PARITY_ERROR = 16777216;
        public static final int LISTENING_EVENT_PORT_DISCONNECTED = 268435456;
        private static final String versionString = "2.9.2";
        private static final String tmpdirAppIdProperty = "fazecast.jSerialComm.appid";
        private static final List<Thread> shutdownHooks = new ArrayList();
        private static boolean isWindows = false;
        private static boolean isAndroid = false;
        private static volatile boolean isShuttingDown = false;
        private volatile long portHandle = 0L;
        private volatile int baudRate = 9600;
        private volatile int dataBits = 8;
        private volatile int stopBits = 1;
        private volatile int parity = 0;
        private volatile int eventFlags = 0;
        private volatile int timeoutMode = 0;
        private volatile int readTimeout = 0;
        private volatile int writeTimeout = 0;
        private volatile int flowControl = 0;
        private volatile int sendDeviceQueueSize = 4096;
        private volatile int receiveDeviceQueueSize = 4096;
        private volatile int safetySleepTimeMS = 200;
        private volatile int rs485DelayBefore = 0;
        private volatile int rs485DelayAfter = 0;
        private volatile byte xonStartChar = 17;
        private volatile byte xoffStopChar = 19;
        private volatile SerialPortDataListener userDataListener = null;
        private volatile com.fazecast.jSerialComm.SerialPort.SerialPortEventListener serialEventListener = null;
        private volatile String comPort;
        private volatile String friendlyName;
        private volatile String portDescription;
        private volatile String portLocation;
        private volatile boolean eventListenerRunning = false;
        private volatile boolean disableConfig = false;
        private volatile boolean disableExclusiveLock = false;
        private volatile boolean rs485Mode = false;
        private volatile boolean rs485ActiveHigh = true;
        private volatile boolean rs485RxDuringTx = false;
        private volatile boolean rs485EnableTermination = false;
        private volatile boolean isRtsEnabled = true;
        private volatile boolean isDtrEnabled = true;
        private volatile boolean autoFlushIOBuffers = false;
        private volatile boolean requestElevatedPermissions = false;

        private static boolean isSymbolicLink(File file) throws IOException {
            File canonicalFile = file.getParent() == null ? file : new File(file.getParentFile().getCanonicalFile(), file.getName());
            return !canonicalFile.getCanonicalFile().equals(canonicalFile.getAbsoluteFile());
        }

        private static void cleanUpDirectory(File path) {
            if (path.isDirectory()) {
                File[] files = path.listFiles();
                if (files != null) {
                    File[] arr$ = files;
                    int len$ = files.length;

                    for(int i$ = 0; i$ < len$; ++i$) {
                        File file = arr$[i$];
                        if (!file.getName().equals("2.9.2")) {
                            cleanUpDirectory(file);
                        }
                    }
                }
            }

            if (!path.getName().equals("2.9.2")) {
                path.delete();
            }

        }

        private static boolean loadNativeLibrary(String absoluteLibraryPath, Vector<String> errorMessages) {
            try {
                System.load(absoluteLibraryPath);
                return true;
            } catch (UnsatisfiedLinkError var3) {
                errorMessages.add(var3.getMessage());
                return false;
            } catch (Exception var4) {
                errorMessages.add(var4.getMessage());
                return false;
            }
        }

        public final String toString() {
            return this.getPortDescription();
        }

        public static String getVersion() {
            return "2.9.2";
        }

        public static synchronized void addShutdownHook(Thread hook) {
            shutdownHooks.add(hook);
        }

        public static synchronized com.fazecast.jSerialComm.SerialPort[] getCommPorts() {
            return getCommPortsNative();
        }

        public static synchronized com.fazecast.jSerialComm.SerialPort getCommPort(String portDescriptor) throws SerialPortInvalidPortException {
            try {
                if (portDescriptor.startsWith("~" + File.separator)) {
                    portDescriptor = System.getProperty("user.home") + portDescriptor.substring(1);
                }

                if (isWindows) {
                    portDescriptor = "\\\\.\\" + portDescriptor.substring(portDescriptor.lastIndexOf(92) + 1);
                } else if (isSymbolicLink(new File(portDescriptor))) {
                    portDescriptor = (new File(portDescriptor)).getCanonicalPath();
                } else if (!(new File(portDescriptor)).exists()) {
                    portDescriptor = "/dev/" + portDescriptor;
                    if (!(new File(portDescriptor)).exists()) {
                        portDescriptor = "/dev/" + portDescriptor.substring(portDescriptor.lastIndexOf(47) + 1);
                    }

                    if (!(new File(portDescriptor)).exists()) {
                        throw new IOException();
                    }
                }
            } catch (Exception var2) {
                throw new SerialPortInvalidPortException("Unable to create a serial port object from the invalid port descriptor: " + portDescriptor, var2);
            }

            com.fazecast.jSerialComm.SerialPort serialPort = new com.fazecast.jSerialComm.SerialPort();
            serialPort.comPort = portDescriptor;
            serialPort.friendlyName = "User-Specified Port";
            serialPort.portDescription = "User-Specified Port";
            serialPort.portLocation = "0-0";
            serialPort.retrievePortDetails();
            return serialPort;
        }

        public final synchronized boolean openPort(int safetySleepTime, int deviceSendQueueSize, int deviceReceiveQueueSize) {
            this.safetySleepTimeMS = safetySleepTime;
            this.sendDeviceQueueSize = deviceSendQueueSize > 0 ? deviceSendQueueSize : this.sendDeviceQueueSize;
            this.receiveDeviceQueueSize = deviceReceiveQueueSize > 0 ? deviceReceiveQueueSize : this.receiveDeviceQueueSize;
            if (this.portHandle != 0L) {
                return this.configPort(this.portHandle);
            } else {
                if (this.safetySleepTimeMS > 0) {
                    try {
                        Thread.sleep((long)this.safetySleepTimeMS);
                    } catch (Exception var34) {
                        Thread.currentThread().interrupt();
                    }
                }

                File portFile = isAndroid ? new File(this.comPort) : null;
                if (portFile != null && (!portFile.canRead() || !portFile.canWrite())) {
                    label283: {
                        Process process = null;

                        boolean var7;
                        try {
                            process = Runtime.getRuntime().exec("su");
                            DataOutputStream writer = new DataOutputStream(process.getOutputStream());
                            writer.writeBytes("chmod 666 " + this.comPort + "\n");
                            writer.writeBytes("exit\n");
                            writer.flush();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                            while(true) {
                                if (reader.readLine() == null) {
                                    break label283;
                                }
                            }
                        } catch (Exception var35) {
                            var35.printStackTrace();
                            var7 = false;
                        } finally {
                            if (process == null) {
                                return false;
                            }

                            try {
                                process.waitFor();
                            } catch (InterruptedException var33) {
                                Thread.currentThread().interrupt();
                                return false;
                            }

                            try {
                                process.getInputStream().close();
                            } catch (IOException var32) {
                                var32.printStackTrace();
                                return false;
                            }

                            try {
                                process.getOutputStream().close();
                            } catch (IOException var31) {
                                var31.printStackTrace();
                                return false;
                            }

                            try {
                                process.getErrorStream().close();
                            } catch (IOException var30) {
                                var30.printStackTrace();
                                return false;
                            }

                            try {
                                Thread.sleep(500L);
                            } catch (InterruptedException var29) {
                                Thread.currentThread().interrupt();
                                return false;
                            }
                        }

                        return var7;
                    }
                }

                this.portHandle = this.openPortNative();
                if (this.portHandle != 0L && this.serialEventListener != null) {
                    this.serialEventListener.startListening();
                }

                return this.portHandle != 0L;
            }
        }

        public final synchronized boolean openPort(int safetySleepTime) {
            return this.openPort(safetySleepTime, this.sendDeviceQueueSize, this.receiveDeviceQueueSize);
        }

        public final synchronized boolean openPort() {
            return this.openPort(0);
        }

        public final synchronized boolean closePort() {
            if (this.serialEventListener != null) {
                this.serialEventListener.stopListening();
            }

            if (this.portHandle != 0L) {
                this.portHandle = this.closePortNative(this.portHandle);
            }

            return this.portHandle == 0L;
        }

        public final synchronized boolean isOpen() {
            return this.portHandle != 0L;
        }

        public final synchronized void disablePortConfiguration() {
            this.disableConfig = true;
        }

        public final synchronized void disableExclusiveLock() {
            this.disableExclusiveLock = true;
        }

        public final synchronized void allowElevatedPermissionsRequest() {
            this.requestElevatedPermissions = true;
        }

        public final synchronized int getLastErrorLocation() {
            return this.getLastErrorLocation(this.portHandle);
        }

        public final synchronized int getLastErrorCode() {
            return this.getLastErrorCode(this.portHandle);
        }

        private static native void uninitializeLibrary();

        private static native com.fazecast.jSerialComm.SerialPort[] getCommPortsNative();

        private native void retrievePortDetails();

        private native long openPortNative();

        private native long closePortNative(long var1);

        private native boolean configPort(long var1);

        private native boolean configTimeouts(long var1, int var3, int var4, int var5, int var6);

        private native boolean flushRxTxBuffers(long var1);

        private native int waitForEvent(long var1);

        private native int bytesAvailable(long var1);

        private native int bytesAwaitingWrite(long var1);

        private native int readBytes(long var1, byte[] var3, long var4, long var6, int var8, int var9);

        private native int writeBytes(long var1, byte[] var3, long var4, long var6, int var8);

        private native void setEventListeningStatus(long var1, boolean var3);

        private native boolean setBreak(long var1);

        private native boolean clearBreak(long var1);

        private native boolean setRTS(long var1);

        private native boolean clearRTS(long var1);

        private native boolean presetRTS();

        private native boolean preclearRTS();

        private native boolean setDTR(long var1);

        private native boolean clearDTR(long var1);

        private native boolean presetDTR();

        private native boolean preclearDTR();

        private native boolean getCTS(long var1);

        private native boolean getDSR(long var1);

        private native boolean getDCD(long var1);

        private native boolean getDTR(long var1);

        private native boolean getRTS(long var1);

        private native boolean getRI(long var1);

        private native int getLastErrorLocation(long var1);

        private native int getLastErrorCode(long var1);

        public final int bytesAvailable() {
            return this.portHandle != 0L ? this.bytesAvailable(this.portHandle) : -1;
        }

        public final int bytesAwaitingWrite() {
            return this.portHandle != 0L ? this.bytesAwaitingWrite(this.portHandle) : -1;
        }

        public final int readBytes(byte[] buffer, long bytesToRead) {
            return this.portHandle != 0L ? this.readBytes(this.portHandle, buffer, bytesToRead, 0L, this.timeoutMode, this.readTimeout) : -1;
        }

        public final int readBytes(byte[] buffer, long bytesToRead, long offset) {
            return this.portHandle != 0L ? this.readBytes(this.portHandle, buffer, bytesToRead, offset, this.timeoutMode, this.readTimeout) : -1;
        }

        public final int writeBytes(byte[] buffer, long bytesToWrite) {
            int totalNumWritten;
            int numWritten;
            for(totalNumWritten = 0; this.portHandle != 0L && (long)totalNumWritten != bytesToWrite; totalNumWritten += numWritten) {
                numWritten = this.writeBytes(this.portHandle, buffer, bytesToWrite - (long)totalNumWritten, (long)totalNumWritten, this.timeoutMode);
                if (numWritten <= 0) {
                    break;
                }
            }

            return this.portHandle != 0L && totalNumWritten >= 0 ? totalNumWritten : -1;
        }

        public final int writeBytes(byte[] buffer, long bytesToWrite, long offset) {
            int totalNumWritten;
            int numWritten;
            for(totalNumWritten = 0; this.portHandle != 0L && (long)totalNumWritten != bytesToWrite; totalNumWritten += numWritten) {
                numWritten = this.writeBytes(this.portHandle, buffer, bytesToWrite - (long)totalNumWritten, offset + (long)totalNumWritten, this.timeoutMode);
                if (numWritten <= 0) {
                    break;
                }
            }

            return this.portHandle != 0L && totalNumWritten >= 0 ? totalNumWritten : -1;
        }

        public final int getDeviceWriteBufferSize() {
            return this.sendDeviceQueueSize;
        }

        public final int getDeviceReadBufferSize() {
            return this.receiveDeviceQueueSize;
        }

        public final boolean setBreak() {
            return this.portHandle != 0L && this.setBreak(this.portHandle);
        }

        public final boolean clearBreak() {
            return this.portHandle != 0L && this.clearBreak(this.portHandle);
        }

        public final boolean setRTS() {
            this.isRtsEnabled = true;
            return this.portHandle != 0L ? this.setRTS(this.portHandle) : this.presetRTS();
        }

        public final boolean clearRTS() {
            this.isRtsEnabled = false;
            return this.portHandle != 0L ? this.clearRTS(this.portHandle) : this.preclearRTS();
        }

        public final boolean setDTR() {
            this.isDtrEnabled = true;
            return this.portHandle != 0L ? this.setDTR(this.portHandle) : this.presetDTR();
        }

        public final boolean clearDTR() {
            this.isDtrEnabled = false;
            return this.portHandle != 0L ? this.clearDTR(this.portHandle) : this.preclearDTR();
        }

        public final boolean getCTS() {
            return this.portHandle != 0L && this.getCTS(this.portHandle);
        }

        public final boolean getDSR() {
            return this.portHandle != 0L && this.getDSR(this.portHandle);
        }

        public final boolean getDCD() {
            return this.portHandle != 0L && this.getDCD(this.portHandle);
        }

        public final boolean getDTR() {
            return this.portHandle != 0L && this.getDTR(this.portHandle);
        }

        public final boolean getRTS() {
            return this.portHandle != 0L && this.getRTS(this.portHandle);
        }

        public final boolean getRI() {
            return this.portHandle != 0L && this.getRI(this.portHandle);
        }

        private SerialPort() {
        }

        public final synchronized boolean addDataListener(SerialPortDataListener listener) {
            if (this.userDataListener != null) {
                return false;
            } else {
                this.userDataListener = listener;
                this.eventFlags = listener.getListeningEvents();
                if ((this.eventFlags & 16) > 0) {
                    this.eventFlags |= 1;
                }

                this.serialEventListener = this.userDataListener instanceof SerialPortPacketListener ? new com.fazecast.jSerialComm.SerialPort.SerialPortEventListener(((SerialPortPacketListener)this.userDataListener).getPacketSize()) : (this.userDataListener instanceof SerialPortMessageListener ? new com.fazecast.jSerialComm.SerialPort.SerialPortEventListener(((SerialPortMessageListener)this.userDataListener).getMessageDelimiter(), ((SerialPortMessageListener)this.userDataListener).delimiterIndicatesEndOfMessage()) : new com.fazecast.jSerialComm.SerialPort.SerialPortEventListener());
                if (this.portHandle != 0L) {
                    this.configTimeouts(this.portHandle, this.timeoutMode, this.readTimeout, this.writeTimeout, this.eventFlags);
                    this.serialEventListener.startListening();
                }

                return true;
            }
        }

        public final synchronized void removeDataListener() {
            this.eventFlags = 0;
            if (this.serialEventListener != null) {
                this.serialEventListener.stopListening();
                this.serialEventListener = null;
                if (this.portHandle != 0L) {
                    this.configTimeouts(this.portHandle, this.timeoutMode, this.readTimeout, this.writeTimeout, this.eventFlags);
                }
            }

            this.userDataListener = null;
        }

        public final InputStream getInputStream() {
            return new com.fazecast.jSerialComm.SerialPort.SerialPortInputStream(false);
        }

        public final InputStream getInputStreamWithSuppressedTimeoutExceptions() {
            return new com.fazecast.jSerialComm.SerialPort.SerialPortInputStream(true);
        }

        public final OutputStream getOutputStream() {
            return new com.fazecast.jSerialComm.SerialPort.SerialPortOutputStream();
        }

        public final synchronized boolean flushIOBuffers() {
            this.autoFlushIOBuffers = true;
            return this.portHandle != 0L ? this.flushRxTxBuffers(this.portHandle) : true;
        }

        public final boolean setComPortParameters(int newBaudRate, int newDataBits, int newStopBits, int newParity) {
            return this.setComPortParameters(newBaudRate, newDataBits, newStopBits, newParity, this.rs485Mode);
        }

        public final synchronized boolean setComPortParameters(int newBaudRate, int newDataBits, int newStopBits, int newParity, boolean useRS485Mode) {
            this.baudRate = newBaudRate;
            this.dataBits = newDataBits;
            this.stopBits = newStopBits;
            this.parity = newParity;
            this.rs485Mode = useRS485Mode;
            if (this.portHandle != 0L) {
                if (this.safetySleepTimeMS > 0) {
                    try {
                        Thread.sleep((long)this.safetySleepTimeMS);
                    } catch (Exception var7) {
                        Thread.currentThread().interrupt();
                    }
                }

                return this.configPort(this.portHandle);
            } else {
                return true;
            }
        }

        public final synchronized boolean setComPortTimeouts(int newTimeoutMode, int newReadTimeout, int newWriteTimeout) {
            this.timeoutMode = newTimeoutMode;
            if (isWindows) {
                this.readTimeout = newReadTimeout;
                this.writeTimeout = newWriteTimeout;
            } else if (newReadTimeout > 0 && newReadTimeout <= 100) {
                this.readTimeout = 100;
            } else {
                this.readTimeout = Math.round((float)newReadTimeout / 100.0F) * 100;
            }

            if (this.portHandle != 0L) {
                if (this.safetySleepTimeMS > 0) {
                    try {
                        Thread.sleep((long)this.safetySleepTimeMS);
                    } catch (Exception var5) {
                        Thread.currentThread().interrupt();
                    }
                }

                return this.configTimeouts(this.portHandle, this.timeoutMode, this.readTimeout, this.writeTimeout, this.eventFlags);
            } else {
                return true;
            }
        }

        public final synchronized boolean setBaudRate(int newBaudRate) {
            this.baudRate = newBaudRate;
            if (this.portHandle != 0L) {
                if (this.safetySleepTimeMS > 0) {
                    try {
                        Thread.sleep((long)this.safetySleepTimeMS);
                    } catch (Exception var3) {
                        Thread.currentThread().interrupt();
                    }
                }

                return this.configPort(this.portHandle);
            } else {
                return true;
            }
        }

        public final synchronized boolean setNumDataBits(int newDataBits) {
            this.dataBits = newDataBits;
            if (this.portHandle != 0L) {
                if (this.safetySleepTimeMS > 0) {
                    try {
                        Thread.sleep((long)this.safetySleepTimeMS);
                    } catch (Exception var3) {
                        Thread.currentThread().interrupt();
                    }
                }

                return this.configPort(this.portHandle);
            } else {
                return true;
            }
        }

        public final synchronized boolean setNumStopBits(int newStopBits) {
            this.stopBits = newStopBits;
            if (this.portHandle != 0L) {
                if (this.safetySleepTimeMS > 0) {
                    try {
                        Thread.sleep((long)this.safetySleepTimeMS);
                    } catch (Exception var3) {
                        Thread.currentThread().interrupt();
                    }
                }

                return this.configPort(this.portHandle);
            } else {
                return true;
            }
        }

        public final synchronized boolean setFlowControl(int newFlowControlSettings) {
            this.flowControl = newFlowControlSettings;
            if (this.portHandle != 0L) {
                if (this.safetySleepTimeMS > 0) {
                    try {
                        Thread.sleep((long)this.safetySleepTimeMS);
                    } catch (Exception var3) {
                        Thread.currentThread().interrupt();
                    }
                }

                return this.configPort(this.portHandle);
            } else {
                return true;
            }
        }

        public final synchronized boolean setParity(int newParity) {
            this.parity = newParity;
            if (this.portHandle != 0L) {
                if (this.safetySleepTimeMS > 0) {
                    try {
                        Thread.sleep((long)this.safetySleepTimeMS);
                    } catch (Exception var3) {
                        Thread.currentThread().interrupt();
                    }
                }

                return this.configPort(this.portHandle);
            } else {
                return true;
            }
        }

        public final boolean setRs485ModeParameters(boolean useRS485Mode, boolean rs485RtsActiveHigh, int delayBeforeSendMicroseconds, int delayAfterSendMicroseconds) {
            return this.setRs485ModeParameters(useRS485Mode, rs485RtsActiveHigh, false, false, delayBeforeSendMicroseconds, delayAfterSendMicroseconds);
        }

        public final synchronized boolean setRs485ModeParameters(boolean useRS485Mode, boolean rs485RtsActiveHigh, boolean enableTermination, boolean rxDuringTx, int delayBeforeSendMicroseconds, int delayAfterSendMicroseconds) {
            this.rs485Mode = useRS485Mode;
            this.rs485ActiveHigh = rs485RtsActiveHigh;
            this.rs485EnableTermination = enableTermination;
            this.rs485RxDuringTx = rxDuringTx;
            this.rs485DelayBefore = delayBeforeSendMicroseconds;
            this.rs485DelayAfter = delayAfterSendMicroseconds;
            if (this.portHandle != 0L) {
                if (this.safetySleepTimeMS > 0) {
                    try {
                        Thread.sleep((long)this.safetySleepTimeMS);
                    } catch (Exception var8) {
                        Thread.currentThread().interrupt();
                    }
                }

                return this.configPort(this.portHandle);
            } else {
                return true;
            }
        }

        public final synchronized boolean setXonXoffCharacters(byte xonStartCharacter, byte xoffStopCharacter) {
            this.xonStartChar = xonStartCharacter;
            this.xoffStopChar = xoffStopCharacter;
            if (this.portHandle != 0L) {
                if (this.safetySleepTimeMS > 0) {
                    try {
                        Thread.sleep((long)this.safetySleepTimeMS);
                    } catch (Exception var4) {
                        Thread.currentThread().interrupt();
                    }
                }

                return this.configPort(this.portHandle);
            } else {
                return true;
            }
        }

        public final String getDescriptivePortName() {
            return this.friendlyName.trim();
        }

        public final String getSystemPortName() {
            return isWindows ? this.comPort.substring(this.comPort.lastIndexOf(92) + 1) : this.comPort.substring(this.comPort.lastIndexOf(47) + 1);
        }

        public final String getSystemPortPath() {
            return this.comPort;
        }

        public final String getPortDescription() {
            return this.portDescription.trim();
        }

        public final String getPortLocation() {
            return this.portLocation;
        }

        public final int getBaudRate() {
            return this.baudRate;
        }

        public final int getNumDataBits() {
            return this.dataBits;
        }

        public final int getNumStopBits() {
            return this.stopBits;
        }

        public final int getParity() {
            return this.parity;
        }

        public final int getReadTimeout() {
            return this.readTimeout;
        }

        public final int getWriteTimeout() {
            return this.writeTimeout;
        }

        public final int getFlowControlSettings() {
            return this.flowControl;
        }

        static {
            String[] architectures = null;
            String libraryPath = "";
            String libraryFileName = "";
            String OS = System.getProperty("os.name").toLowerCase();
            String manualLibraryPath = System.getProperty("jSerialComm.library.path", "");
            String tempFileDirectory = System.getProperty("java.io.tmpdir");
            String userHomeDirectory = System.getProperty("user.home");
            if (!tempFileDirectory.endsWith("\\") && !tempFileDirectory.endsWith("/")) {
                tempFileDirectory = tempFileDirectory + File.separator;
            }

            if (!userHomeDirectory.endsWith("\\") && !userHomeDirectory.endsWith("/")) {
                userHomeDirectory = userHomeDirectory + File.separator;
            }

            if (!manualLibraryPath.isEmpty() && !manualLibraryPath.endsWith("\\") && !manualLibraryPath.endsWith("/")) {
                manualLibraryPath = manualLibraryPath + File.separator;
            }

            tempFileDirectory = tempFileDirectory + "jSerialComm" + File.separator + System.getProperty("fazecast.jSerialComm.appid", "");
            userHomeDirectory = userHomeDirectory + ".jSerialComm" + File.separator + System.getProperty("fazecast.jSerialComm.appid", "");
            if (!tempFileDirectory.endsWith("\\") && !tempFileDirectory.endsWith("/")) {
                tempFileDirectory = tempFileDirectory + File.separator;
            }

            if (!userHomeDirectory.endsWith("\\") && !userHomeDirectory.endsWith("/")) {
                userHomeDirectory = userHomeDirectory + File.separator;
            }

            cleanUpDirectory(new File(tempFileDirectory));
            cleanUpDirectory(new File(userHomeDirectory));
            tempFileDirectory = tempFileDirectory + "2.9.2" + File.separator;
            userHomeDirectory = userHomeDirectory + "2.9.2" + File.separator;
            if (System.getProperty("java.vm.vendor").toLowerCase().contains("android")) {
                isAndroid = true;
                libraryPath = "Android";
                libraryFileName = "libjSerialComm.so";
                architectures = new String[]{"arm64-v8a", "armeabi-v7a", "x86_64", "x86"};
            } else if (OS.contains("win")) {
                isWindows = true;
                libraryPath = "Windows";
                libraryFileName = "jSerialComm.dll";
                architectures = new String[]{"aarch64", "armv7", "x86_64", "x86"};
            } else if (OS.contains("mac")) {
                libraryPath = "OSX";
                libraryFileName = "libjSerialComm.jnilib";
                architectures = new String[]{"aarch64", "x86_64", "x86"};
            } else if (!OS.contains("sunos") && !OS.contains("solaris")) {
                if (OS.contains("freebsd")) {
                    libraryPath = "FreeBSD";
                    libraryFileName = "libjSerialComm.so";
                    architectures = new String[]{"arm64", "x86_64", "x86"};
                } else if (OS.contains("openbsd")) {
                    libraryPath = "OpenBSD";
                    libraryFileName = "libjSerialComm.so";
                    architectures = new String[]{"amd64", "x86"};
                } else if (!OS.contains("nix") && !OS.contains("nux")) {
                    System.err.println("This operating system is not supported by the jSerialComm library.");
                    System.exit(-1);
                } else {
                    libraryPath = "Linux";
                    libraryFileName = "libjSerialComm.so";
                    architectures = new String[]{"armv8_64", "x86_64", "armv8_32", "armv7hf", "armv7", "armv6hf", "armv6", "armv5", "ppc64le", "x86"};
                }
            } else {
                libraryPath = "Solaris";
                libraryFileName = "libjSerialComm.so";
                architectures = new String[]{"sparcv9_64", "sparcv8plus_32", "x86_64", "x86"};
            }

            boolean libraryLoaded = false;
            Vector<String> errorMessages = new Vector();
            int attempt;
            if (!manualLibraryPath.isEmpty()) {
                for(attempt = 0; !libraryLoaded && attempt < architectures.length; ++attempt) {
                    libraryLoaded = loadNativeLibrary((new File(manualLibraryPath + libraryPath + File.separator + architectures[attempt] + File.separator + libraryFileName)).getAbsolutePath(), errorMessages);
                }

                if (!libraryLoaded) {
                    libraryLoaded = loadNativeLibrary((new File(manualLibraryPath + libraryFileName)).getAbsolutePath(), errorMessages);
                }
            }

            for(attempt = 0; !libraryLoaded && attempt < 2; ++attempt) {
                File nativeLibrary = new File((attempt == 0 ? tempFileDirectory : userHomeDirectory) + libraryFileName);
                libraryLoaded = nativeLibrary.exists() && loadNativeLibrary(nativeLibrary.getAbsolutePath(), errorMessages);
            }

            String attempt1Library = "";

            for(int attempt = 0; !libraryLoaded && attempt < 2; ++attempt) {
                File tempNativeLibrary = new File((attempt == 0 ? tempFileDirectory : userHomeDirectory) + libraryFileName);
                if (attempt == 0) {
                    attempt1Library = tempNativeLibrary.getAbsolutePath();
                }

                if (tempNativeLibrary.getParentFile().exists() || tempNativeLibrary.getParentFile().mkdirs()) {
                    tempNativeLibrary.getParentFile().setReadable(true, false);
                    tempNativeLibrary.getParentFile().setWritable(true, true);
                    tempNativeLibrary.getParentFile().setExecutable(true, false);

                    for(int i = 0; !libraryLoaded && i < architectures.length; ++i) {
                        InputStream fileContents = com.fazecast.jSerialComm.SerialPort.class.getResourceAsStream("/" + libraryPath + "/" + architectures[i] + "/" + libraryFileName);
                        if (fileContents != null) {
                            try {
                                tempNativeLibrary.delete();
                                FileOutputStream destinationFileContents = new FileOutputStream(tempNativeLibrary);
                                byte[] transferBuffer = new byte[4096];

                                int numBytesRead;
                                while((numBytesRead = fileContents.read(transferBuffer)) > 0) {
                                    destinationFileContents.write(transferBuffer, 0, numBytesRead);
                                }

                                destinationFileContents.close();
                                fileContents.close();
                                tempNativeLibrary.setReadable(true, false);
                                tempNativeLibrary.setWritable(false, false);
                                tempNativeLibrary.setExecutable(true, false);
                                errorMessages.add("Loading for arch: " + architectures[i]);
                                libraryLoaded = loadNativeLibrary(tempNativeLibrary.getAbsolutePath(), errorMessages);
                                if (libraryLoaded) {
                                    errorMessages.add("Successfully loaded!");
                                }
                            } catch (Exception var17) {
                                var17.printStackTrace();
                            }
                        }
                    }

                    if (!libraryLoaded) {
                        tempNativeLibrary.delete();
                        if (attempt > 0) {
                            StringBuilder errorMessage = new StringBuilder("Cannot load native library. Errors as follows:\n");

                            for(int i = 0; i < errorMessages.size(); ++i) {
                                errorMessage.append('[').append(i + 1).append("]: ").append((String)errorMessages.get(i)).append('\n');
                            }

                            throw new UnsatisfiedLinkError(errorMessage.toString());
                        }
                    }
                }
            }

            Runtime.getRuntime().addShutdownHook(SerialPortThreadFactory.get().newThread(new Runnable() {
                public void run() {
                    try {
                        Iterator i$ = com.fazecast.jSerialComm.SerialPort.shutdownHooks.iterator();

                        while(i$.hasNext()) {
                            Thread hook = (Thread)i$.next();
                            hook.start();
                            hook.join();
                        }
                    } catch (InterruptedException var3) {
                    }

                    com.fazecast.jSerialComm.SerialPort.isShuttingDown = true;
                    com.fazecast.jSerialComm.SerialPort.uninitializeLibrary();
                }
            }));
        }

        private final class SerialPortOutputStream extends OutputStream {
            private final byte[] byteBuffer = new byte[1];

            public SerialPortOutputStream() {
            }

            public final void write(int b) throws SerialPortIOException, SerialPortTimeoutException {
                if (com.fazecast.jSerialComm.SerialPort.this.portHandle == 0L) {
                    throw new SerialPortIOException("This port appears to have been shutdown or disconnected.");
                } else {
                    this.byteBuffer[0] = (byte)(b & 255);
                    int bytesWritten = com.fazecast.jSerialComm.SerialPort.this.writeBytes(com.fazecast.jSerialComm.SerialPort.this.portHandle, this.byteBuffer, 1L, 0L, com.fazecast.jSerialComm.SerialPort.this.timeoutMode);
                    if (bytesWritten < 0) {
                        throw new SerialPortIOException("No bytes written. This port appears to have been shutdown or disconnected.");
                    } else if (bytesWritten == 0) {
                        throw new SerialPortTimeoutException("The write operation timed out before all data was written.");
                    }
                }
            }

            public final void write(byte[] b) throws NullPointerException, SerialPortIOException, SerialPortTimeoutException {
                this.write(b, 0, b.length);
            }

            public final void write(byte[] b, int off, int len) throws NullPointerException, IndexOutOfBoundsException, SerialPortIOException, SerialPortTimeoutException {
                if (b == null) {
                    throw new NullPointerException("A null pointer was passed in for the write buffer.");
                } else if (len >= 0 && off >= 0 && off + len <= b.length) {
                    int numWritten;
                    for(int totalNumWritten = 0; totalNumWritten != len; totalNumWritten += numWritten) {
                        if (com.fazecast.jSerialComm.SerialPort.this.portHandle == 0L) {
                            throw new SerialPortIOException("This port appears to have been shutdown or disconnected.");
                        }

                        numWritten = com.fazecast.jSerialComm.SerialPort.this.writeBytes(com.fazecast.jSerialComm.SerialPort.this.portHandle, b, (long)(len - totalNumWritten), (long)(off + totalNumWritten), com.fazecast.jSerialComm.SerialPort.this.timeoutMode);
                        if (numWritten < 0) {
                            throw new SerialPortIOException("No bytes written. This port appears to have been shutdown or disconnected.");
                        }

                        if (numWritten == 0) {
                            throw new SerialPortTimeoutException("The write operation timed out before all data was written.");
                        }
                    }

                } else {
                    throw new IndexOutOfBoundsException("The specified write offset plus length extends past the end of the specified buffer.");
                }
            }
        }

        private final class SerialPortInputStream extends InputStream {
            private final boolean timeoutExceptionsSuppressed;
            private final byte[] byteBuffer = new byte[1];

            public SerialPortInputStream(boolean suppressReadTimeoutExceptions) {
                this.timeoutExceptionsSuppressed = suppressReadTimeoutExceptions;
            }

            public final int available() throws SerialPortIOException {
                if (com.fazecast.jSerialComm.SerialPort.this.portHandle == 0L) {
                    throw new SerialPortIOException("This port appears to have been shutdown or disconnected.");
                } else {
                    return com.fazecast.jSerialComm.SerialPort.this.bytesAvailable();
                }
            }

            public final int read() throws SerialPortIOException, SerialPortTimeoutException {
                if (com.fazecast.jSerialComm.SerialPort.this.portHandle == 0L) {
                    throw new SerialPortIOException("This port appears to have been shutdown or disconnected.");
                } else {
                    int numRead = com.fazecast.jSerialComm.SerialPort.this.readBytes(this.byteBuffer, 1L);
                    if (numRead == 0) {
                        if (this.timeoutExceptionsSuppressed) {
                            return -1;
                        } else {
                            throw new SerialPortTimeoutException("The read operation timed out before any data was returned.");
                        }
                    } else {
                        return numRead < 0 ? -1 : this.byteBuffer[0] & 255;
                    }
                }
            }

            public final int read(byte[] b) throws NullPointerException, SerialPortIOException, SerialPortTimeoutException {
                if (b == null) {
                    throw new NullPointerException("A null pointer was passed in for the read buffer.");
                } else if (com.fazecast.jSerialComm.SerialPort.this.portHandle == 0L) {
                    throw new SerialPortIOException("This port appears to have been shutdown or disconnected.");
                } else if (b.length == 0) {
                    return 0;
                } else {
                    int numRead = com.fazecast.jSerialComm.SerialPort.this.readBytes(b, (long)b.length);
                    if (numRead == 0 && !this.timeoutExceptionsSuppressed) {
                        throw new SerialPortTimeoutException("The read operation timed out before any data was returned.");
                    } else {
                        return numRead;
                    }
                }
            }

            public final int read(byte[] b, int off, int len) throws NullPointerException, IndexOutOfBoundsException, SerialPortIOException, SerialPortTimeoutException {
                if (b == null) {
                    throw new NullPointerException("A null pointer was passed in for the read buffer.");
                } else if (len >= 0 && off >= 0 && len <= b.length - off) {
                    if (com.fazecast.jSerialComm.SerialPort.this.portHandle == 0L) {
                        throw new SerialPortIOException("This port appears to have been shutdown or disconnected.");
                    } else if (b.length != 0 && len != 0) {
                        int numRead = com.fazecast.jSerialComm.SerialPort.this.readBytes(b, (long)len, (long)off);
                        if (numRead == 0 && !this.timeoutExceptionsSuppressed) {
                            throw new SerialPortTimeoutException("The read operation timed out before any data was returned.");
                        } else {
                            return numRead;
                        }
                    } else {
                        return 0;
                    }
                } else {
                    throw new IndexOutOfBoundsException("The specified read offset plus length extends past the end of the specified buffer.");
                }
            }

            public final long skip(long n) throws SerialPortIOException {
                if (com.fazecast.jSerialComm.SerialPort.this.portHandle == 0L) {
                    throw new SerialPortIOException("This port appears to have been shutdown or disconnected.");
                } else {
                    byte[] buffer = new byte[(int)n];
                    return (long) com.fazecast.jSerialComm.SerialPort.this.readBytes(buffer, n);
                }
            }
        }

        private final class SerialPortEventListener {
            private final boolean messageEndIsDelimited;
            private final byte[] dataPacket;
            private final byte[] delimiters;
            private final ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
            private int dataPacketIndex = 0;
            private int delimiterIndex = 0;
            private Thread serialEventThread = null;

            public SerialPortEventListener() {
                this.dataPacket = new byte[0];
                this.delimiters = new byte[0];
                this.messageEndIsDelimited = true;
            }

            public SerialPortEventListener(int packetSizeToReceive) {
                this.dataPacket = new byte[packetSizeToReceive];
                this.delimiters = new byte[0];
                this.messageEndIsDelimited = true;
            }

            public SerialPortEventListener(byte[] messageDelimiters, boolean delimiterForMessageEnd) {
                this.dataPacket = new byte[0];
                this.delimiters = messageDelimiters;
                this.messageEndIsDelimited = delimiterForMessageEnd;
            }

            public final void startListening() {
                if (!com.fazecast.jSerialComm.SerialPort.this.eventListenerRunning) {
                    com.fazecast.jSerialComm.SerialPort.this.eventListenerRunning = true;
                    this.dataPacketIndex = 0;
                    com.fazecast.jSerialComm.SerialPort.this.setEventListeningStatus(com.fazecast.jSerialComm.SerialPort.this.portHandle, true);
                    this.serialEventThread = SerialPortThreadFactory.get().newThread(new Runnable() {
                        public void run() {
                            while(com.fazecast.jSerialComm.SerialPort.this.eventListenerRunning && !com.fazecast.jSerialComm.SerialPort.isShuttingDown) {
                                try {
                                    com.fazecast.jSerialComm.SerialPort.SerialPortEventListener.this.waitForSerialEvent();
                                } catch (Exception var2) {
                                    com.fazecast.jSerialComm.SerialPort.this.eventListenerRunning = false;
                                    if (com.fazecast.jSerialComm.SerialPort.this.userDataListener instanceof SerialPortDataListenerWithExceptions) {
                                        ((SerialPortDataListenerWithExceptions) com.fazecast.jSerialComm.SerialPort.this.userDataListener).catchException(var2);
                                    } else if (com.fazecast.jSerialComm.SerialPort.this.userDataListener instanceof SerialPortMessageListenerWithExceptions) {
                                        ((SerialPortMessageListenerWithExceptions) com.fazecast.jSerialComm.SerialPort.this.userDataListener).catchException(var2);
                                    }
                                }
                            }

                        }
                    });
                    this.serialEventThread.start();
                }
            }

            public final void stopListening() {
                if (com.fazecast.jSerialComm.SerialPort.this.eventListenerRunning) {
                    com.fazecast.jSerialComm.SerialPort.this.eventListenerRunning = false;
                    com.fazecast.jSerialComm.SerialPort.this.configTimeouts(com.fazecast.jSerialComm.SerialPort.this.portHandle, 0, 0, 0, 0);
                    com.fazecast.jSerialComm.SerialPort.this.setEventListeningStatus(com.fazecast.jSerialComm.SerialPort.this.portHandle, false);

                    try {
                        if (!Thread.currentThread().equals(this.serialEventThread)) {
                            do {
                                this.serialEventThread.join(500L);
                                if (this.serialEventThread.isAlive()) {
                                    this.serialEventThread.interrupt();
                                }
                            } while(this.serialEventThread.isAlive());
                        }
                    } catch (InterruptedException var2) {
                        Thread.currentThread().interrupt();
                    }

                    this.serialEventThread = null;
                }
            }

            public final void waitForSerialEvent() throws Exception {
                int event = com.fazecast.jSerialComm.SerialPort.this.waitForEvent(com.fazecast.jSerialComm.SerialPort.this.portHandle) & com.fazecast.jSerialComm.SerialPort.this.eventFlags;
                if ((event & 1) > 0 && (com.fazecast.jSerialComm.SerialPort.this.eventFlags & 16) > 0) {
                    event &= -18;

                    label94:
                    while(true) {
                        while(true) {
                            int bytesRemaining;
                            int newBytesIndex;
                            byte[] newBytes;
                            do {
                                int numBytesAvailable;
                                if (!com.fazecast.jSerialComm.SerialPort.this.eventListenerRunning || (numBytesAvailable = com.fazecast.jSerialComm.SerialPort.this.bytesAvailable()) <= 0) {
                                    break label94;
                                }

                                newBytesIndex = 0;
                                newBytes = new byte[numBytesAvailable];
                                bytesRemaining = com.fazecast.jSerialComm.SerialPort.this.readBytes(newBytes, (long)newBytes.length);
                            } while(bytesRemaining <= 0);

                            if (this.delimiters.length > 0) {
                                int startIndex = 0;

                                for(int offset = 0; offset < bytesRemaining; ++offset) {
                                    if (newBytes[offset] == this.delimiters[this.delimiterIndex]) {
                                        if (++this.delimiterIndex == this.delimiters.length) {
                                            this.messageBytes.write(newBytes, startIndex, 1 + offset - startIndex);
                                            byte[] byteArray = this.messageEndIsDelimited ? this.messageBytes.toByteArray() : Arrays.copyOf(this.messageBytes.toByteArray(), this.messageBytes.size() - this.delimiters.length);
                                            if (byteArray.length > 0 && (this.messageEndIsDelimited || this.delimiters[0] == byteArray[0])) {
                                                com.fazecast.jSerialComm.SerialPort.this.userDataListener.serialEvent(new SerialPortEvent(com.fazecast.jSerialComm.SerialPort.this, 16, byteArray));
                                            }

                                            startIndex = offset + 1;
                                            this.messageBytes.reset();
                                            this.delimiterIndex = 0;
                                            if (!this.messageEndIsDelimited) {
                                                this.messageBytes.write(this.delimiters, 0, this.delimiters.length);
                                            }
                                        }
                                    } else if (this.delimiterIndex != 0) {
                                        this.delimiterIndex = newBytes[offset] == this.delimiters[0] ? 1 : 0;
                                    }
                                }

                                this.messageBytes.write(newBytes, startIndex, bytesRemaining - startIndex);
                            } else if (this.dataPacket.length == 0) {
                                com.fazecast.jSerialComm.SerialPort.this.userDataListener.serialEvent(new SerialPortEvent(com.fazecast.jSerialComm.SerialPort.this, 16, (byte[])newBytes.clone()));
                            } else {
                                while(bytesRemaining >= this.dataPacket.length - this.dataPacketIndex) {
                                    System.arraycopy(newBytes, newBytesIndex, this.dataPacket, this.dataPacketIndex, this.dataPacket.length - this.dataPacketIndex);
                                    bytesRemaining -= this.dataPacket.length - this.dataPacketIndex;
                                    newBytesIndex += this.dataPacket.length - this.dataPacketIndex;
                                    this.dataPacketIndex = 0;
                                    com.fazecast.jSerialComm.SerialPort.this.userDataListener.serialEvent(new SerialPortEvent(com.fazecast.jSerialComm.SerialPort.this, 16, (byte[])this.dataPacket.clone()));
                                }

                                if (bytesRemaining > 0) {
                                    System.arraycopy(newBytes, newBytesIndex, this.dataPacket, this.dataPacketIndex, bytesRemaining);
                                    this.dataPacketIndex += bytesRemaining;
                                }
                            }
                        }
                    }
                }

                if (com.fazecast.jSerialComm.SerialPort.this.eventListenerRunning && !com.fazecast.jSerialComm.SerialPort.isShuttingDown && event != 0) {
                    com.fazecast.jSerialComm.SerialPort.this.userDataListener.serialEvent(new SerialPortEvent(com.fazecast.jSerialComm.SerialPort.this, event));
                }

            }
        }
    }**/

}
