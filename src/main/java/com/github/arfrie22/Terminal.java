package com.github.arfrie22;

import jexer.TApplication;
import jexer.TMessageBox;

import java.io.UnsupportedEncodingException;

public class Terminal extends TApplication {
    public Terminal() throws UnsupportedEncodingException {
        super(BackendType.SWING);
        addToolMenu();
        addFileMenu();
        addWindowMenu();

    }

    @Override
    public void run() {
        super.run();
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        Terminal app = new Terminal();
        (new Thread(app)).start();

        app.invokeLater(() -> app.messageBox("no balls", "vine boom", TMessageBox.Type.YESNOCANCEL));
    }
}
