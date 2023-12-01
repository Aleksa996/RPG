package main;

import entity.Entity;
import object.OBJ_Heart;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class UI {

    GamePanel gp;
    Font arial_40,arial_80B;
    Graphics2D g2;

    BufferedImage heart_full, heart_half, heart_blank;
    public boolean messageOn = false;
    ArrayList<String> message = new ArrayList<>();
    ArrayList<Integer> messageCounter = new ArrayList<>();
    public boolean gameFinished = false;
    public String currentDialouge = "";
    public int commandNum = 0;
    public int titleScreenState = 0; // 0: the first screen 1: second screen
    public int slotCol = 0;
    public int slotRow = 0;

    double playTime;
    DecimalFormat dFormat = new DecimalFormat("#0.00");

    public UI(GamePanel gp){
        this.gp = gp;
        arial_40 = new Font("Arial", Font.PLAIN, 40);
        arial_80B = new Font("Arial", Font.BOLD, 80);
        //CREATE HUD OBJECT
        Entity heart = new OBJ_Heart(gp);
        heart_full = heart.image;
        heart_half = heart.image2;
        heart_blank = heart.image3;
    }

    public void addMessage(String text){
        message.add(text);
        messageCounter.add(0);
    }

    public void draw(Graphics2D g2){
        this.g2 = g2;
        g2.setFont(arial_40);
        g2.setColor(Color.WHITE);

        if(gp.gameState == gp.titleState){
            drawTitleScreen();
        }

        if(gp.gameState == gp.playState){
            drawPlayerLife();
            drawMessage();
        }
        if(gp.gameState == gp.pauseState){
            drawPlayerLife();
            drawPauseScreen();
        }
        if(gp.gameState == gp.dialougeState){
            drawPlayerLife();
            drawDialougeScreen();
        }
        //CHARACTER STATE
        if(gp.gameState == gp.characterState){
            drawCharacterScreen();
            drawInventory();
        }
    }

    public void drawInventory(){
        //FRAME
        int frameX = gp.tileSize*9;
        int frameY = gp.tileSize;
        int frameWidth = gp.tileSize * 6;
        int frameHeight = gp.tileSize * 5;
        drawSubWindow(frameX,frameY,frameWidth,frameHeight);
        //SLOT
        final int slotXstart = frameX + 20;
        final int slotYstart = frameY + 20;
        int slotX = slotXstart;
        int slotY = slotYstart;
        int slotSize = gp.tileSize + 3;
        //DRAW PLAYERs ITEMS

        for(int i = 0; i < gp.player.inventory.size(); i++){

            g2.drawImage(gp.player.inventory.get(i).down1, slotX, slotY,null);
            slotX += slotSize;

            if(i == 4 || i == 9 || i == 14){
                slotX = slotXstart;
                slotY = slotSize;
            }

        }

        //CURSOR
        int cursorX = slotXstart + (slotSize * slotCol);
        int cursorY = slotYstart + (slotSize * slotRow);
        int cursorWidth = gp.tileSize;
        int cursorHeight = gp.tileSize;
        // DRAW CURSOR
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(cursorX,cursorY,cursorWidth,cursorHeight,10,10);


        //Description frame
        int dFrameX = frameX;
        int dFrameY = frameY + frameHeight;
        int dFrameWidth = frameWidth;
        int dFrameHeight = gp.tileSize * 3;
        drawSubWindow(dFrameX,dFrameY,dFrameWidth,dFrameHeight);
        // DRAW DESCRIPTION TEXT
        int textX = dFrameX + 20;
        int textY = dFrameY + gp.tileSize;
        g2.setFont(g2.getFont().deriveFont(28F));

        int itemIndex = getItemIndexOnSlot();

        if(itemIndex < gp.player.inventory.size()){
            for(String line: gp.player.inventory.get(itemIndex).description.split("\n")){
                g2.drawString(line, textX,textY);
                textY += 32;
            }
        }
    }

    public int getItemIndexOnSlot(){
        int itemIndex = slotCol + (slotRow*5);
        return itemIndex;
    }

    public void drawDialougeScreen(){
        //WINDOW
        int x = gp.tileSize*2;
        int y = gp.tileSize/2;
        int width = gp.screenWidth - (gp.tileSize * 4);
        int height = gp.tileSize * 4;
        drawSubWindow(x, y, width, height);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,32F));
        x += gp.tileSize;
        y += gp.tileSize;

        for(String line: currentDialouge.split("/n")){
            g2.drawString(line,x,y);
            y += 40;
        }
    }
    public void drawSubWindow(int x,int y,int width,int height){
        Color c = new Color(0,0,0,210);
        g2.setColor(c);
        g2.fillRoundRect(x,y,width,height,35,35);

        c = new Color(255,255,255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x + 5,y + 5,width - 10,height - 10,35,35);
    }

    public void drawPauseScreen(){
        String text = "PAUSED";
        int x = getXforCenteredText(text);
        int y = gp.screenHeight/2;
        g2.drawString(text, x, y);
    }
    public int getXforCenteredText(String text){
        int lenght = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth/2 - lenght/2;
        return x;
    }
    public void drawTitleScreen(){
        if(titleScreenState == 0){
            g2.setFont(g2.getFont().deriveFont(Font.BOLD,66F));
            String text = "Blue Boy Adventure";
            int x = getXforCenteredText(text);
            int y = gp.tileSize * 3;
            g2.setColor(Color.WHITE);
            g2.drawString(text,x,y);
            //MENU
            g2.setFont(g2.getFont().deriveFont(Font.BOLD,56F));
            text = "NEW GAME";
            x = getXforCenteredText(text);
            y += gp.tileSize*2;
            g2.drawString(text,x,y);
            if(commandNum == 0){
                g2.drawString(">",x - gp.tileSize,y);
            }
            text = "LOAD GAME";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text,x,y);
            if(commandNum == 1){
                g2.drawString(">",x - gp.tileSize,y);
            }
            text = "QUIT";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text,x,y);
            if(commandNum == 2){
                g2.drawString(">",x - gp.tileSize,y);
            }
        }else if(titleScreenState == 1){
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(42F));
            String text = "Select your class";
            int x = getXforCenteredText(text);
            int y = gp.tileSize*3;
            g2.drawString(text,x,y);

            text = "Fighter";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text,x,y);
            if(commandNum == 0){
                g2.drawString(">", x - gp.tileSize,y);
            }
            text = "Thief";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text,x,y);
            if(commandNum == 1){
                g2.drawString(">", x - gp.tileSize,y);
            }
            text = "Sorcerer";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text,x,y);
            if(commandNum == 2){
                g2.drawString(">", x - gp.tileSize,y);
            }
            text = "Back";
            x = getXforCenteredText(text);
            y += gp.tileSize*2;
            g2.drawString(text,x,y);
            if(commandNum == 3){
                g2.drawString(">", x - gp.tileSize,y);
            }
        }
    }

    public void drawPlayerLife(){
        int x = gp.tileSize/2;
        int y = gp.tileSize/2;
        int i = 0;
        //DRAW BLANK HEART
        while(i < gp.player.maxLife/2){
            g2.drawImage(heart_blank,x,y,null);
            i++;
            x += gp.tileSize;
        }
        //RESET
        x = gp.tileSize/2;
        y = gp.tileSize/2;
        i = 0;
        //DRAW CURRENT LIFE
        while(i < gp.player.life){
            g2.drawImage(heart_half,x,y,null);
            i++;
            if(i < gp.player.life){
                g2.drawImage(heart_full,x,y,null);
            }
            i++;
            x+= gp.tileSize;
        }
    }

    public void drawCharacterScreen(){

        //CREATE A FRAME
        final int frameX = gp.tileSize;
        final int frameY = gp.tileSize;
        final int frameWidth = gp.tileSize * 5;
        final int frameHeight = gp.tileSize * 10;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        // TEXT
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(32F));

        int textX = frameX + 20;
        int textY = frameY + gp.tileSize;
        final int lineHeight = 35;

        //NAMES
        g2.drawString("Level",textX,textY);
        textY += lineHeight;
        g2.drawString("Life",textX,textY);
        textY += lineHeight;
        g2.drawString("Strength",textX,textY);
        textY += lineHeight;
        g2.drawString("Dexterity",textX,textY);
        textY += lineHeight;
        g2.drawString("Attack",textX,textY);
        textY += lineHeight;
        g2.drawString("Defense",textX,textY);
        textY += lineHeight;
        g2.drawString("Exp",textX,textY);
        textY += lineHeight;
        g2.drawString("Next Level",textX,textY);
        textY += lineHeight;
        g2.drawString("Coin",textX,textY);
        textY += lineHeight + 20;
        g2.drawString("Weapon",textX,textY);
        textY += lineHeight + 15;
        g2.drawString("Shield",textX,textY);
        textY += lineHeight;

        //VALUES
        int tailX = (frameX + frameWidth) - 30;
        //RESET textY
        textY = frameY + gp.tileSize;
        String value;


        value = String.valueOf(gp.player.level);
        textX = getXforAlignRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.life);
        textX = getXforAlignRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.strength);
        textX = getXforAlignRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.dexterity);
        textX = getXforAlignRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.attack);
        textX = getXforAlignRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.defense);
        textX = getXforAlignRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.exp);
        textX = getXforAlignRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.nextLvlExp);
        textX = getXforAlignRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.coin);
        textX = getXforAlignRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        g2.drawImage(gp.player.currentWeapon.down1, tailX - gp.tileSize, textY - 14, null);
        textY += lineHeight;
        g2.drawImage(gp.player.currentShield.down1, tailX - gp.tileSize, textY - 14, null);

    }

    public int getXforAlignRightText(String text, int tailX){
        int lenght = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = tailX - lenght;
        return x;
    }

    public void drawMessage(){
        int messageX = gp.tileSize;
        int messageY = gp.tileSize * 2;

        g2.setFont(g2.getFont().deriveFont(Font.BOLD,32f));

        for(int i = 0; i < message.size(); i++){
            if(message.get(i) != null){
                g2.setColor(Color.BLACK);
                g2.drawString(message.get(i), messageX+2, messageY+2);
                g2.setColor(Color.WHITE);
                g2.drawString(message.get(i), messageX, messageY);

                int counter = messageCounter.get(i) + 1;
                messageCounter.set(i, counter);
                messageY += 50;

                if(messageCounter.get(i) > 180){
                    message.remove(i);
                    messageCounter.remove(i);
                }
            }
        }

    }

}

















