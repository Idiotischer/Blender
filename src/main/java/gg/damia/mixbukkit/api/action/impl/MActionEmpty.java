package gg.damia.mixbukkit.api.action.impl;

import gg.damia.mixbukkit.api.action.MixinAction;
import org.objectweb.asm.tree.MethodNode;

public class MActionEmpty implements MixinAction {
    @Override
    public void action(Class<?> owner, MethodNode method) {}
}
