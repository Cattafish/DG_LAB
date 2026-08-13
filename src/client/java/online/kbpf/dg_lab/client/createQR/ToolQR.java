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
            hints.put(EncodeHintType.MARGIN, 1); // 极窄边框：只保留 1 个小方块宽度的白边

            // 1. 传入 0, 0，让 ZXing 生成无多余填充的原始矩阵
            BitMatrix bitMatrix = new MultiFormatWriter().encode(url.toString(), BarcodeFormat.QR_CODE, 0, 0, hints);
            int matrixWidth = bitMatrix.getWidth();
            int matrixHeight = bitMatrix.getHeight();

            // 2. 将每个二维码小方块精准放大 6 倍
            int scale = 6;
            int sizeX = matrixWidth * scale;
            int sizeY = matrixHeight * scale;

            BufferedImage image = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_RGB);
            NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, sizeX, sizeY, false);

            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    boolean bit = bitMatrix.get(x / scale, y / scale);
                    int color = bit ? 0xFF000000 : 0xFFFFFFFF;
                    image.setRGB(x, y, color);
                    nativeImage.setColor(x, y, color);
                }
            }

            File qrCodeFile = new File(filePath);
            ImageIO.write(image, "png", qrCodeFile);

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
