/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schoolgame.Game;

import schoolgame.Engine.GameEngine;
import schoolgame.Models.GameObject;
import schoolgame.Models.IGameObjectComponent;
import schoolgame.Models.MotionComponent;
import schoolgame.Models.TextObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import schoolgame.Engine.SoundEngine;

/**
 * @author 14cfirth
 */
public class GameController {

    public static GameController singleton;
    public int round = 0;
    public boolean canFire = false;
    public int ballCount = 1;
    public GameObject base;
    public TextObject tutorial;
    public TextObject scoreCounter;
    public TextObject ballCounter;
    public boolean isFiring = false;
    public boolean isMoving = false;
    private int BaseX = 400;

    public GameController() {
        singleton = this;
        new GameEngine();
        new GameObject("bg", 400, 400, -100, "/schoolgame/resources/background.png", false);
        new GameObject("base",400,735,-50, "/schoolgame/resources/base.png", false);
        new TextObject("fps", 0, 15, 100, "FPS: 0", new FrameCounterComponent());
        Start();
    }


    public void SetBase(int X) {
        BaseX = X;
        if (GameEngine.singleton.GetGameObjectsByName("ball").size() == 1 && !isFiring) MoveBase(true);
    }

    public void MoveBase(boolean resetRotation) {
        base.X = BaseX;
        if (resetRotation) base.rotation = 0;
    }

    public void Start() {
        base = new GameObject("base", BaseX, 700, 1, "/schoolgame/resources/base2.png", true, new BaseComponent());
        base.X = (BaseX);
        base.Y = 700 - base.sprite.getHeight(null) / 2f;
        tutorial = new TextObject("tutorial", 220, 400, 1000, "Left and right arrow to rotate, space to fire");
        scoreCounter = new TextObject("score", 0, 45, 1000, "Score: 0", new ScoreCounterComponent());
        ballCounter = new TextObject("balls", 0, 65, 1000, "Balls: 1", new BallCounterComponent());
    }

    public void CheckNextRound() {
        if (GameEngine.singleton.GetGameObjectsByName("ball").size() == 0 && !isFiring) NextRound();
    }

    public void NextRound() {
        round++;
        if (round % 5 == 0) ballCount++;
        CompletableFuture.supplyAsync((Supplier<Void>) () -> {
            MoveBase(true);
            isMoving = true;
            GameEngine.singleton.BlockForTicks(17);
            isMoving = false;
            return null;
        });
        new Thread(() -> {
            Thread.currentThread().setName("NextRoundThread");
            GameEngine.singleton.GetGameObjectsByName("box").forEach(box -> {
                box.AddMotion(new MotionComponent(0, 5, 17));
            });
            GameEngine.singleton.GetGameObjectsByName("ballBox").forEach(box -> {
                box.AddMotion(new MotionComponent(0, 5, 17));
            });
            int ballBoxes = 0;
            int boxes = 0;
            for (int x = 10; x < 720; x += 85) {
                if (RNG(0, 100) < Math.min(Math.ceil(round), 8)) {
                    if (ballBoxes < 2) {
                        GameObject box = new GameObject("ballBox", x + 40, -45, 10, "/schoolgame/resources/ballBox.png", true);
                        box.AddMotion(new MotionComponent(0, 5, 17));
                        ballBoxes++;
                    }
                } else if (RNG(0, 100) < Math.min(Math.ceil(round * 2), 45)) {
                    GameObject box = new GameObject("box", x + 40, -45, 10, "/schoolgame/resources/box2.png", true, new BoxComponent(Math.max(round + RNG(-2, 2), 1)));
                    box.AddMotion(new MotionComponent(0, 5, 17));
                    boxes++;
                }
            }
            if (boxes == 0) {
                int x = 10 + (RNG(1, 9) * 85);
                int forceSpawnTried = 0;
                while (BBoxExistsAt(x)) {
                    if (forceSpawnTried >= 9) break;
                    x = 10 + (RNG(1, 9) * 85);
                    forceSpawnTried++;
                }
                if (forceSpawnTried < 9) {
                    GameObject box = new GameObject("box", x + 40, -45, 10, "/schoolgame/resources/box2.png", true, new BoxComponent(Math.max(round + RNG(-2, 2), 1)));
                    box.AddMotion(new MotionComponent(0, 5, 17));
                }
            }
            if (GameEngine.singleton.GetGameObjectsByName("ballBox").size() > 0 || GameEngine.singleton.GetGameObjectsByName("box").size() > 0) {
                GameEngine.singleton.BlockForTicks(17);
            }
            if (GameEngine.singleton.GetGameObjectsByName("box").stream().anyMatch(go -> go.Y > 685)) {
                GameEngine.singleton.GetGameObjectsByName("box").stream().filter(go -> go.Y > 685).forEach(go -> GameEngine.singleton.GetComponentFromGameObject(go, BoxComponent.class).forEach(igo -> {
                    if (igo instanceof  BoxComponent) {
                        BoxComponent box = (BoxComponent) igo;
                        box.DontChangeColour = true;
                        box.ChangeColourOverride(go, Color.RED);
                    }
                }));
                new SoundEngine().Play("/schoolgame/resources/GameOverSound.wav");
                BaseX = 400;
                MoveBase(true);
                GameEngine.singleton.BlockForTicks(100);
                GameEngine.singleton.GetGameObjectsByName("box").forEach(GameObject::Destroy);
                GameEngine.singleton.GetGameObjectsByName("ballBox").forEach(GameObject::Destroy);
                if (getHighscore() < round) setHighscore(round);
                tutorial = new TextObject("tutorial", 140, 400, 1000, "Game Over! Press space to try again. Score: " + round + " High Score: " + getHighscore());
                round = 0;
                ballCount = 1;
                return;
            }
            GameEngine.singleton.GetGameObjectsByName("ballBox").forEach(go -> {
                if (go.Y > 685) {
                    go.Destroy();
                }
            });
        }).start();
    }

    private boolean BBoxExistsAt(int x) {
        return GameEngine.singleton.GetGameObjectsByName("ballBox").stream().anyMatch(go -> go.X == (x + 40) && go.Y <= 40);
    }

    private int RNG(int low, int high) {
        Random r = new Random();
        return r.nextInt(high-low) + low;
    }
    
    private int getHighscore() {
        try {
            if (!new File("scores.dat").exists()) return 0;
            BufferedReader br = new BufferedReader(new FileReader(new File("scores.dat")));
            List<String> lines = br.lines().collect(Collectors.toList());
            if (lines.stream().anyMatch(l -> l.startsWith("hs:"))) {
                return Integer.parseInt(lines.stream().filter(l -> l.startsWith("hs:")).findFirst().get().substring(3));
            } else {
                return 0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    private void setHighscore(int score) {
        try {
            List<String> lines;
            if (new File("scores.dat").exists()) {
                BufferedReader br = new BufferedReader(new FileReader(new File("scores.dat")));
                lines = br.lines().collect(Collectors.toList());
                lines.replaceAll(new HSUpdater(score));
            } else {
                lines = new ArrayList<String>() {
                    {
                        add("hs:" + score);
                    }
                };
            }
            File file = new File("scores.dat");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeBytes(ListToString(lines));
            dos.close();
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private String ListToString(List<String> list) {
        String listString = "";
        for (String l : list) {
            listString += (l + "\n");
        }
        return listString;
    }
    
    class HSUpdater implements UnaryOperator<String> 
    {
        private int score;
        
        public HSUpdater(int score) {
            this.score  = score;
        }
        
        @Override
        public String apply(String t) {
            if (t.startsWith("hs:")) return "hs:" + score;
            return t;
        }
    }
}
