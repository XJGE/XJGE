package org.xjge.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import static org.lwjgl.stb.STBTruetype.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.xjge.core.ErrorUtils;
import org.xjge.core.Logger;
import org.xjge.core.XJGE;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.xjge.graphics.Color;

/**
 * Created: Jun 3, 2021
 * <br><br>
 * Parses data from a font file and provides it as an object that can be used to
 * render text. TrueType (.ttf) and Bitmap (.bmf) are the preferred formats for 
 * this engine.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Font {

    private final boolean isBitmapFont;
    
    private final float scale = 1.5f;
    
    public static final int DEFAULT_FONT_SIZE = 32;
    
    public final int size;
    private final int textureHandle;
    private final int largestGlyphWidth;
    private final int bitmapWidth;
    private final int bitmapHeight;
    private final int vaoHandle;
    private final int vboHandle;
    private final int iboHandle;
    private final int posOffsetHandle;
    private final int texOffsetHandle;
    private final int colOffsetHandle;
    private int numGlyphsAllocated;
    
    public static final Font placeholder = new Font("/org/xjge/assets/", "font_source_code_pro.ttf", DEFAULT_FONT_SIZE);
    
    private final String charset = " !\"#$%&\'()*+,-./" +
                                   "0123456789:;<=>?"   +
                                   "@ABCDEFGHIJKLMNO"   +
                                   "PQRSTUVWXYZ[\\]^_"  + 
                                   "`abcdefghijklmno"   +
                                   "pqrstuvwxyz{|}~";
    
    private final FloatBuffer vertexBuffer;
    private final IntBuffer indexBuffer;
    
    private final List<Glyph> glyphPool = new ArrayList<>();
    private final Map<Character, GlyphMetrics> glyphMetrics = new HashMap<>();
    
    private record GlyphMetrics (
        float texCoordX,
        float texCoordY,
        int advance,
        int bearingX,
        int bearingY
    ) {}
    
    public Font(String filename, int size) {
        this(XJGE.getAssetsFilepath(), filename, size);
    }
    
    private Font(String filepath, String filename, int size) {
        int[] info = loadFont(filepath, filename, size);
        
        isBitmapFont      = info[0] == 1;
        this.size         = info[1];
        textureHandle     = info[2];
        largestGlyphWidth = info[3];
        bitmapWidth       = info[4];
        bitmapHeight      = info[5];
        vaoHandle         = info[6];
        vboHandle         = info[7];
        iboHandle         = info[8];
        colOffsetHandle   = info[9];
        posOffsetHandle   = info[10];
        texOffsetHandle   = info[11];
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            vertexBuffer = stack.mallocFloat(20);
            indexBuffer  = stack.mallocInt(6);
            
            float subImageWidth  = (float) largestGlyphWidth / bitmapWidth;
            float subImageHeight = (float) this.size / bitmapHeight;
            
            //(vec3 position), (vec2 texCoords)
            vertexBuffer.put(0)                .put(this.size).put(0)   .put(0)            .put(0);
            vertexBuffer.put(largestGlyphWidth).put(this.size).put(0)   .put(subImageWidth).put(0);
            vertexBuffer.put(largestGlyphWidth).put(0)        .put(0)   .put(subImageWidth).put(subImageHeight);
            vertexBuffer.put(0)                .put(0)        .put(0)   .put(0)            .put(subImageHeight);
            
            indexBuffer.put(0).put(1).put(2);
            indexBuffer.put(2).put(3).put(0);
            
            vertexBuffer.flip();
            indexBuffer.flip();
        }
        
        glBindVertexArray(vaoHandle);
        
        glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboHandle);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    
    private int[] loadFont(String filepath, String filename, int size) {
        try(InputStream file = Font.class.getResourceAsStream(filepath + filename)) {
            if(size <= 0 || size > 128) {
                throw new IllegalStateException("Invalid font size used. Font size must be between 1 and 128");
            }
            
            String extension = filename.substring(filename.length() - 3, filename.length());
            
            int[] info = new int[12];
            
            info[0] = extension.equals("bmf") ? 1 : 0;
            info[1] = size;
            info[2] = glGenTextures();
            
            if(extension.equals("bmf")) {
                loadBitmapFont(filepath, file, info);
            } else {
                loadVectorFont(file, info);
            }
            
            //Initialize various OpenGL objects
            info[6] = glGenVertexArrays();
            for(int i = 7; i < 12; i++) info[i] = glGenBuffers();
            
            return info;
            
        } catch(Exception exception) {
            Logger.logWarning("Failed to load font \"" + filename + "\" a placeholder will be used instead", exception);
            
            glyphMetrics.putAll(placeholder.glyphMetrics);
            
            return new int[] {
                0,
                DEFAULT_FONT_SIZE,
                placeholder.textureHandle,
                placeholder.largestGlyphWidth,
                placeholder.bitmapWidth,
                placeholder.bitmapHeight,
                placeholder.vaoHandle,
                placeholder.vboHandle,
                placeholder.iboHandle,
                placeholder.posOffsetHandle,
                placeholder.texOffsetHandle,
                placeholder.colOffsetHandle
            };
        }
    }
    
    private void loadBitmapFont(String filepath, InputStream file, int[] info) throws XMLStreamException, IOException {
        var xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(file);
        
        float subImageWidth  = 0;
        float subImageHeight = 0;
        int advance          = 0;
        int descent          = 0;
        var advanceValues    = new HashMap<Character, Integer>();
        var descentValues    = new HashMap<Character, Integer>();
        
        while(xmlReader.hasNext()) {
            final int ADVANCE = advance;
            final int DESCENT = descent;
            
            switch(xmlReader.next()) {
                case XMLStreamConstants.START_ELEMENT -> {
                    if(xmlReader.getName().getLocalPart().equals("font")) {
                        String imageFilename = xmlReader.getAttributeValue(null, "texture");
                        info[3] = Integer.parseInt(xmlReader.getAttributeValue(null, "width"));
                        info[1] = Integer.parseInt(xmlReader.getAttributeValue(null, "height"));
                        
                        /**
                         * Create a new OpenGL texture object using the image 
                         * provided by the XML file.
                         */
                        try(InputStream imageFile = Font.class.getResourceAsStream(filepath + imageFilename);
                            MemoryStack stack = MemoryStack.stackPush()) {
                            byte[] data = imageFile.readAllBytes();
                            
                            ByteBuffer imageBuffer  = MemoryUtil.memAlloc(data.length).put(data).flip();
                            IntBuffer widthBuffer   = stack.mallocInt(1);
                            IntBuffer heightBuffer  = stack.mallocInt(1);

                            ByteBuffer image = stbi_load_from_memory(imageBuffer, widthBuffer, heightBuffer, stack.mallocInt(1), STBI_rgb_alpha);
                            
                            if(image == null) {
                                MemoryUtil.memFree(imageBuffer);
                                throw new RuntimeException("STBI failed to parse image data for raster font: " + stbi_failure_reason());
                            }
                            
                            info[4] = widthBuffer.get();
                            info[5] = heightBuffer.get();
                            
                            subImageWidth  = (float) info[3] / info[4];
                            subImageHeight = (float) info[1] / info[5];
                            
                            glBindTexture(GL_TEXTURE_2D, info[2]);
                            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, info[4], info[5], 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
                            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                            glBindTexture(GL_TEXTURE_2D, 0);
                            
                            stbi_image_free(image);
                            MemoryUtil.memFree(imageBuffer);
                        }
                    } else if(xmlReader.getName().getLocalPart().equals("group")) {
                        advance = Integer.parseInt(xmlReader.getAttributeValue(null, "advance"));
                        descent = Integer.parseInt(xmlReader.getAttributeValue(null, "descent"));
                    }
                }
                case XMLStreamConstants.END_ELEMENT -> {
                    if(xmlReader.getName().getLocalPart().equals("font")) xmlReader.close();
                }
                case XMLStreamConstants.CHARACTERS -> {
                    BufferedReader reader = new BufferedReader(new StringReader(xmlReader.getText().trim()));
                        
                    reader.lines().forEach(line -> {
                        for(String value : line.trim().split(",")) {
                            char character = (char) Integer.parseInt(value);
                            advanceValues.put(character, ADVANCE);
                            descentValues.put(character, DESCENT);
                        }
                    });
                }
            }
        }
        
        float texCoordX = 0;
        float texCoordY = 0;
        
        //Assign glyph metrics using data parsed from the XML file 
        for(char character : charset.toCharArray()) {
            if(character == ' ' || character == '0' || character == '@' ||
               character == 'P' || character == '`' || character == 'p') {
                texCoordX = 0;
                texCoordY += (character == ' ') ? 0 : subImageHeight;
            } else {
                texCoordX += subImageWidth;
            }
            
            GlyphMetrics metrics = new GlyphMetrics(texCoordX, texCoordY,
                                                    advanceValues.get(character),
                                                    0, descentValues.get(character));

            glyphMetrics.put(character, metrics);
        }
    }
    
    private void loadVectorFont(InputStream file, int[] info) throws IOException {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            byte[] data = file.readAllBytes();

            ByteBuffer fontBuffer  = MemoryUtil.memAlloc(data.length).put(data).flip();
            STBTTFontinfo fontInfo = STBTTFontinfo.malloc(stack);

            if(!stbtt_InitFont(fontInfo, fontBuffer)) {
                throw new IllegalStateException("Failed to parse font information from file");
            }

            float pixelScale   = stbtt_ScaleForPixelHeight(fontInfo, info[1]);
            int[] advanceWidth = new int[1];
            int[] leftBearing  = new int[1];

            //Find the width of the largest glyph
            for(int i = 0; i < charset.length(); i++) {
                stbtt_GetCodepointHMetrics(fontInfo, charset.charAt(i), advanceWidth, leftBearing);
                float scaledAdvance = advanceWidth[0] * pixelScale;
                if(scaledAdvance > info[3]) info[3] = Math.round(scaledAdvance * scale);
            }

            boolean containsAllGlyphs = false;
            int bitmapSizeInPixels    = 128;

            ByteBuffer imageBuffer = null;
            STBTTPackedchar.Buffer packedCharBuffer = STBTTPackedchar.malloc(charset.length());

            /*
            Generate a bitmap that evenly spaces each glyph according to the
            dimensions of the largest one. This mitigates texture bleeding.
            */
            while(!containsAllGlyphs) {
                info[4]     = Math.round(bitmapSizeInPixels * scale);
                info[5]     = Math.round(bitmapSizeInPixels * scale);
                imageBuffer = MemoryUtil.memAlloc(info[4] * info[5]);

                try(STBTTPackContext context = STBTTPackContext.malloc()) {
                    stbtt_PackBegin(context, imageBuffer, info[4], info[5], 0, (int) (info[3] * scale), NULL);
                    stbtt_PackSetOversampling(context, 1, 1);
                    containsAllGlyphs = stbtt_PackFontRange(context, fontBuffer, 0, info[1], 32, packedCharBuffer);
                    stbtt_PackEnd(context);
                }

                if(containsAllGlyphs) break;

                MemoryUtil.memFree(imageBuffer);
                bitmapSizeInPixels += 16;
            }

            glBindTexture(GL_TEXTURE_2D, info[2]);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, info[4], info[5], 0, GL_ALPHA, GL_UNSIGNED_BYTE, imageBuffer);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glBindTexture(GL_TEXTURE_2D, 0);

            MemoryUtil.memFree(imageBuffer);
            MemoryUtil.memFree(fontBuffer);

            //Store glyph data in a format the engine can use
            for(int i = 0; i < charset.length(); i++) {
                STBTTPackedchar glyph = packedCharBuffer.get(i);
                GlyphMetrics metrics  = new GlyphMetrics((float) (glyph.x0()) / info[4],
                                                         (float) (glyph.y0()) / info[5],
                                                         (int) glyph.xadvance(),
                                                         (int) glyph.xoff(), 
                                                         (int) (-glyph.yoff() - info[1]));

                glyphMetrics.put(charset.charAt(i), metrics);
            }

            MemoryUtil.memFree(packedCharBuffer);
        }
    }
    
    private void offsetPosition() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer positions = stack.mallocFloat(glyphPool.size() * Float.BYTES);
            
            glyphPool.forEach(glyph -> {
                positions.put(glyph.positionX).put(glyph.positionY).put(0);
            });
            
            positions.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, posOffsetHandle);
            glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);
        }
        
        glVertexAttribPointer(2, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(2);
        glVertexAttribDivisor(2, 1);
    }
    
    private void offsetTexture() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer cells = stack.mallocFloat(glyphPool.size() * Float.BYTES);
            
            glyphPool.forEach(glyph -> {
                cells.put(getGlyphTexCoordX(glyph.character)).put(getGlyphTexCoordY(glyph.character));
            });
            
            cells.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, texOffsetHandle);
            glBufferData(GL_ARRAY_BUFFER, cells, GL_STATIC_DRAW);
        }
        
        glVertexAttribPointer(3, 2, GL_FLOAT, false, (2 * Float.BYTES), 0);
        glEnableVertexAttribArray(3);
        glVertexAttribDivisor(3, 1);
    }
    
    private void offsetColor() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer colors = stack.mallocFloat(glyphPool.size() * Float.BYTES);
            
            glyphPool.forEach(glyph -> {
                colors.put(glyph.color.r).put(glyph.color.g).put(glyph.color.b).put(glyph.opacity);
            });
            
            colors.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, colOffsetHandle);
            glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);
        }
        
        glVertexAttribPointer(4, 4, GL_FLOAT, false, (4 * Float.BYTES), 0);
        glEnableVertexAttribArray(4);
        glVertexAttribDivisor(4, 1);
    }
    
    private void drawString(String text, int positionX, int positionY, Color color, float opacity, TextEffect effect) {
        XJGE.getUIProgram().use();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        glBindVertexArray(vaoHandle);
        
        //Update object pool size to accommodate text requirements
        if(text.length() > numGlyphsAllocated) {
            for(int i = glyphPool.size(); i < text.length(); i++) glyphPool.add(new Glyph());
            numGlyphsAllocated = text.length();
        }
        
        int advance = 0;
            
        //Update glyph values to match the current requirements of the text
        for(int i = 0; i < glyphPool.size(); i++) {
            Glyph glyph = glyphPool.get(i);
            glyph.reset();

            if(i < text.length()) {
                glyph.character = text.charAt(i);
                glyph.positionX = (positionX + advance + getGlyphBearingX(glyph.character)) + glyph.positionOffsetX;
                glyph.positionY = (positionY + getGlyphBearingY(glyph.character)) + glyph.positionOffsetY;
                
                if(effect == null) {
                    glyph.opacity = opacity;
                    glyph.color   = color;
                } else {
                    effect.apply(glyph, i);
                }
                
                if(effect != null) effect.apply(glyph, i);

                advance += getGlyphAdvance(glyph.character);
            }
        }
        
        if(effect != null) effect.reset();
        
        offsetPosition();
        offsetTexture();
        offsetColor();
        
        XJGE.getUIProgram().setUniform("uType", 0);
        XJGE.getUIProgram().setUniform("uTexture", 0);
        XJGE.getUIProgram().setUniform("uIsBitmapFont", (isBitmapFont) ? 1 : 0);
        
        glDrawElementsInstanced(GL_TRIANGLES, indexBuffer.capacity(), GL_UNSIGNED_INT, 0, glyphPool.size());
        glDisable(GL_BLEND);
        
        ErrorUtils.checkGLError();
    }
    
    public void drawString(String text, int positionX, int positionY, Color color, float opacity) {
        drawString(text, positionX, positionY, color, opacity, null);
    }
    
    public void drawString(String text, int positionX, int positionY, TextEffect effect) {
        drawString(text, positionX, positionY, Color.WHITE, 1f, effect);
    }
    
    public void delete() {
        if(textureHandle != placeholder.textureHandle) {
            glDeleteTextures(textureHandle);
            glDeleteVertexArrays(vaoHandle);
            glDeleteBuffers(vboHandle);
            glDeleteBuffers(iboHandle);
        }
    }
    
    public float getGlyphTexCoordX(char character) {
        return glyphMetrics.get((!glyphMetrics.containsKey(character) ? '?' : character)).texCoordX;
    }
    
    public float getGlyphTexCoordY(char character) {
        return glyphMetrics.get((!glyphMetrics.containsKey(character) ? '?' : character)).texCoordY;
    }
    
    public int getGlyphAdvance(char character) {
        return glyphMetrics.get((!glyphMetrics.containsKey(character) ? '?' : character)).advance;
    }
    
    public int getGlyphBearingX(char character) {
        return glyphMetrics.get((!glyphMetrics.containsKey(character) ? '?' : character)).bearingX;
    }
    
    public int getGlyphBearingY(char character) {
        return glyphMetrics.get((!glyphMetrics.containsKey(character) ? '?' : character)).bearingY;
    }
    
    public int lengthInPixels(String text) {
        int length = 0;
        for(char character : text.toCharArray()) length += getGlyphAdvance(character);
        return length;
    }
    
    public int numCharOccurences(String text, char character, int index) {
        if(index >= text.length()) return 0;
        int count = (text.charAt(index) == character) ? 1 : 0;
        return count + numCharOccurences(text, character, index + 1);
    }
    
    public String wrap(String text, int advanceLimit) {
        var words  = new ArrayList<String>();
        var string = new StringBuilder();
        
        for(int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);

            if(i != text.length() - 1) {
                if(character != ' ') {
                    string.append(character);
                } else {
                    words.add(string.toString());
                    string.delete(0, string.length());
                }
            } else {
                string.append(character);
                words.add(string.toString());
                string.delete(0, string.length());
            }
        }
        
        int wordLength = 0;
        text = "";

        for(int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            wordLength += lengthInPixels(word + " ");
            
            if(i != words.size() - 1 && wordLength + lengthInPixels(words.get(i + 1)) > advanceLimit) {
                text += words.get(words.indexOf(word)).concat("\n");
                wordLength = 0;
            } else {
                if(words.indexOf(word) != words.size() - 1) {
                    text += words.get(words.indexOf(word)).concat(" ");
                } else {
                    text += words.get(words.indexOf(word));
                }
            }
        }
        
        return text;
    }
    
}