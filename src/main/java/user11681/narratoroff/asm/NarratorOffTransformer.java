package user11681.narratoroff.asm;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import net.gudenau.lib.unsafe.Unsafe;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class NarratorOffTransformer implements IMixinConfigPlugin {
    @Override
    public void onLoad(final String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(final String targetClassName, final String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(final Set<String> myTargets, final Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {}

    @Override
    public void postApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {}

    static {
        try {
            final ClassNode klass = new ClassNode();
            new ClassReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/mojang/text2speech/Narrator.class")).accept(klass, 0);

            final List<MethodNode> methods = klass.methods;
            MethodNode method;

            for (int i = 0, size = methods.size(); i < size; i++) {
                if (methods.get(i).name.equals("getNarrator")) {
                    method = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "getNarrator", "()Lcom/mojang/text2speech/Narrator;", null, null);

                    method.visitTypeInsn(Opcodes.NEW, "com/mojang/text2speech/NarratorDummy");
                    method.visitInsn(Opcodes.DUP);
                    method.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/mojang/text2speech/NarratorDummy", "<init>", "()V", false);
                    method.visitInsn(Opcodes.ARETURN);

                    klass.methods.set(i, method);

                    break;
                }
            }

            final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

            klass.accept(writer);

            final byte[] bytecode = writer.toByteArray();
            Unsafe.defineClass("com.mojang.text2speech.Narrator", bytecode, 0, bytecode.length, ClassLoader.getSystemClassLoader(), null);
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
