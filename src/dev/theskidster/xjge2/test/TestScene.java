package dev.theskidster.xjge2.test;

import dev.theskidster.xjge2.scene.Scene;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

public class TestScene extends Scene {

    @Override
    public void update() {
        entityList.forEach(entity -> entity.update());
    }

    @Override
    public void render() {
    }

}