package com.pixelpennant;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pixelpennant.engine.BaseballGame;
import com.pixelpennant.engine.Half;
import com.pixelpennant.engine.LineScore;

import java.util.ArrayList;
import java.util.List;

/** Complete touch-first V0.1.0 presentation; baseball rules live in the engine package. */
public final class PixelPennantGame extends ApplicationAdapter {
    private static final float W = 960, H = 540;
    private enum Scene { TITLE, INNINGS, PLAY, FINAL }
    private enum Phase { PITCH_TYPE, PITCH_LOCATION, PITCH_POWER, BATTING, FIELDING, RUNNING }
    private static final class Button {
        final float x, y, w, h;
        final String label;
        final Runnable action;

        Button(float x, float y, float w, float h, String label, Runnable action) {
            this.x = x; this.y = y; this.w = w; this.h = h; this.label = label; this.action = action;
        }
        boolean hit(float px, float py) { return px >= x && px <= x+w && py >= y && py <= y+h; }
    }

    private final FitViewport viewport = new FitViewport(W, H);
    private final List<Button> buttons = new ArrayList<>();
    private SpriteBatch batch;
    private ShapeRenderer shapes;
    private BitmapFont font;
    private Scene scene = Scene.TITLE;
    private Phase phase = Phase.PITCH_TYPE;
    private BaseballGame game;
    private int chosenInnings = 3;
    private float aimX = 480, aimY = 265, meter, meterDirection = 1;
    private String pitch = "FAST", message = "CHOOSE YOUR PITCH";
    private String playCall = "";
    private float playCallTimer = 0f;
    private boolean ballAnimating = false;
    private float ballT = 0f, ballFromX = 480f, ballFromY = 300f, ballToX = 480f, ballToY = 215f;
    private float runnerT = 0f;
    private int runnerFrom = 0, runnerTo = 0;
    private float fielderT = 0f, fieldBallX = 480f, fieldBallY = 315f;
    private float swingT = 0f, pitchVisualT = 0f;
    private boolean batterPitchActive = false;
    private final Color navy = Color.valueOf("071B35"), blue = Color.valueOf("178BDB");
    private final Color cyan = Color.valueOf("52D9FF"), cream = Color.valueOf("FFF3C4");
    private final Color orange = Color.valueOf("FF7A32"), grass = Color.valueOf("218C4A");

    @Override public void create() {
        batch = new SpriteBatch(); shapes = new ShapeRenderer(); font = new BitmapFont();
        font.getData().setScale(1.55f); font.getRegion().getTexture().setFilter(
                com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest,
                com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest);
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override public boolean touchDown(int sx, int sy, int pointer, int button) {
                viewport.unproject(tmp.set(sx, sy));
                for (Button b : List.copyOf(buttons)) if (b.hit(tmp.x, tmp.y)) { b.action.run(); return true; }
                if (scene == Scene.PLAY && phase == Phase.PITCH_LOCATION && tmp.x > 385 && tmp.x < 575 && tmp.y > 170 && tmp.y < 360) {
                    aimX = tmp.x; aimY = tmp.y; phase = Phase.PITCH_POWER; message = "Tap the meter near the center!";
                }
                return true;
            }
        });
    }
    private final com.badlogic.gdx.math.Vector2 tmp = new com.badlogic.gdx.math.Vector2();

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void render() {
        float dt = Math.min(Gdx.graphics.getDeltaTime(), .05f);
        if (phase == Phase.PITCH_POWER) { meter += dt * 1.3f * meterDirection; if (meter > 1 || meter < 0) { meterDirection *= -1; meter = MathUtils.clamp(meter, 0, 1); } }
        if (playCallTimer > 0f) playCallTimer = Math.max(0f, playCallTimer - dt);
        if (ballAnimating) { ballT += dt * 2.8f; if (ballT >= 1f) { ballT = 1f; ballAnimating = false; } }
        if (runnerT > 0f) runnerT = Math.max(0f, runnerT - dt * 1.8f);
        if (fielderT > 0f) fielderT = Math.max(0f, fielderT - dt * 1.5f);
        if (swingT > 0f) swingT = Math.max(0f, swingT - dt * 4f);
        if (scene == Scene.PLAY && phase == Phase.BATTING) {
            pitchVisualT += dt * .7f;
            if (pitchVisualT >= 1f) pitchVisualT = 0f;
            batterPitchActive = true;
        } else batterPitchActive = false;
        Gdx.gl.glClearColor(.02f,.06f,.12f,1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply(); shapes.setProjectionMatrix(viewport.getCamera().combined); batch.setProjectionMatrix(viewport.getCamera().combined);
        buttons.clear();
        if (scene == Scene.TITLE) drawTitle(); else if (scene == Scene.INNINGS) drawInnings();
        else if (scene == Scene.PLAY) drawGame(); else drawFinal();
        drawButtons();
        endDrawing();
    }

    private void drawTitle() {
        beginShapes(); rect(0,0,W,H,navy); rect(0,0,W,160,Color.valueOf("D85F32"));
        // Original geometric bird/pennant mark.
        tri(480,410,390,300,570,300,blue); tri(480,395,435,330,525,330,cyan);
        label("PIXEL", 397, 265, cream, 2.4f); label("PENNANT", 354, 205, cyan, 2.4f);
        label("V0.1.1  -  HARBOUR LIGHT PARK", 314, 170, cream, .85f);
        button(350,65,260,70,"PLAY GAME", () -> scene = Scene.INNINGS);
    }
    private void drawInnings() {
        panel(170,100,620,350); label("CHOOSE GAME LENGTH", 303,380,cream,1.35f);
        label("BLUEBIRDS vs MOTORS", 330,330,cyan,1f);
        button(225,205,150,80,"3 INN",()->start(3)); button(405,205,150,80,"6 INN",()->start(6));
        button(585,205,150,80,"9 INN",()->start(9)); button(365,125,230,55,"BACK",()->scene=Scene.TITLE);
    }
    private void start(int innings) { chosenInnings=innings; game=new BaseballGame(innings); phase=Phase.PITCH_TYPE; message="CHOOSE YOUR PITCH"; playCall=""; playCallTimer=0; ballAnimating=false; scene=Scene.PLAY; }

    private void drawGame() {
        drawBallpark(); drawScoreboard(); drawPlayAnimation(); drawAtBatAnimation(); panel(18,18,924,116);
        label(message,38,112,cream,1.28f);
        label(game.isTorontoBatting() ? "TORONTO AT BAT" : "DETROIT AT BAT", 690,112, game.isTorontoBatting()?cyan:orange,.72f);
        if (playCallTimer > 0f && !playCall.isEmpty()) {
            panel(300,360,360,64);
            label(playCall, 330,405, cream,1.45f);
        }
        if (phase == Phase.PITCH_TYPE) {
            button(220,38,155,55,"FAST",()->choosePitch("FAST")); button(402,38,155,55,"CURVE",()->choosePitch("CURVE"));
            button(584,38,155,55,"CHANGE",()->choosePitch("CHANGE"));
        } else if (phase == Phase.PITCH_LOCATION) {
            label("TAP A LOCATION IN THE ZONE",300,76,cyan,.92f); drawZone();
        } else if (phase == Phase.PITCH_POWER) {
            rect(245,45,470,28,Color.DARK_GRAY); rect(255,51,450*meter,16, meterQuality()>0.7f?cyan:orange);
            button(375,78,210,46,"SET POWER",this::resolvePitch);
        } else if (phase == Phase.BATTING) {
            drawBattingZone(); drawTarget();
            label("AIM",70,105,cyan,.72f);
            button(32,52,72,38,"LEFT",()->moveAim(-12,0)); button(184,52,72,38,"RIGHT",()->moveAim(12,0));
            button(108,83,72,38,"UP",()->moveAim(0,12)); button(108,21,72,38,"DOWN",()->moveAim(0,-12));
            button(615,34,145,70,"SWING",()->bat(false)); button(775,34,145,70,"BUNT",()->bat(true));
        } else if (phase == Phase.FIELDING) {
            label("FIELDERS PURSUING - THROW TO:",267,75,cyan,.7f);
            button(220,27,120,48,"1ST",()->throwBase(1)); button(355,27,120,48,"2ND",()->throwBase(2));
            button(490,27,120,48,"3RD",()->throwBase(3)); button(625,27,120,48,"HOME",()->throwBase(4));
        } else {
            button(275,35,140,55,"ADVANCE",()->runChoice(1)); button(430,35,120,55,"HOLD",()->runChoice(0));
            button(565,35,140,55,"RETURN",()->runChoice(-1));
        }
    }
    private void drawBallpark() {
        beginShapes(); rect(0,134,W,406,Color.valueOf("5CC9E8")); rect(0,355,W,45,Color.valueOf("253953"));
        for(int x=0;x<960;x+=32) rect(x,365+(x%64)/8,22,12,(x/32)%2==0?orange:cream);
        rect(0,134,W,225,grass); tri(480,145,100,359,860,359,Color.valueOf("2AA65A"));
        tri(480,155,310,315,650,315,Color.valueOf("D6A05E"));
        diamond(480,255,82,Color.valueOf("E8C47D")); diamond(480,246,7,Color.WHITE);
        // Larger, readable defensive players.
        drawPlayer(480,300,orange,false);
        for (int[] p : new int[][]{{390,270},{570,270},{330,325},{630,325}}) {
            float px=p[0], py=p[1];
            if(fielderT>0f){float q=1f-fielderT; px=MathUtils.lerp(px,fieldBallX,q*.72f); py=MathUtils.lerp(py,fieldBallY,q*.72f);}
            drawPlayer(px,py,orange,false);
        }
        if(game != null && game.isTorontoBatting()) drawBatter(425,205,cyan);
    }
    private void drawPlayer(float x,float y,Color jersey,boolean batter){
        beginShapes();
        shapes.setColor(cream); shapes.circle(x,y+30,8,10);
        shapes.setColor(jersey); shapes.rect(x-9,y+9,18,21);
        shapes.setColor(navy); shapes.rect(x-9,y,6,10); shapes.rect(x+3,y,6,10);
        shapes.setColor(jersey); shapes.rect(x-14,y+16,5,6); shapes.rect(x+9,y+16,5,6);
        if(batter){ shapes.setColor(cream); shapes.rect(x+7,y+10,4,25); }
    }
    private void drawBatter(float x,float y,Color jersey){
        drawPlayer(x,y,jersey,true);
        beginShapes(); shapes.setColor(cream);
        float p=swingT>0f?(1f-swingT):0f;
        float bx=x+13+(p*20f), by=y+17+(p*8f);
        shapes.rect(bx,by,5,34);
    }
    private void drawAtBatAnimation(){
        if(!batterPitchActive || phase!=Phase.BATTING)return;
        float t=MathUtils.clamp(pitchVisualT,0,1);
        // Pitch starts at the mound and grows slightly as it approaches home plate.
        float x=MathUtils.lerp(480,455,t), y=MathUtils.lerp(310,225,t);
        beginShapes();
        shapes.setColor(new Color(1f,1f,1f,.35f));
        shapes.circle(MathUtils.lerp(480,455,Math.max(0,t-.12f)),MathUtils.lerp(310,225,Math.max(0,t-.12f)),4+4*t,10);
        shapes.setColor(Color.WHITE); shapes.circle(x,y,7+5*t,12);
        if(t>.68f) {
            shapes.setColor(cyan); shapes.circle(aimX,aimY,6,12);
        }
        if(t>.82f) label("SWING!",535,245,cream,1.05f);
    }
    private void drawScoreboard() {
        panel(18,410,924,112); label("DET MOTORS",38,490,orange,1.0f); label("TOR BLUEBIRDS",38,450,cyan,1.0f);
        label(String.valueOf(game.awayRuns()),235,490,cream,1f); label(String.valueOf(game.homeRuns()),235,450,cream,1f);
        label((game.half()==Half.TOP?"TOP ":"BOT ")+game.inning(),315,485,cream,1.15f);
        label("B "+game.balls()+"  S "+game.strikes()+"  O "+game.outs(),430,485,cream,1.05f);
        label("HITS  "+game.awayHits()+" / "+game.homeHits(),655,485,cream,.78f);
        boolean[] b=game.bases(); diamond(836,464,12,b[1]?cyan:Color.DARK_GRAY); diamond(812,443,12,b[2]?cyan:Color.DARK_GRAY); diamond(860,443,12,b[0]?cyan:Color.DARK_GRAY);
    }
    private void drawZone() {
        beginShapes();
        shapes.setColor(new Color(0,0,0,.28f)); shapes.rect(385,170,190,190);
        shapes.end(); shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(cream); shapes.rect(385,170,190,190);
        shapes.line(448,170,448,360); shapes.line(512,170,512,360);
        shapes.line(385,233,575,233); shapes.line(385,297,575,297);
        shapes.end(); shapes.begin(ShapeRenderer.ShapeType.Filled);
    }
    private void drawBattingZone() {
        beginShapes(); shapes.end(); shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(cream); shapes.rect(430,205,100,100);
        shapes.line(463,205,463,305); shapes.line(497,205,497,305);
        shapes.line(430,238,530,238); shapes.line(430,272,530,272);
        shapes.end(); shapes.begin(ShapeRenderer.ShapeType.Filled);
    }
    private void drawTarget() {
        beginShapes(); shapes.setColor(cyan); shapes.circle(aimX,aimY,4,10);
        shapes.end(); shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(cyan); shapes.circle(aimX,aimY,12,14);
        shapes.line(aimX-18,aimY,aimX+18,aimY); shapes.line(aimX,aimY-18,aimX,aimY+18);
        shapes.end(); shapes.begin(ShapeRenderer.ShapeType.Filled);
    }

    private void choosePitch(String value) { pitch=value; phase=Phase.PITCH_LOCATION; message=value+"BALL - PICK A SPOT"; }
    private void moveAim(float dx, float dy) {
        aimX = MathUtils.clamp(aimX + dx, 435, 525);
        aimY = MathUtils.clamp(aimY + dy, 210, 300);
    }
    private float meterQuality() { return 1f-Math.abs(meter-.5f)*2f; }
    private void callPlay(String text){ playCall=text; playCallTimer=1.25f; }
    private void animateBall(float x1,float y1,float x2,float y2){ballFromX=x1;ballFromY=y1;ballToX=x2;ballToY=y2;ballT=0;ballAnimating=true;}
    private void fieldersChase(float x,float y){fieldBallX=x;fieldBallY=y;fielderT=1f;}
    private void drawPlayAnimation(){
        if(ballAnimating){
            float t=MathUtils.clamp(ballT,0,1), x=MathUtils.lerp(ballFromX,ballToX,t), y=MathUtils.lerp(ballFromY,ballToY,t);
            beginShapes(); shapes.setColor(Color.WHITE); shapes.circle(x,y,6,12);
        }
        if(runnerT>0f){
            float p=1f-runnerT, x=MathUtils.lerp(480,565,p), y=MathUtils.lerp(246,300,p);
            beginShapes(); shapes.setColor(cyan); shapes.circle(x,y,8,12);
        }
    }
    private void resolvePitch() {
        float quality=meterQuality(); meter=0; animateBall(480,300,aimX,190);
        if (quality<.25f) { game.recordBall(); message="BALL - outside"; callPlay("BALL!"); nextPitch(); }
        else if (MathUtils.random() < .42f + quality * .32f) {
            game.recordStrike(); message="STRIKE - painted the zone"; callPlay("STRIKE!"); nextPitch();
        } else {
            phase=Phase.FIELDING; message="IN PLAY - FIELDERS CHASING!"; callPlay("IN PLAY!"); float fx=MathUtils.random(300,660), fy=MathUtils.random(300,350); animateBall(480,215,fx,fy); fieldersChase(fx,fy);
        }
    }
    private void bat(boolean bunt) {
        swingT=1f; batterPitchActive=false; pitchVisualT=0f;
        float distance=Math.abs(aimX-480)+Math.abs(aimY-265);
        float skill=MathUtils.random()+meterQuality()*.45f-distance/350f+(bunt?.05f:0);
        if(skill<.34f){game.recordStrike();message="SWING AND A MISS";callPlay("MISS!");animateBall(458,238,480,205);nextPitch();}
        else if(skill<.55f){game.recordOut();message=bunt?"BUNT - OUT AT FIRST":"GROUNDER - OUT";callPlay("OUT!");animateBall(458,238,565,300);fieldersChase(565,300);nextPitch();}
        else { int bases=skill>.98f&&!bunt?2:1;game.recordHit(bases);phase=Phase.RUNNING;message=bases==2?"DOUBLE - INTO THE GAP!":"SINGLE - CLEAN BASE HIT!";callPlay(bases==2?"DOUBLE!":"SINGLE!");float hx=MathUtils.random(300,660), hy=MathUtils.random(315,350);animateBall(458,238,hx,hy);fieldersChase(hx,hy);runnerT=1f;checkEnd(); }
    }
    private void throwBase(int base) { boolean out=MathUtils.random()<.62f+(base==1?.14f:0); animateBall(480,320,base==1?565:base==2?480:base==3?395:480,base==4?246:300); if(out){game.recordOut();message="THROW IN TIME - OUT!";callPlay("OUT!");}else{game.recordHit(1);message="SAFE - BASE HIT!";callPlay("SAFE!");runnerT=1f;} checkEnd(); if(scene==Scene.PLAY){phase=Phase.RUNNING;} }
    private void runChoice(int choice) { message=choice>0?"RUNNERS ADVANCE":choice<0?"RUNNERS RETURN":"RUNNERS HOLD"; callPlay(choice>0?"ADVANCE!":choice<0?"RETURN!":"HOLD!"); if(choice>0)runnerT=1f; nextPitch(); }
    private void nextPitch() { checkEnd(); if(scene==Scene.PLAY){ phase=game.isTorontoBatting()?Phase.BATTING:Phase.PITCH_TYPE; message=game.isTorontoBatting()?"BLUEBIRDS BATTING - LINE IT UP!":"CHOOSE YOUR PITCH"; } }
    private void checkEnd(){if(game.isGameOver())scene=Scene.FINAL;}

    private void drawFinal() {
        panel(100,55,760,430); label("FINAL",420,450,cream,1.5f); LineScore s=game.lineScore();
        label("TEAM",145,390,cyan,.75f); for(int i=0;i<s.innings();i++) label(String.valueOf(i+1),300+i*45,390,cream,.65f);
        label("R  H",750,390,cyan,.75f); label("DET",145,340,orange,.8f); label("TOR",145,295,cyan,.8f);
        for(int i=0;i<s.innings();i++){label(String.valueOf(s.away().get(i)),300+i*45,340,cream,.7f);label(String.valueOf(s.home().get(i)),300+i*45,295,cream,.7f);}
        label(game.awayRuns()+"  "+game.awayHits(),750,340,cream,.8f);label(game.homeRuns()+"  "+game.homeHits(),750,295,cream,.8f);
        label(game.homeRuns()>game.awayRuns()?"BLUEBIRDS WIN!":"MOTORS WIN!",350,230,game.homeRuns()>game.awayRuns()?cyan:orange,1.1f);
        button(245,105,210,65,"REMATCH",()->start(chosenInnings)); button(505,105,210,65,"MAIN MENU",()->scene=Scene.TITLE);
    }

    private void panel(float x,float y,float w,float h){
        beginShapes(); shapes.setColor(navy); shapes.rect(x,y,w,h);
        shapes.end(); shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(blue); shapes.rect(x,y,w,h);
        shapes.end(); shapes.begin(ShapeRenderer.ShapeType.Filled);
    }
    private void button(float x,float y,float w,float h,String text,Runnable action){buttons.add(new Button(x,y,w,h,text,action));}
    private void drawButtons(){for(Button b:buttons){beginShapes();rect(b.x,b.y,b.w,b.h,blue);rect(b.x+5,b.y+5,b.w-10,b.h-10,navy);label(b.label,b.x+14,b.y+b.h/2+7,cream,.7f);}}
    private void beginShapes(){if(batch.isDrawing())batch.end();if(!shapes.isDrawing())shapes.begin(ShapeRenderer.ShapeType.Filled);}
    private void beginBatch(){if(shapes.isDrawing())shapes.end();if(!batch.isDrawing())batch.begin();}
    private void rect(float x,float y,float w,float h,Color c){beginShapes();shapes.setColor(c);shapes.rect(x,y,w,h);}
    private void tri(float x1,float y1,float x2,float y2,float x3,float y3,Color c){beginShapes();shapes.setColor(c);shapes.triangle(x1,y1,x2,y2,x3,y3);}
    private void diamond(float x,float y,float r,Color c){tri(x,y+r,x-r,y,x,y-r,c);tri(x,y+r,x+r,y,x,y-r,c);}
    private void label(String text,float x,float y,Color c,float scale){beginBatch();font.getData().setScale(scale);font.setColor(c);font.draw(batch,text,x,y);}
    private void endDrawing(){if(batch.isDrawing())batch.end();if(shapes.isDrawing())shapes.end();}
    @Override public void dispose(){endDrawing();batch.dispose();shapes.dispose();font.dispose();}
}
