package gg.damia.mixbukkit.api.locator.impl;

import gg.damia.mixbukkit.api.locator.HookLocator;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.util.ArrayList;
import java.util.List;

public class HLocatorTail implements HookLocator {
    @Override
    public List<Integer> getLineNumber(InsnList insnList) {
        List<Integer> list = new ArrayList<>();

        AbstractInsnNode insnNode = insnList.getLast();
        if (insnNode.getOpcode() >= 172 && insnNode.getOpcode() <= 177) {
            list.add(insnList.size() - 1);
        }

        return list;
    }
}
