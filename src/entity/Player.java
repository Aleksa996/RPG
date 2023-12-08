package entity;

import main.GamePanel;
import main.KeyHandler;
import object.OBJ_Fireball;
import object.OBJ_Key;
import object.OBJ_Shield_Wood;
import object.OBJ_Sword_Normal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends Entity {

    KeyHandler keyH;
    public final int screenX;
    public final int screenY;
    public boolean attackCanceled = false;

    public ArrayList<Entity> inventory = new ArrayList<>();
    public final int maxInventorySize = 20;

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

        setDefaultValues();
        getPlayerImage();
        getPlayerAttackImage();
        setItems();
    }
    public void setDefaultValues(){
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        direction = "down";
        //PLAYER STATUS
        maxLife = 6;
        life = maxLife;
        level = 1;
        strength = 2;
        dexterity = 1;
        exp = 0;
        nextLvlExp = 5;
        coin = 0;
        maxMana = 4;
        mana = maxMana;
        ammo = 10;
        currentWeapon = new OBJ_Sword_Normal(gp);
        currentShield = new OBJ_Shield_Wood(gp);
        projectile = new OBJ_Fireball(gp);
        attack = getAttack();
        defense = getDefense();
    }

    public void setItems(){
        inventory.add(currentWeapon);
        inventory.add(currentShield);
        inventory.add(new OBJ_Key(gp));
    }

    public int getAttack(){
        attackArea = currentWeapon.attackArea;
        return attack = strength * currentWeapon.attackValue;
    }
    public int getDefense(){
        return defense = dexterity * currentShield.defenseValue;
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

        if(currentWeapon.type == type_sword){
            attackUp1 = setup("attack/boy_attack_up_1",gp.tileSize,gp.tileSize*2);
            attackUp2 = setup("attack/boy_attack_up_2",gp.tileSize,gp.tileSize*2);
            attackDown1 = setup("attack/boy_attack_down_1",gp.tileSize,gp.tileSize*2);
            attackDown2  = setup("attack/boy_attack_down_2",gp.tileSize,gp.tileSize*2);
            attackLeft1 = setup("attack/boy_attack_left_1",gp.tileSize * 2,gp.tileSize);
            attackLeft2 = setup("attack/boy_attack_left_2",gp.tileSize * 2,gp.tileSize);
            attackRight1 = setup("attack/boy_attack_right_1",gp.tileSize * 2,gp.tileSize);
            attackRight2 = setup("attack/boy_attack_right_2",gp.tileSize * 2,gp.tileSize);
        }
        if(currentWeapon.type == type_axe){
            attackUp1 = setup("attack/boy_axe_up_1",gp.tileSize,gp.tileSize*2);
            attackUp2 = setup("attack/boy_axe_up_2",gp.tileSize,gp.tileSize*2);
            attackDown1 = setup("attack/boy_axe_down_1",gp.tileSize,gp.tileSize*2);
            attackDown2  = setup("attack/boy_axe_down_2",gp.tileSize,gp.tileSize*2);
            attackLeft1 = setup("attack/boy_axe_left_1",gp.tileSize * 2,gp.tileSize);
            attackLeft2 = setup("attack/boy_axe_left_2",gp.tileSize * 2,gp.tileSize);
            attackRight1 = setup("attack/boy_axe_right_1",gp.tileSize * 2,gp.tileSize);
            attackRight2 = setup("attack/boy_axe_right_2",gp.tileSize * 2,gp.tileSize);
        }

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
            //CHECK INTERACTIVE TILE COLLISION
            int iTileIndex = gp.cChecker.checkEntity(this,gp.iTile);

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

            if(keyH.enterPressed && !attackCanceled){
                gp.playSE(7);
                attacking = true;
                spriteCounter = 0;
            }

            attackCanceled = false;

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

        if(gp.keyH.shotKeyPressed && !projectile.alive && shotAvailableCounter == 30 && projectile.haveResource(this)){

            //SET DEFAULT COORDINANTES, DIRECTION AND USER
            projectile.set(worldX,worldY,direction,true,this);
            projectile.subtractResource(this);
            //ADD IT TO THE LIST
            gp.projectileList.add(projectile);
            shotAvailableCounter = 0;
            gp.playSE(10);
        }

        //THIS NEED TO BE OUTSIDE OF KEY IF STATEMENT
        if(invincible){
            invicibleCounter++;
            if(invicibleCounter > 60){
                invincible = false;
                invicibleCounter = 0;
            }
        }
        if(shotAvailableCounter < 30){
            shotAvailableCounter++;
        }
        if(life > maxLife){
            life = maxLife;
        }
        if(mana > maxMana){
            mana = maxMana;
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
            damageMonster(monsterIndex,attack);
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
    public void damageMonster(int i, int attack){
        if(i != 999){
            if(!gp.monster[i].invincible){
                gp.playSE(5);
                int damage = attack - gp.monster[i].defense;
                if(damage < 0){
                    damage = 0;
                }
                gp.monster[i].life -= damage;
                gp.ui.addMessage(damage + "damage!");

                gp.monster[i].invincible = true;
                gp.monster[i].damageReaction();
                if(gp.monster[i].life <= 0){
                    gp.monster[i].dying = true;
                    gp.ui.addMessage("killed the " + gp.monster[i].name + "!");
                    exp += gp.monster[i].exp;
                    checkLevelUp();
                }
            }
        }
    }

    public void checkLevelUp(){

        if(exp > nextLvlExp){
            level++;
            nextLvlExp = nextLvlExp*2;
            strength++;
            dexterity++;
            attack = getAttack();
            defense = getDefense();

            gp.playSE(8);
            gp.gameState = gp.dialougeState;
            gp.ui.currentDialouge = "You are level " + level + "now!\n"
                    + "You feal stronger!";
        }
    }

    public void selectItem(){
        int itemIndex = gp.ui.getItemIndexOnSlot();

        if(itemIndex < inventory.size()){
            Entity selectedItem = inventory.get(itemIndex);
            if(selectedItem.type == type_sword || selectedItem.type == type_axe){
                currentWeapon = selectedItem;
                attack = getAttack();
                getPlayerAttackImage();
            }
            if(selectedItem.type == type_shield){
                currentShield = selectedItem;
                defense = getDefense();
            }
            if(selectedItem.type == type_consumable){
                selectedItem.use(this);
                inventory.remove(itemIndex);
            }
        }
    }

    public void pickUpObject(int i){
        if(i != 999){
            //PICK UP ONLY ITEMS
            if(gp.obj[i].type == type_pickupOnly){
                gp.obj[i].use(this);
                gp.obj[i] = null;
            }else{
                String text;
                if(inventory.size() != maxInventorySize) {
                    inventory.add(gp.obj[i]);
                    gp.playSE(1);
                    text = "Got a " + gp.obj[i].name + "!";
                }else{
                    text = "You cannot carry any more";
                }
                gp.ui.addMessage(text);
                gp.obj[i] = null;
            }
        }

    }
    public void interactNPC(int i){
        if(i != 999){
            if(gp.keyH.enterPressed){
                attackCanceled = true;
                gp.gameState = gp.dialougeState;
                gp.npc[i].speak();
            }
        }
    }
    public void contactMonster(int i){
        if(i != 999){
            if(!invincible && !gp.monster[i].dying){
                int damage = gp.monster[i].attack - defense;
                if(damage < 0){
                    damage = 0;
                }
                gp.playSE(6);
                life -= damage;
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
