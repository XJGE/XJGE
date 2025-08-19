package org.xjge.test;

import java.util.Map;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import org.xjge.core.Audio2;
import org.xjge.core.Camera;
import org.xjge.core.Entity;
import org.xjge.core.Scene;
import org.xjge.core.SoundSource2;
import org.xjge.core.XJGE;
import org.xjge.graphics.GLProgram;

/**
 * Created: Jan 31, 2025
 * 
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {
    
    SoundSource2 source;
    
    int tickCount;
    
    public TestScene() {
        super("test");
        
        Entity entity = new Entity();
        entity.addComponent(new TestMesh(1, 0f, 0f, -5f));
        
        addEntity(entity);
        
        Audio2.loadSound("shimmer", "test_shimmer2.ogg");
        Audio2.loadSound("music_intro", "msc_battle_intro.ogg");
        Audio2.loadSound("music_body", "msc_battle_body.ogg");
        
        //source = Audio2.findSoundSource(true).queueSound("shimmer", true).play();
        
        source = Audio2.findSoundSource(true)
                       .queueSound("music_intro", false)
                       .queueSound("shimmer", true) //if this were true the queue will not proceed until setLooping(false) is called
                       .queueSound("music_body", true)
                       .play();
        
        //UI.addWidget(GLFW_JOYSTICK_1, "font_test", new TestWidget());
        //XJGE.setClearColor(Color.BLACK);
    }

    @Override
    public void update(double targetDelta, double trueDelta) {
        /*
        if(XJGE.tick(30) && tickCount < 5) {
            tickCount++;
            if(tickCount == 5) {
                source.play();
            }
        }*/
        
        /*
        if(XJGE.tick(120) && source.getCurrentSound().equals("music_body")) {
            source.seek(11f);
        }
        */
        
        if(XJGE.tick(30) && tickCount < 20) {
            tickCount++;
            if(tickCount == 20) {
                System.out.println(source.getCurrentSound());
                source.setLooping(false);
            }
        }
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera, int depthTexHandle) {
        entities.values().forEach(entity -> {
            if(entity.hasComponent(TestMesh.class)) entity.getComponent(TestMesh.class).render(glPrograms.get("test"));
        });
    }

    @Override
    public void renderShadows(GLProgram depthProgram) {
    }

    @Override
    public void exit() {
    }

}