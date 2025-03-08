package gg.damia.mixbukkit.api.shellcode.impl.api;

import gg.damia.mixbukkit.api.shellcode.LocalVarManager;
import gg.damia.mixbukkit.api.shellcode.ShellCode;
import gg.damia.mixbukkit.api.shellcode.ShellCodeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.objectweb.asm.tree.*;

import static javassist.bytecode.Opcode.GETSTATIC;
import static javassist.bytecode.Opcode.INVOKEVIRTUAL;

@AllArgsConstructor
@Getter
@ShellCodeInfo(
        name = "Print Type",
        description = "System.out the full class name of the content that's on the top of the stack",
        requireVarManager = false,
        stacksContent = {},
        requiredStacksContent = {},
        calledDirectly = true
)
public class ShellCodePrintTopStackType extends ShellCode {

    private String message;


    @Override
    public InsnList generate(MethodNode methodNode, LocalVarManager varManager) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        list.add(new LdcInsnNode(message));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
        return list;
    }
}
