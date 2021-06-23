package dev.theskidster.xjge2.core;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author J Hoffman
 * Created: May 8, 2021
 */

public final class ErrorUtils {

    public static void checkALError() {
        int alError = alGetError();
        
        if(alError != AL_NO_ERROR) {
            String desc = "";
        
            switch(alError) {
                case AL_INVALID_NAME      -> desc = "invalid name";
                case AL_INVALID_ENUM      -> desc = "invalid enum";
                case AL_INVALID_VALUE     -> desc = "invalid value";
                case AL_INVALID_OPERATION -> desc = "invalid operation";
                case AL_OUT_OF_MEMORY     -> desc = "out of memory";
            }

            Logger.logSevere("OpenAL Error: (" + alError + ") " + desc, null);
        }
    }
    
    public static void checkGLError() {
        int glError = glGetError();
        
        if(glError != GL_NO_ERROR) {
            String desc = "";
            
            switch(glError) {
                case GL_INVALID_ENUM      -> desc = "invalid enum";
                case GL_INVALID_VALUE     -> desc = "invalid value";
                case GL_INVALID_OPERATION -> desc = "invalid operation";
                case GL_STACK_OVERFLOW    -> desc = "stack overflow";
                case GL_STACK_UNDERFLOW   -> desc = "stack underflow";
                case GL_OUT_OF_MEMORY     -> desc = "out of memory";
            }
            
            Logger.logSevere("OpenGL Error: (" + glError + ") " + desc, null);
        }
    }
    
    static void checkFBStatus(int target) {
        int status  = glCheckFramebufferStatus(target);
        String desc = "";
        
        if(status != GL_FRAMEBUFFER_COMPLETE) {
            switch(status) {
                case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT         -> desc = "incomplete attachment";
                case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT -> desc = "missing attachment";
                case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER        -> desc = "incomplete draw buffer";
                case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER        -> desc = "incomplete read buffer";
                case GL_FRAMEBUFFER_UNSUPPORTED                   -> desc = "unsupported";
                case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE        -> desc = "incomplete multisample";
                case GL_FRAMEBUFFER_UNDEFINED                     -> desc = "undefined";
            }
            
            Logger.setDomain("core");
            Logger.logSevere("Framebuffer Error: (" + status + ") " + desc, null);
        }
    }
    
}