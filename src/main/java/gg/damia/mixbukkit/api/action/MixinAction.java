package gg.damia.mixbukkit.api.action;

import org.objectweb.asm.tree.MethodNode;

public interface MixinAction {

    void action(Class<?> owner, MethodNode method);

}
