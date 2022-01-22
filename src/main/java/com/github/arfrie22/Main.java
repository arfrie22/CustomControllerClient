package com.github.arfrie22;

import com.fazecast.jSerialComm.SerialPort;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();


        inst.startClientTeam(467);
        inst.startDSClient();
        inst.startClient("localhost");
        NetworkTable controllerTable = inst.getTable("controller");

        NetworkTableEntry robotConnectedEntry = controllerTable.getEntry("robotConnected"); // bool
        NetworkTableEntry clientConnectedEntry = controllerTable.getEntry("clientConnected"); // bool

        NetworkTableEntry commandEntry = controllerTable.getEntry("command"); // raw bytes
        NetworkTableEntry hasCommandEntry = controllerTable.getEntry("hasCommand"); // bool
        NetworkTableEntry responseEntry = controllerTable.getEntry("response"); // raw bytes
        NetworkTableEntry hasResponseEntry = controllerTable.getEntry("hasResponse"); // bool

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Thread.sleep(200);
                clientConnectedEntry.setBoolean(false);
                Thread.sleep(200);
                System.out.println("Shutting down...");
                inst.close();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }));

        try {
            // inst.wait(100);
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            System.out.println("interrupted");
            return;
        }

        System.out.println(inst.isConnected());

        clientConnectedEntry.setBoolean(true);
        SerialPort controllerPort = null;

        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            if (port.getPortDescription().equals("Controller CDC")) {
                port.openPort();
                byte[] buf = new byte[64];
                port.writeBytes(new byte[]{(byte) 0x01}, 1);
                Thread.sleep(100);
                port.readBytes(buf, 64, 0);
                int protocolVersion = (Byte.toUnsignedInt(buf[1]) << 8) | Byte.toUnsignedInt(buf[0]);
                System.out.println("Protocol version: " + protocolVersion);

                port.writeBytes(new byte[]{(byte) 0xFD}, 1);
                Thread.sleep(100);
                port.readBytes(buf, 64, 0);
                int teamNumber = (Byte.toUnsignedInt(buf[1]) << 24) | (Byte.toUnsignedInt(buf[2]) << 16) | (Byte.toUnsignedInt(buf[3]) << 8) | Byte.toUnsignedInt(buf[4]);
                System.out.println("Team number: " + teamNumber);

                port.closePort();
                controllerPort = port;
            }
        }

        commandEntry.setRaw(new byte[]{0x05, 0x03, 0x00, 0x03, 0x03});
        hasCommandEntry.setBoolean(true);

        if (controllerPort != null) {
            controllerPort.openPort();
            NTSerialDataListener ntSerialDataListener = new NTSerialDataListener(responseEntry, hasResponseEntry);
            controllerPort.addDataListener(ntSerialDataListener);
            while (ntSerialDataListener.isConnected()) {
                if (hasCommandEntry.getBoolean(false)) {
                    hasCommandEntry.setBoolean(false);
                    byte[] data = commandEntry.getRaw(new byte[0]);
                    controllerPort.writeBytes(data, data.length);
                }
            }
            controllerPort.removeDataListener();
            controllerPort.closePort();
        }
    }
}