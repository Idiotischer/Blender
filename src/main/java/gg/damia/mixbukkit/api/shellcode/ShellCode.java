package gg.damia.mixbukkit.api.shellcode;

import gg.damia.mixbukkit.api.action.impl.MActionCallSuper;
import gg.damia.mixbukkit.api.action.impl.MActionMethodReplacer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;

public abstract class ShellCode implements IShellCode {

    public ShellCode() {
        if (getShellCodeInfo() == null) throw new NullPointerException("Shellcode: " + this.getClass().getName() + " is invalid! @ShellCodeInfo annotation is not presented.");
    }

    public InsnList generate() {
        return generate(null, null);
    }

    public InsnList popExtraStack() {
        InsnList list = new InsnList();
        for (int i = 0; i < getShellCodeInfo().stacksContent().length; i++) {
            list.add(new InsnNode(Opcodes.POP));
        }
        return list;
    }

    public ShellCodeInfo getShellCodeInfo() {
        try {
            return getClass().getAnnotationsByType(ShellCodeInfo.class)[0];
        } catch (Exception ignored) {
            return null;
        }
    }

}
