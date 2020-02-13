/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Models;

import java.awt.event.KeyEvent;

/**
 *
 * @author 14NBill
 */
public interface IKeyCallback {
    void KeyType(KeyEvent ke);
    void KeyPress(KeyEvent ke);
    void KeyRelease(KeyEvent ke);
}
