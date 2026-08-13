package online.kbpf.dg_lab.client.createQR;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import online.kbpf.dg_lab.client.Dg_labClient;
import online.kbpf.dg_lab.client.Config.ModConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ToolQR {
    private ToolQR() {
    }

    public static Identifier CreateQR() {
        ModConfig modConfig = Dg_labClient.modConfig;
        String ipAddress = modConfig.getAddress();
        if(ipAddress.equals("error")) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal("没有指定的ip地址").withColor(0xFF5555), false);
            }
            return null;
        }

        int port = modConfig.getPort();
        StringBuilder url = new StringBuilder("https://www.dungeon-lab.com/app-download.php#DGLAB-SOCKET#ws://").append(ipAddress).append(':').append(port).append("/1234-123456789-12345-12345-01");
        String filePath = "QR.png";

        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            int size = 160;
            BitMatrix bitMatrix = new MultiFormatWriter().encode(url.toString(), BarcodeFormat.QR_CODE, size, size, hints);

            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, size, size, false);

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    boolean bit = bitMatrix.get(x, y);
                    int color = bit ? 0xFF000000 : 0xFFFFFFFF;
                    image.setRGB(x, y, color);
                    nativeImage.setColor(x, y, color);
                }
            }

            // 仍旧写出 QR.png 文件到游戏本地目录（删除了 cmd 弹窗指令）
            File qrCodeFile = new File(filePath);
            ImageIO.write(image, "png", qrCodeFile);

            // 注册为 Minecraft 动态纹理供游戏内 UI 使用
            NativeImageBackedTexture texture = new NativeImageBackedTexture(nativeImage);
            Identifier identifier = Identifier.of("dg_lab", "dynamic_qr_code");
            MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, texture);

            return identifier;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
