package org.xjge.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.xjge.core.Component;
import org.xjge.core.Control;
import org.xjge.core.Entity;
import static org.xjge.core.Input.NO_DEVICE;
import org.xjge.core.Puppet;

/**
 * 
 * @author J Hoffman
 * @since 
 */
class ComponentUnit extends Component implements PropertyChangeListener {
    
    int inputDeviceID;
    
    private final Puppet puppetExplore;
    private final Puppet puppetBattle;
    
    final Entity entity;
    
    ComponentUnit(Entity entity, CameraOverhead camera, int inputDeviceID) {
        this.entity = entity;
        this.inputDeviceID = inputDeviceID;
        
        puppetExplore = new Puppet("unit_explore");
        puppetBattle  = new Puppet("unit_battle");
        
        puppetExplore.commands.put(Control.LEFT_STICK_X, new CommandMove(entity, camera));
        puppetExplore.commands.put(Control.LEFT_STICK_Y, new CommandMove(entity, camera));
        
        puppetExplore.setInputDevice(inputDeviceID);
    }
    
    boolean turnFinished(Scene3D scene) {
        return false;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("GAMEMODE_CHANGED")) {
            if(evt.getNewValue() instanceof GameModeExplore) {
                puppetExplore.setInputDevice(inputDeviceID);
                puppetBattle.setInputDevice(NO_DEVICE);
            } else if(evt.getNewValue() instanceof GameModeBattle) {
                puppetExplore.setInputDevice(NO_DEVICE);
                puppetBattle.setInputDevice(inputDeviceID);
            }
        }
    }
    
}