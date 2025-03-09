package gg.damia.mixbukkit.attach;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.io.InputStream;
import java.util.Base64;

public class ExternalAgentBytecodeDump {
    public static void main(String[] args) throws Throwable {
        try (InputStream code = ExternalAgentBytecodeDump.class.getResourceAsStream("ExternalAgent.class")) {
            if (code == null) {
                throw new IllegalArgumentException("ExternalAgent.class not found in resources");
            }

            ClassNode cn = new ClassNode();
            new ClassReader(code).accept(cn, 0);

            cn.version = Opcodes.V1_8;
            cn.module = null;
            cn.innerClasses.clear();
            cn.nestHostClass = null;
            cn.nestMembers = null;
            cn.permittedSubclasses = null;
            cn.outerClass = null;
            cn.outerMethod = null;
            cn.outerMethodDesc = null;

            byte[] bc;
            {
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                cn.accept(cw);
                bc = cw.toByteArray();
            }

            String str = Base64.getEncoder().encodeToString(bc);
            System.out.println("\"" + str.replace("\n", "") + "\"");
        }
    }
}
