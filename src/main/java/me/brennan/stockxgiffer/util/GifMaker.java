package me.brennan.stockxgiffer.util;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @author Brennan
 * @since 5/15/2021
 *
 * simple gif maker i put together using stackoverflow and docs
 **/
public class GifMaker {
    private final ImageWriter gifWriter;
    private final ImageOutputStream imageOutputStream;
    private final IIOMetadata metadata;

    public GifMaker(String out, int delay, boolean loop) throws IOException {
        this.imageOutputStream = ImageIO.createImageOutputStream(new File(out));
        this.gifWriter = getWriter();
        this.metadata = generateMetadata(delay, loop);
    }

    public void generateGIF(List<BufferedImage> images) throws IOException {
        gifWriter.setOutput(imageOutputStream);
        gifWriter.prepareWriteSequence(null);

        for (BufferedImage bufferedImage : images) {
            final IIOImage tempImage = new IIOImage(bufferedImage, null, metadata);
            gifWriter.writeToSequence(tempImage, null);
        }

        gifWriter.endWriteSequence();
    }

    private IIOMetadata generateMetadata(int delay, boolean loop) throws IIOInvalidTreeException {
        final ImageTypeSpecifier imageType = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);
        final IIOMetadata metadata = gifWriter.getDefaultImageMetadata(imageType, null);
        final String nativeFormatName = metadata.getNativeMetadataFormatName();
        final IIOMetadataNode nodeTree = (IIOMetadataNode)metadata.getAsTree(nativeFormatName);

        final IIOMetadataNode graphicsNode = getNode("GraphicControlExtension", nodeTree);
        graphicsNode.setAttribute("delayTime", String.valueOf(delay));
        graphicsNode.setAttribute("disposalMethod", "none");
        graphicsNode.setAttribute("userInputFlag", "FALSE");

        if (loop) makeLoopy(nodeTree);

        metadata.setFromTree(nativeFormatName, nodeTree);

        return metadata;
    }

    private void makeLoopy(IIOMetadataNode root) {
        final IIOMetadataNode applicationExtensions = getNode("ApplicationExtensions", root);
        final IIOMetadataNode appNode = getNode("ApplicationExtension", applicationExtensions);

        appNode.setAttribute("applicationID", "NETSCAPE");
        appNode.setAttribute("authenticationCode", "2.0");
        appNode.setUserObject(new byte[]{ 0x1, (byte) (0), (byte) ((0) & 0xFF)});

        applicationExtensions.appendChild(appNode);
        root.appendChild(applicationExtensions);
    }

    private IIOMetadataNode getNode(String node_name, IIOMetadataNode root) {
        IIOMetadataNode node;

        for (int i = 0; i < root.getLength(); i++) {
            if (root.item(i).getNodeName().compareToIgnoreCase(node_name) == 0) {
                node = (IIOMetadataNode) root.item(i);
                return node;
            }
        }

        node = new IIOMetadataNode(node_name);
        root.appendChild(node);

        return node;
    }

    private ImageWriter getWriter() {
        Iterator<ImageWriter> itr = ImageIO.getImageWritersByFormatName("gif");

        if (itr.hasNext())
            return itr.next();

        return null;
    }

}
