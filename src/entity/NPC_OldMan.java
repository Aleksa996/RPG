package entity;

import main.GamePanel;

import java.util.Random;


public class NPC_OldMan extends Entity{

    public NPC_OldMan(GamePanel gp){
        super(gp);
        direction = "down";
        speed = 1;
        getImage();
        setDialouge();
    }

    public void setDialouge(){
        dialogues[0] = "Hello, lad!";
        dialogues[1] = "So you come to this /n island to find tresure?";
        dialogues[2] = "I used to be a great wizard /n but now... I'm bit too old fot that shit!";
        dialogues[3] = "Well, good luck!";
    }

    public void getImage(){
        up1 = setup("npc/oldman_up_1");
        up2 = setup("npc/oldman_up_2");
        down1 = setup("npc/oldman_down_1");
        down2 = setup("npc/oldman_down_2");
        left1 = setup("npc/oldman_left_1");
        left2 = setup("npc/oldman_left_2");
        right1 = setup("npc/oldman_right_1");
        right2 = setup("npc/oldman_right_2");
    }

    public void setAction(){

        actionLockCounter++;

        if(actionLockCounter == 120){
            Random random = new Random();
            int i = random.nextInt(100) + 1; //pick up number from 1 top 100

            if(i <= 25){
                direction = "up";
            }
            if(i > 25 && i <= 50){
                direction = "down";
            }
            if(i > 50 && i <= 75){
                direction = "left";
            }
            if(i > 75){
                direction = "right";
            }
            actionLockCounter = 0;
        }
    }
    public void speak(){
        super.speak();
    }

}
