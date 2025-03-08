package gg.damia.mixbukkit.agent;

import com.sun.tools.attach.VirtualMachine;
import gg.damia.mixbukkit.attach.JVMSelfAttach;
import gg.damia.mixbukkit.MixBukkit;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.management.ManagementFactory;

public class JVMAttacher {

    private MixBukkit mixBukkit;

    public JVMAttacher(MixBukkit mixBukkit) {
        this.mixBukkit = mixBukkit;
    }

    @SneakyThrows
    public void attach() {
        JVMSelfAttach.init(new File(System.getProperty("java.io.tmpdir")));
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        VirtualMachine vm = VirtualMachine.attach(pid);

        System.out.println("Self-attached to process: " + pid);
        vm.detach();

        MixBukkit.INSTRUMENTATION = JVMSelfAttach.getInstrumentation();

    }

    public int getCurrentPID() {
        return Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }

}
