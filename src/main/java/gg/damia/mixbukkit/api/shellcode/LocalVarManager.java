package gg.damia.mixbukkit.api.shellcode;

import gg.damia.mixbukkit.utils.ASMUtils;
import lombok.Getter;
import org.objectweb.asm.tree.MethodNode;

public class LocalVarManager {

    @Getter
    private final MethodNode methodNode;

    private int latestVarNumber;

    public LocalVarManager(MethodNode methodNode) {
        this.methodNode = methodNode;
        latestVarNumber = ASMUtils.getLatestVarNumber(methodNode.instructions) + 1;
    }

    public int allocateVarNumber() {
        return latestVarNumber++;
    }

    public int getLatestUnusedVarNumber() {
        return latestVarNumber;
    }

}
