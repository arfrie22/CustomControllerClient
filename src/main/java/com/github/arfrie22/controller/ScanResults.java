package com.github.arfrie22.controller;

import com.fazecast.jSerialComm.SerialPort;

public class ScanResults {
    private final CustomController[] validControllers;
    private final SerialPort[] validPorts;
    private final SerialPort[] invalidPorts;
    private final SerialPort[] allPorts;

    protected ScanResults(SerialPort[] validPorts, SerialPort[] invalidPorts) throws InterruptedException {
        this.validPorts = validPorts;
        this.invalidPorts = invalidPorts;
        this.validControllers = new CustomController[validPorts.length];

        for (int i = 0; i < validPorts.length; i++) {
            validControllers[i] = new CustomController(validPorts[i]);
        }

        this.allPorts = new SerialPort[validPorts.length + invalidPorts.length];
        System.arraycopy(validPorts, 0, allPorts, 0, validPorts.length);
        System.arraycopy(invalidPorts, 0, allPorts, validPorts.length, invalidPorts.length);
    }

    public SerialPort[] getValidPorts() {
        return validPorts;
    }

    public SerialPort[] getInvalidPorts() {
        return invalidPorts;
    }

    public CustomController[] getValidControllers() {
        return validControllers;
    }

    public SerialPort[] getAllPorts() {
        return allPorts;
    }
}
