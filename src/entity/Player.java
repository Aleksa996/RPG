package entity;

import main.GamePanel;
import main.KeyHandler;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {

    KeyHandler keyH;

    public final int screenX;
    public final int screenY;

    public Player(GamePanel gp, KeyHandler keyH){
        super(gp);
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize/2);
        screenY = gp.screenHeight / 2 - (gp.tileSize/2);

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 32;
        solidArea.height = 32;

        attackArea.width = 36;
        attackArea.height = 36;

        setDefaultValues();
        getPlayerImage();
        getPlayerAttackImage();
    }
    public void setDefaultValues(){
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        direction = "down";
        //PLAYER STATUS
        maxLife = 6;
        life = maxLife;
    }
    public void getPlayerImage(){
            up1 = setup("boy_up_1",gp.tileSize,gp.tileSize);
            up2 = setup("boy_up_2",gp.tileSize,gp.tileSize);
            down1 = setup("boy_down_1",gp.tileSize,gp.tileSize);
            down2 = setup("boy_down_2",gp.tileSize,gp.tileSize);
            left1 = setup("boy_left_1",gp.tileSize,gp.tileSize);
            left2 = setup("boy_left_2",gp.tileSize,gp.tileSize);
            right1 = setup("boy_right_1",gp.tileSize,gp.tileSize);
            right2 = setup("boy_right_2",gp.tileSize,gp.tileSize);
    }

    public void getPlayerAttackImage(){
        attackUp1 = setup("attack/boy_attack_up_1",gp.tileSize,gp.tileSize*2);
        attackUp2 = setup("attack/boy_attack_up_2",gp.tileSize,gp.tileSize*2);
        attackDown1 = setup("attack/boy_attack_down_1",gp.tileSize,gp.tileSize*2);
        attackDown2  = setup("attack/boy_attack_down_2",gp.tileSize,gp.tileSize*2);
        attackLeft1 = setup("attack/boy_attack_left_1",gp.tileSize * 2,gp.tileSize);
        attackLeft2 = setup("attack/boy_attack_left_2",gp.tileSize * 2,gp.tileSize);
        attackRight1 = setup("attack/boy_attack_right_1",gp.tileSize * 2,gp.tileSize);
        attackRight2 = setup("attack/boy_attack_right_2",gp.tileSize * 2,gp.tileSize);
    }

    public void update(){

        if(attacking){
            attacking();
        }
        else if(keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed || keyH.enterPressed){
            if(keyH.upPressed){
                direction = "up";
            }
            else if(keyH.downPressed){
                direction = "down";
            }
            else if(keyH.leftPressed){
                direction = "left";
            }
            if(keyH.rightPressed){
                direction = "right";
            }
            //CHECK TILE COLLISION
            collisionOn = false;
            gp.cChecker.checkTile(this);
            //CHECK OBJ COLLISION
            int objIndex = gp.cChecker.checkObject(this,true);
            pickUpObject(objIndex);
            //CHECK NPC COLLISION
            int npcIndex = gp.cChecker.checkEntity(this,gp.npc);
            interactNPC(npcIndex);
            //CHECK MONSTER COLLISION
            int monsterIndex = gp.cChecker.checkEntity(this,gp.monster);
            contactMonster(monsterIndex);
            //CHECK EVENT
            gp.eHandler.checkEvent();

            //IF COLLISION IS FALSE, PLAYER CAN MOVE
            if(!collisionOn){
                switch(direction){
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }
            gp.keyH.enterPressed = false;

            spriteCounter++;
            if(spriteCounter > 12){
                if(spriteNum == 1){
                    spriteNum = 2;
                } else if (spriteNum == 2) {
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
        }
        //THIS NEED TO BE OUTSIDE OF KEY IF STATEMENT
        if(invincible){
            invicibleCounter++;
            if(invicibleCounter > 60){
                invincible = false;
                invicibleCounter = 0;
            }
        }
    }
    public void attacking(){
        spriteCounter++;
        if(spriteCounter <= 5){
            spriteNum = 1;
        }
        if(spriteCounter > 5 && spriteCounter <= 25){
            spriteNum = 2;
            //SAVE THE CURRENT WORLDX WORLDY SOLIDAREA
            int currentWorldX = worldX;
            int currentWorldY = worldY;
            int solidAreaWidth = solidArea.width;
            int solidAreaHeight = solidArea.height;
            //ADJUST PLAYERS WORLDX/Y FOR THE ATTACKAREA
            switch(direction){
                case "up": worldY -= attackArea.height; break;
                case "down": worldY += attackArea.height; break;
                case "left": worldX -= attackArea.width; break;
                case "right": worldX += attackArea.width; break;
            }
            // attackArea becomes solidArea
            solidArea.width = attackArea.width;
            solidArea.height = attackArea.height;
            //Check monster collision with the updated worldX,worldY and solidArea
            int monsterIndex = gp.cChecker.checkEntity(this,gp.monster);
            damageMonster(monsterIndex);
            //After checking collision, restore the original data
            worldX = currentWorldX;
            worldY = currentWorldY;
            solidArea.width = solidAreaWidth;
            solidArea.height = solidAreaHeight;

        }
        if(spriteCounter > 25){
            spriteNum = 1;
            spriteCounter = 0;
            attacking = false;
        }
    }
    public void damageMonster(int i){
        if(i != 999){
            System.out.println("Hit!");
        }else{
            System.out.println("Miss!");
        }
    }
    public void pickUpObject(int i){
        if(i != 999){

        }
    }
    public void interactNPC(int i){
        if(i != 999){
            if(gp.keyH.enterPressed){
                gp.gameState = gp.dialougeState;
                gp.npc[i].speak();
            }
        }else{
            if(gp.keyH.enterPressed){
                attacking = true;
            }
        }
    }
    public void contactMonster(int i){
        if(i != 999){
            if(!invincible){
                life -= 1;
                invincible = true;
            }
        }
    }
    public void draw(Graphics2D g2){

        BufferedImage image = null;
        int tempScreenX = screenX;
        int tempScreenY = screenY;

        switch (direction){
            case "up":
                if(!attacking){
                    if(spriteNum == 1){image = up1;}
                    if(spriteNum == 2){image = up2;}
                }
                if(attacking){
                    tempScreenY = screenY - gp.tileSize;
                    if(spriteNum == 1){image = attackUp1;}
                    if(spriteNum == 2){image = attackUp2;}
                }
                break;
            case "down":
                if(!attacking){
                    if(spriteNum == 1){image = down1;}
                    if(spriteNum == 2){image = down2;}
                }
                if(attacking){
                    if(spriteNum == 1){image = attackDown1;}
                    if(spriteNum == 2){image = attackDown2;}
                }
                break;
            case "left":
                if(!attacking){
                    if(spriteNum == 1){image = left1;}
                    if(spriteNum == 2) {image = left2;}
                }
                if(attacking){
                    tempScreenX = screenX - gp.tileSize;
                    if(spriteNum == 1){image = attackLeft1;}
                    if(spriteNum == 2) {image = attackLeft2;}
                }
                break;
            case "right":
                if(!attacking){
                    if(spriteNum == 1){image = right1;}
                    if(spriteNum == 2) {image = right2;}
                }
                if(attacking){
                    if(spriteNum == 1){image = attackRight1;}
                    if(spriteNum == 2) {image = attackRight2;}
                }
                break;
        }
        if(invincible){
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3F));
        }
        g2.drawImage(image, tempScreenX, tempScreenY,null);
        //RESET ALPHA
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
    }
}
