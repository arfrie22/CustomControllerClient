package com.github.arfrie22;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.Arrays;

public class NTSerialDataListener implements SerialPortDataListener {
    private boolean isConnected = true;
    NetworkTableEntry responseEntry; // raw bytes
    NetworkTableEntry hasResponseEntry; // bool

    public NTSerialDataListener(NetworkTableEntry responseEntry, NetworkTableEntry hasResponseEntry) {
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
                responseEntry.setRaw(serialPortEvent.getReceivedData());
                hasResponseEntry.setBoolean(true);
                break;

            case SerialPort.LISTENING_EVENT_PORT_DISCONNECTED:
                isConnected = false;
                break;

            default:
                    break;
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
