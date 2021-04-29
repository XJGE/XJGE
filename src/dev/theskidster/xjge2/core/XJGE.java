package dev.theskidster.xjge2.core;

/**
 * @author J Hoffman
 * Created: Apr 28, 2021
 */

public class XJGE {
    
    private static boolean initCalled;
    
    private static String filepath;
    
    //TODO: likely wont need to use this constructor...
    static {
        /*
        XJGE.init(String filepath);
        ...setup shaders...
        XJGE.start(Map<String, GLProgram> programs, Scene initialScene);
        
        init() should be called once at the beginning of the application to 
        initialize the engines various libraries and set the filepath it will 
        use locate whatever resources it needs such as game assets and other 
        files.
        
        it's assumed the implementing application will compile it's shaders 
        between the init() and start() methods, a collection of these compiled 
        programs will be supplied to the engine via the "programs" parameter in
        the start method. Additonally, there's another parameter that will be 
        used to set the initial scene the engine will enter upon startup.
        */
    }
    
    public static void init(String filepath) {
        if(!initCalled) {
            XJGE.filepath = filepath;
            
            //TODO: init stuff
        } else {
            //TODO: log warning since the engine has already been initialized.
        }
    }
    
    public static void start() {
        
    }
    
    public static String getFilepath() {
        return filepath;
    }
    
}