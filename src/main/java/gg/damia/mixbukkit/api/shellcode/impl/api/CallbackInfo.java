package gg.damia.mixbukkit.api.shellcode.impl.api;

import gg.damia.mixbukkit.api.shellcode.LocalVarManager;
import gg.damia.mixbukkit.api.shellcode.impl.inner.IShellCodeLoadClassFromPCL;
import gg.damia.mixbukkit.api.shellcode.impl.inner.IShellCodeMethodInvoke;
import gg.damia.mixbukkit.api.shellcode.impl.inner.IShellCodeReflectionMethodInvoke;
import gg.damia.mixbukkit.utils.ASMUtils;
import javassist.bytecode.Opcode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.objectweb.asm.tree.*;

@Setter
@Getter
public class CallbackInfo {

    private Object returnValue = null;
    private boolean returned;

    @SneakyThrows
    public static InsnList generateCallBackInfo() {
        InsnList out = new InsnList();
        out.add(new IShellCodeLoadClassFromPCL(CallbackInfo.class).generate());
        out.add(new IShellCodeMethodInvoke(Class.class.getDeclaredMethod("newInstance")).generate());
        return out;
    }

    @SneakyThrows
    public static InsnList processCallBackInfo(MethodNode hookedMethod, LocalVarManager varManager, int varLocation) {
        InsnList out = new InsnList();
        LabelNode returnBranch = new LabelNode();
        LabelNode defaultBranch = new LabelNode();

        out.add(new VarInsnNode(Opcode.ALOAD, varLocation));
        out.add(new IShellCodeReflectionMethodInvoke(CallbackInfo.class.getDeclaredMethod("isReturned")).generate(null, varManager));
        out.add(new TypeInsnNode(Opcode.CHECKCAST, Boolean.class.getName().replace(".", "/")));
        out.add(new IShellCodeMethodInvoke(Boolean.class.getDeclaredMethod("booleanValue")).generate());
        out.add(new JumpInsnNode(Opcode.IFEQ, defaultBranch));
        out.add(returnBranch);
        out.add(new VarInsnNode(Opcode.ALOAD, varLocation));
        out.add(new IShellCodeReflectionMethodInvoke(CallbackInfo.class.getDeclaredMethod("getReturnValue")).generate(null, varManager));
        if (!hookedMethod.desc.endsWith("V")) {
            Class<?> returnType = ASMUtils.getReturnType(hookedMethod.desc);
            out.add(ASMUtils.cast(returnType));
            out.add(ASMUtils.genReturnNode(returnType));
        } else {
            out.add(new InsnNode(Opcode.RETURN));
        }

        out.add(defaultBranch);
        return out;
    }

}