package gg.damia.mixbukkit.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.TraceClassVisitor;

public abstract class CustomPrinter extends Printer {
    protected final int api;
    protected final StringBuilder stringBuilder;
    public final List<Object> text;

    protected CustomPrinter(int api) {
        super(api);
        this.api = api;
        this.stringBuilder = new StringBuilder();
        this.text = new ArrayList<>();
    }

    public abstract void visit(int var1, int var2, String var3, String var4, String var5, String[] var6);

    public abstract void visitSource(String var1, String var2);

    public CustomPrinter visitModule(String name, int access, String version) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public void visitNestHost(String nestHost) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public abstract void visitOuterClass(String var1, String var2, String var3);

    public abstract CustomPrinter visitClassAnnotation(String var1, boolean var2);

    public CustomPrinter visitClassTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public abstract void visitClassAttribute(Attribute var1);

    public void visitNestMember(String nestMember) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public void visitPermittedSubclass(String permittedSubclass) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public abstract void visitInnerClass(String var1, String var2, String var3, int var4);

    public CustomPrinter visitRecordComponent(String name, String descriptor, String signature) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public abstract CustomPrinter visitField(int var1, String var2, String var3, String var4, Object var5);

    public abstract CustomPrinter visitMethod(int var1, String var2, String var3, String var4, String[] var5);

    public abstract void visitClassEnd();

    public void visitMainClass(String mainClass) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public void visitPackage(String packaze) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public void visitRequire(String module, int access, String version) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public void visitExport(String packaze, int access, String... modules) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public void visitOpen(String packaze, int access, String... modules) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public void visitUse(String service) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public void visitProvide(String service, String... providers) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public void visitModuleEnd() {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public abstract void visit(String var1, Object var2);

    public abstract void visitEnum(String var1, String var2, String var3);

    public abstract CustomPrinter visitAnnotation(String var1, String var2);

    public abstract CustomPrinter visitArray(String var1);

    public abstract void visitAnnotationEnd();

    public CustomPrinter visitRecordComponentAnnotation(String descriptor, boolean visible) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public CustomPrinter visitRecordComponentTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public void visitRecordComponentAttribute(Attribute attribute) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public void visitRecordComponentEnd() {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public abstract CustomPrinter visitFieldAnnotation(String var1, boolean var2);

    public CustomPrinter visitFieldTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public abstract void visitFieldAttribute(Attribute var1);

    public abstract void visitFieldEnd();

    public void visitParameter(String name, int access) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public abstract CustomPrinter visitAnnotationDefault();

    public abstract CustomPrinter visitMethodAnnotation(String var1, boolean var2);

    public CustomPrinter visitMethodTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public CustomPrinter visitAnnotableParameterCount(int parameterCount, boolean visible) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public abstract CustomPrinter visitParameterAnnotation(int var1, String var2, boolean var3);

    public abstract void visitMethodAttribute(Attribute var1);

    public abstract void visitCode();

    public abstract void visitFrame(int var1, int var2, Object[] var3, int var4, Object[] var5);

    public abstract void visitInsn(int var1);

    public abstract void visitIntInsn(int var1, int var2);

    public abstract void visitVarInsn(int var1, int var2);

    public abstract void visitTypeInsn(int var1, String var2);

    public abstract void visitFieldInsn(int var1, String var2, String var3, String var4);

    /** @deprecated */
    @Deprecated
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor) {
        this.visitMethodInsn(opcode, owner, name, descriptor, opcode == 185);
    }

    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public abstract void visitInvokeDynamicInsn(String var1, String var2, Handle var3, Object... var4);

    public abstract void visitJumpInsn(int var1, Label var2);

    public abstract void visitLabel(Label var1);

    public abstract void visitLdcInsn(Object var1);

    public abstract void visitIincInsn(int var1, int var2);

    public abstract void visitTableSwitchInsn(int var1, int var2, Label var3, Label... var4);

    public abstract void visitLookupSwitchInsn(Label var1, int[] var2, Label[] var3);

    public abstract void visitMultiANewArrayInsn(String var1, int var2);

    public CustomPrinter visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public abstract void visitTryCatchBlock(Label var1, Label var2, Label var3, String var4);

    public CustomPrinter visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public abstract void visitLocalVariable(String var1, String var2, String var3, Label var4, Label var5, int var6);

    public CustomPrinter visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
        throw new UnsupportedOperationException("Must be overridden");
    }

    public abstract void visitLineNumber(int var1, Label var2);

    public abstract void visitMaxs(int var1, int var2);

    public abstract void visitMethodEnd();

    public List<Object> getText() {
        return this.text;
    }

    public void print(PrintWriter printWriter) {
        printList(printWriter, this.text);
    }

    static void printList(PrintWriter printWriter, List<?> list) {
        Iterator var2 = list.iterator();

        while(var2.hasNext()) {
            Object o = var2.next();
            if (o instanceof List) {
                printList(printWriter, (List)o);
            } else {
                printWriter.print(o.toString());
            }
        }

    }

    public static void appendString(StringBuilder stringBuilder, String string) {
        stringBuilder.append('"');

        for(int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c == '\n') {
                stringBuilder.append("\\n");
            } else if (c == '\r') {
                stringBuilder.append("\\r");
            } else if (c == '\\') {
                stringBuilder.append("\\\\");
            } else if (c == '"') {
                stringBuilder.append("\\\"");
            } else if (c >= ' ' && c <= 127) {
                stringBuilder.append(c);
            } else {
                stringBuilder.append("\\u");
                if (c < 16) {
                    stringBuilder.append("000");
                } else if (c < 256) {
                    stringBuilder.append("00");
                } else if (c < 4096) {
                    stringBuilder.append('0');
                }

                stringBuilder.append(Integer.toString(c, 16));
            }
        }

        stringBuilder.append('"');
    }

    static void main(String[] args, String usage, CustomPrinter printer, PrintWriter output, PrintWriter logger) throws IOException {
        if (args.length >= 1 && args.length <= 2 && (!args[0].equals("-debug") && !args[0].equals("-nodebug") || args.length == 2)) {
            TraceClassVisitor traceClassVisitor = new TraceClassVisitor((ClassVisitor)null, printer, output);
            String className;
            byte parsingOptions;
            if (args[0].equals("-nodebug")) {
                className = args[1];
                parsingOptions = 2;
            } else {
                className = args[0];
                parsingOptions = 0;
            }

            if (!className.endsWith(".class") && className.indexOf(92) == -1 && className.indexOf(47) == -1) {
                (new ClassReader(className)).accept(traceClassVisitor, parsingOptions);
            } else {
                FileInputStream inputStream = new FileInputStream(className);

                try {
                    (new ClassReader(inputStream)).accept(traceClassVisitor, parsingOptions);
                } catch (Throwable var12) {
                    try {
                        inputStream.close();
                    } catch (Throwable var11) {
                    }

                    throw var12;
                }

                inputStream.close();
            }

        } else {
            logger.println(usage);
        }
    }
}
