package gg.damia.mixbukkit.api.shellcode.impl.api;

import gg.damia.mixbukkit.api.shellcode.LocalVarManager;
import gg.damia.mixbukkit.api.shellcode.ShellCode;
import gg.damia.mixbukkit.api.shellcode.ShellCodeInfo;
import gg.damia.mixbukkit.utils.ASMUtils;
import javassist.bytecode.Opcode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

@ShellCodeInfo(
        name = "Comment",
        description = "Leave a comment, and it won't do anything other than wasting performance",
        calledDirectly = true
)
@RequiredArgsConstructor
public class ShellCodeComment extends ShellCode {

    @NonNull
    private String comment;

    @Override
    public InsnList generate(MethodNode methodNode, LocalVarManager varManager) {
        return ASMUtils.asInsnList(
                new LdcInsnNode(comment),
                new InsnNode(Opcode.POP)
        );
    }
}
