package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.core.Audio;
import dev.theskidster.xjge2.core.Camera;
import dev.theskidster.xjge2.core.Game;
import dev.theskidster.xjge2.core.Scene;
import dev.theskidster.xjge2.core.Song;
import dev.theskidster.xjge2.core.Sound;
import dev.theskidster.xjge2.graphics.GLProgram;
import java.util.Map;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

public class TestScene extends Scene {

    Sound sound;
    Song song;
    
    public TestScene() {
        super("test");
        
        sound = new Sound("sfx_beep.ogg");
        //song  = new Song("msc_battle_intro.ogg", "msc_battle_body.ogg");
        song = new Song("msc_unatco.ogg");
        
        entities.put("test",  new TestEntity(0, 0, -3));
        //entities.put("test2", new TestEntity2(2, 1, -3));
        entities.put("test3", new TestEntity3(2, -0.5f, -3));
        
        Audio.playMusic(song);
        //Audio.playSound(sound, new Vector3f(10, 0, 0), true);
    }
    
    @Override
    public void update(double targetDelta) {
        
        //light.position.x += 0.1f;
        
        /*
        if(Game.tick(60)) {
            Audio.playSound(sound, new Vector3f(10, 0, 0), false);
        }*/
        
        entities.values().forEach(entity -> entity.update(targetDelta));
    }

    @Override
    public void render(Map<String, GLProgram> glPrograms, int viewportID, Camera camera) {
        GLProgram defaultProgram = glPrograms.get("default");
        defaultProgram.use();
        
        entities.values().forEach(entity -> entity.render(defaultProgram, camera, getLightSources(), getNumLights()));
    }

    @Override
    public void exit() {
    }

}