package me.brennan.stockxgiffer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.luciad.imageio.webp.WebPReadParam;
import me.brennan.stockxgiffer.util.GifMaker;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Brennan
 * @since 5/15/2021
 **/
public class StockXGiffer {
    private final String url;

    private final List<BufferedImage> bufferedImages = new LinkedList<>();

    private final GifMaker gifMaker;

    public StockXGiffer(String url, String out) throws Exception {
        this.url = String.format("https://stockx.com/api/products/%s?includes=market&currency=usd", url.substring(19));
        this.gifMaker = new GifMaker(out + ".gif", 8, true);

        //gather our images and convert to buffered
        final JsonArray gatheredImages = getImages();

        if (gatheredImages != null) {
            for (JsonElement jsonElement : getImages()) {
                final BufferedImage bufferedImage = getImage(jsonElement.getAsString());
                if (bufferedImage != null)
                    bufferedImages.add(bufferedImage);
            }
        }
    }

    /**
     * generate our gif
     * @throws IOException
     */
    public void perform() throws IOException {
        gifMaker.generateGIF(bufferedImages);
    }

    private BufferedImage getImage(String url) throws Exception {
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = new OkHttpClient().newCall(request).execute()) {
            if (response.code() == 200) {
                final InputStream stream = response.body().byteStream();
                final BufferedImage isJPEG = ImageIO.read(stream);

                //some images return as a JPEG, ImageIO#read can read .JPEG but not .WEBP so i found a hacky lib that allows us to read and decode .webp
                if (isJPEG == null) {
                    ImageReader reader = ImageIO.getImageReadersByMIMEType("image/webp").next();
                    WebPReadParam readParam = new WebPReadParam();
                    readParam.setBypassFiltering(true);

                    reader.setInput(stream);

                    return reader.read(0, readParam);
                } else {
                    return isJPEG;
                }
            }
        }

        return null;
    }

    /**
     * gather our images from stockX
     * @return returned images json array
     * @throws Exception the request fails
     */
    private JsonArray getImages() throws Exception {
        final Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = new OkHttpClient().newCall(request).execute()) {
            if (response.code() == 200) {
                return JsonParser.parseString(response.body().string())
                        .getAsJsonObject()
                        .getAsJsonObject("Product")
                        .getAsJsonObject("media")
                        .getAsJsonArray("360");
            }
        }

        return null;
    }
}
