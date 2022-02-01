package com.github.arfrie22;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.Arrays;

public class NTSerialDataListener implements SerialPortDataListener {
    private boolean isConnected = true;
    private final CustomController controller;
    private final NetworkTableEntry responseEntry;
    private final NetworkTableEntry hasResponseEntry;

    public NTSerialDataListener(CustomController controller, NetworkTableEntry responseEntry, NetworkTableEntry hasResponseEntry) {
        this.controller = controller;
        this.responseEntry = responseEntry;
        this.hasResponseEntry = hasResponseEntry;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED | SerialPort.LISTENING_EVENT_PORT_DISCONNECTED;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        switch (serialPortEvent.getEventType()) {
            case SerialPort.LISTENING_EVENT_DATA_RECEIVED:
                this.responseEntry.setRaw(serialPortEvent.getReceivedData());
                this.hasResponseEntry.setBoolean(true);
                break;

            case SerialPort.LISTENING_EVENT_PORT_DISCONNECTED:
                isConnected = false;
                controller.close();
                break;

            default:
                    break;
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
