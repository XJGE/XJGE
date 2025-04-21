package org.xjge.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector2i;
import static org.lwjgl.opengl.GL11C.*;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import static org.lwjgl.stb.STBTruetype.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.xjge.graphics.Color;

/**
 * Created: Jun 3, 2021
 * <p>
 * Parses data from a font file and provides it as an object that can be passed 
 * to the {@linkplain Widget#drawString(Font, String, Vector2i, Color, float) drawString()} 
 * method. TrueType (.ttf) and Bitmap (.bmf) are the preferred file formats of 
 * this engine for fonts.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Font {
    
    final static int DEFAULT_SIZE = 32;
    
    int texHandle;
    int size;
    
    private int bitmapWidth;
    private int bitmapHeight;
    private int largestGlyphWidth;
    
    private final float SCALE = 1.5f;
    
    boolean isBitmapFont;
    
    Map<Character, Vector2f> texOffsets = new HashMap<>();
    
    private final HashMap<Character, Vector2i> posOffsets   = new HashMap<>();
    private final HashMap<Character, Integer> advanceValues = new HashMap<>();
    
    /**
     * Creates a new font object using the default font metrics of the engine.
     */
    Font() {
        this.size = DEFAULT_SIZE;
        texHandle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texHandle);
        loadVectorFont(Font.class.getResourceAsStream("/org/xjge/assets/font_vector_sourcecodepro.ttf"), size);
    }
    
    /**
     * Creates a new font object from an existing one.
     * 
     * @param font the font to copy
     */
    Font(Font font) {
        texHandle         = font.texHandle;
        size              = font.size;
        bitmapWidth       = font.bitmapWidth;
        bitmapHeight      = font.bitmapHeight;
        largestGlyphWidth = font.largestGlyphWidth;
        texOffsets        = font.texOffsets;
        
        posOffsets.putAll(font.posOffsets);
        advanceValues.putAll(font.advanceValues);
    }
    
    /**
     * Creates a new font object of the specified size using data parsed from a 
     * font file. Font objects are immutable by design so if you wish to render 
     * a single font in different sizes you should create a new font object for 
     * each desired size.
     * <p>
     * NOTE: The size specified in this constructor encompasses the entire 
     * glyph, including its advance, descent, and bearing space. As such, the 
     * actual visible portion of the glyph produced may not correspond directly 
     * to the desired size in pixels. This value will be discarded if a .bmf 
     * file is used.
     * 
     * @param filename the name of the file to load. Expects the file extension 
     *                 to be included.
     * @param size the desired size (in pixels) to generate this fonts glyphs at
     */
    public Font(String filename, int size) {
        try(InputStream file = Font.class.getResourceAsStream(XJGE.getAssetsFilepath() + filename)) {
            if(size <= 0) throw new IllegalStateException("Invalid font size " + size + " used");
            
            int periodIndex = filename.lastIndexOf(".");
            
            if(periodIndex > 0) {
                if(filename.substring(periodIndex + 1).equals("bmf")) {
                    loadBitmapFont(file);
                } else {
                    loadVectorFont(file, size);
                }
            } else {
                throw new IllegalStateException("Font filename has no extension");
            }
        } catch(Exception e) {
            Logger.logError("Failed to load font \"" + filename + "\"", e);
            loadVectorFont(Font.class.getResourceAsStream("/org/xjge/assets/font_vector_sourcecodepro.ttf"), DEFAULT_SIZE);
        }
        
        ErrorUtils.checkGLError();
    }
    
    /**
     * Parses font data from a .bmf (bitmap font) file. This format is used 
     * exclusively by the engine for low-resolution fonts such as those that 
     * would accompany a "retro" aesthetic.
     * 
     * @param file the file to parse font data from
     */
    private void loadBitmapFont(InputStream file) {
        isBitmapFont = true;
        
        /*
        try {
            XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(file);
            
            Texture texture;
            int advance = 0;
            int descent = 0;
            
            float subImageWidth  = 0;
            float subImageHeight = 0;
            
            while(xmlReader.hasNext()) {
                final int ADVANCE = advance;
                final int DESCENT = descent;
                
                switch(xmlReader.next()) {
                    case XMLStreamConstants.START_ELEMENT -> {
                        if(xmlReader.getName().getLocalPart().equals("font")) {
                            
                            texture = new Texture(xmlReader.getAttributeValue(null, "texture"));
                                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                            glBindTexture(GL_TEXTURE_2D, 0);
                            
                            texHandle         = texture.handle;
                            largestGlyphWidth = Integer.parseInt(xmlReader.getAttributeValue(null, "width"));
                            size              = Integer.parseInt(xmlReader.getAttributeValue(null, "height"));
                            bitmapWidth       = texture.width;
                            bitmapHeight      = texture.height;
                            
                            subImageWidth  = (float) largestGlyphWidth / bitmapWidth;
                            subImageHeight = (float) size / bitmapHeight;
                            
                        } else if(xmlReader.getName().getLocalPart().equals("group")) {
                            advance = Integer.parseInt(xmlReader.getAttributeValue(null, "advance"));
                            descent = Integer.parseInt(xmlReader.getAttributeValue(null, "descent"));
                        }
                    }
                        
                    case XMLStreamConstants.END_ELEMENT -> {
                        if(xmlReader.getName().getLocalPart().equals("font")) {
                            xmlReader.close();
                        }
                    }
                    
                    case XMLStreamConstants.CHARACTERS -> {
                        BufferedReader reader = new BufferedReader(new StringReader(xmlReader.getText().trim()));
                        
                        reader.lines().forEach(line -> {
                            for(String value : line.trim().split(",")) {
                                char character = (char) Integer.parseInt(value);
                                
                                advanceValues.put(character, ADVANCE);
                                posOffsets.put(character, new Vector2i(0, DESCENT));
                            }
                        });
                    }
                }
            }
            
            String charset = " !\"#$%&\'()*+,-./" + "\r" +
                             "0123456789:;<=>?"   + "\r" +
                             "@ABCDEFGHIJKLMNO"   + "\r" +
                             "PQRSTUVWXYZ[\\]^_"  + "\r" + 
                             "`abcdefghijklmno"   + "\r" +
                             "pqrstuvwxyz{|}~";
            
            float charPosX = 0;
            float charPosY = 0;
            
            for(char c : charset.toCharArray()) {
                if(c != '\r') {
                    texOffsets.put(c, new Vector2f(charPosX, charPosY));
                    charPosX += subImageWidth;
                } else {
                    charPosX = 0;
                    charPosY += subImageHeight;
                }
            }
        } catch(XMLStreamException e) {
            Logger.setDomain("ui");
            Logger.logSevere("Failed to parse bitmap font file data", e);
        }
        */
    }
    
    /**
     * Generates a bitmap image that will be used as a texture atlas during 
     * instanced rendering.
     * 
     * @param file the file to parse font data from
     * @param size the desired size (in pixels) to generate this fonts glyphs at
     */
    private void loadVectorFont(InputStream file, int size) {
        this.size = size;
        
        texHandle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texHandle);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            byte[] data = file.readAllBytes();
            
            ByteBuffer fontBuf = MemoryUtil.memAlloc(data.length).put(data).flip();
            STBTTFontinfo info = STBTTFontinfo.mallocStack(stack);
            
            if(!stbtt_InitFont(info, fontBuf)) {
                throw new IllegalStateException("Failed to parse font information");
            }
            
            int bitmapSizeInPixels = 128;
            int exitStatus         = -1;
            int extraCells         = -1;
            
            bitmapWidth  = 0;
            bitmapHeight = 0;
            
            String charset = " !\"#$%&\'()*+,-./" +
                             "0123456789:;<=>?"   +
                             "@ABCDEFGHIJKLMNO"   +
                             "PQRSTUVWXYZ[\\]^_"  + 
                             "`abcdefghijklmno"   +
                             "pqrstuvwxyz{|}~";
            
            ByteBuffer imageBuf                = MemoryUtil.memAlloc(bitmapWidth * bitmapHeight);
            STBTTBakedChar.Buffer bakedCharBuf = STBTTBakedChar.malloc(charset.length());
            
            /*
            Continuously generate a bitmap image until its large enough to
            contain every glyph in the font. 
            */
            while(exitStatus <= 0) {
                bitmapWidth  = Math.round(bitmapSizeInPixels * SCALE);
                bitmapHeight = Math.round(bitmapSizeInPixels * SCALE);
                imageBuf     = MemoryUtil.memAlloc(bitmapWidth * bitmapHeight);
                
                bakedCharBuf = STBTTBakedChar.malloc(charset.length());
                
                extraCells = stbtt_BakeFontBitmap(fontBuf, size * SCALE, imageBuf, bitmapWidth, bitmapHeight, 32, bakedCharBuf);
                exitStatus = Math.abs(extraCells) - charset.length();
                
                if(extraCells > 0) break;
                
                MemoryUtil.memFree(bakedCharBuf);
                MemoryUtil.memFree(imageBuf);
                
                bitmapSizeInPixels += 16;
            }
            
            /*
            No guarantee this buffer was freed in the while loop, so we'll just 
            make sure here.
            */
            MemoryUtil.memFree(bakedCharBuf);
            
            largestGlyphWidth = 0;
            
            for(int i = 0; i < charset.length(); i++) {
                int glyphWidth = Math.round(bakedCharBuf.get(i).xadvance());
                if(glyphWidth > largestGlyphWidth) largestGlyphWidth = glyphWidth;
            }
            
            STBTTPackedchar.Buffer packedCharBuf = STBTTPackedchar.malloc(charset.length());
            
            boolean containsAllGlyphs = false;
            
            /*
            Generate a new bitmap that evenly spaces each glyph according to
            the dimensions of the largest one. This extra step helps mitigate  
            texture bleeding.
            */
            while(!containsAllGlyphs) {
                bitmapWidth  = Math.round(bitmapSizeInPixels * SCALE);
                bitmapHeight = Math.round(bitmapSizeInPixels * SCALE);
                imageBuf     = MemoryUtil.memAlloc(bitmapWidth * bitmapHeight);
                
                try(STBTTPackContext pc = STBTTPackContext.malloc()) {
                    stbtt_PackBegin(pc, imageBuf, bitmapWidth, bitmapHeight, 0, (int) (largestGlyphWidth * SCALE), NULL);
                    stbtt_PackSetOversampling(pc, 1, 1);
                    containsAllGlyphs = stbtt_PackFontRange(pc, fontBuf, 0, size, 32, packedCharBuf);
                    stbtt_PackEnd(pc);
                }
                
                if(containsAllGlyphs) break;
                
                MemoryUtil.memFree(imageBuf);
                bitmapSizeInPixels += 16;
            }
            
            glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, bitmapWidth, bitmapHeight, 0, GL_ALPHA, GL_UNSIGNED_BYTE, imageBuf);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glBindTexture(GL_TEXTURE_2D, 0);
            
            MemoryUtil.memFree(imageBuf);
            MemoryUtil.memFree(fontBuf);
            
            //Store glyph data in a format that we can use.
            for(int i = 0; i < charset.length(); i++) {
                STBTTPackedchar glyph = packedCharBuf.get(i);
                
                char character  = charset.charAt(i);
                float texCoordX = (float) (glyph.x0()) / bitmapWidth;
                float texCoordY = (float) (glyph.y0()) / bitmapHeight;
                
                texOffsets.put(character, new Vector2f(texCoordX, texCoordY));
                posOffsets.put(character, new Vector2i((int) glyph.xoff(), (int) (-glyph.yoff() - size)));
                advanceValues.put(character, (int) glyph.xadvance());
            }
            
            texOffsets = Collections.unmodifiableMap(texOffsets);
            
            MemoryUtil.memFree(packedCharBuf);
            
        } catch(IOException e) {
            Logger.logError("Failed to parse vector font file data", e);
        }
    }
    
    /**
     * Provides the width of this fonts bitmap image.
     * 
     * @return the width of the bitmap in pixels
     */
    int getBitmapWidth() {
        return bitmapWidth;
    }
    
    /**
     * Provides the height of this fonts bitmap image.
     * 
     * @return the height of the bitmap in pixels
     */
    int getBitmapHeight() {
        return bitmapHeight;
    }
    
    /**
     * Obtains the width of the largest {@link Glyph} in the bitmap.
     * 
     * @return the width of the largest glyph in pixels
     */
    int getLargestGlyphWidth() {
        return largestGlyphWidth;
    }
    
    /**
     * Obtains the horizontal advance (or stride) that the specified 
     * {@link Glyph} will offset the position of next glyph in sequence by.
     * 
     * @param c the character which corresponds to the glyph in question
     * 
     * @return the horizontal advance of the specified glyph
     */
    int getGlyphAdvance(char c) {
        if(!advanceValues.containsKey(c)) {
            return advanceValues.get('');
        } else {
            return advanceValues.get(c);
        }
    }
    
    /**
     * Obtains the horizontal bearing space (or padding) that is applied to the 
     * specified glyphs left side during rendering. 
     * 
     * @param c the character which corresponds to the glyph in question
     * 
     * @return the horizontal bearing space of the specified glyph
     */
    int getGlyphBearing(char c) {
        if(!posOffsets.containsKey(c)) {
            return posOffsets.get('').x;
        } else {
            return posOffsets.get(c).x;
        }
    }
    
    /**
     * Obtains the descent value that indicates how far below the baseline the 
     * specified glyph extends.
     * 
     * @param c the character which corresponds to the glyph in question
     * 
     * @return the descent of the specified glyph
     */
    int getGlyphDescent(char c) {
        if(!posOffsets.containsKey(c)) {
            return posOffsets.get('').y;
        } else {
            return posOffsets.get(c).y;
        }
    }
    
    /**
     * Frees the OpenGL texture object used by this class.
     */
    public void freeTexture() {
        glDeleteTextures(texHandle);
    }
    
}