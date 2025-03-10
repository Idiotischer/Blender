package gg.damia.mixbukkit.api.locator.impl;

import gg.damia.mixbukkit.api.locator.HookLocator;
import gg.damia.mixbukkit.utils.ASMUtils;
import gg.damia.mixbukkit.utils.PostPreState;
import javassist.bytecode.Opcode;
import lombok.AllArgsConstructor;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@AllArgsConstructor
public class HLocatorFieldRead implements HookLocator {

    private Field field;
    private PostPreState state;
    private Predicate<Integer> filter;

    @Override
    public List<Integer> getLineNumber(InsnList insnList) {
        List<Integer> out = new ArrayList<>();
        int amount = 0;
        for (int i = 0; i < insnList.size(); i++) {
            if (insnList.get(i) instanceof FieldInsnNode) {
                FieldInsnNode fieldInsnNode = (FieldInsnNode) insnList.get(i);
                String owner = field.getDeclaringClass().getName().replace(".", "/");
                String name = field.getName();
                String desc = ASMUtils.toDescriptorTypeName(field.getType().getName());
                if (fieldInsnNode.owner.equals(owner) && fieldInsnNode.name.equals(name) && fieldInsnNode.desc.equals(desc) && filter.test(amount++)) {
                    if (fieldInsnNode.getOpcode() == Opcode.GETFIELD || fieldInsnNode.getOpcode() == Opcode.GETSTATIC)
                        out.add(i + (state == PostPreState.POST?1:0));
                }
            }
        }
        return out;
    }
}
