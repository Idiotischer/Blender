package gg.damia.mixbukkit.utils.io;

import java.io.*;

public class BukkitErrorOutputStream extends OutputStream {
    @Override
    public void write(int b) {
        if (b == '\n') {
            System.err.print("\u001B[0m");
        }
        System.err.write(b);
        if (b == '\n') {
            System.err.print("\u001B[31m");
        }
    }
}
