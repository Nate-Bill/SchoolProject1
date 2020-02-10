/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Engine;

import schoolgame.Engine.GameEngine;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author 14NBill
 */
public class RenderEngine extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GameEngine.singleton.DoRender(g);
    }
}
