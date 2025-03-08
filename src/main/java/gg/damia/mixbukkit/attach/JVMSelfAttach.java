package gg.damia.mixbukkit.attach;

import com.sun.tools.attach.VirtualMachine;
import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandles;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JVMSelfAttach {
    private static Instrumentation instrumentation;

    /**
     * Initializes self-attachment.
     * @param tmp a temporary directory to store the generated agent JAR.
     */
    public static synchronized void init(File tmp) {
        if (instrumentation != null) return;
        try {
            defineAgentClass();
            init0(tmp);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to initialize JVMSelfAttach", t);
        }
    }

    /**
     * Returns the Instrumentation obtained from the agent.
     */
    public static Instrumentation getInstrumentation() {
        if (instrumentation == null) {
            throw new IllegalStateException("Instrumentation not initialized. Call JVMSelfAttach.init() first.");
        }
        return instrumentation;
    }

    private static String getPID() {
        try {
            return Long.toString(ProcessHandle.current().pid());
        } catch (Throwable t) {
            String jvmName = ManagementFactory.getRuntimeMXBean().getName();
            return jvmName.split("@")[0];
        }
    }

    /**
     * Dynamically defines the ExternalAgent class using defineClass.
     */
    private static void defineAgentClass() throws Exception {
        String agentClassResource = "gg/damia/mixbukkit/attach/ExternalAgent.class";
        try (InputStream in = JVMSelfAttach.class.getClassLoader().getResourceAsStream(agentClassResource)) {
            if (in == null) {
                throw new FileNotFoundException("Could not find resource: " + agentClassResource);
            }
            byte[] classBytes = in.readAllBytes();
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            lookup.defineClass(classBytes);
        }
    }

    /**
     * Generates a temporary agent JAR with a manifest that points to the agent class.
     */
    private static File genAgentJar(File dir, String agentClassName) throws IOException {
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Cannot create directory: " + dir);
        }
        File jarFile = new File(dir, "external-agent-" + System.currentTimeMillis() + ".jar");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(jarFile))) {
            zos.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
            manifest.getMainAttributes().putValue("Agent-Class", agentClassName);
            manifest.getMainAttributes().putValue("Can-Redefine-Classes", "true");
            manifest.getMainAttributes().putValue("Can-Retransform-Classes", "true");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            manifest.write(baos);
            zos.write(baos.toByteArray());
            zos.closeEntry();

            String agentClassPath = agentClassName.replace('.', '/') + ".class";
            try (InputStream in = JVMSelfAttach.class.getClassLoader().getResourceAsStream(agentClassPath)) {
                if (in == null) {
                    throw new FileNotFoundException("Could not find resource: " + agentClassPath);
                }
                zos.putNextEntry(new ZipEntry(agentClassPath));
                byte[] buffer = new byte[4096];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
            }
        }
        return jarFile;
    }


    private static void init0(File tmp) throws Exception {
        String agentClassName = "gg.damia.mixbukkit.attach.ExternalAgent";
        File agentJar = genAgentJar(tmp, agentClassName);
        loadAgent(agentJar.getAbsolutePath());
        fetchInstrumentation(agentClassName);
    }

    /**
     * Loads the agent JAR by attaching to the current JVM.
     */
    private static void loadAgent(String agentJarPath) throws Exception {
        System.setProperty("jdk.attach.allowAttachSelf", "true");
        String pid = getPID();
        VirtualMachine vm = VirtualMachine.attach(pid);
        vm.loadAgent(agentJarPath);
        vm.detach();
    }

    /**
     * Reflectively fetches the instrumentation from the agent class.
     */
    private static void fetchInstrumentation(String agentClassName) throws Exception {
        Class<?> agentClass = Class.forName(agentClassName, true, ClassLoader.getSystemClassLoader());
        Field instField = agentClass.getDeclaredField("instrumentation");
        instField.setAccessible(true);
        instrumentation = (Instrumentation) instField.get(null);
        if (instrumentation == null) {
            throw new IllegalStateException("Failed to obtain instrumentation from agent");
        }
    }

    public static void main(String[] args) {
        File tmpDir = new File("build/jsa");
        init(tmpDir);
        System.out.println("Instrumentation obtained: " + instrumentation);
    }
}