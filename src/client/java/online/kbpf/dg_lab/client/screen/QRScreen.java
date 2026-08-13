package online.kbpf.dg_lab.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class QRScreen extends Screen {
    private final Screen parent;
    private final Identifier qrIdentifier;

    public QRScreen(Screen parent, Identifier qrIdentifier) {
        super(Text.literal("扫描二维码连接设备"));
        this.parent = parent;
        this.qrIdentifier = qrIdentifier;
    }

    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("返回"), button -> {
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 100, this.height / 2 + 105, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 110, 0xFFFFFF);
        
        if (this.qrIdentifier != null) {
            // 在屏幕中央绘制 180x180 大小的完整二维码（全比例无缝拉伸渲染，无大白边）
            int renderSize = 180;
            context.drawTexture(
                this.qrIdentifier,
                this.width / 2 - renderSize / 2,
                this.height / 2 - 95,
                renderSize, renderSize,
                0.0F, 0.0F,
                1, 1,
                1, 1
            );
        } else {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("二维码生成失败"), this.width / 2, this.height / 2, 0xFF5555);
        }
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
